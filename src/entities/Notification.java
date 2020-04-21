/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

import java.sql.Timestamp;

public class Notification {

	public enum notificationType {
		Lock, Suspend, Message
	};

	private int userID;
	private int notificationNum;
	private Timestamp date;
	private String message;
	private notificationType notfType;

	public Notification(int notificationNum, int userID, Timestamp date, String message, String msgType) {
		this.userID = userID;
		this.date = date;
		this.message = message;
		this.notificationNum = notificationNum;
		switch (msgType) {
		case "Lock":
			this.notfType = notificationType.Lock;
			break;
		case "Suspend":
			this.notfType = notificationType.Suspend;
			break;
		case "Message":
			this.notfType = notificationType.Message;
			break;
		default:
			this.notfType = notificationType.Message;
			break;
		}
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
	 * @return the notificationNum
	 */
	public int getNotificationNum() {
		return notificationNum;
	}



	/**
	 * @param notificationNum the notificationNum to set
	 */
	public void setNotificationNum(int notificationNum) {
		this.notificationNum = notificationNum;
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



	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}



	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}



	/**
	 * @return the notfType
	 */
	public notificationType getNotfType() {
		return notfType;
	}



	/**
	 * @param notfType the notfType to set
	 */
	public void setNotfType(notificationType notfType) {
		this.notfType = notfType;
	}



	/**
	 * 
	 * @return if it is a lock notification
	 */
	public boolean isLock()
	{
		return this.notfType == notificationType.Lock;
	}
}
