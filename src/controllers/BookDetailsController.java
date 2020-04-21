/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import entities.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BookDetailsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private TextField txtCatalog;

    @FXML
    private TextField txtCopies;

    @FXML
    private TextField txtShelf;

    @FXML
    private TextArea txtDescirption;

    @FXML
    private TextField txtBookType;

    @FXML
    private Button btnTableOfContents;
    
    private static Book selectedBook;
    
    @FXML
    void btnTableOfCententsPressed(ActionEvent event) {
    	String path = "C:\\obl\\";
    	DatabaseController.getFileFromDB(selectedBook.getBookID(), selectedBook.getName(), "C:\\obl\\");
    	try {
    		
			Desktop.getDesktop().open(new File(path + selectedBook.getName()+" Contents.pdf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    }

    @FXML
    void imgBackClicked(MouseEvent event) {
    	((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // Close stage
    }

    @FXML
    void initialize() {
    	txtBookType.setText(selectedBook.getBookType().name());
		txtBookName.setText(selectedBook.getName());
		txtAuthor.setText(selectedBook.getAuthor());
		txtBookID.setText(Integer.toString(selectedBook.getBookID()));
		txtEdition.setText(selectedBook.getEdition());
		txtPrintYear.setText(Integer.toString(selectedBook.getPrintYear()));
		txtBookSubject.setText(selectedBook.getSubject());
		txtCatalog.setText(Integer.toString(selectedBook.getCatalog()));
		txtCopies.setText(Integer.toString(selectedBook.getCopiesNumber()));
		txtShelf.setText(selectedBook.getShelf());
		txtDescirption.setText(selectedBook.getDescription());
		
    	
    }
    
    public void start(Stage primaryStage, Book selectedBook) {
		try {
			BookDetailsController.selectedBook = selectedBook;
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/gui/BookDetailsForm.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = new Stage();	
			stage.initOwner(primaryStage);
			stage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Book Details");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
