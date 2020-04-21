/**
 * common package contains client/files implemented by client
 */
package common;

/**
 * This interface implements the abstract method used to display objects onto
 * the client or server UIs.
 *
 * @author Dr Robert Lagani&egrave;re
 * @author Dr Timothy C. Lethbridge
 * @version July 2000
 */
public interface OBLclientIF {
	
	/**
	 * Method that when overriden is used to display objects onto a UI.
	 */

	public abstract void display(String message);
	public abstract void serverObj(Object obj);
}
