/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Account;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * User account details GUI
 * 
 * @author Adam Mahameed [Athl1n3], Ahmed Qais
 *
 */
public class AccountDetailsController implements Initializable {

	@FXML
	private TextField txtUserID;

	@FXML
	private TextField txtFirstName;

	@FXML
	private TextField txtLastName;

	@FXML
	private TextField txtMobileNum;

	@FXML
	private TextField txtEmail;

	@FXML
	private Label lblUserID;

	@FXML
	private Label lblStatus;

	@FXML
	private Button btnApplyChanges;

	@FXML
	private TextField txtUsername;

	@FXML
	// private TextField txtVerPassword;
	private PasswordField txtVerPassword;

	@FXML
	// private TextField txtPassword;
	private PasswordField txtPassword;

	@FXML
	private Button btnUpdateLogin;

	private static UserAccount loggedAccount;

	/**
	 * Apply data changes to user account
	 * 
	 * @param event
	 */
	@FXML
	void btnApplyChangesPressed(ActionEvent event) {
		Alert msg = new Alert(AlertType.CONFIRMATION, "Are you sure to update user details?", ButtonType.YES,
				ButtonType.CANCEL);
		clearStyles();
		if (msg.showAndWait().get() == ButtonType.YES) {
			if (!(loggedAccount.getFirstName().equals(txtFirstName.getText())
					&& loggedAccount.getLastName().equals(txtLastName.getText())
					&& loggedAccount.getMobileNum().equals(txtMobileNum.getText())
					&& loggedAccount.getEmail().equals(txtEmail.getText()))) {
				if (validateInput()) {
					loggedAccount.setFirstName(txtFirstName.getText());
					loggedAccount.setLastName(txtLastName.getText());
					loggedAccount.setMobileNum(txtMobileNum.getText());
					loggedAccount.setEmail(txtEmail.getText());
					DatabaseController.updateAccount(loggedAccount);
					new Alert(AlertType.INFORMATION, "User details were updated successfully!", ButtonType.OK).show();
				}
			} else
				new Alert(AlertType.WARNING, "No changes detected!\nNo update is needed", ButtonType.OK).show();
		}
	}

	/**
	 * Update user login details
	 * 
	 * @param event
	 */
	@FXML
	void btnUpdateLoginPressed(ActionEvent event) {
		boolean flag = false;
		Alert msg = new Alert(AlertType.CONFIRMATION, "Are you sure to update Login details?", ButtonType.YES,
				ButtonType.CANCEL);
		clearStyles();
		if (msg.showAndWait().get() == ButtonType.YES)
			if (validateUsernamePasswordInput()) {
				if (!loggedAccount.getUserName().equals(txtUsername.getText()) && !txtUsername.getText().isEmpty()) {
					loggedAccount.setUserName(txtUsername.getText());
					flag = true;
				}
				if (!loggedAccount.getPassword().equals(txtPassword.getText()) && !txtPassword.getText().isEmpty()) {
					loggedAccount.setPassword(txtPassword.getText());
					flag = true;
				}
				if (flag) {
					DatabaseController.updateAccount(loggedAccount);
					new Alert(AlertType.INFORMATION, "Login details updated successfully!", ButtonType.OK).show();
				} else
					new Alert(AlertType.WARNING, "No changes detected!", ButtonType.OK).show();
			}
	}

	/**
	 * Go back to previous scene
	 * 
	 * @param event
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Main");
	}

	/**
	 * Initialize the Account details
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblUserID.setText(String.valueOf(loggedAccount.getAccountID()));
		txtUserID.setText(String.valueOf(loggedAccount.getID()));
		txtUserID.setDisable(true);
		txtFirstName.setText(loggedAccount.getFirstName());
		txtLastName.setText(loggedAccount.getLastName());
		txtMobileNum.setText(loggedAccount.getMobileNum());
		txtEmail.setText(loggedAccount.getEmail());

		switch (loggedAccount.getStatus()) {
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
		lblStatus.setText(loggedAccount.getStatus().toString());

		// disable the btnApplyChanges button until all Textfields are not empty
		BooleanBinding booleanBind = txtFirstName.textProperty().isEmpty().or(txtLastName.textProperty().isEmpty())
				.or(txtMobileNum.textProperty().isEmpty()).or(txtEmail.textProperty().isEmpty());
		btnApplyChanges.disableProperty().bind(booleanBind);

		// login details
		txtUsername.setText(loggedAccount.getUserName());

		// disable the btnUpdateLogin button until all passwords textfields are not
		// empty
		BooleanBinding btnUpdateLoginBind = txtUsername.textProperty().isEmpty();
		btnUpdateLogin.disableProperty().bind(btnUpdateLoginBind);
	}

	/**
	 * Validate updated user data
	 * 
	 * @return true in case of a valid input
	 */
	@FXML
	public boolean validateInput() {
		Alert msg = new Alert(AlertType.ERROR, "", ButtonType.OK);// Prepare alert box
		msg.setHeaderText("Input Error");
		msg.setContentText("One or more of inputs are in an invalid format!");
		boolean validInput = true;
		////////////
		if (!txtFirstName.getText().isEmpty()) {
			for (char c : txtFirstName.getText().toCharArray())// Parse text field into chars array and validate
				if (Character.isDigit(c)) {
					msg.setContentText(msg.getContentText() + "\n*First name must contain letters only!");
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
			if (txtMobileNum.getText().length() > 10) {
				msg.setContentText(msg.getContentText() + "\n*Mobile number must contain 10 digits maximum!");
				txtMobileNum.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				validInput = false;
			} else {
				for (char c : txtMobileNum.getText().toCharArray())// Parse text field into chars array and validate
					if (Character.isAlphabetic(c)) {
						msg.setContentText(msg.getContentText() + "\n*Mobile number must contain numbers only!");
						txtMobileNum.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
						validInput = false;
						break;
					}
			}
		} else {
			msg.setContentText(msg.getContentText() + "\n*Mobile number can't be empty!");
			txtMobileNum.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
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
		/////////////
		if (!validInput)
			msg.show();
		return validInput;// If all inputs are valid

	}

	/**
	 * validate data input
	 * 
	 * @return boolean
	 */
	private boolean validateUsernamePasswordInput() {
		Alert msg = new Alert(AlertType.ERROR, "", ButtonType.OK);// Prepare alert box
		msg.setHeaderText("Input Error");
		if (txtUsername.getText().isEmpty()) {
			msg.setContentText("*Username can't be empty!");
			txtUsername.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			return false;
		} else if (DatabaseController.ifExists("account", "username", txtUsername.getText())
				&& !loggedAccount.getUserName().equals(txtUsername.getText())) {
			msg.setContentText("*Username already exists!");
			txtUsername.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			return false;
		}
		if (txtPassword.getLength() < 6 && !txtPassword.getText().isEmpty()) {
			msg.setContentText("Password length must be at least 6 character !");
			msg.show();
			txtUserID.requestFocus();
			txtPassword.clear();
			txtVerPassword.clear();
			return false;
		}

		if (!txtPassword.getText().equals(txtVerPassword.getText()) && !txtPassword.getText().isEmpty()) {
			msg.setContentText("Password are not matching !");
			msg.show();
			txtUserID.requestFocus();
			txtPassword.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			txtVerPassword.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			return false;
		}
		txtPassword.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
		txtVerPassword.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");

		// success confirm password
		return true;
	}

	void start(Stage primaryStage, Account acc) throws Exception {
		loggedAccount = (UserAccount) acc;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/AccountDetailsForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Account Details");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * Clear text fields
	 */
	private void clearStyles() {
		txtFirstName.setStyle(null);
		txtLastName.setStyle(null);
		txtMobileNum.setStyle(null);
		txtEmail.setStyle(null);
		txtUsername.setStyle(null);
		txtPassword.setStyle(null);
		txtVerPassword.setStyle(null);
	}

}
