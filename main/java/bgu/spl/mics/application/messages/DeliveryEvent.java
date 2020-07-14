package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;

public class DeliveryEvent implements Event<Boolean> {
    private int distance;
    private String address;

    public DeliveryEvent (int distance, String address){
        this.distance = distance;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getDistance() {
        return distance;
    }
}
