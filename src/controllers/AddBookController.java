/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.io.File;
import java.io.IOException;

import entities.Book;
import entities.Book.bookType;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddBookController {

	@FXML
	private TextField txtBookName;

	@FXML
	private TextField txtAuthor;

	@FXML
	private TextField txtBookID;

	@FXML
	private TextField txtEdition;

	@FXML
	private TextField txtPrintYear;

	@FXML
	private TextField txtBookSubject;

	@FXML
	private ImageView imgBack;

	@FXML
	private Button btnAddBook;

	@FXML
	private Button btnClear;

	@FXML
	private TextField txtCatalog;


	@FXML
	private TextField txtShelf;

	@FXML
	private TextArea txtDescirption;

	@FXML
	private Button btnBrowsePath;

	@FXML
	private TextField txtTableOfContents;

	@FXML
	private ComboBox<String> bookTypeCB;

	private Book newBook;

	/**
	 * Validate the input inserted and add a new book to the DB only if the book doesn't exist already in the DB. 
	 * Shows an alert if and only if the book has been created successfully and has been saved
	 * into the DB.
	 * @param event - on pressing the 'Add Book' button.
	 */
	@FXML
	void btnAddBookPressed(ActionEvent event) throws IOException {
		boolean input;
		input = validateInput();

		if (input == true) {
			newBook = new Book(Integer.parseInt(txtBookID.getText()), txtBookName.getText(), txtAuthor.getText(),
					txtEdition.getText(), Integer.parseInt(txtPrintYear.getText()), txtBookSubject.getText(),
					txtDescirption.getText(), Integer.parseInt(txtCatalog.getText()), txtTableOfContents.getText(),
					txtShelf.getText(), 0,
					bookType.valueOf(bookTypeCB.getSelectionModel().getSelectedItem().toString()), 0);
					
			
			DatabaseController.saveFile(txtBookName.getText(), txtTableOfContents.getText(), Integer.parseInt(txtBookID.getText()));

			// check if this id exist
			if ((DatabaseController.getBook(newBook.getBookID())) == null) {
				DatabaseController.addBook(newBook);
				DatabaseController.saveFile(txtBookName.getText(), txtTableOfContents.getText(),
						Integer.parseInt(txtBookID.getText()));
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Succsess");
				alert.setHeaderText("The book has been added successfully");
				alert.showAndWait();
				((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Close stage

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("ERROR");
				alert.setHeaderText("This book ID is exist");
				alert.showAndWait();

			}

		}

	}

	/**
	 * Let the user pick the path of the desired file to upload and save it into the DB.
	 * Display the path that has been chosen.
	 * @param event - when pressing the 'Browse' button.
	 */
	@FXML
	void btnBrowsePathPressed(ActionEvent event) {
		FileChooser fc = new FileChooser();
		File SelectedFile = fc.showOpenDialog(null);

		if (SelectedFile != null) {
			txtTableOfContents.setText(SelectedFile.getAbsolutePath());
		}
	}

	/**
	 * Clears all the fields in the Current GUI.
	 * @param event - when pressing the 'Clear' button.
	 */
	@FXML
	void btnClearPressed(ActionEvent event) {
		txtBookName.clear();
		txtBookID.clear();
		txtEdition.clear();
		txtAuthor.clear();
		txtPrintYear.clear();
		txtBookSubject.clear();
		txtCatalog.clear();
		txtShelf.clear();
		txtDescirption.clear();
		txtTableOfContents.clear();
	}

	/**
	 * Close this stage and get back to the previous stage.
	 * @param event - on pressing the 'back(image)' button.
	 */
	@FXML
	void imgBackClicked(MouseEvent event) throws IOException {
		((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Close stage
	}

	/**
	 * Initialise the 'Add Book' stage.
	 */
	@FXML
	void initialize() {
		DatabaseController.addTextLimiter(txtBookName, 32,"Book name","character");
		DatabaseController.addTextLimiter(txtAuthor, 32,"Book author","character");
		DatabaseController.addTextLimiter(txtBookID	, 12,"Book ID","int");
		DatabaseController.addTextLimiter(txtEdition, 32,"Edition","character");
		DatabaseController.addTextLimiter(txtPrintYear, 4,"Print year","int");
		DatabaseController.addTextLimiter(txtBookName, 256,"Path ","character");
		DatabaseController.addTextLimiter(txtBookSubject, 60,"Subject","character");
		DatabaseController.addTextLimiter(txtCatalog, 32,"Catalog","int");
		DatabaseController.addTextLimiter(txtShelf, 32,"Shelf","character");
		DatabaseController.addTextLimiter(txtDescirption, 256,"Description","character");
		btnAddBook.setDisable(true);
		txtTableOfContents.setEditable(false);
		ObservableList<String> options = FXCollections.observableArrayList("Wanted", "Regular");
		bookTypeCB.getItems().addAll(options);

		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(txtBookName.textProperty(), txtAuthor.textProperty(), txtBookID.textProperty(),
						txtEdition.textProperty(), txtPrintYear.textProperty(), txtBookSubject.textProperty(),
						txtCatalog.textProperty(),
						txtShelf.textProperty(), txtDescirption.textProperty(), txtTableOfContents.textProperty());
			}

			// this function return true if at least one field not filled
			@Override
			protected boolean computeValue() {
				return (txtBookName.getText().isEmpty() || txtAuthor.getText().isEmpty()
						|| txtBookID.getText().isEmpty() || txtEdition.getText().isEmpty()
						|| txtPrintYear.getText().isEmpty() || txtBookSubject.getText().isEmpty()
						|| txtCatalog.getText().isEmpty() 
						|| txtShelf.getText().isEmpty() || txtDescirption.getText().isEmpty()
						|| txtTableOfContents.getText().isEmpty());
			}
		};

		// Enable "add book button" after fill all the fields
		btnAddBook.disableProperty().bind(bb);
		bookTypeCB.getSelectionModel().select(1);

	}

	/**
	 * Load the 'Add Book' stage after initialising it.
	 * @param primaryStage - the stage for display.
	 */
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/AddBookForm.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = new Stage();
			stage.initOwner(primaryStage);
			stage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Add Book Form");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Validate the input inserted into the fields in the GUI.
	 * Display an alert message if at least one of the fields is invalid.
	 * @return true in case of a valid input
	 */
	@FXML
	public boolean validateInput() {

		// Initialise the text fields to the original color
		txtBookName.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtAuthor.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtBookID.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtEdition.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtPrintYear.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtBookSubject.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtCatalog.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtShelf.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtDescirption.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");

		Alert msg = new Alert(AlertType.ERROR, "", ButtonType.OK);// Prepare alert box
		msg.setHeaderText("Input Error");
		String errorMsg = "";

		/**
		 * validate input for all relevant text fields
		 */

		for (char c : txtBookID.getText().toCharArray())// Parse text field into chars array and validate
			if (!Character.isDigit(c)) {
				errorMsg += "Book ID number must contain numbers only!\n";
				txtBookID.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				break;
			}

		for (char c : txtAuthor.getText().toCharArray())// Parse text field into chars array and validate
			if (Character.isDigit(c)) {
				errorMsg += "Author name must contain letters only!\n";
				txtAuthor.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				break;
			}

		for (char c : txtCatalog.getText().toCharArray())// Parse text field into chars array and validate
			if (!Character.isDigit(c)) {
				errorMsg += "Book Catalog number must contain numbers only!\n";
				txtCatalog.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				break;
			}

		for (char c : txtPrintYear.getText().toCharArray())// Parse text field into chars array and validate
			if (!Character.isDigit(c)) {
				errorMsg += "Year must contain numbers only!\n";
				txtPrintYear.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				break;
			}

		// if errorMsg is empty all the text fields input is correct
		if (!(errorMsg.equals(""))) {
			msg.setContentText(errorMsg);
			msg.show();
			return false;
		}

		return true;

	}
	
	

	
}
