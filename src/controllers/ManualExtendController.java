/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import entities.Account;
import entities.Book.bookType;
import entities.LentBook;
import entities.LibrarianAccount;
import entities.ManualExtend;
import entities.UserAccount;
import entities.UserAccount.accountStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * 
 * @author Alaa Grable
 * @version 1.0 [17.1.2019]
 * 
 */

public class ManualExtendController {

	UserAccount acc;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private TableView<LentBook> tableView;

	@FXML
	private TableColumn<LentBook, String> bookNameCol;

	@FXML
	private TableColumn<LentBook, String> bookEditionCol;

	@FXML
	private TableColumn<LentBook, String> bookAuthorCol;

	@FXML
	private TableColumn<LentBook, String> bookTypeCol;

	@FXML
	private TableColumn<LentBook, LocalDate> issuedDateCol;

	@FXML
	private TableColumn<LentBook, LocalDate> dueDateCol;

	@FXML
	private TableColumn<LentBook, String> bookDetails;

	@FXML
	private TextField txtUserID;

	@FXML
	private Button btnUserLookup;

	@FXML
	private TextField txtID;

	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtName;

	@FXML
	private Label lblStatus;

	@FXML
	private Button btnExtendLend;

	private LibrarianAccount loggedLibAccount;

	/**
	 * Validate if the book that has been selected to extend the lend duration can
	 * be extended. if and only if the extension has been successful , an alert
	 * message will be displayed. Otherwise an error alert message will be
	 * displayed.
	 * 
	 * @param event - on pressing the 'Extend Lend' button.
	 */
	@FXML
	void btnExtendLendPressed(ActionEvent event) {
		// get the selected book from the tableView
		LentBook selectedBook = tableView.getSelectionModel().getSelectedItem();

		// validate if the book type is equal to "wanted" or not
		if (selectedBook.getBook().getBookType().equals(bookType.Wanted)) {
			// if the book type is "Wanted" then let the user know that he can't extend the
			// book return time
			alertWarningMessage(
					"The book " + selectedBook.getBook().getName() + " is a 'Wanted'\n book and cannot be extended.");
		} else {
			// validate if there is a week or less to return that book
			if ((LocalDate.now().isBefore(selectedBook.getDueDate().minusWeeks(1))) == true
					|| (LocalDate.now().isEqual(selectedBook.getDueDate().minusWeeks(1))) == true) {
				// if not then let the user know that he can't extend the book return time
				alertWarningMessage(
						"You have more than 1 week left to return this book, therefore you can extend this book returning time.");
			} else {
				// validate if the orders on that book is lesser than the actual available
				// copies in the library
				if (selectedBook.getBook().getAvailableCopies() <= selectedBook.getBook().getBookOrders()) {
					// if not , then let the user know that he can't extend the book return time
					alertWarningMessage("There is a lot of orders on that book , \nTherefore the book " + "'"
							+ selectedBook.getBook().getName() + "'" + " cannot be extended.");
				} else {
					// extend the book return time to 1 more weeks
					boolean update = false;
					if (selectedBook.isLate()) {
						if (new Alert(AlertType.WARNING,
								"Book is already in a late return\n Are you sure to extend return date 1 week from today?",
								ButtonType.YES, ButtonType.CANCEL).showAndWait().get() == ButtonType.YES) {
							selectedBook.setDueDate(LocalDate.now().plusWeeks(1));
							update = true;
						}
					} else {
						selectedBook.setDueDate(selectedBook.getDueDate().plusWeeks(1));
						update = true;
					}

					if (update) {
						if (DatabaseController.updateLentBook(selectedBook)) {
							tableView.refresh();
							ManualExtend extendLog = new ManualExtend(selectedBook.getBook().getBookID(),
									acc.getAccountID(),
									loggedLibAccount.getFirstName() + " " + loggedLibAccount.getLastName(),
									LocalDate.now(), selectedBook.getDueDate());
							DatabaseController.addManualExtend(extendLog);
							// let the user know that the return time for the his book has been extended
							// successfully
							new Alert(AlertType.INFORMATION, "The book" + selectedBook.getBook().getName()
									+ " Due time has been extended successfully.", ButtonType.OK).show();
						} else
							new Alert(AlertType.ERROR, "An error has occured!\n Executing extend failed!",
									ButtonType.OK).show();
					}
				}
			}
		}
	}

	/**
	 * Validate if the inserted user ID exists in the DB or not. if and only if the
	 * user ID exists in the DB , all of his information will be displayed.
	 * Otherwise an error alert will be displayed.
	 * 
	 * @param event - on pressing the 'Extend Lend' button.
	 */
	@FXML
	void btnUserLookupPressed(ActionEvent event) {

		// get the inputed ID
		String usrID = txtID.getText();

		Account accAbs = DatabaseController.getAccount(Integer.parseInt(usrID));
		if (accAbs != null) {
			if (accAbs instanceof UserAccount) {
				acc = (UserAccount) accAbs;
				// display the user details according to the inserted ID
				txtUserID.setText(String.valueOf(acc.getAccountID()));
				txtUserID.setDisable(false);
				txtUsername.setDisable(false);
				txtName.setDisable(false);
				txtUsername.setText(acc.getUserName());
				txtName.setText(acc.getFirstName() + " " + acc.getLastName());
				lblStatus.setText(String.valueOf(acc.getStatus()));
				switch (acc.getStatus()) {
				case Active:
					lblStatus.setTextFill(javafx.scene.paint.Color.GREEN);
					break;
				case Suspended:
					lblStatus.setTextFill(javafx.scene.paint.Color.rgb(153, 153, 0));
					break;
				case Locked:
					lblStatus.setTextFill(javafx.scene.paint.Color.RED);
					break;
				}
				txtID.setStyle(null);
				// get the books of the user as an observableList to display it in the table
				ObservableList<LentBook> list = getLentBookList(usrID);
				// display the data in the tableView
				tableView.setItems(list);
				if (acc.getStatus().equals(accountStatus.Active)) {
					btnExtendLend.disableProperty()
							.bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
					btnExtendLend.setText("Extend Book Lend");
				} else {
					btnExtendLend.setText(acc.getStatus().toString() + " Account");
					btnExtendLend.setDisable(true);
				}
			} else
				alertWarningMessage("Can't perform this action for librarian account!");
		} else {
			alertWarningMessage("User doesn't exist!");
			txtID.requestFocus();
			txtID.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			txtUserID.clear();
			txtUsername.clear();
			txtName.clear();
			txtUserID.setDisable(true);
			txtUsername.setDisable(true);
			txtName.setDisable(true);
			lblStatus.setText("---");
			lblStatus.setTextFill(javafx.scene.paint.Color.BLACK);
			tableView.getItems().clear();
		}
	}

	/**
	 * Initialise the 'Manual Extend Lend' stage.
	 */
	@FXML
	void initialize() {
		loggedLibAccount = (LibrarianAccount) DatabaseController.loggedAccount;
		// a listener to validate if the ID length is not greater than 9 digits and if
		// it's only contain numbers
		txtID.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				txtID.setText(newValue.replaceAll("[^\\d]", ""));
				alertWarningMessage("The ID must contain only numbers");
			}
			if (txtID.getLength() > 9) {
				txtID.setText(oldValue);
				alertWarningMessage("The ID must be 9 numbers");
			}
		});
		txtUserID.setDisable(true);
		txtUsername.setDisable(true);
		txtName.setDisable(true);
		// Defines how to fill data for each cell
		bookNameCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getName());
			}
		});
		bookEditionCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getEdition());
			}
		});
		bookAuthorCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getAuthor());
			}
		});
		bookTypeCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getBookType().toString());
			}
		});
		issuedDateCol.setCellValueFactory(new PropertyValueFactory<LentBook, LocalDate>("IssueDate"));
		dueDateCol.setCellValueFactory(new PropertyValueFactory<LentBook, LocalDate>("DueDate"));
		btnExtendLend.setDisable(true);

		// Enable UserLookUp button only when the user ID textfield is not empty
		BooleanBinding booleanBind = txtID.textProperty().isEmpty();
		btnUserLookup.disableProperty().bind(booleanBind);

	}

	/**
	 * Gets all the lentBooks for the user as an ObservableList
	 * 
	 * @return ObservableList LentBooks list
	 */
	private ObservableList<LentBook> getLentBookList(String userID) {

		// create an observablelist that contains the user let books

		ObservableList<LentBook> list = FXCollections
				.observableArrayList(DatabaseController.getLentBookList(acc.getAccountID()));
		// return the observablelist
		return list;
	}

	/**
	 * Load the 'Manual Extend Lend' stage after initialising it.
	 * 
	 * @param primaryStage - the stage for display.
	 */
	void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/ManualExtendForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Manual Extend Lend book");
		stage.sizeToScene();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Show an appropriate alert to the user when an error or a warning occurs.
	 * 
	 * @param msg
	 */
	private void alertWarningMessage(String msg) {
		new Alert(AlertType.WARNING, msg, ButtonType.OK).show();
	}

	/**
	 * Close this stage and get back to the previous stage.
	 * 
	 * @param event - on pressing the 'back(image)' button.
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Main");
	}
}