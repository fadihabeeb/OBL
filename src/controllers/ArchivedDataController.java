
/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import entities.Archive;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 * @author Adam Mahameed
 * @version 1.2 [12.1.2019]
 * 
 */
public class ArchivedDataController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

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
	private TextField txtUserID;

	@FXML
	private TextField txtPassword;

	@FXML
	private TextField txtUsername;

	private Archive userArcData;
	private static int ID;

	/**
	 * Close form GUI
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void imgBackClicked(MouseEvent event) throws IOException {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
	}

	/**
	 * View user archived data
	 */
	@FXML
	void initialize() {
		userArcData = DatabaseController.getArchiveData(ID);
		if (userArcData != null) {
			txtUserID.setText(String.valueOf(userArcData.getUserId()));
			txtID.setText(String.valueOf(userArcData.getId()));
			txtUsername.setText(userArcData.getUsername());
			txtFirstName.setText(userArcData.getFirstname());
			txtLastName.setText(userArcData.getLastname());
			txtPassword.setText(userArcData.getPassword());
			txtMobileNum.setText(userArcData.getMobileNum());
			txtEmail.setText(userArcData.getEmail());
		} else
			new Alert(AlertType.ERROR, "An error has occured getting archived data!").show();

	}

	void start(Stage primaryStage, int ID) throws Exception {
		this.ID = ID;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/ArchivedDataForm.fxml"));
		Parent root = fxmlLoader.load();
		Stage stage = new Stage();
		stage.initOwner(primaryStage);
		stage.initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(root);
		stage.setTitle("User Archived Data");
		stage.setScene(scene);
		stage.show();
	}
}
