package bgu.spl.mics.application.passiveObjects;


import java.util.Vector;

import static bgu.spl.mics.application.passiveObjects.OrderResult.NOT_IN_STOCK;
import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;
import java.io.IOException;
import java.util.*;
import java.io.*;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
//to implement as-thread safe !!!!!!!!!!!!!!!!!!!!!!!!!!!!*%$*#%$^&$&$^%&%^
	private Vector<BookInventoryInfo> books;
	private int counter;

	private Inventory(){
		counter = 0;
		books=new Vector<>();
	}

	private static class InventoryHolder {
		private static Inventory instance = new Inventory();
	}

	public static Inventory getInstance(){
		return InventoryHolder.instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[] inventory) {

		Collections.addAll(books, inventory);
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book)
	{
		int thePlace = returnThePlaceOfaBook(book);
		if(thePlace == -1)
			return OrderResult.NOT_IN_STOCK;
		books.get(thePlace).setAmount((books.get(thePlace).getAmountInInventory())-1);
		return OrderResult.SUCCESSFULLY_TAKEN;
	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabilityAndGetPrice(String book)
	{
		for(BookInventoryInfo bk: books){
			if(bk.getBookTitle().equals(book))
				return bk.getPrice();
		}
		return -1;
	}
	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a
	 * Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename){

			HashMap<String,Integer> toWrite = new HashMap<>();
			for (BookInventoryInfo tempBook: books)
				toWrite.put(tempBook.getBookTitle(), tempBook.getAmountInInventory());


				try {
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
					oos.writeObject(toWrite);
					oos.close();
				}
				catch (IOException ioe) { ioe.printStackTrace(); }
	}


//	-----public method for the Junit Tester Only!!!-----
	public int getAmount(String bookName) {

		int thePlaceOfaBook = returnThePlaceOfaBook(bookName);
		if (thePlaceOfaBook == -1) throw new NullPointerException("the book is not in the library");

		return books.get(thePlaceOfaBook).getAmountInInventory();
	}


	private int returnThePlaceOfaBook(String book){

		for(int i = 0; i < books.size(); i++)
			if(books.get(i).getBookTitle().equals(book))
				return i;
		return -1;
	}
}
