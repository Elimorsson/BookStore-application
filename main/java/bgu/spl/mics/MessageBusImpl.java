package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<MicroService,Triple> theMap;
	private ConcurrentHashMap <Event<?>, Future<?>> futureVsEventMap;
	private ConcurrentHashMap <Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> eventsSubscribe;
	private ConcurrentHashMap <Class<? extends Broadcast>, Vector<MicroService>> brodcastsSubscribe;

	private MessageBusImpl(){
		theMap = new ConcurrentHashMap<>();
		futureVsEventMap = new ConcurrentHashMap<>();
		eventsSubscribe = new ConcurrentHashMap<>();
		brodcastsSubscribe = new ConcurrentHashMap<>();
	}

	public static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	public static MessageBusImpl getInstance(){

		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//************************************To check about threads******************************
		eventsSubscribe.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		if (!eventsSubscribe.get(type).contains(m))
			eventsSubscribe.get(type).add(m);
		//also add the type of the message to specific list of messages that the microService subscribe to
		//get 1 is to get the list of message the MicroService is subscribe to
		if (!(theMap.get(m).getSecond().contains(type)))
			theMap.get(m).getSecond().add(type);
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//**********************To check better solution about threads******************

			//if the type is already exist at the list just add the MicroService inside
			brodcastsSubscribe.putIfAbsent(type, new Vector<MicroService>());
			if (!(brodcastsSubscribe.get(type).contains(m)))
				brodcastsSubscribe.get(type).add(m);
			//also add the type of the message to specific list of message that the microService subscribe to
			if (!(theMap.get(m).getSecond().contains(type)))
				theMap.get(m).getSecond().add(type);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(futureVsEventMap.containsKey(e)) {
			Future<T> theFuture = (Future<T>) futureVsEventMap.get(e);
			theFuture.resolve(result);
			futureVsEventMap.remove(e);
		}
	}
	@Override
	public void sendBroadcast(Broadcast b) {
		//check who subscribe to get b and put it in b Msvector variable
		//add the message to every queue of each Micro Service
		if (brodcastsSubscribe.get(b.getClass()) != null) {
			for (MicroService temp : brodcastsSubscribe.get(b.getClass())) {
				try {
					(theMap.get(temp).getFirst()).put(b);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//check who subscribe to get e.getClass and send it to RoundRobin Method to send event to the proper MicroService
		if (eventsSubscribe.get(e.getClass()) != null) {
			synchronized (eventsSubscribe.get(e.getClass())) {
				if(eventsSubscribe.get(e.getClass()).isEmpty()) return null;
				Future<T> theFuture = new Future<>();
				futureVsEventMap.put(e, theFuture);
				RoundRobin(e);
				return theFuture;
			}
		}
		return null;
	}

	private <T> void RoundRobin(Event<T> e) {
			try {
				MicroService currentMicroService = eventsSubscribe.get(e.getClass()).poll();
				eventsSubscribe.get(e.getClass()).add(currentMicroService);
				theMap.get(currentMicroService).getFirst().put(e);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	}

	@Override
	public void register(MicroService m) {
		//initialize the object to put in that specific Node
		LinkedBlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>(); //first argument in a Triple
		Vector<Class<? extends Message>> messagesSubscribeTo = new Vector<>();	  //second argument in a Triple
		Triple currentTriple = new Triple(m,messagesQueue,messagesSubscribeTo);

		theMap.put(m,currentTriple);
	}

	@Override
	public void unregister(MicroService m) {
		for (Class<? extends Message> msgType : theMap.get(m).getSecond())
		{
			if (eventsSubscribe.containsKey(msgType) && eventsSubscribe.get(msgType)!=null)
				synchronized (eventsSubscribe.get(msgType)){
					eventsSubscribe.get(msgType).remove(m);
			}
			else if(brodcastsSubscribe.contains(msgType))
				brodcastsSubscribe.get(msgType).remove(m);
		}

		for(Message msg: theMap.get(m).getFirst())
			if(msg instanceof Event)
				complete((Event) msg,null);
		theMap.remove(m);

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (theMap.get(m) == null)
			throw new IllegalStateException("The MicroService doesn't register yet");

		else {
			return theMap.get(m).getFirst().take();
		}
	}
}
