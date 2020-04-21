/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

public class Person {
	protected int ID;
	protected String firstName;
	protected String lastName;
	protected String eMail;
	protected String mobileNum;

	public Person() {
	}

	public Person(int ID, String firstName, String lastName, String eMail, String mobileNum) {
		this.ID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.eMail = eMail;
		this.mobileNum = mobileNum;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the eMail
	 */
	public String getEmail() {
		return eMail;
	}

	/**
	 * @param eMail the eMail to set
	 */
	public void setEmail(String eMail) {
		this.eMail = eMail;
	}

	/**
	 * @return the mobileNum
	 */
	public String getMobileNum() {
		return mobileNum;
	}

	/**
	 * @param mobileNum the mobileNum to set
	 */
	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

}
