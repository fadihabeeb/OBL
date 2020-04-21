/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

import entities.Account;
import entities.LibrarianAccount;
import entities.ManagerAccount;
import entities.Notification;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class NotificationsController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private Label txtUsername;

	@FXML
	private Label txtUserID;

	@FXML
	private TableView<Notification> tableView;

	@FXML
	private TableColumn<Notification, Timestamp> dateCol;

	@FXML
	private TableColumn<Notification, String> messageCol;

	@FXML
	private Button btnProccessNotification;

	@FXML
	private Button btnClearSelected;

	private static Account loggedAccount;
	private ObservableList<Notification> notificationsOlist;

	/**
	 * Clear selected item from DataView
	 * @param event
	 */
	@FXML
	void btnClearSelectedPressed(ActionEvent event) {
		Alert deleteConfirmation = new Alert(AlertType.CONFIRMATION,
				"Are you sure you want to remove the selected notification?(Can't be undone)", ButtonType.YES,
				ButtonType.CANCEL);
		if (!(tableView.getSelectionModel().isEmpty())) {
			if (deleteConfirmation.showAndWait().get() == ButtonType.YES) {
				Notification selectedNotf = tableView.getSelectionModel().getSelectedItem();
				DatabaseController.deleteNotfication(selectedNotf);
				tableView.getItems().remove(selectedNotf);
			}
		} else
			new Alert(AlertType.WARNING, "No notification is selected!", ButtonType.OK).show();
	}
	
	
	/**
	 * Proccess selected notification (LOCK NOTIFICATIONS ONLY CAN BE PROCCESSED)
	 * @param event
	 */
	@FXML
	void btnProccessNotificationPressed(ActionEvent event) {
		ButtonType suspendButton = new ButtonType("Keep Suspended");
		Alert lockConfirmation = new Alert(AlertType.CONFIRMATION,
				"The system has detected 3 delays for this user and awaiting locking account approval\nDo you want to lock it?\n[Keeping an account suspended will reset its delays]",
				ButtonType.YES, suspendButton, ButtonType.CANCEL);// Lock confirmation message

		Notification selectedNotf = tableView.getSelectionModel().getSelectedItem();

		// Parsing notification for accountID
		String accountID = (selectedNotf.getMessage().replaceAll("\\D+", ""));
		accountID = accountID.substring(0, accountID.length() - 1);
		/////
		lockConfirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		ButtonType choice = lockConfirmation.showAndWait().get();
		if (choice == ButtonType.YES) {
			if (DatabaseController.lockAccount(Integer.parseInt(accountID), true)) {
				new Alert(AlertType.INFORMATION, "Account has been locked successfully!").show();
				DatabaseController.deleteNotfication(selectedNotf);
				tableView.getItems().remove(selectedNotf);
			} else
				new Alert(AlertType.ERROR, "An error has occured!").show();
		}else if(choice == suspendButton)
		{
			if (DatabaseController.lockAccount(Integer.parseInt(accountID), false)) {
				new Alert(AlertType.INFORMATION, "Account has been kept as suspended successfully!\n Delays has been reset!").show();
				DatabaseController.deleteNotfication(selectedNotf);
				tableView.getItems().remove(selectedNotf);
			} else
				new Alert(AlertType.ERROR, "An error has occured!").show();
		}
	}
	
	/**
	 * Open manual extend form
	 * @param event
	 */
	@FXML
    void btnManualExtendClicked(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		ManualExtendNotificationController manExNot = new ManualExtendNotificationController();
		try {
			manExNot.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	/**
	 * Return to the previous stage
	 * @param event
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
	 * Initialize notifications for this user
	 */
	@FXML
	void initialize() {
		txtUserID.setText(String.valueOf(loggedAccount.getAccountID()));
		txtUsername.setText(loggedAccount.getFirstName() + " " + loggedAccount.getLastName());

		ArrayList<Notification> notificationsList;
		if (loggedAccount instanceof ManagerAccount)
			notificationsList = DatabaseController.getNotifications(1);// get data for manager
		else if (loggedAccount instanceof LibrarianAccount) {
			btnProccessNotification.setVisible(false);
			notificationsList = DatabaseController.getNotifications(2);// get data for librarian
		} else {
			btnProccessNotification.setVisible(false);
			notificationsList = DatabaseController.getNotifications(loggedAccount.getAccountID());// get data for user
		}
		notificationsOlist = FXCollections.observableArrayList(notificationsList);// notifications List
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
		tableView.setItems(notificationsOlist);

		// Set listener to tableView to enable Proccess Notification button only when
		// the message is a lock message
		btnProccessNotification.setDisable(true);
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (!tableView.getItems().isEmpty())
				if (tableView.getSelectionModel().getSelectedItem().isLock())
					btnProccessNotification.setDisable(false);
				else
					btnProccessNotification.setDisable(true);
			else
				btnProccessNotification.setDisable(true);
		});
		
		tableView.setRowFactory( tableView -> {
		    TableRow<Notification> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
		        	Notification rowData = row.getItem();
		            Alert notfMessage = new Alert(AlertType.INFORMATION, rowData.getMessage(), ButtonType.OK);
		            notfMessage.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		            notfMessage.show();
		        }
		    });
		    return row ;
		});
	}

	/**
	 * Startup GUI
	 * @param stage
	 * @param loggedAccount
	 */
	public void start(Stage stage, Account loggedAccount) {
		try {
			NotificationsController.loggedAccount = loggedAccount;
			FXMLLoader fxmlLoader= new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/NotificationsForm.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Notifications");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
