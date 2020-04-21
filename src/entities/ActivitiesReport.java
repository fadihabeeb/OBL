/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.util.ArrayList;

public class ActivitiesReport {
	
	private int totalUsers;
	private int activeUsersNumber;
	private int frozenUsersNumber;
	private int lockedUsersNumber;
	private ArrayList<Book> books;

	private ArrayList<UserAccount> accounts;
	
	
	public ActivitiesReport(int totalUsers, int activeUsersNumber, int frozenUsersNumber, int lockedUsersNumber,
			ArrayList<Book> books, ArrayList<UserAccount> accounts) {
		super();
		this.totalUsers = totalUsers;
		this.activeUsersNumber = activeUsersNumber;
		this.frozenUsersNumber = frozenUsersNumber;
		this.lockedUsersNumber = lockedUsersNumber;
		this.books = books;
		this.setAccounts(accounts);
	}
	
	public ActivitiesReport() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Initialise the total users in the system
	 * @param totalUsers
	 */
	public void setTotalUsers(int totalUsers) {
		this.totalUsers = totalUsers;
	}
	/**
	 * Get the number of total users in the system
	 * @return totalUsers
	 */
    public int getTotalUsers() {
    	return totalUsers;
    }
   	/**
   	 * Gets the active user number.
   	 * 
   	 * @return  activeUsersNumber.
   	 */
	public int getActiveUsersNumber() {
		return activeUsersNumber;
	}
	/**
	 * Initialise the active user number
	 * @param activeUsersNumber set the active user number
	 */
	public void setActiveUsersNumber(int activeUsersNumber) {
		this.activeUsersNumber = activeUsersNumber;
	}
   	/**
   	 * Gets the frozen users number.
   	 * 
   	 * @return  frozenUsersNumber
   	 */
	public int getFrozenUsersNumber() {
		return frozenUsersNumber;
	}
	/**
	 * Initialise the frozen Users Number
	 * @param unActiveUsersNumber set the frozen user number
	 */
	public void setFrozenUsersNumber(int frozenUsersNumber) {
		this.frozenUsersNumber = frozenUsersNumber;
	}
	
   	/**
   	 * Gets the locked Users Number.
   	 * 
   	 * @return  lockedUsersNumber
   	 */
	public int getLockedUsersNumber() {
		return lockedUsersNumber;
	}
	/**
	 * Initialise the locked Users Number
	 * @param lockedUsersNumber set the locked Users Number
	 */
	public void setLockedUsersNumber(int lockedUsersNumber) {
		this.lockedUsersNumber = lockedUsersNumber;
	}
  
	
	
	public int getAllLibraryBooksNum() {
		int num = 0;
		for(Book x : books) {
			num = num + x.getCopiesNumber();
		}
		return num;
		
	}

	public ArrayList<UserAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(ArrayList<UserAccount> accounts) {
		this.accounts = accounts;
	}
	
	public ArrayList<Book> getBooks() {
		return books;
	}

	public void setBooks(ArrayList<Book> books) {
		this.books = books;
	}
}
