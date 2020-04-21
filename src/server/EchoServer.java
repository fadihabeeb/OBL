/**
 * server package contains server side code layer and connection to the MySQL database
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import common.PDFfile;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 */
public class EchoServer extends AbstractServer {
	// The default port to listen on.
	final public static int DEFAULT_PORT = 5555;
	private static MySQLConnection DBcon;
	private static LibraryServices libraryServices;

	// @param port The port number to connect on.
	public EchoServer(int port) {
		super(port);
	}

	// Instance methods ************************************************
	Object obj;

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("[Client " + client.getId() + "] Message received: " + msg.toString());
		if (msg instanceof PDFfile)
			handelFileFromClient(msg, client);
		else if (msg instanceof Integer) {
			graduateStudent((Integer) msg);
			obj = new String("Student ID " + msg + "has been set as graduated");
		} else if (msg instanceof ArrayList) {
			ArrayList<String> arr = (ArrayList<String>) msg;
			if (arr.get(arr.size() - 1).equals("@") || arr.get(arr.size() - 1).equals("!")) {
				getFileFromDB(arr, client);
			}else if (arr.get(0).equals("#")) {
				boolean graduate = false;
				int graduateID = 0;
				arr.remove(0);
				orderNotification(Integer.parseInt(arr.get(0))); // book ID
				arr.remove(0);// Remove book ID
				
				if (arr.get(0).equals("$")) {
					arr.remove(0);//Remove $
					graduate = true;
					graduateID = Integer.parseInt(arr.get(0)); // Get graduate ID
					arr.remove(0);// Remove graduate ID
				}
				obj = DBcon.executeQuery(msg);
				if (graduate == true)
					graduateReturn(graduateID);//Execute check after issuing book return
			} else
				obj = DBcon.executeQuery(msg);
		} else
			obj = DBcon.executeQuery(msg);
		if (obj == null)
			obj = true;
		try {
			client.sendToClient(obj);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * handle the file received from client and pass it to MySQLConnection class to
	 * be saved in DB
	 * 
	 * @param msg
	 * @param client
	 */
	public void handelFileFromClient(Object msg, ConnectionToClient client) {
		InputStream is = new ByteArrayInputStream(((PDFfile) msg).getMybytearray());
		DBcon.updateFile(is, msg);
	}

	/**
	 * upload the file from DB to specific path on server
	 * 
	 * @param msg
	 * @param client
	 * @param outputFileName
	 * @throws IOException
	 */
	public void uploadFileToServer(Object msg, ConnectionToClient client, String outputFileName) throws IOException {
		System.out.println("upload file");
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {// get the file from output stream to output file path
			fos = new FileOutputStream(outputFileName);
			bos = new BufferedOutputStream(fos);
			bos.write(((PDFfile) msg).getMybytearray(), 0, ((PDFfile) msg).getSize());
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();
		}
	}

	/**
	 * get the file from DB and prepared it to be pass to client
	 * 
	 * @param arr
	 * @param client
	 */
	public void getFileFromDB(ArrayList<String> arr, ConnectionToClient client) {
		FileOutputStream output = null;
		BufferedInputStream buffer = null;
		try {// getting blob file as resultSet from DB
			ResultSet rs = DBcon.executeFileQuery(arr);
			// save the file on server
			//File newFile = new File("@/../" + arr.get(1));
			File newFile = new File("C:\\obl\\" + arr.get(1));
			output = new FileOutputStream(newFile);
			if (rs.next()) {
				byte[] mybytearray = new byte[2048];
				InputStream is = null;
				if(arr.get(arr.size()-1).equals("@"))
					 is = rs.getBinaryStream("tableOfContents");
				else
					 is = rs.getBinaryStream("report");

				buffer = new BufferedInputStream(is);
				// write the file to specific path
				while (is.read(mybytearray) > 0) {
					output.write(mybytearray);
				}
				sentFileToCient(arr, newFile, client);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null)
					output.close();
				if (buffer != null)
					buffer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * get the file from the server where it's saved and send it to client
	 * 
	 * @param arr
	 * @param newFile
	 * @param client
	 */
	public void sentFileToCient(ArrayList<String> arr, File newFile, ConnectionToClient client) {
		try {
			PDFfile uploadedFile = new PDFfile(arr.get(1));
			uploadedFile.setFilePath(arr.get(2));

			byte[] mybytearray = new byte[(int) newFile.length()];
			FileInputStream fis = new FileInputStream(newFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			uploadedFile.initArray(mybytearray.length);
			uploadedFile.setSize(mybytearray.length);

			bis.read(uploadedFile.getMybytearray(), 0, mybytearray.length);
			client.sendToClient(uploadedFile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (newFile.exists())
				newFile.delete();
		}

	}

	/**
	 * Call libraryServices graduate student method and set student status as
	 * graduated
	 * 
	 * @param studentID
	 */
	public void graduateStudent(int studentID) {
		libraryServices.graduateStudent(studentID);
	}

	/**
	 * Call libraryServices graduate student method and set student status as
	 * graduated
	 * 
	 * @param studentID
	 */
	public void graduateReturn(int graduateID) {
		libraryServices.graduateReturn(graduateID);
	}

	/**
	 * Send a notification to the next student in book order queue
	 * 
	 * @param bookID
	 */
	public void orderNotification(int bookID) {
		libraryServices.orderNotification(bookID);
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	@Override
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	/**
	 * This method is responsible for the creation of the server instance (there is
	 * no UI in this phase).
	 *
	 * @param args[0] The port number to listen on. Defaults to 5555 if no argument
	 *        is entered.
	 */

	/**
	 * Hook method called each time a new client connection is accepted.
	 * 
	 * @param client the connection connected to the client.
	 */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("Client " + client.getId() + " has connected to the server");
	}

	/**
	 * Hook method called each time a client disconnects.
	 * 
	 * @param client the connection with the client.
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		System.out.println("Client " + client.getId() + " has disconnected from the server");
	}

	/**
	 * the main witch activate the server
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int port = 0; // Port to listen on
		Properties props = new Properties();
		FileInputStream in = new FileInputStream("@/../Server.properties");
		props.load(in);
		in.close();
		String schema = props.getProperty("jdbc.schema");
		String username = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");

		try {
			port = Integer.parseInt(props.getProperty("server.port")); // Get port from command line
		} catch (Throwable t) {
			port = DEFAULT_PORT; // Set port to 5555
		}
		DBcon = new MySQLConnection(schema, username, password);
		EchoServer sv = new EchoServer(port);
		libraryServices = new LibraryServices(EchoServer.DBcon);
		try {
			sv.listen(); // Start listening for connections
			// sv.orderNotification(1234);
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}
}
//End of EchoServer class
