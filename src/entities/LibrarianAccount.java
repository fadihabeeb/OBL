/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.util.ArrayList;

public class LibrarianAccount extends Account {

	private int workerID;

	public LibrarianAccount() {
		if (this instanceof ManagerAccount)
			this.userType = UserType.Manager;
		else
			this.userType = UserType.Librarian;
	}

	public LibrarianAccount(int id, String firstName, String lastName, String eMail, String mobileNum, int userID,
			String userName, String password, int workerID, boolean logged) {
		super(id, firstName, lastName, eMail, mobileNum, userID, userName, password, UserType.Librarian, logged);
		if (this instanceof ManagerAccount)
			this.userType = UserType.Manager;
		this.workerID = workerID;
	}

	/**
	 * Gets the Worker ID.
	 * 
	 * @return workerID
	 */
	public int getWorkerID() {
		return workerID;
	}

	/**
	 * set worker ID
	 * @param workerID
	 */
	public void setWorkerID(int workerID) {
		this.workerID = workerID;
	}

	/**
	 * Parse array list into account and fill its data
	 * @param accountArray
	 */
	public void parseArrayIntoAccount(ArrayList<String> accountArray) {////////// NEEDS MODIFICATION
		this.setID(Integer.parseInt(accountArray.get(0)));
		this.setFirstName(accountArray.get(1));
		this.setLastName(accountArray.get(2));
		this.setEmail(accountArray.get(3));
		this.setMobileNum(accountArray.get(4));
		this.setAccountID(Integer.parseInt(accountArray.get(5)));
		this.setUserName(accountArray.get(6));
		this.setPassword(accountArray.get(7));
		this.setUserTypeString(accountArray.get(8));
		this.setLogged(accountArray.get(11).equals("1") ? true : false);
	}

}
