/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Account;
import entities.LibrarianAccount;
import entities.ManagerAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LibraryManagementController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private ImageView imgManageBooks;

	@FXML
	private ImageView imgReports;

	private static LibrarianAccount loggedLibAccount;
	private static Stage stage;
	private static Scene scene;

	@FXML
	void imgBackClicked(MouseEvent event) {
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Main");
	}

	@FXML
	void imgManageBooksClicked(MouseEvent event) {
		SceneController.push(scene);
		// stage.initModality(Modality.APPLICATION_MODAL);
		ManageLibraryController ManageLibraryForm = new ManageLibraryController();
		try {
			ManageLibraryForm.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void imgReportsClicked(MouseEvent event) {
		SceneController.push(scene);
		// stage.initModality(Modality.APPLICATION_MODAL);
		ReportsController ReportsForm = new ReportsController();
		try {
			ReportsForm.start(stage, loggedLibAccount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void start(Stage primaryStage, Account loggedLibAccount) throws Exception {
		this.loggedLibAccount = (LibrarianAccount) loggedLibAccount;
		stage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/LibraryManagmentForm.fxml"));
		Parent root = fxmlLoader.load();
		scene = new Scene(root);
		primaryStage.setTitle("Library Management");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	@FXML
	void initialize() {
		if (!(loggedLibAccount instanceof ManagerAccount)) {
				imgReports.setImage(new Image("/images/ReportsGS.png"));
				imgReports.setDisable(true);
		}

	}
}
