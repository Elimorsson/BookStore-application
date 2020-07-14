package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;

import java.io.Serializable;

public class Services implements Serializable {
    public Time time;
    public int selling;
    public int inventoryService;
    public int logistics;
    public int resourcesService;
    public Customer[] customers;

    public int getLength (){
        return (1 + selling + inventoryService + logistics + resourcesService + customers.length);
    }

}
