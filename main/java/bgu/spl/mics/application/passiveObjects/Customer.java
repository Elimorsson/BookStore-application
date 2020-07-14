package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	////-------------------------------------variables--------------
	private int id, distance;
	private String name, address;
	private CreditCard creditCard;
	private OrderSchedule[] orderSchedule;
	private Vector<OrderReceipt> receiptsList;

	//---------------------------------constructor-------------------------------------
	private Customer (){
		receiptsList=new Vector<>();

	}
	//------------------------------------- getters-----------------------------------
	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() { return name; }

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() { return id; }

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receiptsList;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return creditCard.amount;
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return creditCard.number;
	}

	public OrderSchedule[] getOrderSchedule(){
		return orderSchedule;
	}


	//--------------------------------------------function-------------------------------

	public boolean CanCharge(int amount){

		if (this.creditCard.amount < amount)
			return false;
		return true;
	}


	public void Charge(int amount) {

		this.creditCard.amount -= amount;

	}

	public void addReceipt (OrderReceipt receipt){
		receiptsList.add(receipt);
	}
}

