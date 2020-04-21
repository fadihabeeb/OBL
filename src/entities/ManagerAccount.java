/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

public class ManagerAccount extends LibrarianAccount {
	public ManagerAccount() {

	}

	public ManagerAccount(int id, String firstName, String lastName, String eMail, String mobileNum, int userID,
			String userName, String password, int managerID, boolean logged) {
		super(id, firstName, lastName, eMail, mobileNum, userID, userName, password, managerID, logged);
	}
}
