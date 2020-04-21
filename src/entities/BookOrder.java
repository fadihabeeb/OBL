/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.sql.Timestamp;

public class BookOrder {
	
	private int orderID;
	private int userID;
	private int bookID;
	private Timestamp orderDate;
	
	public BookOrder(int userID, int bookID, Timestamp orderDate) {
		super();
		this.userID = userID;
		this.bookID = bookID;
		this.orderDate = orderDate;
	}

	
	
  	/**
  	 * Gets the order ID.
  	 * 
  	 * @return  orderID
  	 */
	public int getOrderID() {
		return orderID;
	}
	/**
	 * Instantiates orderID
	 * @param  orderID 
	 */
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
  	/**
  	 * Gets the user ID.
  	 * 
  	 * @return  userID
  	 */
	public int getUserID() {
		return userID;
	}
	/**
	 * Instantiates user ID
	 * @param  userID 
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
  	/**
  	 * Gets the book ID.
  	 * 
  	 * @return  bookID
  	 */
	public int getBookID() {
		return bookID;
	}
	/**
	 * Instantiates book ID
	 * @param  bookID 
	 */
	public void setBookID(int bookID) {
		this.bookID = bookID;
	}


	/**
	 * get order date
	 * @return	Timestamp
	 */
	public Timestamp getOrderDate() {
		return orderDate;
	}

	/**
	 * set order date
	 * @param orderDate
	 */
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}
}
