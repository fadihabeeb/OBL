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
import entities.UserAccount;
import entities.UserAccount.accountStatus;
import javafx.beans.binding.Bindings;
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

public class ExtendLendController {

	LentBook lntBook;

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
	private TableColumn<LentBook, LocalDate> bookIssuedDate;

	@FXML
	private TableColumn<LentBook, LocalDate> bookDueDate;

	@FXML
	private TableColumn<LentBook, String> bookDetails;

	@FXML
	private Label txtUsername;

	@FXML
	private Label txtUserID;

	@FXML
	private Button btnExtendBook;

	private static UserAccount loggedAccount;

	/**
	 * Validate if the book that has been selected to extend the lend duration can be extended.
	 * if and only if the extension has been successful , an alert message will be displayed.
	 * Otherwise an error alert message will be displayed.
	 * @param event - on pressing the 'Extend Lend' button.
	 */
	@FXML
	void btnExtendBookPressed(ActionEvent event) {
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
					if (selectedBook.isLate())
						new Alert(AlertType.WARNING, "Book is already in a late return and cannot be extended",
								ButtonType.OK).show();
					else {
						selectedBook.setDueDate(selectedBook.getDueDate().plusWeeks(1));
						update = true;
					}

					if (update) {
						if (DatabaseController.updateLentBook(selectedBook)) {
							tableView.refresh();
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
	 * Initialise the 'Extend Lend' stage.
	 */
	@FXML
	void initialize() {

		txtUsername.setText(loggedAccount.getFirstName() + " " + loggedAccount.getLastName());
		txtUserID.setText(String.valueOf(loggedAccount.getAccountID()));

		// Defines how to fill data for each cell
		bookNameCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getName());
			}
		});
		bookAuthorCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getAuthor());
			}
		});
		bookEditionCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getEdition());
			}
		});
		bookTypeCol.setCellValueFactory(new Callback<CellDataFeatures<LentBook, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<LentBook, String> c) {
				return new SimpleStringProperty(c.getValue().getBook().getBookType().toString());
			}
		});
		bookIssuedDate.setCellValueFactory(new PropertyValueFactory<LentBook, LocalDate>("IssueDate"));
		bookDueDate.setCellValueFactory(new PropertyValueFactory<LentBook, LocalDate>("DueDate"));

		// get the books of the user as an observableList to display it in the table
		ObservableList<LentBook> list = getLentBookList();
		// display the data in the tableView
		tableView.setItems(list);
		// Enable the ExtendBook button only when an item is selected from the tableView
		if (loggedAccount.getStatus() == accountStatus.Active) {
			btnExtendBook.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
			btnExtendBook.setText("Extend Book Lend");
		} else {
			btnExtendBook.setText("Suspended Account");
			btnExtendBook.setDisable(true);
		}
	}

	/**
	 * Gets all the lentBooks for the user as an ObservableList
	 * @return ObservableList LentBooks list
	 */
	private ObservableList<LentBook> getLentBookList() {

		// create an observablelist that contains the user let books
		ObservableList<LentBook> list = FXCollections
				.observableArrayList(DatabaseController.getLentBookList(loggedAccount.getAccountID()));

		// return the observablelist
		return list;
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
	 * Load the 'Extend Lend' stage after initialising it.
	 * @param primaryStage - the stage for display.
	 */
	void start(Stage stage, Account loggedAccount) throws Exception {
		this.loggedAccount = (UserAccount) loggedAccount;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/ExtendLendForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Extend Lend book");
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