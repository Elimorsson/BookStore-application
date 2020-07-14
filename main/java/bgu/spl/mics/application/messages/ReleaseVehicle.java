package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicle implements Event<Boolean> {
    private DeliveryVehicle deliveryVehicle;

    public ReleaseVehicle (DeliveryVehicle deliveryVehicle){
        this.deliveryVehicle = deliveryVehicle;
    }

    public DeliveryVehicle getDeliveryVehicle() {
        return deliveryVehicle;
    }
}
