package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class BookInventoryEvent implements Event<Integer> {
    private String bookTitle;

    public BookInventoryEvent (String bookTitle){
        this.bookTitle = bookTitle;
    }
    public String getBookTitle() {
        return bookTitle;
    }
}
