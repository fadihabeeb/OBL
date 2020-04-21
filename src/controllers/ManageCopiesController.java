/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import entities.Book;
import entities.BookCopy;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManageCopiesController {
	Book editedBook;

	@FXML
	private ImageView imgBack;

	@FXML
	private Label lblBookName; 

	@FXML
	private Label lblBookID;

	@FXML
	private TableView<BookCopy> tableView;

	@FXML
	private TableColumn<BookCopy, String> colSerialNumber;

	@FXML
	private TableColumn<BookCopy, String> colPurchaseDate;

	@FXML
	private TableColumn<BookCopy, String> colLendStatus;

	@FXML
	private DatePicker dtPurchaseDate;

	@FXML
	private TextField txtSerialNumber;

	@FXML
	private Button btnAddCopy;

	@FXML
	private Button btnDeleteCopy;

	private static Book selectedBook;

	/**
	 * Validates if the selected book copy is already exists in DB.
	 * Displays a succession alert if and only if the book copy has been added into the DB successfully.
	 * Otherwise displays an error alert.
	 * @param event - when pressing the 'Add Copy' button.
	 */
	@FXML
void btnAddCopyPressed(ActionEvent event) {
		
		if(DatabaseController.getBookCopy(Integer.toString(selectedBook.getBookID()), txtSerialNumber.getText())==null) {
		BookCopy newCopy = new BookCopy(selectedBook.getBookID(), txtSerialNumber.getText(), dtPurchaseDate.getValue(),
				false);
		DatabaseController.addBookCopy(newCopy);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Succsess");
		alert.setHeaderText("The book has been added successfully");
		alert.showAndWait();
		initialize();
		}
		
		else
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("The copy is exist!");
			alert.showAndWait();
		}
	}

	/**
	 * Deletes the selected copy of the selected book and update the DB.
	 * Displays a succession alert if and only if the book copy has been deleted successfully from DB.
	 * Otherwise displays an error alert.
	 * @param event - when pressing the 'Delete Copy' button.
	 */
	@FXML
	void btnDeleteCopyPressed(ActionEvent event) {

		BookCopy selectedForDelete = (BookCopy) tableView.getSelectionModel().getSelectedItem();
		try {

			if (selectedForDelete == null)
				throw new Exception();
			else {
				Alert confirmation = new Alert(AlertType.CONFIRMATION);
				confirmation.setTitle("Confirmation");
				confirmation.setHeaderText("Are you sure want to delete this book");
				// confirmation.setContentText("Select a book for delete!");
				confirmation.showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						DatabaseController.deleteBookCopy(selectedForDelete.getBookID(), selectedForDelete.getSerialNumber());
						initialize();
					}
				});

			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("No book has selected");
			alert.setContentText("Select a book for delete!");
			alert.showAndWait();
		}

	}

	/**
	 * Close this stage and get back to the previous stage.
	 * @param event - on pressing the 'back(image)' button.
	 */
	@FXML
	void imgBackClicked(MouseEvent event) throws IOException {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Close stage
	}

	public void start(Stage primaryStage, Book selectedBook) {
		try {
			this.selectedBook = selectedBook;
			FXMLLoader fxmlLoader= new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/ManageCopiesForm.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = new Stage();
			stage.initOwner(primaryStage);
			stage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Manage compies Form");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialise the 'Manage Copies' stage.
	 */
	@FXML
	void initialize() {
		DatabaseController.addTextLimiter(txtSerialNumber, 32,"Serial number","character");
		lblBookName.setText(selectedBook.getName());
		lblBookID.setText(Integer.toString(selectedBook.getBookID()));

		dtPurchaseDate.setValue(LocalDate.now());
		ArrayList<BookCopy> copiesList = new ArrayList<BookCopy>();
		copiesList = DatabaseController.getbookCopyList(selectedBook.getBookID());
		if (copiesList == null)
			return;
		colSerialNumber.setCellValueFactory(new PropertyValueFactory<BookCopy, String>("SerialNumber"));
		colPurchaseDate.setCellValueFactory(new PropertyValueFactory<BookCopy, String>("purchaseDate"));
		colLendStatus.setCellValueFactory(new PropertyValueFactory<BookCopy, String>("lent"));
		tableView.setItems(getCopies());

		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(txtSerialNumber.textProperty());

			}

			// this function return true if at least one field not filled
			@Override
			protected boolean computeValue() {
				return (txtSerialNumber.getText().isEmpty());
			}
		};
		btnAddCopy.disableProperty().bind(bb);
	}

	/**
	 * Gets all the copies for a specific book
	 * @return ObservableList BookCopy list
	 */
	private ObservableList<BookCopy> getCopies() {
		ArrayList<BookCopy> allCopies;
		allCopies = DatabaseController.getbookCopyList(selectedBook.getBookID());
		ObservableList<BookCopy> Copies = FXCollections.observableArrayList();
		for (int i = 0; i < allCopies.size(); i++)
			Copies.add(allCopies.get(i));
		return Copies;
	}

}
