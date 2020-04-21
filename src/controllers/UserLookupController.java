/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import entities.Account;
import entities.Account.UserType;
import entities.LibrarianAccount;
import entities.UserAccount;
import entities.UserAccount.accountStatus;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This GUI is used to make a user lookup and manage all of his data
 * 
 * @author Adam Mahameed
 * @version 1.4 [16.1.2019]
 * 
 */
public class UserLookupController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button btnLock;

	@FXML
	private Label lblUserID;

	@FXML
	private Button btnSuspend;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblOnlineStatus;

	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtPassword;

	@FXML
	private CheckBox cbEditUser;

	@FXML
	private Button btnView;

	@FXML
	private Button btnClear;

	@FXML
	private Button btnViewHistory;

	@FXML
	private Button btnArchive;

	@FXML
	private TextField txtID;

	@FXML
	private TextField txtFirstName;

	@FXML
	private TextField txtLastName;

	@FXML
	private TextField txtMobileNum;

	@FXML
	private TextField txtEmail;

	@FXML
	private ImageView imgBack;

	@FXML
	private Button btnEditData;

	@FXML
	private DatePicker dtDatePicker;

	private static UserAccount lookupAccount;
	private static LibrarianAccount librarianAccount;

	/**
	 * Show looked up user archived data
	 * 
	 * @param event
	 */
	@FXML
	void btnArchivePressed(ActionEvent event) {
		if (txtID.isDisabled()) {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			ArchivedDataController archiveForm = new ArchivedDataController();
			try {
				archiveForm.start(stage, lookupAccount.getID());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			new Alert(AlertType.WARNING, "A user must be looked up first!", ButtonType.OK).show();
	}

	/**
	 * Clear layout to its original state
	 * 
	 * @param event
	 */
	@FXML
	void btnClearPressed(ActionEvent event) {
		txtUsername.clear();
		txtID.clear();
		txtFirstName.clear();
		txtLastName.clear();
		txtMobileNum.clear();
		txtEmail.clear();
		txtPassword.clear();
		lblStatus.setText("---");
		lblUserID.setText("---");
		txtID.setDisable(false);
		txtFirstName.setStyle(null);
		txtLastName.setStyle(null);
		txtMobileNum.setStyle(null);
		txtEmail.setStyle(null);
		txtUsername.setStyle(null);
		txtPassword.setStyle(null);
		cbEditUser.setSelected(false);
		lblOnlineStatus.setText("---");
		lookupAccount = null;
		dtDatePicker.setValue(null);
	}

	/**
	 * Edits user data according to the new inserted data
	 * 
	 * @param event
	 */
	@FXML
	void btnEditDataPressed(ActionEvent event) {
		Alert msg = new Alert(AlertType.CONFIRMATION, "Are you sure to update user data?", ButtonType.YES,
				ButtonType.CANCEL);
		if (msg.showAndWait().get() == ButtonType.YES) {
			txtFirstName.setStyle(null);
			txtLastName.setStyle(null);
			txtMobileNum.setStyle(null);
			txtEmail.setStyle(null);
			if (validateInput()) {
				lookupAccount.setUserName(txtUsername.getText());
				lookupAccount.setPassword(txtPassword.getText());
				lookupAccount.setFirstName(txtFirstName.getText());
				lookupAccount.setLastName(txtLastName.getText());
				lookupAccount.setMobileNum(txtMobileNum.getText());
				lookupAccount.setEmail(txtEmail.getText());
				LoadUserData();
				cbEditUser.setSelected(false);
				btnEditData.setDisable(true);
				DatabaseController.updateAccount(lookupAccount);
				new Alert(AlertType.INFORMATION, "User data was updated successfully!", ButtonType.OK).show();
			}
		}
	}

	/**
	 * Validate updated user data
	 * 
	 * @return true in case of a valid input
	 */
	private boolean validateInput() {
		Alert msg = new Alert(AlertType.ERROR, "", ButtonType.OK);// Prepare alert box
		msg.setHeaderText("Input Error");
		msg.setContentText("One or more of inputs are in an invalid format!");
		boolean validInput = true;
		////////////
		if (!txtFirstName.getText().isEmpty()) {
			for (char c : txtFirstName.getText().toCharArray())// Parse text field into chars array and validate
				if (Character.isDigit(c)) {
					msg.setContentText(msg.getContentText() + "\n*First name must contain letters only!");
					;
					txtFirstName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					validInput = false;
					break;
				}
		} else {
			msg.setContentText(msg.getContentText() + "\n*First name can't be empty!");
			txtFirstName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		}
		////////////
		if (!txtLastName.getText().isEmpty()) {
			for (char c : txtLastName.getText().toCharArray())// Parse text field into chars array and validate
				if (Character.isDigit(c)) {
					msg.setContentText(msg.getContentText() + "\n*Last name must contain letters only!");
					txtLastName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					validInput = false;
					break;
				}
		} else {
			msg.setContentText(msg.getContentText() + "\n*Last name can't be empty!");
			txtLastName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		}
		//////////
		if (!txtMobileNum.getText().isEmpty()) {
			for (char c : txtMobileNum.getText().toCharArray())// Parse text field into chars array and validate
				if (Character.isAlphabetic(c)) {
					msg.setContentText(msg.getContentText() + "\n*Mobile number must contain numbers only!");
					txtMobileNum.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					validInput = false;
					break;
				}
		} else {
			msg.setContentText(msg.getContentText() + "\n*Mobile number can't be empty!");
			txtMobileNum.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		}
		////////////
		if (txtUsername.getText().isEmpty()) {
			msg.setContentText(msg.getContentText() + "\n*Username can't be empty!");
			txtUsername.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		} else if (DatabaseController.ifExists("account", "username", txtUsername.getText())
				&& !lookupAccount.getUserName().equals(txtUsername.getText())) {
			msg.setContentText(msg.getContentText() + "\n*Username already exists!");
			txtUsername.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		}
		////////////
		if (txtPassword.getText().length() < 6) {
			msg.setContentText(msg.getContentText() + "\n*Password must be 6 characters minimum!");
			txtPassword.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		}
		////////////
		if (!txtEmail.getText().isEmpty()) {
			// Validate email format using
			String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
			java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
			java.util.regex.Matcher m = p.matcher(txtEmail.getText());
			if (!m.matches()) {
				msg.setContentText(msg.getContentText() + "\n*Invalid email format!");
				txtEmail.requestFocus();
				txtEmail.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				validInput = false;
			}
		} else {
			msg.setContentText(msg.getContentText() + "\n*Email can't be empty!");
			txtEmail.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			validInput = false;
		}
		////////////
		if (!validInput)
			msg.show();
		return validInput;// If all inputs are valid
	}

	/**
	 * Lock/Unlock user account
	 * 
	 * @param event
	 */
	@FXML
	void btnLockPressed(ActionEvent event) {
		Alert statMsg = new Alert(AlertType.INFORMATION, "", ButtonType.OK);

		if (txtID.isDisabled()) {
			if (lookupAccount.getStatus() == accountStatus.Locked) {
				Alert confirmationMsg = new Alert(AlertType.CONFIRMATION,
						"Unlocking a user will reset his delays\nAre you sure to perform this operation?",
						ButtonType.YES, ButtonType.CANCEL);
				if (confirmationMsg.showAndWait().get() == ButtonType.YES) {
					lookupAccount.setStatus(accountStatus.Active);
					DatabaseController.updateUserStatus(lookupAccount, true);
					statMsg.setContentText("User account was successfully set to 'Active' and delays has been reset");
					statMsg.show();
				}
			} else {
				lookupAccount.setStatus(accountStatus.Locked);
				DatabaseController.updateUserStatus(lookupAccount, false);
				statMsg.setContentText("User account was successfully set to 'Locked'");
				statMsg.show();
			}
			LoadUserData();
		} else {
			statMsg.setAlertType(AlertType.WARNING);
			statMsg.setContentText("A user must be looked up first!");
			txtID.requestFocus();
			statMsg.show();
		}
	}

	/**
	 * Suspend/Unsuspend user account
	 * 
	 * @param event
	 */
	@FXML
	void btnSuspendPressed(ActionEvent event) {
		int status = 0;
		Alert statMsg = new Alert(AlertType.INFORMATION, "", ButtonType.OK);

		if (txtID.isDisabled()) {
			if (lookupAccount.getStatus() == accountStatus.Suspended) {
				lookupAccount.setStatus(accountStatus.Active);
				dtDatePicker.getEditor().clear();
				DatabaseController.deleteScheduledSuspension(lookupAccount.getAccountID());
				DatabaseController.updateUserStatus(lookupAccount, false);
				status = 0;
			} else {
				if (dtDatePicker.getValue() == null) {
					lookupAccount.setStatus(accountStatus.Suspended);
					DatabaseController.updateUserStatus(lookupAccount, false);
					status = 1;
				} else {
					if (dtDatePicker.getValue().isBefore(LocalDate.now())
							|| dtDatePicker.getValue().isEqual(LocalDate.now()))
						status = 3;
					else {
						lookupAccount.setStatus(accountStatus.Suspended);
						DatabaseController.addScheduledSuspension(lookupAccount.getAccountID(), dtDatePicker.getValue());
						DatabaseController.updateUserStatus(lookupAccount, false);
						status = 2;
					}
				}

			}
			
			switch(status)
			{
			case 0:
				statMsg.setContentText("User account was successfully set to 'Active'");
				dtDatePicker.setValue(null);
				break;
			case 1:
				statMsg.setContentText("User account was successfully set to 'Suspended'\n[No schedueled suspension was determined]");
				break;
			case 2:
				statMsg.setContentText("User account was successfully set to 'Suspended'\n Suspended until: ["+dtDatePicker.getValue()+"]");
				break;
				default:
					statMsg.setContentText("Schedueled suspension date must be a valid date starting from tomorrow!");
					dtDatePicker.getEditor().clear();
					dtDatePicker.setValue(null);
					break;		
			}
			
			LoadUserData();
		} else {
			statMsg.setAlertType(AlertType.WARNING);
			statMsg.setContentText("A user must be looked up first!");
			txtID.requestFocus();
		}
		statMsg.show();
	}

	/**
	 * Look up for user
	 * 
	 * @param event
	 */
	@FXML
	void btnView(ActionEvent event) {
		boolean userFound = false;
		if (txtID.getText().isEmpty()) {
			Alert msg = new Alert(AlertType.WARNING, "User ID must be inserted", ButtonType.OK);
			msg.show();
		} else {
			if (txtID.getText().length() == 9) {
				try {
					Integer.parseInt((txtID.getText()));
					Account acc = DatabaseController.getAccount(Integer.parseInt(txtID.getText()));
					if (acc != null)
						userFound = true;
					if (acc instanceof UserAccount)
						lookupAccount = (UserAccount) acc;
					if (userFound == true) {
						if (lookupAccount != null) {
							LoadUserData();
							txtID.setDisable(true);
						} else
							new Alert(AlertType.WARNING, "Unable to lookup for a librarian/manager account!",
									ButtonType.OK).show();
					} else
						new Alert(AlertType.WARNING, "User doesn't exist!", ButtonType.OK).show();
				} catch (NumberFormatException exc) {
					new Alert(AlertType.WARNING, "ID must contain numbers only", ButtonType.OK).show();
				}
			} else
				new Alert(AlertType.WARNING, "ID must be 9 numbers only!", ButtonType.OK).show();
		}
	}

	/**
	 * View user activity history
	 * 
	 * @param event
	 */
	@FXML
	void btnViewHistoryPressed(ActionEvent event) {
		if (txtID.isDisabled()) {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = ((Node) event.getSource()).getScene();
			SceneController.push(scene);
			ActivityController activityForm = new ActivityController();
			try {
				activityForm.start(stage, lookupAccount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			new Alert(AlertType.WARNING, "A user must be looked up first!", ButtonType.OK).show();
	}

	/**
	 * Go back to previous page
	 * 
	 * @param event
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Users Management");
	}

	/**
	 * Initialize GUI layout and checkbox event handler and initialize gui depending
	 * on user type
	 */
	@FXML
	void initialize() {
		if (librarianAccount.getUserType() == UserType.Librarian) {
			btnLock.setVisible(false);
			btnSuspend.setVisible(false);
			btnArchive.setVisible(false);
			dtDatePicker.setVisible(false);
		}
		lblStatus.setText("---");
		btnEditData.setDisable(true);
		cbEditUser.setOnAction(new EventHandler<ActionEvent>() {// Edit user check box event handler
			@Override
			public void handle(ActionEvent event) {
				if (txtID.isDisabled()) {
					btnEditData.setDisable(cbEditUser.isSelected() ? false : true);
					txtFirstName.setEditable(cbEditUser.isSelected() ? true : false);
					txtLastName.setEditable(cbEditUser.isSelected() ? true : false);
					txtUsername.setEditable(cbEditUser.isSelected() ? true : false);
					txtPassword.setEditable(cbEditUser.isSelected() ? true : false);
					txtMobileNum.setEditable(cbEditUser.isSelected() ? true : false);
					txtEmail.setEditable(cbEditUser.isSelected() ? true : false);
					if (cbEditUser.isSelected() == false && lookupAccount != null)// Revert looked up user data
						LoadUserData();
				} else {
					new Alert(AlertType.WARNING, "A user must be looked up first!", ButtonType.OK).show();
					cbEditUser.setSelected(false);
				}
			}
		});
	}

	/**
	 * Start up form
	 * 
	 * @param primaryStage
	 * @param librarian
	 * @throws Exception if starting fails
	 */
	void start(Stage primaryStage, Account librarian) throws Exception {
		librarianAccount = (LibrarianAccount) librarian;
		FXMLLoader fxmlLoader= new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/UserLookupForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("User lookup");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * Load user account data into GUI corresponding text fields
	 */
	void LoadUserData() {
		txtUsername.setText(lookupAccount.getUserName());
		txtPassword.setText(lookupAccount.getPassword());
		txtFirstName.setText(lookupAccount.getFirstName());
		txtLastName.setText(lookupAccount.getLastName());
		txtMobileNum.setText(String.valueOf(lookupAccount.getMobileNum()));
		txtEmail.setText(String.valueOf(lookupAccount.getEmail()));
		lblUserID.setText(String.valueOf(lookupAccount.getAccountID()));
		switch (lookupAccount.getStatus()) {
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
		if (lookupAccount.isLogged()) {
			lblOnlineStatus.setTextFill(javafx.scene.paint.Color.GREEN);
			lblOnlineStatus.setText("User is Online");
		} else {
			lblOnlineStatus.setTextFill(javafx.scene.paint.Color.RED);
			lblOnlineStatus.setText("User is Offline");
		}
		lblStatus.setText(lookupAccount.getStatus().toString());
		
		LocalDate schedueledDate = DatabaseController.getSchedueledSuspension(lookupAccount.getAccountID());
		if(schedueledDate != null)
			dtDatePicker.setValue(schedueledDate);
	}
}