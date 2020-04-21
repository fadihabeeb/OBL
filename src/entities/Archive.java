/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

public class Archive {
	
	
	
	private int Id;
	private int userID;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String mobileNum;
	private String Email;
	
	
	public Archive(int userID, int id, String username, String password, String firstName, String lastName,
			String mobileNum, String Email) {
		super();
		Id = id;
		this.userID = userID;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobileNum = mobileNum;
		this.Email = Email;
	}
	
	/**
	 * Gets the ID.
	 * 
	 * @return  ID
	 */
	public int getId() {
		return Id;
	}
	
	/**
	 * set ID
	 * @param id
	 */
	public void setId(int id) {
		Id = id;
	}
	
	/**
	 * Gets the user ID.
	 * 
	 * @return  userID
	 */
	public int getUserId() {
		return userID;
	}
	/**
	 * Instantiates the user Id
	 * @param userId
	 */
	public void setUserId(int userId) {
		userID = userId;
	}
	/**
	 * Gets the user name.
	 * 
	 * @return  username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Instantiates the user name
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * Gets the password.
	 * 
	 * @return  password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * set password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Gets the first name.
	 * 
	 * @return firstName
	 */
	public String getFirstname() {
		return firstName;
	}
	/**
	 * Instantiates the first name
	 * @param firstname
	 */
	public void setFirstname(String firstname) {
		this.firstName = firstname;
	}
	/**
	 * Gets the last name.
	 * 
	 * @return  lastName
	 */
	public String getLastname() {
		return lastName;
	}
	/**
	 * Set the last name
	 * @param lastname
	 */
	public void setLastname(String lastname) {
		this.lastName = lastname;
	}
	
	/**
	 * Gets the phone number
	 * @return mobileNum
	 */
	public String getMobileNum() {
		return mobileNum;
	}
	
	/**
	 * set mobile num
	 * @param mobileNum
	 */
	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}
	
	/**
	 * get eMail
	 * @return  eMail
	 */
	public String getEmail() {
		return Email;
	}
	/**
	 * Set Email.
	 * @param eMail
	 */
	public void setEmail(String eMail) {
		this.Email = eMail;
	}
	
	
	
	
	
	
	
	
	
	

}
