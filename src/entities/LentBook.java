/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.time.LocalDate;

//need to extend from book
public class LentBook {
	
	Book book;
	BookCopy bookCopy;
	private int userID;
	private LocalDate issueDate;
	private LocalDate dueDate;
	private LocalDate returnDate;
	private boolean  late;
	private boolean returned;
	
	public LentBook(int userID,Book book, BookCopy bookCopy, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,boolean late) {
		this.userID = userID;
		this.book = book;
		this.bookCopy = bookCopy;
		this.issueDate = issueDate;
		this.dueDate = dueDate;
		this.late = late;
		this.returnDate = returnDate;
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
	public Book getBook() {
		return book;
	}
	/**
	 * set book
	 * @param book
	 */
	public void setBook(Book book) {
		this.book = book;
	}
	
	/**
	 * Gets true if late.
	 * 
	 * @return  late
	 */
	public boolean isLate() {
		return late;
	}
	/**
	 * Instantiates update late
	 * @param  late 
	 */
	public void setLate(boolean late) {
		this.late = late;
	}
	/**
	 * @return the issueDate
	 */
	public LocalDate getIssueDate() {
		return issueDate;
	}
	/**
	 * @param issueDate
	 */
	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}
	/**
	 * @return return due date
	 */
	public LocalDate getDueDate() {
		return dueDate;
	}
	/**
	 * Set due date
	 * @param dueDate
	 */
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * get book copy
	 * @return
	 */
	public BookCopy getBookCopy() {
		return bookCopy;
	}

	/**
	 * set book copy
	 * @param bookCopy
	 */
	public void setBookCopy(BookCopy bookCopy) {
		this.bookCopy = bookCopy;
	}
	
	/**
	 * get return date
	 * @return
	 */
	public LocalDate getReturnDate() {
		return returnDate;
	}
	/**
	 * set actual return date
	 * @param returnDate
	 */
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
	
	/**
	 * check if the book returned
	 * @return returned
	 */
	public boolean isReturned() {
		return returned;
	}

	/**
	 * set returned
	 * @param returned
	 */
	public void setReturned(boolean returned) {
		this.returned = returned;
	}
}
