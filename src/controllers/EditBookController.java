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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditBookController {

	Book editedBook;

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
	private TextField txtSubject;

	@FXML
	private ImageView imgBack;

	@FXML
	private Button btnEditBook;

	@FXML
	private TextField txtCatalog;

    @FXML
    private TextField txtcopiesNumber;
    
    @FXML
    private Button btnEditCopies;
    
	@FXML
	private TextField txtShelf;

	@FXML
	private TextArea txtDescription;

	@FXML
	private Button btnBrowsePath;

	@FXML
	private TextField txtTableOfContents;

	@FXML
    private ChoiceBox<String> bookTypeCB;
	
	
	private static Book selectedBook;


	/**
	 * Opens the 'Edit book Copies' stage.
	 * @param event - on pressing the 'Edit Copies' button.
	 */
    @FXML
    void btnEditCopiesPressed(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		ManageCopiesController manageCopiesForm = new ManageCopiesController();
		try {
			manageCopiesForm.start(stage,selectedBook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
    }
	
	/** 
	 * Browse the contents PDF file and display the path in the relevant text field.
	 * @param event - on pressing the 'Browse' button.
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
	 * Validate the inserted input and only then update the DB with the new modified data.
	 * @param event- on pressing the 'Edit Book' button.
	 */
	@FXML
	void btnEditBookPressed(ActionEvent event) {
		/*
		 * update the edited data in the DB
		 */
		if (validateInput() == true) {
			editedBook.setName(txtBookName.getText());
			editedBook.setAuthor(txtAuthor.getText());
			editedBook.setPrintYear(Integer.parseInt(txtPrintYear.getText()));
			editedBook.setSubject(txtSubject.getText());
			editedBook.setCatalog(Integer.parseInt(txtCatalog.getText()));
			editedBook.setDescription(txtDescription.getText());
			editedBook.setShelf(txtShelf.getText());
			editedBook.setEdition(txtEdition.getText());
			editedBook.setSubject(txtSubject.getText());
			editedBook.setTableOfContents(txtTableOfContents.getText());
			editedBook.setBookType(bookType.valueOf(bookTypeCB.getSelectionModel().getSelectedItem().toString()));
			if (txtTableOfContents.getText() != null)
				DatabaseController.saveFile(txtBookName.getText(), txtTableOfContents.getText(),
						Integer.parseInt(txtBookID.getText()));
			DatabaseController.editBook(editedBook);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setContentText("this changes has updated successfully");
			alert.showAndWait();
			((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Close stage
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

	/**
	 * Initialise the 'Edit Book' stage.
	 * Displays the selected book information into a GUI.
	 */
	@FXML
	void initialize() {
		
		DatabaseController.addTextLimiter(txtBookName, 32,"Book name","character");
		DatabaseController.addTextLimiter(txtAuthor, 32,"Book author","character");
		DatabaseController.addTextLimiter(txtBookID	, 12,"Book ID","int");
		DatabaseController.addTextLimiter(txtEdition, 32,"Edition","character");
		DatabaseController.addTextLimiter(txtBookName, 256,"Path ","character");
		DatabaseController.addTextLimiter(txtPrintYear, 4,"Print year","int");
		DatabaseController.addTextLimiter(txtSubject, 60,"Subject","character");
		DatabaseController.addTextLimiter(txtCatalog, 32,"Catalog","int");
		DatabaseController.addTextLimiter(txtShelf, 32,"Shelf","character");
		DatabaseController.addTextLimiter(txtDescription, 256,"Description","character");
		
	    txtcopiesNumber.setEditable(false);
	    txtTableOfContents.setEditable(false);
		btnEditBook.setDisable(true);
		ObservableList<String> options = 
    		    FXCollections.observableArrayList(
    		       ( selectedBook.getBookType().name()=="Wanted"?  "Regular" : "Wanted")
    		       
    		    );
		ObservableList<String> init = 
    		    FXCollections.observableArrayList(
    		    		selectedBook.getBookType().name()
    		    );
    	bookTypeCB.getItems().addAll(options);
		txtBookName.setText(selectedBook.getName());
		txtAuthor.setText(selectedBook.getAuthor());
		txtBookID.setText(Integer.toString(selectedBook.getBookID()));
		txtEdition.setText(selectedBook.getEdition());
		txtPrintYear.setText(Integer.toString(selectedBook.getPrintYear()));
		txtSubject.setText(selectedBook.getSubject());
		txtCatalog.setText(Integer.toString(selectedBook.getCatalog()));
		txtcopiesNumber.setText(Integer.toString(selectedBook.getCopiesNumber()));
		txtShelf.setText(selectedBook.getShelf());
		txtDescription.setText(selectedBook.getDescription());
		bookTypeCB.getItems().addAll(init);
		bookTypeCB.getSelectionModel().select(0	);
		txtTableOfContents.setText(selectedBook.getTableOfContents());
		editedBook=selectedBook;
		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(txtBookName.textProperty(), txtAuthor.textProperty(), txtBookID.textProperty(),
						txtEdition.textProperty(),  txtPrintYear.textProperty(),
						txtSubject.textProperty(), txtCatalog.textProperty(), 
						txtShelf.textProperty(), txtDescription.textProperty(), txtTableOfContents.textProperty());
			}

			// this function return true if at least one field not filled
			@Override
			protected boolean computeValue() {
				return (txtBookName.getText().isEmpty() || txtAuthor.getText().isEmpty()
						|| txtBookID.getText().isEmpty() || txtEdition.getText().isEmpty()
					 || txtPrintYear.getText().isEmpty()
						|| txtSubject.getText().isEmpty() || txtCatalog.getText().isEmpty()
						||  txtShelf.getText().isEmpty()
						|| txtDescription.getText().isEmpty());
			}
		};
		// Enable "add book button" after fill all the fields
				btnEditBook.disableProperty().bind(bb);
	}

	
	/**
	 * Load the 'Edit Book' stage after initialising it.
	 * @param primaryStage - the stage for display.
	 */
	public void start(Stage primaryStage, Book selectedBook) {
		try {
			this.selectedBook = selectedBook;
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/EditBookForm.fxml"));
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
	 * Validates the inserted input. returns true if and only if the inserted input is valid.
	 * Otherwise returns false.
	 * @return Boolean statment
	 */
	public boolean validateInput() {
		// Initialise the text fields to the original color
		txtBookName.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtAuthor.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtBookID.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtEdition.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtPrintYear.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtSubject.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtCatalog.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		
		txtShelf.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");
		txtDescription.setStyle("-fx-border-color: white ; -fx-border-width: 2px ;");

		Alert msg = new Alert(AlertType.ERROR, "", ButtonType.OK);// Prepare alert box
		msg.setHeaderText("Input Error");
		String errorMsg = "";
	
		 
		
		 //validate input for all the text fields
		for (char c : txtAuthor.getText().toCharArray())// Parse text field into chars array and validate
			if (Character.isDigit(c)) {
				errorMsg += "Author name must contain letters only!\n";
				txtAuthor.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				break;
			}



	
		
		for (char c : txtCatalog.getText().toCharArray())// Parse text field into chars array and validate
			if (!Character.isDigit(c)) {
				errorMsg += "Copies number must contain numbers only!\n";
				txtCatalog.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
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
