/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Account;
import entities.LibrarianAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
/**
 *
 * @author Adam Mahameed [Athl1n3]
 *
 */
public class UsersManagementController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private ImageView imgUserLookup;

	@FXML
	private ImageView imgCreateAccount;

	private static LibrarianAccount librarianAccount;

	@FXML
	void imgBackClicked(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Main Window");
	}

	/**
	 * Opens creating new account form
	 * @param event
	 */
	@FXML
	void imgCreateAccountClicked(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = ((Node) event.getSource()).getScene();
		SceneController.push(scene);
		// stage.initModality(Modality.APPLICATION_MODAL);
		NewAccountController newAccountForm = new NewAccountController();
		try {
			newAccountForm.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens user lookup form
	 * @param event
	 */
	@FXML
	void imgUserLookupClicked(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = ((Node) event.getSource()).getScene();
		SceneController.push(scene);
		// stage.initModality(Modality.APPLICATION_MODAL);
		UserLookupController userLookupForm = new UserLookupController();
		try {
			userLookupForm.start(stage, librarianAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void start(Stage primaryStage, Account librarian) throws Exception {
		librarianAccount = (LibrarianAccount) librarian;
		FXMLLoader fxmlLoader= new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/UsersManagementForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Users Management");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	@FXML
	void initialize() {

	}
}
