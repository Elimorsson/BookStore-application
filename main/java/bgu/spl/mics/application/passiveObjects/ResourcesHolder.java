package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {

	private LinkedBlockingQueue<DeliveryVehicle> availbleVehicles;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> awaitingList;

	private ResourcesHolder (){
		availbleVehicles = new LinkedBlockingQueue<>();
		awaitingList = new LinkedBlockingQueue<>();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	private static class ResourcesHolderHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	public static ResourcesHolder getInstance() {
		return ResourcesHolderHolder.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> theFuture = new Future<>();
		synchronized (availbleVehicles) {
			if (!availbleVehicles.isEmpty())
				theFuture.resolve(availbleVehicles.poll());
			else
				awaitingList.add(theFuture);
		}
		return theFuture;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(vehicle==null){
			for(Future ft: awaitingList)
				ft.resolve(null);
		}
		else {
			synchronized (awaitingList) {
				if (!awaitingList.isEmpty())
					awaitingList.poll().resolve(vehicle);
				else
					availbleVehicles.add(vehicle);
			}
		}
	}
	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		Collections.addAll(availbleVehicles, vehicles);
	}

}
