package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.io.Serializable;

public class InputGson implements Serializable {
    public BookInventoryInfo [] initialInventory;
    public Vehicles[] initialResources;
    public Services services;

}
