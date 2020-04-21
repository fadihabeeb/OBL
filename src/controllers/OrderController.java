/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import entities.Book;
import entities.BookOrder;
import entities.UserAccount;
import entities.UserAccount.accountStatus;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class OrderController implements Initializable {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private TextField txtBookName;

	@FXML
	private TextField txtBookType;

	@FXML
	private TextField txtBookID;

	@FXML
	private DatePicker dtOrderDate;

	@FXML
	private TextField txtUserID;

	@FXML
	private TextField txtName;

	@FXML
	private Label lblStatus;

	@FXML
	private Button btnPlaceOrder;

	private static Book orderedBook;
	
	
	/**
	 * pressing the place order button
	 * @param event
	 */
	@FXML
	void btnPlaceOrderPressed(ActionEvent event) {
		// check if the user status is Active
		if (!((UserAccount) DatabaseController.loggedAccount).getStatus().equals(accountStatus.Active)) {
			// show alert and exit
			showAlert("ERROR", "You Can't make an order since your acount is Suspended.", event);
		} else {
			// check if the user is already make an order for this book
			if (DatabaseController.checkExistingOrder(DatabaseController.loggedAccount.getAccountID(),
					orderedBook.getBookID()) || DatabaseController.ifExists("savedCopy", "userID = '" + DatabaseController.loggedAccount.getAccountID()
					+ "' AND bookID = '" + orderedBook.getBookID() + "'")) {
				// show alert and exit
				showAlert("WARNING", "You have already have an existing order for this book", event);
				
			} else if (DatabaseController.getCount("bookOrder", "bookID",
					String.valueOf(orderedBook.getBookID())) == orderedBook.getCopiesNumber()) {
				showAlert("WARNING", "There are already too many orders for this book!", event);
			} else {
				Timestamp currentTimeStamp = new Timestamp(new Date().getTime());
				BookOrder order = new BookOrder(DatabaseController.loggedAccount.getAccountID(),
						orderedBook.getBookID(), currentTimeStamp);
				DatabaseController.placeOrder(order);

				DatabaseController.addActivity(DatabaseController.loggedAccount.getAccountID(),
						"Ordered Book [Book ID: " + orderedBook.getBookID() + "]");
				// show alert and exit
				showAlert(
						"Order Placed Successfully", "book: " + orderedBook.getName() + "\nOrdered By: "
								+ DatabaseController.loggedAccount.getID() + "\nOrder Date: " + currentTimeStamp,
						event);
			}
		}
	}

	/**
	 * back to the previous window 
	 * @param event
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {

	}

	public void start(Book orderedBook) {
		OrderController.orderedBook = orderedBook;
		try {
			FXMLLoader fxmlLoader= new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/OrderForm.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Book Order Form");
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * initialize the order Form
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		txtBookID.setDisable(true);
		txtBookName.setDisable(true);
		txtBookType.setDisable(true);
		txtBookName.setDisable(true);
		dtOrderDate.setDisable(true);
		txtUserID.setDisable(true);
		txtName.setDisable(true);
		txtBookID.setText(String.valueOf(orderedBook.getBookID()));
		txtBookName.setText(orderedBook.getName());
		txtBookType.setText(orderedBook.getBookType().toString());
		txtBookName.setText(orderedBook.getName());
		dtOrderDate.setValue(LocalDate.now());
		txtUserID.setText(String.valueOf(DatabaseController.loggedAccount.getID()));
		txtName.setText(DatabaseController.loggedAccount.getFullName());
		//initialize the user status 
		if (((UserAccount) DatabaseController.loggedAccount).getStatus() == accountStatus.Active) {
			lblStatus.setText("Active");
		} else if (((UserAccount) DatabaseController.loggedAccount).getStatus().equals(accountStatus.Suspended)) {
			lblStatus.setText("Suspended");
		} else
			lblStatus.setText("Locked");
	}

	/**
	 * show information alert to user
	 * @param header
	 * @param content
	 * @param event
	 */
	public void showAlert(String header, String content, ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION, content, ButtonType.OK);
		alert.setHeaderText(header);
		if (alert.showAndWait().get() == ButtonType.OK)
			((Node) event.getSource()).getScene().getWindow().hide();
	}

}
