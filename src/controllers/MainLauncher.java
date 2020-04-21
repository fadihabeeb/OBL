/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.io.FileInputStream;
import java.util.Properties;

import client.ClientConnection;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * OBL System 2018-2019
 * 
 * Credits: -Adam Mahameed [GUI Design, LibraryServices, MySQLConnection,
 * ArchivedDataController, ActivityController, AccountDetailsController,
 * SceneController, UserLookupController, NotificationsController, Main forms
 * controllers] -Saleh Kasem [DatabaseController, ReturnController,
 * LoginController, SearchController, MySQLConnection, OrderController] -Fadi
 * Habeeb [AddBookController, EditBookController, ManageLibraryController] -Alaa
 * Grable [NewAccountController, ExtendLendController, ManualExtendController,
 * LendController, ReportsController] -Ahmad Qais [AccountDetailsController]
 */
public class MainLauncher extends Application {
	public static void main(String[] args) { // TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("@/../Client.properties");
			props.load(in);
			in.close();
			String host = props.getProperty("server.host");
			int port = Integer.parseInt(props.getProperty("server.port"));
			DatabaseController.InitiateClient(new ClientConnection(host, port));
			primaryStage.setOnCloseRequest(event -> DatabaseController.terminateClient());

			MainFormController aFrame = new MainFormController();
			aFrame.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("OPPS!!!\n\nSomthing Went Wrong, Open Form Faild.");
			alert.show();
		}
	}
}
