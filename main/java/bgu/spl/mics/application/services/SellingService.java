package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.sql.SQLOutput;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private MoneyRegister moneyRegister;
	private int currentTick;
	private AtomicInteger canStartTime;

	public SellingService(int id, AtomicInteger canStartTime) {
		super("SellingService "+id);
		moneyRegister = MoneyRegister.getInstance();
		currentTick = 0;
		this.canStartTime=canStartTime;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, b ->{
			currentTick = b.getCurrentTick();
		});

		subscribeEvent(BookOrderEvent.class, event ->{
			int proccessTick = currentTick;

			Future <Integer> future = sendEvent(new BookInventoryEvent(event.getBookTitle()));
			Customer customer = event.getCustomer();
			if ((future != null) && (future.get() != null)) {

				int bookPrice = future.get();
				if (bookPrice > 0 && customer.CanCharge(bookPrice)) {
					Future<OrderResult> resultFuture = sendEvent(new TakeBookEvent(event.getBookTitle()));
					if ((resultFuture != null) && (resultFuture.get() != null) && (resultFuture.get().equals(OrderResult.SUCCESSFULLY_TAKEN))) {
						moneyRegister.chargeCreditCard(customer, bookPrice);
						Future<Boolean> deliverFuture = sendEvent(new DeliveryEvent(customer.getDistance(), customer.getAddress()));
						OrderReceipt receipt = new OrderReceipt(0, customer.getId(), getName(), bookPrice, currentTick, event.getTick(), proccessTick, event.getBookTitle());
						moneyRegister.file(receipt);
						complete(event, receipt);
						//check about notInstock option!!!******************************
					} else complete(event, null);
				} else complete(event, null);
			}
			else complete(event, null);
		});

		subscribeBroadcast(TerminateBroadcast.class, b->{
			this.terminate();
		});
		canStartTime.incrementAndGet();
	}

}
