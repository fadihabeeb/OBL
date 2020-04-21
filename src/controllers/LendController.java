/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import entities.Account;
import entities.Book;
import entities.Book.bookType;
import entities.BookCopy;
import entities.LentBook;
import entities.LibrarianAccount;
import entities.UserAccount;
import entities.UserAccount.accountStatus;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 
 * @author Alaa Grable
 * @version 1.0 [17.1.2019]
 * 
 */

public class LendController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableView<LentBook> tableView;

	@FXML
	private ImageView imgBack;

	@FXML
	private TextField txtBookID;

	@FXML
	private TextField txtBookName;

	@FXML
	private TextField txtBookType;

	@FXML
	private TextField txtAvailableCopies;

	@FXML
	private TextField txtUserID;

	@FXML
	private DatePicker dtDueDate;

	@FXML
	private TextField txtName;

	@FXML
	private Button btnBookLookup;

	@FXML
	private DatePicker dtIssueDate;

	@FXML
	private Button btnLendBook;

	@FXML
	private Button btnClear;

	@FXML
	private TextField txtSerialNumber;

	private Account lenderAccount;
	private Book bookData;
	private boolean lookedUp;
	private static Book selectedBook = null;

	/**
	 * Get the relevant information of the inserted book ID. Get the relevant information of the inserted user ID.
	 * Display the information if the inserted book ID and user ID.
	 * @param event - on pressing the 'Look Up' button.
	 */
	@FXML
	void btnBookLookupPressed(ActionEvent event) {

		if (lookedUp == false) {
			bookData = DatabaseController.getBook(Integer.parseInt(txtBookID.getText()));
			// validate if there is such a book with the inputed book ID
			if (bookData == null) {
				// if not , then let the user know
				alertWarningMessage("There is no such book in the library");
			} else {
				lenderAccount = DatabaseController.getAccount(Integer.parseInt(txtUserID.getText()));
				// validate if there is such an account with the inputed ID
				if (lenderAccount == null) {
					// if not , then let the user know
					alertWarningMessage("User doesn't exist!");
				} else {
					// validate if the user account status is not active
					if (lenderAccount instanceof UserAccount) {
						if (!((UserAccount) lenderAccount).getStatus().equals(accountStatus.Active)) {
							alertWarningMessage(
									"This account is " + ((UserAccount) lenderAccount).getStatus().toString()
											+ "\nNo lending can be performed");
						} else {
							// if the book ID & the user ID is found in the DB , then display the details
							// about the book and the user
							txtBookName.setText(bookData.getName());
							txtBookType.setText(bookData.getBookType().toString());
							txtAvailableCopies.setText(String.valueOf(bookData.getAvailableCopies()));
							txtName.setText(lenderAccount.getFirstName());

							if (bookData.getBookType() == bookType.Wanted)
								dtDueDate.setValue(LocalDate.now().plusDays(3));// the book is "wanted" so lent the book
																				// for // 3 days only
							else
								dtDueDate.setValue(LocalDate.now().plusWeeks(2));// the books is "regular" , then lent
																					// the
																					// book for 2 weeks
							// the user account status is active , then validate if there is copies of that
							// book
							if (bookData.getAvailableCopies() == 0 || ((DatabaseController.getCount("savedCopy",
									"bookID", String.valueOf(bookData.getBookID())) == bookData.getAvailableCopies())
									&& !(DatabaseController.ifExists("savedCopy",
											"userID = '" + lenderAccount.getAccountID() + "' AND bookID = '"
													+ bookData.getBookID() + "'")))) {
								alertWarningMessage("There are no copies available to lend this book \n" + "'"
										+ bookData.getName() + "'");// if there is no copies of that book then let the
																	// user know that
								txtAvailableCopies.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
								btnLendBook.setDisable(true);
							} else {
								// if everything is okay then enable the button to let the user be able to lent
								// the book
								btnLendBook.setDisable(false);
							}
							txtBookID.setDisable(true);
							txtUserID.setDisable(true);
							lookedUp = true;
						}
					} else if (lenderAccount instanceof LibrarianAccount)
						alertWarningMessage("Can't use a librarian ID for lending!");
				}
			}
		} else
			alertWarningMessage("User/Book already looked up!");
	}

	/**
	 * Clears all the fields in the Current GUI.
	 * @param event - when pressing the 'Clear' button.
	 */
	@FXML
	void btnClearPressed(ActionEvent event) {
		txtBookID.clear();
		txtBookName.clear();
		txtBookType.clear();
		txtAvailableCopies.clear();
		txtUserID.clear();
		txtName.clear();
		dtDueDate.getEditor().clear();
		dtDueDate.setValue(null);
		txtBookID.setDisable(false);
		txtUserID.setDisable(false);
		lookedUp = false;
		txtSerialNumber.clear();
	}

	/**
	 * Validate if the chosen book copy is lent or not. if and only if this book copy is not lent then creates a new lent book request
	 * and saves the request in the DB.
	 * @param event - on pressing the 'Lend Book' button.
	 */
	@FXML
	void btnLendBookPressed(ActionEvent event) {

		BookCopy bookCopy = DatabaseController.getBookCopy(txtBookID.getText(), txtSerialNumber.getText());

		if (bookCopy != null) {
			if (bookCopy.isLent())
				alertWarningMessage("Book with this serial number is already lent");
			else {
				if (DatabaseController.ifExists("savedCopy", "userID = '" + lenderAccount.getAccountID()
						+ "' AND bookID = '" + bookData.getBookID() + "'")) {
					DatabaseController.deleteSavedCopy(bookData.getBookID(),lenderAccount.getAccountID());
				}
				LentBook lntbook = new LentBook(lenderAccount.getAccountID(), bookData, bookCopy, LocalDate.now(),
						dtDueDate.getValue(), LocalDate.parse("1970-01-01"), false);
				// lent the book to the user
				bookCopy.setLent(true);
				DatabaseController.addLentBook(lntbook);
				DatabaseController.updateBookCopy(bookCopy);
				DatabaseController.updateBookAvailableCopies(bookData, -1);
				// let the user know that the lent process has been cone successfully
				DatabaseController.addActivity(lenderAccount.getAccountID(),
						"Lent Book [Book ID: " + lntbook.getBook().getBookID() + "]");
				Alert alert = new Alert(AlertType.INFORMATION, "Book has been lent successfully", ButtonType.OK);
				alert.show();
			}
		} else
			alertWarningMessage("This book serial number doesn't exist!");

	}

	/**
	 * Close this stage and get back to the previous stage.
	 * @param event - on pressing the 'back(image)' button.
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		// get the previous scene
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Main");
	}

	/**
	 * Initialise the 'Lend ' stage.
	 */
	@FXML
	void initialize() {
		lookedUp = false;
		dtDueDate.setEditable(false);
		dtDueDate.setOnMouseClicked(e -> {
		     if(!dtDueDate.isEditable())
		    	 dtDueDate.hide();
		});
		dtIssueDate.setOnMouseClicked(e -> {
		     if(!dtIssueDate.isEditable())
		    	 dtIssueDate.hide();
		});
		// a listener to validate if the ID length is not greater than 9 digits and if
		// it's only contain numbers
		txtBookID.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				txtBookID.setText(newValue.replaceAll("[^\\d]", ""));
				alertWarningMessage("The book ID must contain only numbers");
			}

		});

		// a listener to validate if the ID length is not greater than 9 digits and if
		// it's only contain numbers
		txtUserID.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				txtUserID.setText(newValue.replaceAll("[^\\d]", ""));
				alertWarningMessage("The ID must contain only numbers");
			}
			if (txtUserID.getLength() > 9) {
				txtUserID.setText(oldValue);
				alertWarningMessage("The ID must be 9 numbers");
			}
		});
		// display the date of today
		dtIssueDate.setValue(LocalDate.now());
		// make this field inedible
		dtIssueDate.setEditable(false);
		// disable LendBook button
		btnLendBook.setDisable(true);

		if(selectedBook != null)
			txtBookID.setText(String.valueOf(selectedBook.getBookID()));
		// Enable BookLookUp button only when the book ID textfield & user ID textField
		// is not empty
		BooleanBinding booleanBind = txtBookID.textProperty().isEmpty().or(txtUserID.textProperty().isEmpty());
		btnBookLookup.disableProperty().bind(booleanBind);
	}

	/**
	 * Load the 'Lend ' stage after initialising it.
	 * @param stage - the stage for display.
	 * @throws Exception
	 */
	void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/LendForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Lend book");
		stage.sizeToScene();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Load the 'Lend' stage from the 'Search' stage with the selected book after initialising it.
	 * @param stage - the stage for display.
	 * @throws Exception
	 */
	void start(Stage stage, Book selectedBook) throws Exception {
		this.selectedBook = selectedBook;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/LendForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Lend book");
		stage.sizeToScene();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Show an appropriate alert to the user when an error or a warning occurs.
	 * @param msg
	 */
	private void alertWarningMessage(String msg) {
		new Alert(AlertType.WARNING, msg, ButtonType.OK).show();
	}
}