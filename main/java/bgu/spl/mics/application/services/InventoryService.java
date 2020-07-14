package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookInventoryEvent;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;
	private AtomicInteger canStartTime;
	public InventoryService(int id, AtomicInteger canStartTime) {
		super("InventoryService "+id);
		this.inventory = Inventory.getInstance();
		this.canStartTime=canStartTime;
	}

	@Override
	protected void initialize() {

		subscribeEvent(BookInventoryEvent.class, event ->{
			Integer result = inventory.checkAvailabilityAndGetPrice(event.getBookTitle());
			complete(event,result); });

		subscribeEvent(TakeBookEvent.class, event ->{
			OrderResult orderResult = inventory.take(event.getBookTitle());
			complete(event,orderResult); });

		subscribeBroadcast(TerminateBroadcast.class, brod ->{
			this.terminate(); });

		canStartTime.incrementAndGet();
	}
}
