package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.io.IOException;

public class BookOrderEvent implements Event<OrderReceipt> {
    private String bookTitle;
    private int Tick;
    private Customer customer;


    public BookOrderEvent (String bookTitle, int Tick,Customer customer)
    {
        this.customer=customer;
        this.bookTitle = bookTitle;
        this.Tick = Tick;
    }

    public int getTick() {
        return Tick;
    }
    public String getBookTitle() {
        return bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }
}
