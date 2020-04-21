/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.sql.Timestamp;

public class UserActivity {

	private int userID;
	private String activityName;
	private Timestamp date;

	public UserActivity(int userID, String activityName, Timestamp date) {
		this.userID = userID;
		this.activityName = activityName;
		this.date = date;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * @param activityName the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return the date
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Timestamp date) {
		this.date = date;
	}

	
}
