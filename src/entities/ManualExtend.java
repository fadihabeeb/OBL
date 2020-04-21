/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.time.LocalDate;

public class ManualExtend {
	private int bookID;
	private int userID;
	private String WorkerName;
	private LocalDate extendDate;
	private LocalDate dueDate;

	public ManualExtend(int bookID, int userID, String workerName, LocalDate extendDate, LocalDate dueDate) {
		super();
		this.bookID = bookID;
		this.userID = userID;
		WorkerName = workerName;
		this.extendDate = extendDate;
		this.dueDate = dueDate;
	}

	/**
	 * @return the extendDate
	 */
	public LocalDate getExtendDate() {
		return extendDate;
	}

	/**
	 * @param extendDate the extendDate to set
	 */
	public void setExtendDate(LocalDate extendDate) {
		this.extendDate = extendDate;
	}

	/**
	 * @return the dueDate
	 */
	public LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Gets the book ID.
	 * 
	 * @return bookID
	 */
	public int getBookID() {
		return bookID;
	}

	/**
	 * Instantiates book ID
	 * 
	 * @param bookID
	 */
	public void setBookID(int bookID) {
		this.bookID = bookID;
	}

	/**
	 * Gets the user ID.
	 * 
	 * @return userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * Instantiates user ID
	 * 
	 * @param userID
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * Gets the Worker Name.
	 * 
	 * @return WorkerName
	 */
	public String getWorkerName() {
		return WorkerName;
	}

	/**
	 * set worker name
	 * 
	 * @param workerName
	 */
	public void setWorkerName(String workerName) {
		WorkerName = workerName;
	}
}
