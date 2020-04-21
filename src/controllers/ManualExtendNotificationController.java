package controllers;

import java.time.LocalDate;
import java.util.ArrayList;

import entities.ManualExtend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManualExtendNotificationController {

	@FXML
	private ImageView imgBack;

	@FXML
	private TableView<ManualExtend> tableView;

	@FXML
	private TableColumn<ManualExtend, Integer> colBookID;

	@FXML
	private TableColumn<ManualExtend, Integer> colUserID;

	@FXML
	private TableColumn<ManualExtend, String> colWorkerName;

	@FXML
	private TableColumn<ManualExtend, LocalDate> colExtendDate;

	@FXML
	private TableColumn<ManualExtend, LocalDate> colDueDate;

	private ObservableList<ManualExtend> extendOList;
	
	/**
	 * close current stage
	 * @param event
	 */
	@FXML
	void imgBackClicked(MouseEvent event) {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Close stage

	}

	@FXML
	void initialize() {
		ArrayList<ManualExtend> extendList;
		extendList = DatabaseController.getManualExtendList();
		extendOList = FXCollections.observableArrayList(extendList);// notifications List
		colBookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
		colUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
		colWorkerName.setCellValueFactory(new PropertyValueFactory<>("workerName"));
		colExtendDate.setCellValueFactory(new PropertyValueFactory<>("extendDate"));
		colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		tableView.setItems(extendOList);
	}
	
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/ManualExtendNotificationForm.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = new Stage();
			stage.initOwner(primaryStage);
			stage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Manual Extend Notifications Panel");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
