package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer theCustomer;
	private int orderID;
	private OrderSchedule[] orderSchedule;
	private int currentTick;
	private AtomicInteger canStartTime;

	public APIService(int id, Customer c, AtomicInteger canStartTime) {
		super("API Service "+id);
		theCustomer = c;
		this.orderSchedule = theCustomer.getOrderSchedule();
		sort(orderSchedule);
		orderID = 0;
		this.canStartTime=canStartTime;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, tick ->{
		this.currentTick = tick.getCurrentTick();

			for (int i=0;i<orderSchedule.length;i++){
				if (currentTick==orderSchedule[i].tick) {
					Future<OrderReceipt> receiptFuture = sendEvent(new BookOrderEvent(orderSchedule[i].bookTitle, orderSchedule[i].tick,theCustomer));
					if (receiptFuture != null) theCustomer.addReceipt(receiptFuture.get());

				}
			}
		});


		subscribeBroadcast(TerminateBroadcast.class, b -> {
			this.terminate();
		});
		canStartTime.incrementAndGet();
	}

	/*Function to sort the OrderSchedule array using insertion sort*/
	private void sort(OrderSchedule[] orderSchedule) {
		int n = orderSchedule.length;
		for (int i = 1; i < n; ++i)
			{
				OrderSchedule key = orderSchedule[i];
				int j = i-1;

            /* Move elements of OrderSchedule[0..i-1], that there tick is
               greater than key tick, to one position ahead
               of their current position */
				while (j >= 0 && orderSchedule[j].tick > key.tick)
				{
					orderSchedule[j+1] = orderSchedule[j];
					j = j-1;
				}
				orderSchedule[j+1] = key;
			}
	}
}
