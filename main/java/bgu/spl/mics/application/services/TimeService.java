package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int currentTick;
	private int speed;
	private int duration;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		currentTick = 1;
	}

	@Override
	protected void initialize() {
		TimeUnit unit = TimeUnit.MILLISECONDS;
		while (currentTick < duration) {
			Broadcast temp = new TickBroadcast(currentTick);
			sendBroadcast(temp);
			try {
				unit.sleep(speed);
				currentTick++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendBroadcast(new TerminateBroadcast());
		terminate();
	}

}
