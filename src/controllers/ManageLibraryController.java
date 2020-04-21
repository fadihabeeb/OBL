/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;

import entities.Book;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ManageLibraryController {

	@FXML
	private TextField txtSearch;

	@FXML
	private ImageView imgRef;

	@FXML
	private ImageView imgBack;
	
	@FXML
    private Button btnSearch;

	@FXML
	private TableView<Book> tableView;

	@FXML
	private TableColumn<Book, String> bookID;

	@FXML
	private TableColumn<Book, String> name;

	@FXML
	private TableColumn<Book, String> author;

	@FXML
	private TableColumn<Book, Double> edition;

	@FXML
	private TableColumn<Book, Year> year;

	@FXML
	private TableColumn<Book, Integer> copies;

	@FXML
	private TableColumn<Book, Integer> availableCopies;

	@FXML
	private Button btnAddBook;

	@FXML
	private Button btnDeleteBook;

	@FXML
	private Button btnEditBook;

	/**
	 * When 'Add Book' button is clicked , this method is called to display the 'Add Book' stage.
	 * @param event - on pressing the 'Add Book' button.
	 * @throws IOException
	 */
	@FXML
	void btnAddBookPressed(ActionEvent event) throws IOException {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		AddBookController AddBookForm = new AddBookController();
		try {
			AddBookForm.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the selected book from DB. if and only if the user Confirm the deletion.
	 * Update the DB. Display alert for succession if and only if the book has been deleted successfully.
	 * @param event
	 */
	@FXML
	void btnDeleteBookPressed(ActionEvent event) {

		Book selectedForDelete = (Book) tableView.getSelectionModel().getSelectedItem();
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
						DatabaseController.deleteBookCopies(selectedForDelete.getBookID());
						DatabaseController.deleteBook(selectedForDelete.getBookID());
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
	 * When 'Edit Book' button is clicked , this method is called to display the 'Edit Book' stage.
	 * @param event - on pressing the 'Edit Book' button.
	 * @throws IOException
	 */
	@FXML
	void btnEditBookPressed(ActionEvent event) throws Exception {
		Book selectedForEdit = (Book) tableView.getSelectionModel().getSelectedItem();

		try {
			if (selectedForEdit == null)
				throw new Exception();
			else {
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				EditBookController EditBookForm = new EditBookController();
				try {
					EditBookForm.start(stage, selectedForEdit);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("No book has selected");
			alert.setContentText("Select a book for edit!");
			alert.showAndWait();
		}

	}

	/**
	 * Close this stage and get back to the previous stage.
	 * @param event - on pressing the 'back(image)' button.
	 */
	@FXML
	void imgBackClicked(MouseEvent event) throws IOException {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Library Management");
	}

	/**
	 * Initialise the 'Manage Library' stage.
	 */
	@FXML
	public void initialize() {

		// ObservableList<Book> search = FXCollections.observableArrayList();
		// add all the relevant fields to the view table
		bookID.setCellValueFactory(new PropertyValueFactory<Book, String>("bookID"));
		name.setCellValueFactory(new PropertyValueFactory<Book, String>("name"));
		author.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
		edition.setCellValueFactory(new PropertyValueFactory<Book, Double>("edition"));
		year.setCellValueFactory(new PropertyValueFactory<Book, Year>("printYear"));
		copies.setCellValueFactory(new PropertyValueFactory<Book, Integer>("copiesNumber"));
		availableCopies.setCellValueFactory(new PropertyValueFactory<Book, Integer>("availableCopies"));
		tableView.setItems(getBooks());

	}
	/**
	 * Gets all the Books in the DB as an ObservableList
	 * @return ObservableList Book list
	 */
	public ObservableList<Book> getBooks() {
		ArrayList<Book> allBooks;
		allBooks = DatabaseController.getAllBooks();
		ObservableList<Book> books = FXCollections.observableArrayList();
		for (int i = 0; i < allBooks.size(); i++)
			books.add(allBooks.get(i));
		return books;
	}

	/**
	 * Refreshes the data in the tableView.
	 * 
	 * @param event
	 */
	@FXML
	void imgRefreshClicked(MouseEvent event) {
		initialize();
	}

	/**
	 * Load the 'Manage Library' stage after initialising it.
	 * @param primaryStage - the stage for display.
	 */
	void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader= new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/ManageLibraryForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Manage");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Validates if the specified book is in the observableList exists.
	 * returns true if and only if the book does exists.
	 * Otherwise returns false
	 * 
	 * @param search - an ObservableList
	 * @param book - specified book
	 * @return Boolean statement
	 */
	boolean contain(ObservableList<Book> search, Book book) {
		for (int i = 0; i < search.size(); i++)
			if (search.get(i).getBookID() == book.getBookID())
				return true;
		return false;

	}

	/**
	 * Opens the 'Manage Copies' stage for the selected book.
	 * @param event - on pressing the 'Manage Copies' button.
	 */
	@FXML
	void btnManageCopiesPressed(ActionEvent event) {
	
	Book selectedForEdit = (Book) tableView.getSelectionModel().getSelectedItem();

	try {
		if (selectedForEdit == null)
			throw new Exception();
		else {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			ManageCopiesController manageCopiesForm = new ManageCopiesController();
			try {
				manageCopiesForm.start(stage, selectedForEdit);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	} catch (Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("No book has selected");
		alert.setContentText("Select a book for manage copies!");
		alert.showAndWait();
	}

}
	 @FXML
	    void btnSearchPressed(ActionEvent event) {
		 int flag=0;
		 ArrayList<Book> arr = new ArrayList<Book>();
			ObservableList<Book> search = FXCollections.observableArrayList();
			if (DatabaseController.bookSearch(txtSearch.getText(), "name") != null)
				arr.addAll(DatabaseController.bookSearch(txtSearch.getText(), "name"));

			if (DatabaseController.bookSearch(txtSearch.getText(), "author") != null)
				arr.addAll(DatabaseController.bookSearch(txtSearch.getText(), "author"));

			if (DatabaseController.bookSearch(txtSearch.getText(), "subject") != null)
				arr.addAll(DatabaseController.bookSearch(txtSearch.getText(), "subject"));

			if (DatabaseController.bookSearch(txtSearch.getText(), "description") != null)
				arr.addAll(DatabaseController.bookSearch(txtSearch.getText(), "description"));
			
			for (char c : txtSearch.getText().toCharArray())// Parse text field into chars array and validate
				if (!Character.isDigit(c)) {//contain characters
				flag=1;
				}
			if(flag!=1) {
				if (DatabaseController.bookSearch(txtSearch.getText(), "book id") != null)
					arr.addAll(DatabaseController.bookSearch(txtSearch.getText(), "book id"));
			}
			
			
			
			if (arr != null)
				for (int i = 0; i < arr.size(); i++)
					if (!contain(search, arr.get(i)))
						search.add(arr.get(i));
			tableView.setItems(search);

	    }

}
