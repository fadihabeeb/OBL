/**
 * client package contains classes that help client connect to the server
 */
package client;

import java.io.IOException;
import java.util.ArrayList;

import common.OBLclientIF;
import controllers.DatabaseController;

public class ClientConnection implements OBLclientIF {

	// The default port to connect on.
	final public static int DEFAULT_PORT = 5555;

	// The instance of the client that created this ConsoleChat.
	OBLclient client;

	/*
	 * @param host The host to connect to.
	 * 
	 * @param port The port to connect on.
	 */

	public ClientConnection() {
		this("localhost", DEFAULT_PORT);
		/*
		 * try { client = new ChatClient("localhost", DEFAULT_PORT, this);
		 * System.out.println("connected"); } catch (IOException Exception) {
		 * System.out.println("Error: Can't setup connection!" +
		 * " Terminating client."); System.exit(1); }
		 */
	}
	
	/**
	 * make a connection to host on specific port 
	 * @param host
	 * @param port
	 */
	public ClientConnection(String host, int port) {
		try {
			client = new OBLclient(host, port, this);
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating client.");
			System.exit(1);
		}
	}

	/**
	 * send the query to OBLclient to be send to server and executed
	 * @param obj
	 */
	public void executeQuery(Object obj) {
		client.handleMessageFromClientUI(obj);
	}
	
	/**
	 * send the file details to OBLclient to be send to server and saved in DB
	 * @param bookName
	 * @param filePath
	 * @param bookID
	 */
	public void saveFile(ArrayList<String> arr, String filePath, String value) {
		client.handleFileFromClientUI(arr, filePath, value);
	}


	/**
	 * send file path and details to OBLclient to upload it from DB 
	 * @param obj
	 */
	public void uploadFile(Object obj) {
		client.handleMessageFromClientUI(obj);
	}
	
	/**
	 * This method was built only for testing purposes (External system sends a
	 * graduation note with graduated student ID to OBL server)
	 */
	public void graduateStudent(Integer studentID) {
		client.handleMessageFromClientUI(studentID);
	}

	// **************************************//
	private Object obj;

	/**
	 * object received from server
	 */
	@Override
	public void serverObj(Object obj) {
		System.out.println("> Object received from server.");
		this.obj = obj;
	}
	
	/**
	 * return the object that has been received from server
	 * @return obj 
	 */
	public Object getObject() {
		return this.obj;
	}

	/**
	 * parse the object received from server to ArrayList and return it
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getList() {
		return (ArrayList<String>) this.obj;
	}

	/**
	 * This method overrides the method in the ChatIF interface. It displays a
	 * message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}

	/**
	 * Terminate client connection to the server
	 */
	public void terminate() {
		client.quit();
	}

	/**
	 * Initiate the connected client
	 */
	public void init() {
		DatabaseController.InitiateClient(this);
	}
}
//End of ConsoleChat class
