/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Account;
import entities.UserAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/**
 * User main window
 * @author Athl1n3
 *
 */
public class UserMainController implements Initializable {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label lblUsername;

	@FXML
	private Label lblStatus;

	@FXML
	private ImageView imgLogout;

	@FXML
	private ImageView imgSearch;
	
	@FXML
	private ImageView imgNotification;

	@FXML
	private ImageView imgHistory;

	@FXML
	private ImageView imgSettings;

	@FXML
	private ImageView imgExtendBook;

	private static Stage stage;
	private static Scene scene;

	private static UserAccount loggedAccount;

	@FXML
	void imgExtendBookClicked(MouseEvent event) {
		SceneController.push(scene);
		ExtendLendController ExtendLendForm = new ExtendLendController();
		try {
			ExtendLendForm.start(stage, loggedAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void imgHistoryClicked(MouseEvent event) {
		SceneController.push(scene);
		ActivityController ActivityForm = new ActivityController();
		try {
			ActivityForm.start(stage, loggedAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void imgLogoutClicked(MouseEvent event) {
		Alert confirmLogout = new Alert(AlertType.CONFIRMATION, "Are you sure you want to log out of this account?",
				ButtonType.YES, ButtonType.CANCEL);
		if (confirmLogout.showAndWait().get() == ButtonType.YES) {
			loggedAccount.setLogged(false);
			DatabaseController.logAccount(loggedAccount);
			DatabaseController.loggedAccount = null;
			Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
			// get the previous scene
			Scene scene = SceneController.pop();
			stage.setScene(scene);
			stage.setTitle("Main Form");
		}
	}

	@FXML
	void imgNotificationClicked(MouseEvent event)
	{
		SceneController.push(scene);
		NotificationsController notificationForm = new NotificationsController();
		try {
			 notificationForm.start(stage, loggedAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	void imgSearchClicked(MouseEvent event) {
		SceneController.push(scene);
		SearchController SearchForm = new SearchController();
		try {
			SearchForm.start(stage, loggedAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void imgSettingsClicked(MouseEvent event) {
		SceneController.push(scene);
		AccountDetailsController AccountDetailsForm = new AccountDetailsController();
		try {
			AccountDetailsForm.start(stage, loggedAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblUsername.setText(loggedAccount.getFirstName() + " " + loggedAccount.getLastName());

		switch (loggedAccount.getStatus()) {
		case Active:
			lblStatus.setTextFill(javafx.scene.paint.Color.GREEN);
			break;
		case Suspended:
			lblStatus.setTextFill(javafx.scene.paint.Color.rgb(153, 153, 0));
			break;
		}
		lblStatus.setText(loggedAccount.getStatus().toString());
	}

	void start(Stage primaryStage, Account loggedAccount) throws Exception {
		this.loggedAccount = (UserAccount) loggedAccount;
		stage = primaryStage;
		FXMLLoader fxmlLoader= new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/UserMainForm.fxml"));
		Parent root = fxmlLoader.load();
		scene = new Scene(root);
		primaryStage.setTitle("User Main");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
