/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import client.ClientConnection;
import entities.Account;
import entities.Account.UserType;
import entities.ActivitiesReport;
import entities.Archive;
import entities.Book;
import entities.Book.bookType;
import entities.BookCopy;
import entities.BookOrder;
import entities.LentBook;
import entities.LibrarianAccount;
import entities.ManagerAccount;
import entities.ManualExtend;
import entities.Notification;
import entities.UserAccount;
import entities.UserAccount.accountStatus;
import entities.UserActivity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DatabaseController {

	private static ClientConnection clientConnection;

	static Account loggedAccount;

	/**
	 * create new account
	 * 
	 * @param arr
	 */
	public static void addAccount(UserAccount newAccount) {
		ArrayList<String> arr = new ArrayList<String>();
		String query = "INSERT INTO account(ID, firstName, lastName, eMail, mobileNum, userID, userName, password, userType, status, delays,logged)VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		arr.add(String.valueOf(newAccount.getID()));
		arr.add(newAccount.getFirstName());
		arr.add(newAccount.getLastName());
		arr.add(newAccount.getEmail());
		arr.add(String.valueOf(newAccount.getMobileNum()));
		arr.add(String.valueOf(newAccount.getAccountID()));
		arr.add(newAccount.getUserName());
		arr.add(newAccount.getPassword());
		arr.add(newAccount.getUserType().toString());
		arr.add(newAccount.getStatus().toString());
		arr.add(String.valueOf(newAccount.getDelays()));
		arr.add(String.valueOf(0));
		arr.add(query);
		clientConnection.executeQuery(arr);
		clientConnection.executeQuery(
				"INSERT INTO archive(userID, ID, userName, password, firstName, lastName, mobileNum, eMail)VALUES ('"
						+ newAccount.getAccountID() + "','" + newAccount.getID() + "','" + newAccount.getUserName()
						+ "','" + newAccount.getPassword() + "','" + newAccount.getFirstName() + "','"
						+ newAccount.getLastName() + "','" + newAccount.getMobileNum() + "','" + newAccount.getEmail()
						+ "')");

	}

	/**
	 * Generate a new account ID for the new user
	 * 
	 * @return account ID for the new user
	 */
	public static int generateAccountID() {
		return ((getTableRowsNumber("account", null) + 1) * 264 + 759);
	}

	/**
	 * <<<<<<< HEAD returns rows number in table according to type if type =null ,
	 * return all the rows number ======= returns rows number in table >>>>>>>
	 * branch 'master' of https://github.com/Athl1n3/OBL-Project.git
	 * 
	 * @param tableName
	 * @return int table rows number
	 */
	public static int getTableRowsNumber(String tableName, String type) {
		if (type != null)
			clientConnection.executeQuery("SELECT COUNT(*) FROM " + tableName + " WHERE userType = '" + type + "';");
		else
			clientConnection.executeQuery("SELECT COUNT(*) FROM " + tableName + ";");
		return Integer.parseInt(clientConnection.getList().get(0));
	}

	public static int getCount(String table, String field, String fieldVal) {
		clientConnection.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + field + "= '" + fieldVal + "';");
		return Integer.parseInt(clientConnection.getList().get(0));
	}

	/**
	 * update account details including logged field
	 * 
	 * @param account
	 */
	public static void updateAccount(Account account) {
		/*
		 * String query = "UPDATE account SET firstName = '" + account.getFirstName() +
		 * "', lastName = '" + account.getLastName() + "', eMail = '" +
		 * account.getEmail() + "', mobileNum = '" + account.getMobileNum() +
		 * "', userName = '" + account.getUserName() + "', password = '" +
		 * account.getPassword() + "', logged = '" + (account.isLogged() == true ? 1:0)
		 * + "' WHERE userID = '" + account.getAccountID() + "';";
		 */
		String query = "UPDATE account SET firstName = '" + account.getFirstName() + "', lastName = '"
				+ account.getLastName() + "', eMail = '" + account.getEmail() + "', mobileNum = '"
				+ account.getMobileNum() + "', userName = '" + account.getUserName() + "', password = '"
				+ account.getPassword() + "', loginCount = '" + account.getLoginCount() + "' WHERE userID = '"
				+ account.getAccountID() + "';";
		clientConnection.executeQuery(query);
	}

	/**
	 * se account to logged
	 * 
	 * @param account
	 */
	public static void logAccount(Account account) {
		clientConnection.executeQuery("UPDATE account SET logged = '" + (account.isLogged() == true ? 1 : 0)
				+ "' WHERE userID = '" + account.getAccountID() + "';");
	}

	/**
	 * update user's status
	 * 
	 * @param userAccount
	 */
	public static void updateUserStatus(UserAccount userAccount, boolean resetDelays) {
		String query;
		if (resetDelays)
			query = "UPDATE account SET status = '" + userAccount.getStatus()
					+ "', delays = '0', loginCount = '0' WHERE userID = '" + userAccount.getAccountID() + "';";
		else
			query = "UPDATE account SET status = '" + userAccount.getStatus() + "', loginCount = '0' WHERE userID = '"
					+ userAccount.getAccountID() + "';";
		clientConnection.executeQuery(query);
	}

	/**
	 * Add notification for librarians+managers
	 * 
	 * @param addNotf
	 */
	public static void addNotfication(String notfMessage) {
		Timestamp now = new Timestamp(new Date().getTime());
		clientConnection
				.executeQuery("INSERT INTO notification(userID, date, message, usertype, messageType)VALUES ('0','"
						+ now + "','" + notfMessage + "','Librarian','Message')");
	}

	/**
	 * Lock user account or keep suspended and reset its delays
	 * 
	 * @param userAccount
	 */
	public static boolean lockAccount(int accountID, boolean lock) {
		String query;
		if (lock)
			query = "UPDATE account SET status = 'Locked' WHERE userID = '" + accountID + "';";// Lock account
		else
			query = "UPDATE account SET delays = '0' WHERE userID = '" + accountID + "';";// Keep account suspended
		clientConnection.executeQuery(query);
		clientConnection.getObject();
		return (Boolean) clientConnection.getObject();
	}

	/**
	 * update user's delays
	 * 
	 * @param userAccount
	 */
	public static void updateUserDelays(UserAccount userAccount) {
		String query = "UPDATE account SET delays = '" + userAccount.getDelays() + "' WHERE userID = '"
				+ userAccount.getAccountID() + "';";
		clientConnection.executeQuery(query);
	}

	/**
	 * finds the account in DB according to user id and returned it, if the account
	 * doesn't exists then return null
	 * 
	 * @param ID
	 * @return Account
	 */
	public static Account getAccount(int ID) {
		clientConnection.executeQuery("SELECT * FROM Account WHERE ID = " + ID + ";");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			Account userAccount;
			if (res.get(8).equals("User")) {
				userAccount = new UserAccount();
				((UserAccount) userAccount).parseArrayIntoAccount(res);
			} else {
				if (res.get(8).equals("Librarian"))
					userAccount = new LibrarianAccount();
				else
					userAccount = new ManagerAccount();
				((LibrarianAccount) userAccount).parseArrayIntoAccount(res);
			}
			return userAccount;
		} else
			return null;
	}

	/**
	 * finds the account in DB according to user id and returned it, if the account
	 * doesn't exists then return null
	 * 
	 * @param ID
	 * @return Account
	 */
	public static Account getAccountByAccountID(int accountID) {
		clientConnection.executeQuery("SELECT * FROM Account WHERE userID = " + accountID + ";");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			Account userAccount;
			if (res.get(8).equals("User")) {
				userAccount = new UserAccount();
				((UserAccount) userAccount).parseArrayIntoAccount(res);
			} else {
				if (res.get(8).equals("Librarian"))
					userAccount = new LibrarianAccount();
				else
					userAccount = new ManagerAccount();
				((LibrarianAccount) userAccount).parseArrayIntoAccount(res);
			}
			return userAccount;
		} else
			return null;
	}

	/**
	 * check if specific value is exists in specific table
	 * 
	 * @param table
	 * @param field
	 * @param fieldVal
	 * @return boolean
	 */
	public static boolean ifExists(String table, String field, String fieldVal) {
		clientConnection
				.executeQuery("SELECT EXISTS(SELECT * FROM " + table + " WHERE " + field + " = '" + fieldVal + "');");
		if (clientConnection.getList().get(0).equals("0"))
			return false;// Field value doesn't exist
		return true;// Field value already exists
	}

	/**
	 * check if value exists in table
	 * 
	 * @param table
	 * @param whereQuery
	 * @return boolean
	 */
	public static boolean ifExists(String table, String whereQuery) {
		clientConnection.executeQuery("SELECT EXISTS(SELECT * FROM " + table + " WHERE " + whereQuery + ");");
		if (clientConnection.getList().get(0).equals("0"))
			return false;// Field value doesn't exist
		return true;// Field value already exists
	}

	/**
	 * finds the account in DB according to (username && password) and returned it,
	 * if the account doesn't exists then return null
	 * 
	 * @param username
	 * @param password
	 * @return Account
	 */
	public static Account getAccount(String username) {
		String query = "SELECT * FROM Account WHERE username = '" + username + "';";
		clientConnection.executeQuery(query);
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			Account loggedAccount;
			if (res.get(8).equals("User")) {
				loggedAccount = new UserAccount();
				((UserAccount) loggedAccount).parseArrayIntoAccount(res);
			} else {
				if (res.get(8).equals("Librarian"))
					loggedAccount = new LibrarianAccount();
				else
					loggedAccount = new ManagerAccount();
				((LibrarianAccount) loggedAccount).parseArrayIntoAccount(res);
			}
			return loggedAccount;
		} else
			return null;
	}

	/**
	 * return user accounts list according to account status
	 * 
	 * @param status
	 * @return arrayList of user account
	 */
	public static ArrayList<UserAccount> getUserAccounts(accountStatus status) {
		clientConnection
				.executeQuery("SELECT * FROM Account WHERE userType = " + "'User'" + " AND status = '" + status + "';");
		try {
			ArrayList<String> res = clientConnection.getList();
			ArrayList<UserAccount> arr = new ArrayList<UserAccount>();
			while (res.size() != 0) {
				UserAccount userAccount = new UserAccount();
				userAccount.parseArrayIntoAccount(res);
				arr.add(userAccount);
				res.subList(0, 14).clear();
			}
			return arr;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * adds new book to the library book list
	 * 
	 * @param newBook
	 */
	public static void addBook(Book newBook) {
		ArrayList<String> arr = new ArrayList<String>();
		String query = "INSERT INTO BOOk(bookID, name, author, edition, printYear, subject, description, catalog,"
				+ " shelf, copiesNumber, Type, availableCopies) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		arr.add(String.valueOf(newBook.getBookID()));
		arr.add(newBook.getName());
		arr.add(newBook.getAuthor());
		arr.add(newBook.getEdition());
		arr.add(String.valueOf(newBook.getPrintYear()));
		arr.add(newBook.getSubject());
		arr.add(newBook.getDescription());
		arr.add(String.valueOf(newBook.getCatalog()));
		arr.add(newBook.getShelf());
		arr.add(String.valueOf(newBook.getCopiesNumber()));
		arr.add(String.valueOf(newBook.getBookType()));
		arr.add(String.valueOf(newBook.getAvailableCopies()));
		arr.add(query);
		clientConnection.executeQuery(arr);
	}

	/**
	 * this function updates existed book data according to book id
	 * 
	 * @param existingBook
	 */
	public static void editBook(Book existingBook) {
		clientConnection.executeQuery("UPDATE book SET copiesNumber = '" + existingBook.getCopiesNumber()
				+ "', shelf = '" + existingBook.getShelf() + "', description = '" + existingBook.getDescription()
				+ "', author = '" + existingBook.getAuthor() + "', edition = '" + existingBook.getEdition()
				+ "', printYear = '" + existingBook.getPrintYear() + "', catalog = '" + existingBook.getCatalog()
				+ "', subject = '" + existingBook.getSubject() + "', type = '" + existingBook.getBookType().name()
				+ "', name = '" + existingBook.getName() + "' WHERE bookID = '" + existingBook.getBookID() + "' ;");
	}

	/**
	 * deletes book from library books list
	 * 
	 * @param bookToDelete
	 */
	public static void deleteBook(int bookID) {
		clientConnection.executeQuery("DELETE FROM book WHERE bookID = '" + bookID + "';");
	}

	/**
	 * Delete saved book copy and its notification when its orderer lents it
	 * 
	 * @param bookID
	 */
	public static void deleteSavedCopy(int bookID, int accountID) {
		clientConnection.executeQuery("SELECT notificationNum FROM savedCopy WHERE bookID = '" + bookID
				+ "' AND userID = '" + accountID + "';");
		String notificationNum = clientConnection.getList().get(0);
		clientConnection.executeQuery("DELETE FROM notification WHERE notificationNum = '" + notificationNum + "';");
		clientConnection.executeQuery(
				"DELETE FROM savedCopy WHERE bookID = '" + bookID + "' AND userID = '" + accountID + "';");
	}

	/**
	 * search for a specific book according to its id and if its founded, return it,
	 * else return null
	 * 
	 * @param id
	 * @return Book
	 */
	public static Book getBook(int id) {
		clientConnection.executeQuery(
				"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book WHERE  bookID= '"
						+ id + "' ;");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			Book book = new Book(Integer.parseInt(res.get(0)), res.get(1), res.get(2), res.get(3),
					Integer.parseInt(res.get(4)), res.get(5), res.get(6), Integer.parseInt(res.get(7)), null,
					res.get(8), Integer.parseInt(res.get(9)),
					res.get(10).equals("Regular") ? bookType.Regular : bookType.Wanted, Integer.parseInt(res.get(11)));

			return book;
		}

		return null;
	}

	/**
	 * return list of books from DB
	 * 
	 * @return ArrayList<Book>
	 */
	public static ArrayList<Book> getAllBooks() {

		clientConnection.executeQuery(
				"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book");

		ArrayList<String> res = clientConnection.getList();
		ArrayList<Book> bookList = new ArrayList<Book>();
		while (res.size() != 0) {
			Book book = new Book(Integer.parseInt(res.get(0)), res.get(1), res.get(2), res.get(3),
					Integer.parseInt(res.get(4)), res.get(5), res.get(6), Integer.parseInt(res.get(7)), null,
					res.get(8), Integer.parseInt(res.get(9)),
					res.get(10).equals("Regular") ? bookType.Regular : bookType.Wanted, Integer.parseInt(res.get(11)));
			book.setBookOrders(getCount("bookorder", "bookID", res.get(0)));
			res.subList(0, 12).clear();

			bookList.add(book);
		}

		return bookList;
	}

	/**
	 * search for specific book according to its name,author, subject or description
	 * 
	 * @param str      search for book
	 * @param searchBy it could be name, author, subject or description
	 * @return ArrayList<Book>
	 */
	public static ArrayList<Book> bookSearch(String str, String searchBy) throws NumberFormatException {
		switch (searchBy.toLowerCase()) {
		case "book id":
			clientConnection.executeQuery(
					"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book WHERE bookID = '"
							+ Integer.parseInt(str) + "' ;");
			break;
		case "name":
			clientConnection.executeQuery(
					"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book WHERE name LIKE '%"
							+ str.toLowerCase() + "%' ;");
			break;
		case "author":
			clientConnection.executeQuery(
					"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book WHERE author LIKE '%"
							+ str.toLowerCase() + "%' ;");
			break;
		case "subject":
			clientConnection.executeQuery(
					"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book WHERE subject LIKE '%"
							+ str.toLowerCase() + "%' ;");
			break;
		case "description":
			clientConnection.executeQuery(
					"SELECT bookID, name, author, edition, printYear, subject, description, catalog, shelf, copiesNumber, Type, availableCopies FROM book WHERE description LIKE '%"
							+ str.toLowerCase() + "%' ;");
			break;
		default:
			return null;
		}
		// get the result from db
		ArrayList<String> res = clientConnection.getList();
		if (res.isEmpty())
			return null;
		ArrayList<Book> bookList = new ArrayList<Book>();
		while (res.size() != 0) { // convert the result to ArrayList<Book> and return it
			Book book = new Book(Integer.parseInt(res.get(0)), res.get(1), res.get(2), res.get(3),
					Integer.parseInt(res.get(4)), res.get(5), res.get(6), Integer.parseInt(res.get(7)), null,
					res.get(8), Integer.parseInt(res.get(9)),
					res.get(10).equals("Regular") ? bookType.Regular : bookType.Wanted, Integer.parseInt(res.get(11)));
			res.subList(0, 12).clear();
			bookList.add(book);
		}
		return bookList;
	}

	/**
	 * update the Book availableCopies -=1
	 * 
	 * @param book
	 */
	public static void updateBookAvailableCopies(Book book, int val) {
		clientConnection.executeQuery("UPDATE Book SET availableCopies = '" + (book.getAvailableCopies() + val)
				+ "' WHERE BookID = '" + book.getBookID() + "';");
	}

	/**
	 * add new lentBook to the user lentBook list
	 * 
	 * @param newLentBook
	 */
	public static void addLentBook(LentBook newLentBook) {
		ArrayList<String> arr = new ArrayList<String>();
		String query = "INSERT INTO LentBook(userID, bookID,copySerialNumber, issueDate, dueDate, late) VALUES(?,?,?,?,?,?)";
		arr.add(String.valueOf(newLentBook.getUserID()));
		arr.add(String.valueOf(newLentBook.getBook().getBookID()));
		arr.add(String.valueOf(newLentBook.getBookCopy().getSerialNumber()));
		arr.add(String.valueOf(newLentBook.getIssueDate()));
		arr.add(String.valueOf(newLentBook.getDueDate()));
		arr.add(String.valueOf("0"));
		arr.add(query);
		clientConnection.executeQuery(arr);
	}

	/**
	 * delete specific lend book according to accountID and bookID
	 * 
	 * @param accountID
	 * @param bookID
	 */
	public static void deleteLendBook(int accountID, int bookID) {
		clientConnection
				.executeQuery("DELETE FROM LentBook WHERE userID = '" + accountID + "' AND bookID = '" + bookID + "';");
	}

	/**
	 * return the user lent books list from DB if(userID>0) return only the user
	 * lent Book list, if(userID<0) return the whole lent Book list, if userID = 0
	 * return only the late users
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<LentBook> getLentBookList(int userID) {
		String query;
		if (userID > 0) // only for specific user according to userID
			query = "SELECT userID, bookID, copySerialNumber, issueDate, dueDate, returnDate, late, returned FROM LentBook WHERE userID  = '"
					+ userID + "' AND returned = '0';";
		else if (userID < 0)// all the list
			query = "SELECT userID, bookID, copySerialNumber, issueDate, dueDate, returnDate,  late,returned FROM LentBook ;";
		else // only the late one [userID = 0]
			query = "SELECT userID, bookID, copySerialNumber, issueDate ,dueDate, returnDate, late, returned FROM LentBook WHERE late = '1' ;";

		clientConnection.executeQuery(query);
		try {// get teh result and convert it to ArrayList<LentBook>
			ArrayList<String> res = clientConnection.getList();
			ArrayList<LentBook> lentBookList = new ArrayList<LentBook>();
			while (res.size() != 0) {
				LentBook lentBook = new LentBook(Integer.parseInt(res.get(0)), getBook(Integer.parseInt(res.get(1))),
						getBookCopy(res.get(1), res.get(2)), LocalDate.parse(res.get(3)), LocalDate.parse(res.get(4)),
						LocalDate.parse(res.get(5)), res.get(6).equals("1") ? true : false);

				lentBook.setReturned(res.get(7).equals("1") ? true : false);
				res.subList(0, 8).clear();
				lentBookList.add(lentBook);
			}
			return lentBookList;
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * return specific lent Book
	 * 
	 * @param userID
	 * @param bookID
	 * @param serialNumber
	 * @return lentBook
	 */
	public static LentBook getLentBook(int userID, int bookID, String serialNumber) {
		clientConnection.executeQuery(
				"SELECT userID,bookID, copySerialNumber, issueDate,dueDate,returnDate,late FROM LentBook WHERE userID  = '"
						+ userID + "' AND bookID = '" + bookID + "' AND copySerialNumber = '" + serialNumber
						+ "' AND returned = '0';");
		ArrayList<String> res = clientConnection.getList();
		LentBook lentBook;
		if (!res.isEmpty()) {
			lentBook = new LentBook(Integer.parseInt(res.get(0)), getBook(Integer.parseInt(res.get(1))),
					getBookCopy(res.get(1), res.get(2)), LocalDate.parse(res.get(3)), LocalDate.parse(res.get(4)),
					LocalDate.parse(res.get(5)), res.get(6).equals("1") ? true : false);
			return lentBook;
		}
		return null;
	}

	/**
	 * get book copy
	 * 
	 * @param bookID
	 * @param serialNumber
	 * @return
	 */
	public static BookCopy getBookCopy(String bookID, String serialNumber) {
		clientConnection.executeQuery(
				"SELECT * FROM BookCopy WHERE serialNumber= '" + serialNumber + "' AND bookID = '" + bookID + "' ;");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {// convert teh result to bookCopy
			BookCopy bookCopy = new BookCopy(Integer.parseInt(res.get(0)), res.get(1), LocalDate.parse(res.get(2)),
					res.get(3).equals("1") ? true : false);
			return bookCopy;
		}
		return null;
	}
	

	/**
	 * return the book copies list from DB
	 * 
	 * @return ArrayList<BookCopy>
	 */
	public static ArrayList<BookCopy> getbookCopyList(int bookID) {

		clientConnection.executeQuery("SELECT * FROM BookCopy WHERE bookID  = '" + bookID + "';");
		ArrayList<String> res = clientConnection.getList();
		ArrayList<BookCopy> bookCopyList = new ArrayList<BookCopy>();
		while (res.size() != 0) {
			BookCopy bookCopy = new BookCopy(Integer.parseInt(res.get(0)), res.get(1), LocalDate.parse(res.get(2)),
					res.get(2).equals("1") ? true : false);
			res.subList(0, 4).clear();
			bookCopyList.add(bookCopy);
		}

		return bookCopyList;
	}

	/**
	 * get all the copies
	 * 
	 * @return bookCopy list
	 */
	public static ArrayList<BookCopy> getAllCopies() {
		clientConnection.executeQuery("SELECT * FROM BookCopy;");
		ArrayList<String> res = clientConnection.getList();
		ArrayList<BookCopy> bookCopyList = new ArrayList<BookCopy>();
		while (res.size() != 0) {// convert the result to arrayList<BookCopy>
			BookCopy bookCopy = new BookCopy(Integer.parseInt(res.get(0)), res.get(1), LocalDate.parse(res.get(2)),
					res.get(2).equals("1") ? true : false);
			res.subList(0, 4).clear();
			bookCopyList.add(bookCopy);
		}

		return bookCopyList;
	}

	/**
	 * deletes specific copy from booCopy table in DB
	 * 
	 * @param bookID
	 * @param serialNumber
	 */
	public static void deleteBookCopy(int bookID, String serialNumber) {

		clientConnection.executeQuery(
				"DELETE FROM BookCopy WHERE bookID = '" + bookID + "' AND SerialNumber = '" + serialNumber + "';");

		clientConnection.executeQuery("UPDATE Book SET copiesNumber = copiesNumber - 1, availableCopies = availableCopies - 1 WHERE bookID = '"
				+ bookID + "';");
		
	}
	
	/**
	 * deletes all copies for specific book from booCopy table in DB
	 * 
	 * @param bookID
	 * @param serialNumber
	 */
	public static void deleteBookCopies(int bookID) {

		clientConnection.executeQuery(
				"DELETE FROM BookCopy WHERE bookID = '" + bookID + "';");
	}

	/**
	 * add new book Copy to BookCopy table in DB
	 * 
	 * @param copy
	 */
	public static void addBookCopy(BookCopy copy) {
		ArrayList<String> arr = new ArrayList<String>();
		String query = "INSERT INTO BookCopy(bookID,serialNumber, purchaseDate) VALUES(?,?,?)";
		arr.add(String.valueOf(copy.getBookID()));
		arr.add(copy.getSerialNumber());
		// we don't need to set the lent field, because its given a default value = 0
		arr.add(String.valueOf(copy.getPurchaseDate()));
		arr.add(query);
		clientConnection.executeQuery(arr);
		
		clientConnection.executeQuery("UPDATE Book SET copiesNumber = copiesNumber + 1, availableCopies = availableCopies + 1 WHERE bookID = '"
				+ copy.getBookID() + "';");
	}

	/**
	 * update the BookCopy isLent field
	 * 
	 * @param bookCopy
	 */
	public static void updateBookCopy(BookCopy bookCopy) {
		clientConnection
				.executeQuery("UPDATE bookCopy SET isLent = '" + (bookCopy.isLent() ? 1 : 0) + "' WHERE bookID = '"
						+ bookCopy.getBookID() + "' AND serialNumber = '" + bookCopy.getSerialNumber() + "';");
		// return ((Boolean) clientConnection.getObject()) == true ? true : false;
	}

	/**
	 * update return date or due date in lent book table
	 * 
	 * @param LentBook
	 */
	public static void updateBookReturnDate(LentBook lentBook) {
		clientConnection.executeQuery("UPDATE LentBook SET dueDate = '" + lentBook.getDueDate() + "' WHERE userID = '"
				+ lentBook.getUserID() + "' AND bookID = '" + lentBook.getBook().getBookID()
				+ "' AND copySerialNumber = '" + lentBook.getBookCopy().getSerialNumber() + "';");
	}

	/**
	 * update returned and return date fields in lentBook table
	 * 
	 * @param lentBook
	 */
	public static void returnBook(LentBook lentBook, Integer graduateID) {
		ArrayList<String> arr = new ArrayList<>();
		String query = "UPDATE LentBook SET returnDate = '" + lentBook.getReturnDate() + "', returned = '"
				+ (lentBook.isReturned() ? "1" : "0") + "' WHERE userID = '" + lentBook.getUserID() + "' AND bookID = '"
				+ lentBook.getBook().getBookID() + "' AND copySerialNumber = '"
				+ lentBook.getBookCopy().getSerialNumber() + "';";

		arr.add("#");
		arr.add(String.valueOf(lentBook.getBook().getBookID()));
		if (graduateID != 0) {
			arr.add("$");
			arr.add(String.valueOf(graduateID));
		}
		arr.add(query);
		clientConnection.executeQuery(arr);
	}
	
	/**
	 * check if the book is late
	 * @param lentBook
	 * @return boolean
	 */
	public static boolean isLate(LentBook lentBook) {
		clientConnection.executeQuery("SELECT late FROM LentBook WHERE userID = '" + lentBook.getUserID()
				+ "' AND bookID = '" + lentBook.getBook().getBookID() + "' AND copySerialNumber = '"
				+ lentBook.getBookCopy().getSerialNumber() + "';");
		if (Integer.parseInt(clientConnection.getList().get(0)) == 1)
			return true;
		return false;
	}

	/**
	 * place an Book Order in DB
	 * 
	 * @param order
	 */
	public static void placeOrder(BookOrder order) {
		ArrayList<String> arr = new ArrayList<String>();
		String query = "INSERT INTO BookOrder(userID, bookID, orderDate) VALUES(?,?,?)";
		arr.add(String.valueOf(order.getUserID()));
		arr.add(String.valueOf(order.getBookID()));
		arr.add(String.valueOf(order.getOrderDate()));
		arr.add(query);
		clientConnection.executeQuery(arr);
		// clientConnection.executeQuery("Select * FROM BookOrder Order By orderDate ASC
		// LIMIT 1");
		// System.out.println(clientConnection.getList());

	}

	/**
	 * get manual extend list
	 * @return list
	 */
	public static ArrayList<ManualExtend> getManualExtendList(){
		clientConnection.executeQuery("SELECT bookID,userID,workerName,extendDate,dueDate FROM ManualExtend;");
		ArrayList<String> res = clientConnection.getList();
		ArrayList<ManualExtend> list = new ArrayList<ManualExtend>();
		while (res.size() != 0) {// convert the result to arrayList<BookCopy>
			ManualExtend extend = new ManualExtend(Integer.parseInt(res.get(0)), Integer.parseInt(res.get(1)),res.get(2), LocalDate.parse(res.get(3)),
					LocalDate.parse(res.get(4)));
			res.subList(0, 5).clear();
			list.add(extend);
		}

		return list;
	}

	/**
	 * check existence of specific order
	 * 
	 * @param userID
	 * @param bookID
	 * @return Boolean
	 */
	public static boolean checkExistingOrder(int userID, int bookID) {
		clientConnection
				.executeQuery("SELECT * FROM BookOrder WHERE userID = '" + userID + "' AND bookID = '" + bookID + "';");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			return true;
		}
		return false;
	}

	/**
	 * check if there is any orders for specific book
	 * 
	 * @param bookID
	 * @return Boolean
	 */
	public static boolean checkExistngBookOrder(int bookID) {
		clientConnection.executeQuery("SELECT * FROM BookOrder WHERE bookID = '" + bookID + "';");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			return true;
		}
		return false;
	}

	/**
	 * this function returns the user's original data from DB according to its id,
	 * if user not founded,return null
	 * 
	 * @param id
	 * @return Archive
	 */
	public static Archive getArchiveData(int id) {
		clientConnection.executeQuery("SELECT * FROM archive WHERE  ID= '" + id + "' ;");
		ArrayList<String> res = clientConnection.getList();
		if (res.size() != 0) {
			Archive archive = new Archive(Integer.parseInt(res.get(0)), Integer.parseInt(res.get(1)), res.get(2),
					res.get(3), res.get(4), res.get(5), res.get(6), res.get(7));
			return archive;
		}

		return null;

	}

	/**
	 * return user activity list from DB
	 * 
	 * @param AccountID
	 * @return ArrayList<UserActivity>
	 */
	public static ArrayList<UserActivity> getUserActivity(int AccountID) {
		clientConnection.executeQuery(
				"SELECT userid, activityName, date FROM useractivity WHERE userID = '" + AccountID + "';");
		ArrayList<String> res = clientConnection.getList();
		ArrayList<UserActivity> activityList = new ArrayList<UserActivity>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		while (res.size() != 0) {
			try {//prepare the user activity list to be returned 
				Date parsedDate = dateFormat.parse(res.get(2));
				UserActivity activity = new UserActivity(Integer.parseInt(res.get(0)), res.get(1),
						new Timestamp(parsedDate.getTime()));
				res.subList(0, 3).clear();
				activityList.add(activity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return activityList;
	}

	/**
	 * adding user activity to userActivity table in DB
	 * 
	 * @param ID
	 * @param activity
	 */
	public static void addActivity(int ID, String activity) {
		ArrayList<String> arr = new ArrayList<String>();
		String query = "INSERT INTO userActivity(userID, activityName, date) VALUES(?,?,?);";
		arr.add(String.valueOf(ID));
		arr.add(activity);
		// get the current date and time to be saved in DB
		Timestamp now = new Timestamp(new Date().getTime());
		arr.add(String.valueOf(now));
		arr.add(query);
		clientConnection.executeQuery(arr);
	}

	/**
	 * return user notifications list from DB
	 * 
	 * @param AccountID
	 * @return ArrayList Notification
	 */
	public static ArrayList<Notification> getNotifications(int AccountID) {
		if (AccountID != 1 && AccountID != 2)
			clientConnection.executeQuery(
					"SELECT notificationNum, userID, date, message, messageType FROM notification WHERE userID = '"
							+ AccountID + "';");
		else if (AccountID == 1)
			clientConnection.executeQuery(
					"SELECT notificationNum, userID, date, message, messageType FROM notification WHERE userType = 'Manager' OR userType='Librarian';");
		else
			clientConnection.executeQuery(
					"SELECT notificationNum, userID, date, message, messageType FROM notification WHERE userType = 'Librarian';");
		ArrayList<String> res = clientConnection.getList();
		ArrayList<Notification> notificationsList = new ArrayList<Notification>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		while (res.size() != 0) {
			try {
				Date parsedDate = dateFormat.parse(res.get(2));
				Notification notification = new Notification(Integer.parseInt(res.get(0)), Integer.parseInt(res.get(1)),
						new Timestamp(parsedDate.getTime()), res.get(3), res.get(4));
				res.subList(0, 5).clear();
				notificationsList.add(notification);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return notificationsList;
	}

	/**
	 * get the closest return date from DB according to BookID
	 * 
	 * @param bookID
	 * @return LocalDate
	 */
	public static LocalDate getClosestReturnDate(int bookID) {
		clientConnection
				.executeQuery("SELECT dueDate From LentBook WHERE bookID = '" + bookID + "' ORDER BY dueDate LIMIT 1");
		ArrayList<String> res = clientConnection.getList();
		if (!res.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
			return LocalDate.parse(res.get(0), formatter);
		} else
			return null;
	}

	/**
	 * Delete notification from database
	 * 
	 * @param delNotf
	 */
	public static void deleteNotfication(Notification delNotf) {
		clientConnection.executeQuery(
				"DELETE FROM notification WHERE notificationNum = '" + delNotf.getNotificationNum() + "';");
	}

	/**
	 * fills activities report from DB and return it
	 * 
	 * @return ActivitiesReport
	 */
	public static ActivitiesReport getActivityReport() {
		ActivitiesReport report = new ActivitiesReport();
		report.setTotalUsers(getTableRowsNumber("account", UserType.User.toString()));
		report.setActiveUsersNumber(getUserTypeNumberAccordingToStatus(accountStatus.Active.toString()));
		report.setFrozenUsersNumber(getUserTypeNumberAccordingToStatus(accountStatus.Suspended.toString()));
		report.setLockedUsersNumber(getUserTypeNumberAccordingToStatus(accountStatus.Locked.toString()));

		// get book list from DB
		report.setBooks(getAllBooks());
		report.setAccounts(getAllLateUsers());
		return report;
	}

	/**
	 * get the users list witch they where late in return book at least once
	 * 
	 * @return users accounts
	 */
	public static ArrayList<UserAccount> getAllLateUsers() {
		String query = "SELECT DISTINCT userID FROM LentBook WHERE late = '1' AND returned = '1';";
		clientConnection.executeQuery(query);
		try {
			ArrayList<UserAccount> accounts = new ArrayList<UserAccount>();
			ArrayList<String> res = clientConnection.getList();
			while (res.size() != 0) {
				accounts.add((UserAccount) getAccount(Integer.parseInt(res.get(0))));
				res.remove(0);
			}
			return accounts;

		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * get the late book list from lentBook table
	 * 
	 * @return book list
	 */
	public static ArrayList<Book> getAllLateBooks() {
		String query = "SELECT DISTINCT bookID FROM LentBook WHERE late = '1' AND returned = '1';";
		clientConnection.executeQuery(query);
		ArrayList<Book> bookList = new ArrayList<Book>();
		ArrayList<String> res = clientConnection.getList();
		if (res != null) {
			while (res.size() != 0) {
				Book book = getBook(Integer.parseInt(res.get(0)));
				if (book != null)
					bookList.add(book);
				res.remove(0);
			}
			return bookList;
		}
		return null;
	}

	/**
	 * get all the late copies for specific Book as list of lentBooks
	 * 
	 * @param bookID
	 * @return
	 */
	public static ArrayList<LentBook> getLateCopiesForSpecificBook(int bookID) {
		String query = "SELECT DISTINCT copySerialNumber FROM LentBook WHERE  bookID= '" + bookID
				+ "' AND late = '1' AND returned = '1' ;";
		clientConnection.executeQuery(query);
		try {
			ArrayList<LentBook> arr = new ArrayList<LentBook>();
			ArrayList<String> res = clientConnection.getList();
			while (res.size() != 0) {
				LentBook lentBook = getlentBook(bookID, res.get(0));
				if (lentBook != null)
					arr.add(lentBook);
				res.remove(0);
			}
			return arr;

		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * get late lentBook according to bookID and copy SerialNumber
	 * 
	 * @param bookID
	 * @param serialNumber
	 * @return lentBook
	 */
	private static LentBook getlentBook(int bookID, String serialNumber) {
		clientConnection.executeQuery("SELECT userID, bookID, copySerialNumber, issueDate, dueDate, returnDate, late"
				+ " FROM LentBook WHERE bookID= '" + bookID + "' AND copySerialNumber = '" + serialNumber
				+ "' AND late = '1' AND returned = '1' ;");
		ArrayList<String> res = clientConnection.getList();
		LentBook lentBook;
		if (!res.isEmpty()) {
			lentBook = new LentBook(Integer.parseInt(res.get(0)), getBook(Integer.parseInt(res.get(1))),
					getBookCopy(res.get(1), res.get(2)), LocalDate.parse(res.get(3)), LocalDate.parse(res.get(4)),
					LocalDate.parse(res.get(5)), res.get(6).equals("1") ? true : false);
			return lentBook;
		}
		return null;

	}

	/**
	 * 
	 * @param status
	 * @return int number of users according to there status
	 */
	public static int getUserTypeNumberAccordingToStatus(String status) {
		clientConnection
				.executeQuery("SELECT COUNT(*) FROM account WHERE userType = 'User' AND status = '" + status + "';");
		ArrayList<String> arr;
		arr = clientConnection.getList();
		if (arr.size() == 0) {
			return 0;
		}
		return Integer.parseInt(arr.get(0));
	}

	/**
	 * update lent book and return true if succeeded else return false 
	 * @param lentBook
	 * @return boolean
	 */
	public static boolean updateLentBook(LentBook lentBook) {
		clientConnection.executeQuery(
				"UPDATE lentbook SET dueDate = '" + lentBook.getDueDate() + "', late = '0' WHERE userID = '"
						+ lentBook.getUserID() + "' AND bookID = '" + lentBook.getBook().getBookID() + "';");
		return ((Boolean) clientConnection.getObject()) == true ? true : false;
	}

	/**
	 * update book return date manually 
	 * @param extendLog
	 * @return boolean
	 */
	public static boolean addManualExtend(ManualExtend extendLog) {
		String query = "INSERT INTO ManualExtend(bookID, userID, workerName, extendDate, dueDate) VALUES(?,?,?,?,?)";
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(String.valueOf(extendLog.getBookID()));
		arr.add(String.valueOf(extendLog.getUserID()));
		arr.add(String.valueOf(extendLog.getWorkerName()));
		arr.add(String.valueOf(extendLog.getExtendDate()));
		arr.add(String.valueOf(extendLog.getDueDate()));
		arr.add(query);
		clientConnection.executeQuery(arr);

		return ((Boolean) clientConnection.getObject()) == true ? true : false;
	}

	/**
	 * Get user scheduled suspension if it exists
	 * 
	 * @param userID to get suspension scheduled
	 * @return scheduled suspension data / Null if it doesn't exist
	 */
	public static LocalDate getSchedueledSuspension(int userID) {
		if (ifExists("scheduledSuspension", "userID", String.valueOf(userID))) {
			clientConnection.executeQuery("SELECT untilDate FROM scheduledSuspension WHERE userID = '" + userID + "'");
			return LocalDate.parse(clientConnection.getList().get(0));
		}
		return null;
	}

	/**
	 * Add a scheduled suspension
	 * 
	 * @param userID    to suspend
	 * @param untilDate until what date
	 * @return if executed successfully
	 */
	public static boolean addScheduledSuspension(int userID, LocalDate untilDate) {
		if (ifExists("scheduledSuspension", "userID", String.valueOf(userID))) {
			clientConnection.executeQuery(
					"UPDATE scheduledSuspension SET untilDate ='" + untilDate + "' WHERE userID = '" + userID + "'");
		} else
			clientConnection.executeQuery(
					"INSERT INTO scheduledSuspension(userID, untilDate)VALUES ('" + userID + "','" + untilDate + "')");
		return ((Boolean) clientConnection.getObject()) == true ? true : false;
	}

	/**
	 * Delete scheduled account suspension
	 * 
	 * @param userID
	 * @return if executed successfully
	 */
	public static boolean deleteScheduledSuspension(int userID) {
		if (ifExists("scheduledSuspension", "userID", String.valueOf(userID))) {
			clientConnection.executeQuery("DELETE FROM scheduledSuspension WHERE userID = '" + userID + "'");
			return true;
		}
		return false;
	}

	/**
	 * check input length 
	 * @param tf
	 * @param maxLength
	 * @param txtFieldName
	 * @param type
	 */
	public static void addTextLimiter(final TextField tf, final int maxLength, String txtFieldName, String type) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue,
					final String newValue) {
				if (tf.getText().length() > maxLength) {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Warning");
					if (type == "int")
						alert.setContentText(txtFieldName + " must contain maximum of " + maxLength + " digits");
					else
						alert.setContentText(txtFieldName + " must contain maximum of " + maxLength + " characters");
					alert.showAndWait();
					String s = tf.getText().substring(0, maxLength);
					tf.setText(s);
				}
			}
		});
	}

	/**
	 * check long input length like book description and subject 
	 * @param tf
	 * @param maxLength
	 * @param txtFieldName
	 * @param type
	 */
	public static void addTextLimiter(final TextArea tf, final int maxLength, String txtFieldName, String type) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue,
					final String newValue) {
				if (tf.getText().length() > maxLength) {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Warning");
					if (type == "int")
						alert.setContentText(txtFieldName + " must contain maximum of " + maxLength + " digits");
					else
						alert.setContentText(txtFieldName + " must contain maximum of " + maxLength + " characters");
					alert.showAndWait();
					String s = tf.getText().substring(0, maxLength);
					tf.setText(s);
				}
			}
		});
	}

	/**
	 * Initiate a new client connection to the server
	 * 
	 * @param newClientConnection
	 */
	public static void InitiateClient(ClientConnection newClientConnection) {
		clientConnection = newClientConnection;
	}

	/**
	 * Shutdown client server connection when primary stage closes and logout logged
	 * in account
	 */
	public static void terminateClient() {
		if (loggedAccount != null) {
			loggedAccount.setLogged(false);
			logAccount(loggedAccount);
			System.out.println("Logging user out");
		}
		clientConnection.terminate();
	}

	/**
	 * This method was built only for testing purposes (External system sends a
	 * graduation note with graduated student ID to OBL server)
	 * 
	 * @param accountID
	 */
	public static void graduateStudent(Integer studentID) {
		clientConnection.graduateStudent(studentID);
	}

	/**
	 * save the file to data base
	 * @param bookName
	 * @param filePath
	 * @param bookID
	 */
	public static void saveFile(String bookName, String filePath, int bookID) {
		bookName.concat(".pdf");
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("&");
		arr.add(bookName);
		clientConnection.saveFile(arr, filePath, String.valueOf(bookID));
	}

	/**
	 * save Activity report file to data base
	 * @param bookName
	 * @param filePath
	 * @param bookID
	 */
	public static void saveActivityReportFile(LocalDate date, String filePath) {
		String fileName = "Activity Report.pdf";
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(" ");
		arr.add(fileName);
		clientConnection.saveFile(arr, filePath, date.toString());
	}
	
	/**
	 * getting the file from bookContentsFile Table in DB
	 * 
	 * @param id
	 */
	public static void getFileFromDB(int bookID, String bookName, String filePath) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(String.valueOf(bookID));
		arr.add(bookName + " Contents.pdf");
		arr.add(filePath);
		arr.add("@");
		clientConnection.uploadFile(arr);
	}
	
	/**
	 * getting activity report file from reports Table in DB
	 * 
	 * @param id
	 */
	public static void getActivityReportFileFromDB(LocalDate date, String filePath) {
		ArrayList<String> arr = new ArrayList<String>();
		String fileName = date.toString() + " Activity Report.pdf";
		arr.add(date.toString());
		arr.add(fileName);
		arr.add(filePath);
		arr.add("!");
		clientConnection.uploadFile(arr);
	}
	
	public static ArrayList<String> getAllReportsFromDB() {
		String query = "SELECT Date FROM Reports";
		clientConnection.executeQuery(query);
		ArrayList<String> res = clientConnection.getList();
		if (res != null) {
			return res;
		}
		return null;		
	}

}
