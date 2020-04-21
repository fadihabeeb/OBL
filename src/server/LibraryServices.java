/**
 * server package contains server side code layer and connection to the MySQL database
 */
package server;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.activation.FileDataSource;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

/**
 * Library threading services that keep track of user late returns and send
 * emails to notify users for book returns
 * 
 * @author Adam Mahameed
 * @version 1.2
 */

public class LibraryServices {
	Mailer mailConnection;
	static MySQLConnection dbCon;

	public LibraryServices(MySQLConnection dbCon) {
		initMailConnection();
		LibraryServices.dbCon = dbCon;
		emailService();// Initiate book returns email service
		lateReturnsService();// Initiate late returns service
		savedCopiesManager();// Initiate saved copies manager
		scheduledSuspensionManager();// Initiate scheduled suspension manager
	}

	/**
	 * Send a notification to the next student in book order queue
	 * 
	 * @param bookID
	 */
	@SuppressWarnings("unchecked")
	public void orderNotification(int bookID) {// Find next order in queue for this book
		ArrayList<String> nextBookOrder = ((ArrayList<String>) dbCon.executeSelectQuery(
				"SELECT orderID, userID FROM BookOrder WHERE bookID = '" + bookID + "' ORDER BY orderDate LIMIT 1;"));

		if (!(nextBookOrder.size() == 0)) {
			Calendar calendar = Calendar.getInstance();
			java.sql.Timestamp notificationTimeStamp = new java.sql.Timestamp(calendar.getTime().getTime());

			dbCon.executeQuery("DELETE FROM BookOrder WHERE orderID = '" + nextBookOrder.get(0) + "';");
			ArrayList<String> orderedBookData = (ArrayList<String>) dbCon.executeSelectQuery(
					"SELECT name, author, edition, printyear FROM book WHERE bookID = '" + bookID + "';");
			// Notify user that ordered book is available
			dbCon.executeQuery("INSERT INTO notification(userID, date, message, usertype, messageType)VALUES ('"
					+ nextBookOrder.get(1) + "','" + notificationTimeStamp + "','Your ordered book: " + bookID + "-"
					+ orderedBookData.get(0) + "," + orderedBookData.get(2) + " " + orderedBookData.get(3)
					+ " edition by " + orderedBookData.get(1)
					+ " is available for lending at the library and saved for 2 days!','User','Message')");
			int notificationNum = Integer.parseInt(((ArrayList<String>) dbCon
					.executeQuery("SELECT notificationNum FROM notification ORDER BY notificationNum DESC LIMIT 1;"))
							.get(0));
			// Save copy for 2 days
			dbCon.executeQuery("INSERT INTO savedcopy(notificationNum, userID, bookID, availableDate, dueDate)VALUES ('"
					+ notificationNum + "','" + nextBookOrder.get(1) + "','" + bookID + "','" + LocalDate.now() + "','"
					+ LocalDate.now().plusDays(2) + "')");
		} else
			System.out.println("No orders were found for book ID: " + bookID);
	}

	public void savedCopiesManager() {
		TimerTask savedCopiesManagerTask = new TimerTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				ArrayList<String> savedCopiesData = (ArrayList<String>) dbCon
						.executeSelectQuery("SELECT bookID, userID, notificationNum FROM savedCopy WHERE dueDate = '"
								+ LocalDate.now() + "'");
				if (savedCopiesData.size() != 0) {
					for (int i = 0; i < savedCopiesData.size(); i++) {
						int bookID = Integer.parseInt(savedCopiesData.get(0));
						int userID = Integer.parseInt(savedCopiesData.get(1));
						int notificationNum = Integer.parseInt(savedCopiesData.get(2));
						dbCon.executeQuery("DELETE FROM notification WHERE notificationNum = '" + notificationNum
								+ "' AND userID = '" + userID + "';");
						dbCon.executeQuery(
								"DELETE FROM savedCopy WHERE userID = '" + userID + "' AND bookID = '" + bookID + "';");
						dbCon.executeQuery(
								"UPDATE book SET availableCopies = availableCopies+1 WHERE bookID = '" + bookID + "';");
						orderNotification(bookID);
						savedCopiesData.subList(0, 3).clear();
					}
				} else
					System.out.println("No saved copies found");
			}
		};
		Timer savedCopiesManagerTimer = new Timer();
		savedCopiesManagerTimer.schedule(savedCopiesManagerTask, TimeUnit.SECONDS.toMillis(30),
				TimeUnit.DAYS.toMillis(1));
	}

	/**
	 * This method checks and processes scheduled suspension accounts list
	 * Unsuspends an account after its last suspension day
	 */
	public void scheduledSuspensionManager() {
		TimerTask scheduledSuspensionManagerTask = new TimerTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				ArrayList<String> scheduledSuspensionData = (ArrayList<String>) dbCon.executeSelectQuery(
						"SELECT * FROM scheduledSuspension WHERE untilDate = '" + LocalDate.now().minusDays(1) + "';");
				if (scheduledSuspensionData.size() != 0) {
					for (int i = 0; i < scheduledSuspensionData.size(); i++) {
						int userID = Integer.parseInt(scheduledSuspensionData.get(0));
						dbCon.executeQuery("DELETE FROM scheduledSuspension WHERE userID = '" + userID + "';");
						dbCon.executeQuery("UPDATE account SET status = 'Active' WHERE userID = '" + userID + "';");
						scheduledSuspensionData.subList(0, 2).clear();
					}
				} else
					System.out.println("No scheduled accounts need to be changed");
			}
		};
		Timer scheduledSuspensionManagerTimer = new Timer();
		scheduledSuspensionManagerTimer.schedule(scheduledSuspensionManagerTask, TimeUnit.SECONDS.toMillis(10),
				TimeUnit.DAYS.toMillis(1));
	}

	/**
	 * Call libraryServices graduate student method and set student status as
	 * graduated If the student got lent books, a mail gets sent to return them and
	 * suspends his account else account is locked
	 * 
	 * @param studentID
	 */
	@SuppressWarnings("unchecked")
	public void graduateStudent(int studentID) {
		int accountID = Integer.parseInt(((ArrayList<String>) dbCon
				.executeSelectQuery("SELECT userID FROM account WHERE ID = '" + studentID + "';")).get(0));
		ArrayList<String> lentReturns = (ArrayList<String>) dbCon
				.executeSelectQuery("SELECT bookID FROM lentbook WHERE returned = 0 AND userID = '" + accountID + "';");
		ArrayList<String> lenderData = ((ArrayList<String>) dbCon.executeSelectQuery(
				"SELECT firstName,lastName,eMail FROM account WHERE userID = '" + accountID + "';"));
		String firstName = lenderData.get(0);
		String lastName = lenderData.get(1);
		String eMail = lenderData.get(2);
		Calendar calendar = Calendar.getInstance();
		java.sql.Timestamp notificationTimeStamp = new java.sql.Timestamp(calendar.getTime().getTime());

		if (lentReturns.size() == 0) {// Student got no lent books
			dbCon.executeQuery(
					"UPDATE account SET status = 'Locked' , graduate = '1' WHERE userID = '" + accountID + "';");
			dbCon.executeQuery("INSERT INTO notification(date, message, usertype, messageType)VALUES ('"
					+ notificationTimeStamp + "','User ID: " + accountID
					+ " has been \"Locked\" due graduation and he has no lent books,'Librarian', 'Message')");
			// Send proper email to the graduate

			sendEmail(eMail, "Library Lent Books Return Due Graduation", "Hello" + firstName + " " + lastName
					+ "\nCongratulations for your graduation\n Your library account period has expired and your library account has been locked!");

			// Console log
			System.out.println("Email has been sent graduate student ID:" + accountID + " => " + eMail
					+ " notifying that his account has been locked!");
		} else {// If account needs suspension [Student has lent books]
			dbCon.executeQuery(
					"UPDATE account SET status = 'Suspended' , graduate = '1' WHERE userID = '" + accountID + "';");
			dbCon.executeQuery("INSERT INTO notification(date, message, usertype, messageType)VALUES ('"
					+ notificationTimeStamp + "','User ID: " + accountID
					+ " has been suspended due graduation and informed to return his lent books','Librarian', 'Message')");
			ArrayList<String> lentBooks = new ArrayList<>();
			for (int i = 0; i < lentReturns.size(); i++) {
				lentBooks.add(lentReturns.get(i).concat("-" + ((ArrayList<String>) dbCon
						.executeSelectQuery("SELECT name FROM book WHERE bookID = '" + lentReturns.get(i) + "';"))
								.get(0)));
				lentBooks.add("\n");
			}

			///////////////// Delete orders data
			dbCon.executeQuery("DELETE FROM bookorder WHERE userID = '" + accountID + "';");// Delete all student book
																							// orders
			dbCon.executeQuery(
					"DELETE FROM notification WHERE userID = '" + accountID + "' AND message LIKE 'Your ordered%';");// Delete
																														// student
																														// order
																														// notifications
			ArrayList<String> savedCopiesID = (ArrayList<String>) dbCon
					.executeQuery("SELECT bookID FROM savedcopy WHERE userID = '" + accountID + "';");
			dbCon.executeQuery("DELETE FROM savedcopy WHERE userID = '" + accountID + "';");// Delete saved copies for
																							// this user
			for (String bookID : savedCopiesID) // Get next order for saved copies
				orderNotification(Integer.parseInt(bookID));

			// Send proper email to the graduate

			sendEmail(eMail, "Library Lent Books Return Due Graduation", "Hello " + firstName + " " + lastName
					+ "\nCongratulations for your graduation\nYour library account period has expired and has been suspended.\nBut we can see you have a list of lent books that should be returned, please return the following books\n"
					+ lentBooks.toString().substring(1, lentBooks.toString().length() - 1));

			System.out.println("Email has been sent graduate student ID:" + accountID + " => " + eMail
					+ " to return all lent books , and all orders has been removed");
		}
	}

	@SuppressWarnings("unchecked")
	public void graduateReturn(int accountID) {
		int leftLents = Integer.parseInt(((ArrayList<String>) dbCon
				.executeQuery("SELECT COUNT(*) FROM lentBook WHERE userID = '" + accountID + "' AND returned = '0';"))
						.get(0));
		if (leftLents == 0) {
			dbCon.executeQuery("UPDATE account SET status = 'Locked' WHERE userID= '" + accountID + "';");
			System.out.println("Graduate userID = " + accountID + " was locked");
		} else
			System.out.println("Graduate still got " + leftLents + " unreturned books!");
	}

	/**
	 * Email scheduled task to run every 24 hours
	 */
	private void emailService() {
		initMailConnection();
		TimerTask returnsMailsTask = new TimerTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {// Mails students with books they have to return until tomorrow
				ArrayList<String> lentReturns = (ArrayList<String>) dbCon
						.executeSelectQuery("SELECT userID, bookID FROM lentbook WHERE dueDate = '"
								+ LocalDate.now().plusDays(1) + "' AND returned = 0;");
				for (int i = 0; i < lentReturns.size(); i++) {
					ArrayList<String> lenderData = ((ArrayList<String>) dbCon
							.executeSelectQuery("SELECT firstName,lastName,eMail FROM account WHERE userID = '"
									+ lentReturns.get(i) + "';"));
					String eMail = lenderData.get(2);
					String firstName = lenderData.get(0);
					String lastName = lenderData.get(1);
					i++;
					String bookName = ((ArrayList<String>) dbCon
							.executeSelectQuery("SELECT name FROM book WHERE bookID = '" + lentReturns.get(i) + "';"))
									.get(0);

					sendEmail(eMail, "Lent Book Return", "Hello " + firstName + " " + lastName + "\nYour lent book '"
							+ bookName + "' has to be returned by tomorrow " + LocalDate.now().plusDays(1));

					System.out.println("Email has been sent => " + eMail + " to return '" + bookName + "'");
				}
				if (lentReturns.size() == 0) {
					System.out.println("No returns");
				}
			}
		};
		Timer returnMailsTimer = new Timer();
		returnMailsTimer.schedule(returnsMailsTask, TimeUnit.SECONDS.toMillis(10), TimeUnit.DAYS.toMillis(1));
	}

	/**
	 * Checks database for books late returns (books that should have been returned
	 * yesterday) once every 24 hours
	 */
	private void lateReturnsService() {
		TimerTask lateReturnsTask = new TimerTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				// Get books that should have been returned yesterday
				ArrayList<String> lateReturns = (ArrayList<String>) dbCon.executeSelectQuery(
						"SELECT userID, bookID FROM lentbook WHERE dueDate = '" + LocalDate.now().minusDays(1) + "';");
				for (int i = 0; i < lateReturns.size(); i++) {
					// Increment return delay by 1 for this user
					dbCon.executeQuery("UPDATE account SET delays = delays + 1, status = 'Suspended' WHERE userID = '"
							+ lateReturns.get(i) + "';");

					Calendar calendar = Calendar.getInstance();
					java.sql.Timestamp notificationTimeStamp = new java.sql.Timestamp(calendar.getTime().getTime());
					// If user has got 3 delays notify manager
					if (((ArrayList<String>) dbCon.executeQuery("SELECT EXISTS(SELECT * FROM account WHERE userID = '"
							+ lateReturns.get(i) + "' AND delays = 3);")).get(0).equals("0") ? false : true) {
						// get time stamp for notification

						// Manager notification message to confirm user lock
						dbCon.executeQuery("INSERT INTO notification(date, message, usertype, messageType)VALUES ('"
								+ notificationTimeStamp + "','User ID: " + lateReturns.get(i)
								+ " has 3 return delays account lock approval is required','Manager', 'Lock')");
					} else {// Notification to librarian
						dbCon.executeQuery("INSERT INTO notification(date, message, usertype, messageType)VALUES ('"
								+ notificationTimeStamp + "','User ID: " + lateReturns.get(i)
								+ " has been suspended due late return','Librarian', 'Suspend')");
					}
					dbCon.executeQuery("UPDATE lentbook SET late = 1 WHERE bookID = '" + lateReturns.get(i + 1)
							+ "' AND userID = '" + lateReturns.get(i) + "';");
					i++;
				}
				if (lateReturns.size() == 0) {
					System.out.println("No late returns are found");
				}
			}
		};
		Timer lateReturnsTimer = new Timer();
		lateReturnsTimer.schedule(lateReturnsTask, TimeUnit.SECONDS.toMillis(40), TimeUnit.DAYS.toMillis(1));
	}

	/**
	 * Initiate mailing service over SMT protocol via TLS@587
	 */
	private void initMailConnection() {
		mailConnection = MailerBuilder
				.withSMTPServer("smtp.sendgrid.net", 587, "apikey",
						"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")
				.withTransportStrategy(TransportStrategy.SMTP_TLS).withSessionTimeout(10 * 1000)
				.clearEmailAddressCriteria().withDebugLogging(false).buildMailer();
	}

	public void sendEmail(String recipient, String subject, String content) {
		Email email = EmailBuilder.startingBlank().from("athl1n3@sendgrid.net").to(recipient).withSubject(subject)
				.withPlainText(content)
				.withEmbeddedImage("Signature", new FileDataSource(new File(String
						.valueOf(LibraryServices.class.getClassLoader().getResource("images/Logo.png")).substring(6))))
				.buildEmail();
		mailConnection.sendMail(email);
	}
}