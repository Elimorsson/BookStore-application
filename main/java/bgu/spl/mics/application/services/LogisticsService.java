package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AskForCarEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private AtomicInteger canStartTime;

	public LogisticsService(int id, AtomicInteger canStartTime) {
		super("LogisticService "+id);
		this.canStartTime=canStartTime;
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, ev ->{
			int distance = ev.getDistance();
			String address = ev.getAddress();

			Future <Future <DeliveryVehicle>> theFuture = sendEvent(new AskForCarEvent());

			if (theFuture != null) {
				Future<DeliveryVehicle> toBeVehicle = theFuture.get();
				if(toBeVehicle!=null) {
					DeliveryVehicle vehicle = toBeVehicle.get();
					if(vehicle !=null) {
						vehicle.deliver(address, distance);
						sendEvent(new ReleaseVehicle(vehicle));
						complete(ev, true);
					}
					else
						complete(ev, false);
				}
				else
					complete(ev, false);
			}
			else
				complete(ev,false);
		});

		subscribeBroadcast(TerminateBroadcast.class, brod ->{
			this.terminate(); });
		canStartTime.incrementAndGet();
	}

}
