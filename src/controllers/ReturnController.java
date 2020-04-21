/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import entities.Account;
import entities.LentBook;
import entities.UserAccount;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReturnController implements Initializable {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private TextField txtBookID;

	@FXML
	private TextField txtSerialNumber;

	@FXML
	private DatePicker dtIssueDate;

	@FXML
	private DatePicker dtReturnDate;

	@FXML
	private TextField txtBookName;

	@FXML
	private TextField txtUserID;

	@FXML
	private DatePicker dtDueDate;

	@FXML
	private TextField txtName;

	@FXML
	private Button btnLookup;

	@FXML
	private Button btnReturnBook;

	@FXML
	private Button btnClear;

	private LentBook lentBook;
	private Account lenderAccount;
	private boolean lookedUp;

	/**
	 * brows user and book details
	 * 
	 * @param event
	 */
	@FXML
	void btnLookupPressed(ActionEvent event) {
		if (lookedUp == false) {
			lenderAccount = DatabaseController.getAccount(Integer.parseInt(txtUserID.getText()));
			if (lenderAccount != null) {
				if (lenderAccount instanceof UserAccount) {
					lentBook = DatabaseController.getLentBook(lenderAccount.getAccountID(),
							Integer.parseInt(txtBookID.getText()), txtSerialNumber.getText());
					// validate if there is such a book with the inputed book ID
					if (lentBook == null) {
						// if not , then let the user know
						alertWarningMessage("No lent book with the inserted data was found! ");
						txtUserID.setStyle(null);
						txtBookID.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
						txtSerialNumber.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
						txtBookID.requestFocus();
					} else {
						txtBookID.setStyle(null);
						txtSerialNumber.setStyle(null);
						txtUserID.setStyle(null);
						txtBookID.setDisable(true);
						txtUserID.setDisable(true);
						txtSerialNumber.setDisable(true);
						txtBookName.setText(lentBook.getBook().getName());
						txtName.setText(lenderAccount.getFullName());
						dtIssueDate.setValue(lentBook.getIssueDate());
						dtDueDate.setValue(lentBook.getDueDate());
						btnReturnBook.setDisable(false);
						txtBookName.setDisable(false);
						txtName.setDisable(false);
						dtIssueDate.setDisable(false);
						dtDueDate.setDisable(false);
						btnReturnBook.setStyle("-fx-border-color: black ; -fx-border-width: 1px ;");
						lookedUp = true;
						dtReturnDate.setDisable(false);
						dtReturnDate.setDayCellFactory(picker -> new DateCell() {
							@Override
							public void updateItem(LocalDate date, boolean empty) {
								super.updateItem(date, empty);
								LocalDate issueDate = dtIssueDate.getValue();

								setDisable(
										empty || date.compareTo(issueDate) < 0 || date.compareTo(LocalDate.now()) > 0);
							}
						});
					}
				} else
					alertWarningMessage("Librarian ID can't be used for returns!");
			} else {
				alertWarningMessage("User doesn't exist!");
				txtUserID.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				txtUserID.requestFocus();
			}
		} else
			alertWarningMessage("User/Book already looked up!");
	}

	/**
	 * clear all the fields
	 * 
	 * @param event
	 */
	@FXML
	void btnClearPressed(ActionEvent event) {
		txtBookID.clear();
		txtUserID.clear();
		txtName.clear();
		txtBookName.clear();
		txtName.clear();
		dtIssueDate.setValue(null);
		dtDueDate.setValue(null);
		dtReturnDate.setValue(LocalDate.now());
		txtSerialNumber.clear();
		lentBook = null;
		lenderAccount = null;
		lookedUp = false;
		txtBookID.setDisable(false);
		txtUserID.setDisable(false);
		txtSerialNumber.setDisable(false);
		dtReturnDate.setDisable(true);
		txtBookName.setDisable(true);
		txtName.setDisable(true);
		dtIssueDate.setDisable(true);
		dtDueDate.setDisable(true);
		dtReturnDate.setValue(LocalDate.now());
		btnReturnBook.setDisable(true);
		txtBookID.setStyle(null);
		txtSerialNumber.setStyle(null);
		txtUserID.setStyle(null);
		btnReturnBook.setStyle(null);
	}

	/**
	 * Show an appropriate alert to the user when an error occur
	 * 
	 * @param msg
	 */
	private void alertWarningMessage(String msg) {
		new Alert(AlertType.WARNING, msg, ButtonType.OK).show();
	}

	/**
	 * when the button pressed, return the book and update the DB
	 * 
	 * @param event
	 */
	@FXML
	void btnReturnBookPressed(ActionEvent event) {
		Alert returnConfirmation = new Alert(AlertType.CONFIRMATION, "Are you sure to return that book?",
				ButtonType.YES, ButtonType.CANCEL);
		if (returnConfirmation.showAndWait().get() == ButtonType.YES) {
			try { // update Book AvailableCopies += 1
				DatabaseController.updateBookAvailableCopies(lentBook.getBook(), 1);
				// update lent field to false in book copy table
				lentBook.getBookCopy().setLent(false);
				DatabaseController.updateBookCopy(lentBook.getBookCopy());
				lentBook.setReturnDate(dtReturnDate.getValue());
				lentBook.setReturned(true); // update lent Book in DB lentBook table
				if (((UserAccount) lenderAccount).isGraduate())
					DatabaseController.returnBook(lentBook, lenderAccount.getAccountID());// graduated
				else
					DatabaseController.returnBook(lentBook, 0);// Not graduated
				DatabaseController.addActivity(lenderAccount.getAccountID(),
						"Returned Book [Book ID: " + lentBook.getBook().getBookID() + "]");
				if (!lentBook.isLate()) {
					alertWarningMessage("Book was returned successfully");
				} else
					alertWarningMessage("Book was returned in late");
				btnClearPressed(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * open the return Form
	 * 
	 * @param primaryStage
	 * @throws Exception
	 */
	void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/ReturnForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Return Book");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * return to the previous window
	 * 
	 * @param event
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("User Main");
	}

	/**
	 * Initialize the form
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		btnReturnBook.setDisable(true);
		dtReturnDate.setEditable(false);
		txtBookName.setDisable(true);
		txtName.setDisable(true);
		dtIssueDate.setDisable(true);
		dtDueDate.setDisable(true);
		dtDueDate.setOnMouseClicked(e -> {
			if (!dtDueDate.isEditable())
				dtDueDate.hide();
		});
		dtIssueDate.setOnMouseClicked(e -> {
			if (!dtIssueDate.isEditable())
				dtIssueDate.hide();
		});
		dtReturnDate.setDisable(true);
		// set listener to check user id input
		txtUserID.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				txtUserID.setText(newValue.replaceAll("[^\\d]", ""));
				alertWarningMessage("The user ID must contain only numbers");
			}
			if (txtUserID.getLength() > 9) {
				txtUserID.setText(oldValue);
				alertWarningMessage("The ID must be 9 numbers");
			}
		});
		// set listener to check book id input
		txtBookID.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				txtBookID.setText(newValue.replaceAll("[^\\d]", ""));
				alertWarningMessage("Book ID must contain only numbers");
			}
		});
		// disable Book\User Lookup button until the user enters both the bookID and
		// UserID
		dtReturnDate.setValue(LocalDate.now());
		BooleanBinding booleanBind = txtBookID.textProperty().isEmpty().or(txtUserID.textProperty().isEmpty())
				.or(txtSerialNumber.textProperty().isEmpty());
		btnLookup.disableProperty().bind(booleanBind);

	}

}
