/**
 * all the FXML controllers including the DatabaseController witch manage
 * all the controller in the package and connecting theme to the server
 */
package controllers;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import entities.Account;
import entities.Account.UserType;
import entities.ActivitiesReport;
import entities.Book;
import entities.LentBook;
import entities.LibrarianAccount;
import entities.UserAccount;
import entities.UserAccount.accountStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * 
 * @author Alaa Grable
 * @version 1.0 [17.1.2019]
 * 
 */

public class ReportsController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imgBack;

	@FXML
	private MenuButton mnuBtn;

	@FXML
	private Button btnGetReport;

	@FXML
	private RadioButton rdActivityReport;

	@FXML
	private Label lblReport;

	@FXML
	private ToggleGroup colorToggleGroup;

	@FXML
	private RadioButton rdLendsReport;

	@FXML
	private RadioButton rdDelaysReport;

	@FXML
	private RadioButton rdOther;

	@FXML
	private CheckMenuItem itemOrders;

	@FXML
	private CheckMenuItem itemsActive;

	@FXML
	private CheckMenuItem itemLateUsers;

	@FXML
	private CheckMenuItem itemsSuspend;

	@FXML
	private CheckMenuItem itemsLocked;

	@FXML
	private CheckMenuItem itemLents;

	@FXML
	private ComboBox<String> cmbSavedReports;

	@FXML
	private TextField txtPath;

	@FXML
	private Button btnBrowse;

	@FXML
	void btnBrowsePresses(ActionEvent event) {
	}

	private static LibrarianAccount loggedLibAccount;
	private String filePath;
	static int[] Per = new int[11];

	/**
	 * Checks which option the user has chosen. Creates the the specified report
	 * according to the user choice.
	 */
	@FXML
	void btnGetReportPressed(ActionEvent event) {

		// get the selected option
		RadioButton selectedRadioButton = (RadioButton) colorToggleGroup.getSelectedToggle();

		// check which option the user has chose
		switch (selectedRadioButton.getText()) {
		// if the chosen option was 'Activity Report'
		case "Activity Report": {
			String date = cmbSavedReports.getSelectionModel().getSelectedItem();
			if(!date.equals("Today")) {
				DatabaseController.getActivityReportFileFromDB(LocalDate.parse(date), filePath+"/");
				try {
					Desktop.getDesktop().open(new File(filePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				// get the corresponding data from the database
				ActivitiesReport activitiesRprt = DatabaseController.getActivityReport();
				ArrayList<UserAccount> accounts = activitiesRprt.getAccounts();
				ArrayList<Book> books = activitiesRprt.getBooks();
				// validate if we got the data
				if (activitiesRprt == null || accounts == null || books == null) {
					alertWarningMessage("Something went wrong while retrieving the activity report..");
					break;
				}
				try {
					int width, height;
					PdfPCell cell;
					Document document = new Document();
					// check if the file is already open or not
					if (isFileOpen(filePath + "\\Activity Report.pdf") == true) {
						// if it is , then let the user know about that
						new Alert(AlertType.ERROR,
								"The file 'Activity Report.pdf' is already opened.\nClose it to create the report.",
								ButtonType.OK).show();
					} else {

						// create an instance of a pdf
						PdfWriter writer = PdfWriter.getInstance(document,
								new FileOutputStream(filePath + "\\Activity Report.pdf"));
						document.open();
						// set the logo in the pdf
						URL url = ReportsController.class.getResource("/images/pdfLogo.png");
						Image img = Image.getInstance(url);
						img.setAlignment(1);
						document.add(img);

						// initialise the header of the pdf file
						Chunk glue = new Chunk(new VerticalPositionMark());
						Paragraph p = new Paragraph(new Date().toString(), FontFactory.getFont(FontFactory.TIMES_BOLD, 20));
						p.add(new Chunk(glue));
						p.add("Activities Report");
						document.add(p);
						document.add(new Paragraph(
								"----------------------------------------------------------------------------------------------------------------------------------"));

						// Users Info Table (Start)
						PdfPTable table = new PdfPTable(3);
						table.setSpacingBefore(100);

						// inserting header cells into the table
						cell = new PdfPCell(new Paragraph("Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setColspan(3);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.GRAY);
						table.addCell(cell);

						// first row
						cell = new PdfPCell(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table.addCell(cell);

						cell = new PdfPCell(new Paragraph("Number of users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table.addCell(cell);

						cell = new PdfPCell(
								new Paragraph("Percentage of total users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table.addCell(cell);

						// second row
						cell = new PdfPCell(new Paragraph("Users type", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						cell = new PdfPCell(new Paragraph(String.valueOf(activitiesRprt.getTotalUsers())));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						cell = new PdfPCell(new Paragraph(
								String.valueOf((activitiesRprt.getTotalUsers() * 100.00) / activitiesRprt.getTotalUsers())
										+ "%"));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// third row
						// insert the active users cells
						cell = new PdfPCell(new Paragraph("Active Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// get the number of active users
						cell = new PdfPCell(new Paragraph(String.valueOf(activitiesRprt.getActiveUsersNumber())));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// calculate the percentage of the active users from the total users in the
						// system
						double activePer = (activitiesRprt.getActiveUsersNumber() * 100.00)
								/ activitiesRprt.getTotalUsers();
						cell = new PdfPCell(new Paragraph(String.valueOf(activePer) + "%"));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// fourth row
						// insert suspended users cells
						cell = new PdfPCell(new Paragraph("Suspended Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// get the number of suspended users
						cell = new PdfPCell(new Paragraph(String.valueOf(activitiesRprt.getFrozenUsersNumber())));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// calculate the percentage of the suspended users from the total users in the
						// system
						double suspendedPer = (activitiesRprt.getFrozenUsersNumber() * 100.0)
								/ activitiesRprt.getTotalUsers();
						cell = new PdfPCell(new Paragraph(String.valueOf(suspendedPer) + "%"));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// fifth row
						// insert suspended users cells
						cell = new PdfPCell(new Paragraph("Locked Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// get the number of locked users
						cell = new PdfPCell(new Paragraph(String.valueOf(activitiesRprt.getLockedUsersNumber())));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						// calculate the percentage of the locked users from the total users in the
						// system
						double lockedPer = (activitiesRprt.getLockedUsersNumber() * 100.00)
								/ activitiesRprt.getTotalUsers();
						cell = new PdfPCell(new Paragraph(String.valueOf(lockedPer) + "%"));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);

						document.add(table);
						// Users Info Table (End)

						// creates new page
						document.newPage();

						/* creates the pie chart */
						DefaultPieDataset defaultCategoryDataset = new DefaultPieDataset();
						defaultCategoryDataset.setValue("Active " + String.valueOf(activePer) + "%", activePer);
						defaultCategoryDataset.setValue("Suspended " + String.valueOf(suspendedPer) + "%", suspendedPer);
						defaultCategoryDataset.setValue("Locked" + String.valueOf(lockedPer) + "%", lockedPer);

						JFreeChart jFreeChart = ChartFactory.createPieChart("Users Chart", defaultCategoryDataset, true,
								false, false);

						PdfContentByte pdfContentByte = writer.getDirectContent();
						width = 400; // width of PieChart
						height = 300; // height of pieChart
						PdfTemplate pdfTemplate = pdfContentByte.createTemplate(width, height);

						// create graphics
						@SuppressWarnings("deprecation")
						Graphics2D graphics2d = pdfTemplate.createGraphics(width, height, new DefaultFontMapper());

						// create rectangle
						java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);

						jFreeChart.draw(graphics2d, rectangle2d);

						graphics2d.dispose();
						pdfContentByte.addTemplate(pdfTemplate, 100, 500); // 0, 0 will draw PIE chart on bottom left of
																			// page
						document.newPage();
						/* end pie chart */

						// Books Info Table (Start)
						PdfPTable table2 = new PdfPTable(4);
						table2.setSpacingBefore(100);

						// inserting header cells into the table
						cell = new PdfPCell(new Paragraph("Books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setColspan(4);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.GRAY);
						table2.addCell(cell);

						// first row
						cell = new PdfPCell(new Paragraph("Book ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table2.addCell(cell);

						cell = new PdfPCell(new Paragraph("Book Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table2.addCell(cell);

						cell = new PdfPCell(new Paragraph("Number of copies", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table2.addCell(cell);

						cell = new PdfPCell(
								new Paragraph("Percentage of Library", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table2.addCell(cell);

						// iterate through the books to display data for each book
						int libraryBooks = activitiesRprt.getAllLibraryBooksNum();
						DefaultPieDataset defaultCategoryDataset1 = new DefaultPieDataset();
						for (Book book : books) {
							cell = new PdfPCell(new Paragraph(String.valueOf(book.getBookID())));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table2.addCell(cell);

							cell = new PdfPCell(new Paragraph(book.getName()));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table2.addCell(cell);

							cell = new PdfPCell(new Paragraph(String.valueOf(book.getCopiesNumber())));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table2.addCell(cell);

							double Per = Math.round((book.getCopiesNumber() * 100.00) / libraryBooks);
							cell = new PdfPCell(new Paragraph(String.valueOf(Per) + "%"));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table2.addCell(cell);

							defaultCategoryDataset1.setValue(book.getName() + " " + String.valueOf(Per) + "%", Per);
						}

						// display the total different books in the library
						cell = new PdfPCell(new Paragraph("Total different books : " + String.valueOf(books.size()),
								FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setColspan(4);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table2.addCell(cell);

						// display the total books in the library
						cell = new PdfPCell(new Paragraph("Total Books in the library : " + String.valueOf(libraryBooks),
								FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setColspan(4);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table2.addCell(cell);

						document.add(table2);
						// Books Info Table (End)

						/* creates the pie chart */
						document.newPage();
						JFreeChart jFreeChart1 = ChartFactory.createPieChart("Books Chart", defaultCategoryDataset1, true,
								false, false);

						PdfContentByte pdfContentByte1 = writer.getDirectContent();
						width = 400; // width of PieChart
						height = 300; // height of pieChart
						PdfTemplate pdfTemplate1 = pdfContentByte1.createTemplate(width, height);

						// create graphics
						@SuppressWarnings("deprecation")
						Graphics2D graphics2d1 = pdfTemplate1.createGraphics(width, height, new DefaultFontMapper());

						// create rectangle
						java.awt.geom.Rectangle2D rectangle2d1 = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);

						jFreeChart1.draw(graphics2d1, rectangle2d1);

						graphics2d1.dispose();
						pdfContentByte1.addTemplate(pdfTemplate1, 100, 500); // 0, 0 will draw PIE chart on bottom left of
																				// page
						document.newPage();
						/* end pie chart */

						// Late Users Info Table (Start)
						PdfPTable table3 = new PdfPTable(3);
						table3.setSpacingBefore(100);

						// inserting header cells into the table
						cell = new PdfPCell(new Paragraph("Late Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setColspan(3);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.GRAY);
						table3.addCell(cell);

						cell = new PdfPCell(new Paragraph("User ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table3.addCell(cell);

						cell = new PdfPCell(new Paragraph("First Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table3.addCell(cell);

						cell = new PdfPCell(new Paragraph("Last Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table3.addCell(cell);

						// iterate through the accounts and display the data for each account
						for (UserAccount acc : accounts) {

							cell = new PdfPCell(new Paragraph(String.valueOf(acc.getID())));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table3.addCell(cell);

							cell = new PdfPCell(new Paragraph(acc.getFirstName()));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table3.addCell(cell);

							cell = new PdfPCell(new Paragraph(acc.getLastName()));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							table3.addCell(cell);
						}
						// display the total accounts in the system
						cell = new PdfPCell(new Paragraph("Total Accounts : " + String.valueOf(accounts.size()),
								FontFactory.getFont(FontFactory.TIMES_BOLD)));
						cell.setColspan(4);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						table3.addCell(cell);

						document.add(table3); // Late Users Info Table (End)

						document.close();
						// alert the user that the report has been created successfully
						new Alert(AlertType.INFORMATION,
								"Activity Report has been created successfully\n at " + filePath + "\\Activity Report.pdf",
								ButtonType.OK).show();
						// display the file immediately after creating it
						Desktop.getDesktop().open(new File(txtPath.getText() + "\\Activity Report.pdf"));
						DatabaseController.saveActivityReportFile(LocalDate.now(),txtPath.getText().concat("\\Activity Report.pdf"));
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		// if the chosen option was 'Lends Report'
		case "Lends Report": {
			String[] temp;
			PdfPCell cell;

			// check if the file is already open or not
			if (isFileOpen(filePath + "\\Lends Report.pdf") == true) {
				// if the file is open , let the user know
				new Alert(AlertType.ERROR, "The file 'Lends Report.pdf' is already opened.", ButtonType.OK).show();
			} else {
				ArrayList<LentBook> books = DatabaseController.getLentBookList(-1);

				// validate if we have retrieved the data from the data base
				if (books == null) {
					// if not , then let the user know
					alertWarningMessage("Something went wrong while retrieving lent books data from data base.");
					break;
				}
				try {
					// if not then create an instance of a pdf
					Document document = new Document();
					PdfWriter writer = PdfWriter.getInstance(document,
							new FileOutputStream(filePath + "\\Lends Report.pdf"));
					document.open();
					// insert the logo in the header of the pdf
					URL url = ReportsController.class.getResource("/images/pdfLogo.png");
					Image img = Image.getInstance(url);
					img.setAlignment(1);
					document.add(img);

					// initialise the header of the pdf file
					Chunk glue = new Chunk(new VerticalPositionMark());
					Paragraph p = new Paragraph(new Date().toString(), FontFactory.getFont(FontFactory.TIMES_BOLD, 20));
					p.add(new Chunk(glue));
					p.add("Lend Report");
					document.add(p);
					document.add(new Paragraph(
							"----------------------------------------------------------------------------------------------------------------------------------"));

					// create 2 areas to sort the lentbooks by they bookType
					ArrayList<LentBook> wantedBooks = new ArrayList<LentBook>();
					ArrayList<LentBook> regularBooks = new ArrayList<LentBook>();
					// save the sum to calculate the average
					long wntdBooks = 0, rglrBooks = 0;
					int wantedLateBooks = 0, wantedNotLateBooks = 0;
					int regularLateBooks = 0, regularNotLateBooks = 0;
					// create a list to convert them to integer array to make it easier to sort them
					List<String> wantedbks = new ArrayList<String>();
					List<String> regularbks = new ArrayList<String>();

					// iterate through the lent books list
					for (LentBook book : books) {
						// check if this book type is 'wanted'
						if (book.getBook().getBookType().toString().equals(Book.bookType.Wanted.toString()) == true) {
							wantedBooks.add(book);
							if (book.isReturned() == false) {
								wntdBooks = wntdBooks + book.getIssueDate().until(LocalDate.now(), ChronoUnit.DAYS);
								wantedbks.add(
										String.valueOf(book.getIssueDate().until(LocalDate.now(), ChronoUnit.DAYS)));
							} else {
								wntdBooks = wntdBooks
										+ book.getIssueDate().until(book.getReturnDate(), ChronoUnit.DAYS);
								wantedbks.add(String
										.valueOf(book.getIssueDate().until(book.getReturnDate(), ChronoUnit.DAYS)));
							}
							// check whether the user of this lent book request is late to return the book
							if (book.isLate() == true)
								// if yes then count it
								wantedLateBooks++;
							else
								// if not then count it
								wantedNotLateBooks++;
						} else {
							// the book is not a 'regular' book , so we add it to the regular books list
							regularBooks.add(book);
							if (book.isReturned() == false) {
								rglrBooks = rglrBooks + book.getIssueDate().until(LocalDate.now(), ChronoUnit.DAYS);
								regularbks.add(
										String.valueOf(book.getIssueDate().until(LocalDate.now(), ChronoUnit.DAYS)));
							} else {
								rglrBooks = rglrBooks
										+ book.getIssueDate().until(book.getReturnDate(), ChronoUnit.DAYS);
								regularbks.add(String
										.valueOf(book.getIssueDate().until(book.getReturnDate(), ChronoUnit.DAYS)));
							}
							// check whether the user of this lent book request is late to return the book
							if (book.isLate() == true)
								// if yes then count it
								regularLateBooks++;
							else
								// if not then count it
								regularNotLateBooks++;
						}
					}
					int i = 0;
					long[] wantedDelays = new long[wantedBooks.size()];
					long[] regularDelays = new long[regularBooks.size()];
					for (LentBook book : wantedBooks) {
						if (book.isReturned() == false)
							wantedDelays[i++] = book.getIssueDate().until(LocalDate.now(), ChronoUnit.DAYS);
						else
							wantedDelays[i++] = book.getIssueDate().until(book.getReturnDate(), ChronoUnit.DAYS);
					}
					i = 0;
					for (LentBook book : regularBooks) {
						if (book.isReturned() == false)
							regularDelays[i++] = book.getIssueDate().until(LocalDate.now(), ChronoUnit.DAYS);
						else
							regularDelays[i++] = book.getIssueDate().until(book.getReturnDate(), ChronoUnit.DAYS);
					}

					// converting (Start)
					temp = wantedbks.toArray(new String[wantedbks.size()]);
					int[] arrayWanted = Arrays.stream(temp).mapToInt(Integer::parseInt).toArray();

					temp = regularbks.toArray(new String[regularbks.size()]);
					int[] arrayRegular = Arrays.stream(temp).mapToInt(Integer::parseInt).toArray();
					// converting (End)

					// sorting the arrays
					Arrays.sort(arrayWanted);
					Arrays.sort(arrayRegular);

					// Wanted Books info (Start)
					PdfPTable table = new PdfPTable(2);
					table.setSpacingBefore(100);

					// inserting header cells into the table
					cell = new PdfPCell(new Paragraph("'Wanted' Books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setColspan(3);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.GRAY);
					table.addCell(cell);

					cell = new PdfPCell(
							new Paragraph("Average 'Wanted' books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table.addCell(cell);

					// calculate the average of wanted books and display it
					cell = new PdfPCell(new Paragraph(String.valueOf((wntdBooks / (double) wantedBooks.size()))));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(
							new Paragraph("Median of 'Wanted' books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table.addCell(cell);

					// calculate the median of wanted books and display it

					int median;
					if (arrayWanted.length % 2 == 0)
						median = (arrayWanted[arrayWanted.length / 2]
								+ arrayWanted[arrayWanted.length / 2 - 1]) / 2;
					else
						median = arrayWanted[arrayWanted.length / 2];
					cell = new PdfPCell(new Paragraph(String.valueOf((median))));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(new Paragraph("Late returns", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table.addCell(cell);

					// calculate the percentage of the late returned books for wanted books from the
					// total books
					double noLate = Math.round((wantedLateBooks * 100.00) / wantedBooks.size());
					cell = new PdfPCell(new Paragraph(String.valueOf(noLate) + "%"));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(new Paragraph("On time returns", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table.addCell(cell);

					// calculate the percentage of on time returned books for wanted books from the
					// total books
					double Late = Math.round((wantedNotLateBooks * 100.00) / wantedBooks.size());
					cell = new PdfPCell(new Paragraph(String.valueOf(Late) + "%"));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					document.add(table);

					int max = (int) Arrays.stream(wantedDelays).max().getAsLong();

					PdfPTable table2 = new PdfPTable(3);

					cell = new PdfPCell(new Paragraph("'Wanted' Books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setColspan(3);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.GRAY);
					table2.addCell(cell);

					cell = new PdfPCell(new Paragraph("Days", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table2.addCell(cell);

					cell = new PdfPCell(new Paragraph("Percentage of total 'Wanted' books",
							FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table2.addCell(cell);

					cell = new PdfPCell(new Paragraph("Number of books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table2.addCell(cell);
					document.add(table2);

					if (max >= 10)
						maxIsGreaterThan10(document, max, wantedDelays);
					else
						maxIsLesserThan10(document, max, wantedDelays);
					PdfPTable table21 = new PdfPTable(3);
					cell = new PdfPCell(new Paragraph("Total 'Wanted' Books : " + String.valueOf(wantedBooks.size()),
							FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setColspan(3);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.GRAY);
					table21.addCell(cell);
					document.add(table21);

					document.newPage();
					// Regular Books Info (Start)
					PdfPTable table3 = new PdfPTable(2);
					table2.setSpacingBefore(100);

					// initialise the header of the pdf file
					cell = new PdfPCell(new Paragraph("'Regular' Books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setColspan(2);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.GRAY);
					table3.addCell(cell);

					cell = new PdfPCell(
							new Paragraph("Average 'Regular' books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table3.addCell(cell);

					// calculate the average of regular books and display it
					cell = new PdfPCell(new Paragraph(String.valueOf((rglrBooks / (double) regularBooks.size()))));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(cell);

					cell = new PdfPCell(
							new Paragraph("Median of 'Regular' books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table3.addCell(cell);

					// calculate the median of regular books and display it

					if (arrayRegular.length % 2 == 0)
						median = (arrayRegular[arrayRegular.length / 2]
								+ arrayRegular[arrayRegular.length / 2 - 1]) / 2;
					else
						median = arrayRegular[arrayRegular.length / 2];

					cell = new PdfPCell(new Paragraph(String.valueOf(median)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(cell);

					cell = new PdfPCell(new Paragraph("Late returns", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table3.addCell(cell);

					// calculate the percentage of the late returned books for regular books from
					// the total books
					Late = Math.round((regularLateBooks * 100.00) / regularBooks.size());
					cell = new PdfPCell(new Paragraph(String.valueOf(Late) + "%"));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(cell);

					cell = new PdfPCell(new Paragraph("On time returns", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table3.addCell(cell);

					// calculate the percentage of on time returned books for wanted books from the
					// total books
					noLate = (regularNotLateBooks * 100.00) / regularBooks.size();
					cell = new PdfPCell(new Paragraph(String.valueOf(noLate) + "%"));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table3.addCell(cell);

					document.add(table3);
					// Regular Books Info (End)

					max = (int) Arrays.stream(regularDelays).max().getAsLong();

					PdfPTable table4 = new PdfPTable(3);

					cell = new PdfPCell(new Paragraph("'Regular' Books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setColspan(3);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.GRAY);
					table4.addCell(cell);

					cell = new PdfPCell(new Paragraph("Days", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table4.addCell(cell);

					cell = new PdfPCell(new Paragraph("Percentage of total 'Regular' books",
							FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table4.addCell(cell);

					cell = new PdfPCell(new Paragraph("Number of books", FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					table4.addCell(cell);
					document.add(table4);

					if (max >= 10)
						maxIsGreaterThan10(document, max, regularDelays);
					else
						maxIsLesserThan10(document, max, regularDelays);
					PdfPTable table22 = new PdfPTable(3);
					cell = new PdfPCell(new Paragraph("Total 'Regular' Books : " + String.valueOf(regularBooks.size()),
							FontFactory.getFont(FontFactory.TIMES_BOLD)));
					cell.setColspan(3);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBackgroundColor(BaseColor.GRAY);
					table22.addCell(cell);
					document.add(table22);

					document.close();
					// display the file immediately after creating it
					Desktop.getDesktop().open(new File(filePath + "\\Lends Report.pdf"));
					// alert the user that the report has been created successfully
					new Alert(AlertType.INFORMATION,
							"Lends Report has been created successfully\nat " + filePath + "\\Lends Report.pdf",
							ButtonType.OK).show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			break;
		}
		// if the chosen option was 'Return Delays Report'
		case "Return Delays Report": {
			try {
				PdfPCell cell;
				Document document = new Document();
				// check if the file is already open or not
				if (isFileOpen(filePath + "\\Return Delays Report.pdf") == true) {
					// if the file is open , let the user know
					new Alert(AlertType.ERROR, "The file 'Return Delays Report.pdf' is already opened.", ButtonType.OK)
							.show();
				} else {
					// if not then create an instance of a pdf
					PdfWriter.getInstance(document, new FileOutputStream(filePath + "\\Return Delays Report.pdf"));
					document.open();

					// insert the logo in the header of the pdf
					URL url = ReportsController.class.getResource("/images/pdfLogo.png");
					Image img = Image.getInstance(url);
					img.setAlignment(1);
					document.add(img);

					// initialise the header of the pdf file
					Chunk glue = new Chunk(new VerticalPositionMark());
					Paragraph p = new Paragraph(new Date().toString(), FontFactory.getFont(FontFactory.TIMES_BOLD, 20));
					p.add(new Chunk(glue));
					p.add("Return Delay Report");
					document.add(p);
					document.add(new Paragraph(
							"----------------------------------------------------------------------------------------------------------------------------------"));

					// get all the late book from DB
					ArrayList<Book> books = DatabaseController.getAllLateBooks();
					// validate if we have retrieved the data from the data base
					if (books == null) {
						// if not , then let the user know
						alertWarningMessage("Something went wrong while retrieving the books from data base.");
						document.close();
						break;
					}
					for (Book book : books) {
						ArrayList<LentBook> lentBooks = DatabaseController
								.getLateCopiesForSpecificBook(book.getBookID());
						if (lentBooks == null) {
							alertWarningMessage("Something went wrong while retrieving lent books from data base.");
							document.close();
							break;
						} else {
							PdfPTable table = new PdfPTable(2);
							table.setSpacingBefore(100);

							cell = new PdfPCell(new Paragraph("Book ID : " + String.valueOf(book.getBookID()),
									FontFactory.getFont(FontFactory.TIMES_BOLD)));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBackgroundColor(BaseColor.ORANGE);
							table.addCell(cell);

							cell = new PdfPCell(new Paragraph("Book Name : " + book.getName(),
									FontFactory.getFont(FontFactory.TIMES_BOLD)));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBackgroundColor(BaseColor.ORANGE);
							table.addCell(cell);
							document.add(table);

							int i = 0;
							long[] delays = new long[lentBooks.size()];
							for (LentBook lntBook : lentBooks)
								delays[i++] = lntBook.getDueDate().until(lntBook.getReturnDate(), ChronoUnit.DAYS);

							int max = (int) Arrays.stream(delays).max().getAsLong();

							PdfPTable table2 = new PdfPTable(3);

							cell = new PdfPCell(new Paragraph("Days", FontFactory.getFont(FontFactory.TIMES_BOLD)));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
							table2.addCell(cell);

							cell = new PdfPCell(new Paragraph("Percentage of Total Late Users ",
									FontFactory.getFont(FontFactory.TIMES_BOLD)));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
							table2.addCell(cell);

							cell = new PdfPCell(
									new Paragraph("Number of Late Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
							table2.addCell(cell);
							document.add(table2);

							if (max >= 10)
								maxIsGreaterThan10(document, max, delays);
							else
								maxIsLesserThan10(document, max, delays);
						}
					}
					document.close();

					// alert the user that the report has been created successfully
					new Alert(AlertType.INFORMATION, "Return Delays Report has been created successfully\nat "
							+ filePath + "\\Return Delays Report.pdf", ButtonType.OK).show();
					// display the file immediately after creating it
					Desktop.getDesktop().open(new File(filePath + "\\Return Delays Report.pdf"));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}

		case "Other": {
			Document document = new Document();
			try {
				// check if the file is already open or not
				if (isFileOpen(filePath + "\\Report.pdf")) {
					// if the file is open , then let the user know
					new Alert(AlertType.ERROR, "The file 'Report.pdf' is already opened.", ButtonType.OK).show();
				} else {
					// if not then create an instance of a pdf
					PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath + "\\Report.pdf"));
					document.open();
					// insert the logo in the header of the pdf
					URL url = ReportsController.class.getResource("/images/pdfLogo.png");
					Image img = Image.getInstance(url);
					img.setAlignment(1);
					document.add(img);

					// initialise the header of the pdf file
					Chunk glue = new Chunk(new VerticalPositionMark());
					Paragraph p = new Paragraph(new Date().toString(), FontFactory.getFont(FontFactory.TIMES_BOLD, 20));
					p.add(new Chunk(glue));
					p.add("Report");
					document.add(p);
					document.add(new Paragraph(
							"----------------------------------------------------------------------------------------------------------------------------------"));

					// iterate through the menu items to check which one has been selected
					for (MenuItem item : mnuBtn.getItems()) {
						CheckMenuItem checkMenuItem = (CheckMenuItem) item;
						// check if this menu item has been selected
						if (checkMenuItem.isSelected()) {
							// if yes , then check which menu item was it
							String name = checkMenuItem.getText();
							switch (name) {
							case "Orders": {
								ordersPDF(document, writer);
								document.newPage();
								break;
							}
							case "Late Users": {
								lateUsersPDF(document, writer);
								document.newPage();
								break;
							}
							case "Lents": {
								lentsPDF(document, writer);
								document.newPage();
								break;
							}
							case "Suspend Accounts": {
								suspendPDF(document, writer);
								document.newPage();
								break;
							}
							case "Locked Accounts": {
								lockedPDF(document, writer);
								document.newPage();
								break;
							}
							case "Active Accounts": {
								activePDF(document, writer);
								document.newPage();
								break;
							}
							default: {
								alertWarningMessage("No Such Report");
								break;
							}
							}
						}
					}
					document.close();
					// alert the user that the report has been created successfully
					Desktop.getDesktop().open(new File(filePath + "\\Report.pdf"));
					// alert the user that the report has been created successfully
					new Alert(AlertType.INFORMATION,
							"Report has been created successfully\nat " + filePath + "\\Report.pdf", ButtonType.OK)
									.show();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		}

	}

	@FXML
	void imgBackPressed(MouseEvent event) {
		Stage stage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
		Scene scene = SceneController.pop();
		stage.setScene(scene);
		stage.setTitle("Library Management");
	}

	void start(Stage primaryStage, Account loggedLibAccount) throws Exception {
		this.loggedLibAccount = (LibrarianAccount) loggedLibAccount;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/gui/ReportsForm.fxml"));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Reports");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	@FXML
	void initialize() {
		lblReport.setVisible(false);
		mnuBtn.setVisible(false);
		colorToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
			if (rdOther.isSelected() == true) {
				mnuBtn.setVisible(true);
				lblReport.setVisible(true);
				itemOrders.setSelected(true);
			} else {
				mnuBtn.setVisible(false);
				lblReport.setVisible(false);
			}
			if(rdActivityReport.isSelected() == true) {
				txtPath.setVisible(true);
				btnBrowse.setVisible(true);
				cmbSavedReports.setVisible(true);
			}
			else {
				txtPath.setVisible(false);
				btnBrowse.setVisible(false);
				cmbSavedReports.setVisible(false);
			}
		});
		
		mnuBtn.setText(itemOrders.getText());
		itemOrders.setOnAction(e -> {
			mnuBtn.setText(itemOrders.getText());
		});
		itemLateUsers.setOnAction(e -> {
			mnuBtn.setText(itemLateUsers.getText());
		});
		itemLents.setOnAction(e -> {
			mnuBtn.setText(itemLents.getText());
		});
		itemsSuspend.setOnAction(e -> {
			mnuBtn.setText(itemsSuspend.getText());
		});
		itemsLocked.setOnAction(e -> {
			mnuBtn.setText(itemsLocked.getText());
		});
		itemsActive.setOnAction(e -> {
			mnuBtn.setText(itemsActive.getText());
		});
		
		ObservableList<String> options = FXCollections.observableArrayList(DatabaseController.getAllReportsFromDB());
		options.add("Today");
		cmbSavedReports.getItems().addAll(options);
		cmbSavedReports.setValue("Today");
		
		txtPath.setEditable(false);
		final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);
		
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
			 
            @Override
            public void handle(ActionEvent event) {
                File dir = directoryChooser.showDialog(null);
                if (dir != null) {
                    txtPath.setText(dir.getAbsolutePath());
                    filePath = dir.getAbsolutePath();
                } else {
                    txtPath.setText(null);
                }
            }
        });
	}
	
	 private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
	        // Set title for DirectoryChooser
	        directoryChooser.setTitle("Save the report");
	 
	        // Set Initial Directory
	        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	    }

	/**
	 * Show an appropriate alert to the user when an error occur
	 * 
	 * @param msg
	 */
	private void alertWarningMessage(String msg) {
		new Alert(AlertType.WARNING, msg, ButtonType.OK).show();
	}

	/**
	 * Create a report with orders information
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void ordersPDF(Document document, PdfWriter writer) throws DocumentException {

		int orders = 0, width, height;
		PdfPCell cell;
		ArrayList<Book> books = DatabaseController.getAllBooks();
		// validate if the data has been retrieved successfully from the data base
		if (books == null) {
			// if there has been some error , let the user know
			alertWarningMessage("Something went wrong while retrieving the books.");
			return;
		}
		PdfDocument pdfDoc = new PdfDocument();
		// iterate through the books to sum up the total orders
		for (Book tmp : books) {
			orders = orders + tmp.getBookOrders();
		}

		// Users Info Table (Start)
		PdfPTable table = new PdfPTable(6);
		table.setSpacingBefore(100);

		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph("Orders Report", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(6);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book Author", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book Edition", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book Orders", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Orders %", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		DefaultPieDataset defaultCategoryDataset = new DefaultPieDataset();
		// iterate through the books and display their data
		for (Book book : books) {
			cell = new PdfPCell(new Paragraph(String.valueOf(book.getBookID())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(book.getName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(book.getAuthor()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(book.getEdition()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(book.getBookOrders())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			double Per = Math.round((book.getBookOrders() * 100.00) / orders);
			cell = new PdfPCell(new Paragraph(String.valueOf(Per) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			defaultCategoryDataset.setValue(book.getName() + " " + String.valueOf(Per) + "%", Per);
		}
		// display the total orders of the library
		cell = new PdfPCell(
				new Paragraph("Total Orders : " + String.valueOf(orders), FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(7);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);
		document.add(table);

		/* create pie char */
		document.newPage();

		JFreeChart jFreeChart = ChartFactory.createPieChart("Books Orders Chart", defaultCategoryDataset, true, false,
				false);
		PdfContentByte pdfContentByte = writer.getDirectContent();
		width = 400; // width of PieChart
		height = 300; // height of pieChart
		PdfTemplate pdfTemplate = pdfContentByte.createTemplate(width, height);

		// create graphics
		@SuppressWarnings("deprecation")
		Graphics2D graphics2d = pdfTemplate.createGraphics(width, height, new DefaultFontMapper());

		// create rectangle
		java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);

		jFreeChart.draw(graphics2d, rectangle2d);

		graphics2d.dispose();
		pdfContentByte.addTemplate(pdfTemplate, 100, 500);
		/* end pie chart */
	}

	/**
	 * Creates a report with late users information
	 * 
	 * @param document
	 * @throws DocumentException
	 */

	private void lateUsersPDF(Document document, PdfWriter writer) throws DocumentException {

		int notReturned = 0, returned = 0;
		PdfPCell cell;
		PdfPTable table = new PdfPTable(5);
		table.setSpacingBefore(100);

		ArrayList<LentBook> lents = DatabaseController.getLentBookList(0);
		// validate if the data has been retrieved successfully from the data base
		if (lents == null) {
			// if there has been some error , let the user know
			alertWarningMessage("Something went wrong.");
			return;
		}
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph("Late users Report", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("User ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Issue Date", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Days late", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Returned ?", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		// iterate through the lent books list and display it's data
		for (LentBook lent : lents) {
			cell = new PdfPCell(new Paragraph(String.valueOf(lent.getUserID())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(lent.getBook().getBookID())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(lent.getIssueDate().toString()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			// validate if the user has returned this lent book or not
			if (lent.isReturned() == false) {
				notReturned++;
				cell = new PdfPCell(new Paragraph("--"));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph("No"));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			} else {
				returned++;
				// if the user has returned the book , then calculate the difference between
				// dates and display it
				cell = new PdfPCell(
						new Paragraph(String.valueOf(lent.getDueDate().until(lent.getReturnDate(), ChronoUnit.DAYS))));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph("Yes"));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
		}

		// display the total late lents
		cell = new PdfPCell(new Paragraph("Total late Lents : " + String.valueOf(lents.size()),
				FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(7);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);
		document.add(table);

		/* create pie char */
		document.newPage();
		DefaultPieDataset defaultCategoryDataset = new DefaultPieDataset();
		double size = Math.round((returned * 100.00) / lents.size());
		defaultCategoryDataset.setValue("Returned " + String.valueOf(size) + "%", size);
		size = Math.round((notReturned * 100.00) / lents.size());
		defaultCategoryDataset.setValue("Not Returned " + String.valueOf(size) + "%", size);
		JFreeChart jFreeChart = ChartFactory.createPieChart("Late Lents Returns Chart", defaultCategoryDataset, true,
				false, false);
		PdfContentByte pdfContentByte = writer.getDirectContent();
		int width = 400; // width of PieChart
		int height = 300; // height of pieChart
		PdfTemplate pdfTemplate = pdfContentByte.createTemplate(width, height);

		// create graphics
		@SuppressWarnings("deprecation")
		Graphics2D graphics2d = pdfTemplate.createGraphics(width, height, new DefaultFontMapper());

		// create rectangle
		java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);

		jFreeChart.draw(graphics2d, rectangle2d);

		graphics2d.dispose();
		pdfContentByte.addTemplate(pdfTemplate, 100, 500);
		/* end pie chart */
	}

	/**
	 * Creates a report with all lents information
	 * 
	 * @param document
	 * @throws DocumentException
	 */

	private void lentsPDF(Document document, PdfWriter writer) throws DocumentException {

		int late = 0, notLate = 0;
		PdfPCell cell;
		PdfPTable table = new PdfPTable(6);
		table.setSpacingBefore(100);

		ArrayList<LentBook> lents = DatabaseController.getLentBookList(-1);
		// validate if the data has been retrieved successfully from the data base
		if (lents == null) {
			// if there has been some error , let the user know
			alertWarningMessage("Something went wrong while retrieving lent books.");
			return;
		}
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph(String.valueOf(LocalDate.now().getYear()) + " Lents Report",
				FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(6);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("User ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Book ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Serial Number", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Issue Date", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Due Date", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Returned ?", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		// iterate through lent books list and display it's data
		for (LentBook book : lents) {
			if (book.getBookCopy() != null) {
				if (book.getIssueDate().getYear() == LocalDate.now().getYear()) {
					cell = new PdfPCell(new Paragraph(String.valueOf(book.getUserID())));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(String.valueOf(book.getBook().getBookID())));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(book.getBookCopy().getSerialNumber()));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(book.getIssueDate().toString()));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					cell = new PdfPCell(new Paragraph(book.getDueDate().toString()));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);

					// validate if the user has returned this lent book or not
					if (book.isReturned() == false) {
						// if not then display 'No'
						cell = new PdfPCell(new Paragraph("No"));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					} else {
						// otherwise display 'yes' because then the user has returned the book
						cell = new PdfPCell(new Paragraph("Yes"));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}
					// checks if the user is late or not
					if (book.isLate() == true)
						// if he is , count it
						late++;
					else
						// if not , count is as not late
						notLate++;
				}
			}
		}

		document.add(table);
		PdfPTable table2 = new PdfPTable(3);
		table2.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph("Late Report", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Number of late lents", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(
				new Paragraph("Percentage of total late lents", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Number of Late Lents", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		// display how many late lents there was
		cell = new PdfPCell(new Paragraph(String.valueOf(late)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);
		// calculate it's percentage of the total lent books
		double latePer = Math.round((late * 100.00) / lents.size());
		cell = new PdfPCell(new Paragraph(String.valueOf(latePer) + "%"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Number of Not Late Lents", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);
		// display how many user has not been late to return a book
		cell = new PdfPCell(new Paragraph(String.valueOf(notLate)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);
		// calculate it's percentage of the total lent books
		double notLatePer = Math.round((notLate * 100.00) / lents.size());
		cell = new PdfPCell(new Paragraph(String.valueOf(notLatePer) + "%"));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		document.add(table2);

		/* create pie char */
		document.newPage();
		DefaultPieDataset defaultCategoryDataset = new DefaultPieDataset();
		defaultCategoryDataset.setValue("Late " + String.valueOf(latePer) + "%", latePer);
		defaultCategoryDataset.setValue("Not Late " + String.valueOf(notLatePer) + "%", notLatePer);
		JFreeChart jFreeChart = ChartFactory.createPieChart("Lents Chart", defaultCategoryDataset, true, false, false);
		PdfContentByte pdfContentByte = writer.getDirectContent();
		int width = 400; // width of PieChart
		int height = 300; // height of pieChart
		PdfTemplate pdfTemplate = pdfContentByte.createTemplate(width, height);

		// create graphics
		@SuppressWarnings("deprecation")
		Graphics2D graphics2d = pdfTemplate.createGraphics(width, height, new DefaultFontMapper());

		// create rectangle
		java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);

		jFreeChart.draw(graphics2d, rectangle2d);

		graphics2d.dispose();
		pdfContentByte.addTemplate(pdfTemplate, 100, 500);
		/* end pie chart */
	}

	/**
	 * Creates a report with the suspended users information
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void suspendPDF(Document document, PdfWriter writer) throws DocumentException {
		PdfPCell cell;
		PdfPTable table = new PdfPTable(5);
		table.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph("Suspended Users Report", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("First Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Last Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Mobile number", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Delays", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		ArrayList<UserAccount> accounts = DatabaseController.getUserAccounts(accountStatus.Suspended);
		// validate if the data has been retrieved successfully from the data base
		if (accounts == null) {
			// if some error happened , let the user know
			alertWarningMessage("Something went wrong while retrieving the suspended accounts.");
			return;
		}

		// iterate through the suspended accounts list and display it's data
		for (UserAccount acc : accounts) {
			cell = new PdfPCell(new Paragraph(String.valueOf(acc.getID())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getFirstName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getLastName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getMobileNum()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(acc.getDelays())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		document.add(table);

		PdfPTable table2 = new PdfPTable(3);
		table2.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Number of users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Percentage of total users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Suspended Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph(String.valueOf(accounts.size())));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		// get the total users in the system
		int total = DatabaseController.getTableRowsNumber("account", UserType.User.toString());
		// calculate the percentage of the suspended accounts from the total accounts
		if (total != 0) {
			cell = new PdfPCell(new Paragraph(String.valueOf(Math.round((accounts.size() * 100.00) / total)) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.addCell(cell);
		} else {
			cell = new PdfPCell(new Paragraph(String.valueOf(total) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.addCell(cell);
		}
		// display the number of total accounts
		cell = new PdfPCell(
				new Paragraph("Total Users : " + String.valueOf(total), FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table2.addCell(cell);

		document.add(table2);
	}

	/**
	 * Creates a report with locked users information
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void lockedPDF(Document document, PdfWriter writer) throws DocumentException {
		PdfPCell cell;
		PdfPTable table = new PdfPTable(5);
		table.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph("Locked Users Report", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("First Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Last Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Mobile number", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Delays", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		ArrayList<UserAccount> accounts = DatabaseController.getUserAccounts(accountStatus.Locked);
		// validate if the data has been retrieved successfully from the data base
		if (accounts == null) {
			// if some error happened , let the user know
			alertWarningMessage("Something went wrong.");
			return;
		}
		// iterating through the locked accounts lists and display it's data
		for (UserAccount acc : accounts) {
			cell = new PdfPCell(new Paragraph(String.valueOf(acc.getID())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getFirstName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getLastName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getMobileNum()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(acc.getDelays())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		document.add(table);

		PdfPTable table2 = new PdfPTable(3);
		table2.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Number of users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Percentage of total users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Locked Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);
		// display the total number of locked accounts in the system
		cell = new PdfPCell(new Paragraph(String.valueOf(accounts.size())));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);
		// get the total users in the system
		int total = DatabaseController.getTableRowsNumber("account", UserType.User.toString());
		// calculate the percentage of the locked accounts from the total accounts
		if (total != 0) {
			cell = new PdfPCell(new Paragraph(String.valueOf(Math.round((accounts.size() * 100.00) / total)) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.addCell(cell);
		} else {
			cell = new PdfPCell(new Paragraph(String.valueOf(total) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.addCell(cell);
		}
		// display the number of total accounts
		cell = new PdfPCell(
				new Paragraph("Total Users : " + String.valueOf(total), FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table2.addCell(cell);

		document.add(table2);
	}

	/**
	 * Creates a report with active users information
	 * 
	 * @param document
	 * @throws DocumentException
	 */
	private void activePDF(Document document, PdfWriter writer) throws DocumentException {
		PdfPCell cell;
		PdfPTable table = new PdfPTable(5);
		table.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph("Active Users Report", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("ID", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("First Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Last Name", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Mobile number", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph("Delays", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(cell);

		ArrayList<UserAccount> accounts = DatabaseController.getUserAccounts(accountStatus.Active);
		// validate if the data has been retrieved successfully from the data base
		if (accounts == null) {
			// if some error happened , let the user know
			alertWarningMessage("Something went wrong while retrieving the active accounts.");
			return;
		}
		// iterating through the active accounts list and display it's data
		for (UserAccount acc : accounts) {
			cell = new PdfPCell(new Paragraph(String.valueOf(acc.getID())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getFirstName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getLastName()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(acc.getMobileNum()));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(acc.getDelays())));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		document.add(table);

		PdfPTable table2 = new PdfPTable(3);
		table2.setSpacingBefore(100);
		// inserting header cells into the table
		cell = new PdfPCell(new Paragraph(" ", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Number of user", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Percentage of total users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Active Users", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph(String.valueOf(accounts.size())));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		// get the total users in the system
		int total = DatabaseController.getTableRowsNumber("account", UserType.User.toString());
		if (total != 0) {
			// calculate the percentage of the active accounts from the total accounts
			cell = new PdfPCell(new Paragraph(String.valueOf(Math.round((accounts.size() * 100.00) / total)) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.addCell(cell);
		} else {
			// calculate the percentage of the active accounts from the total accounts
			cell = new PdfPCell(new Paragraph(String.valueOf(total) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table2.addCell(cell);
		}
		// display the number of total accounts
		cell = new PdfPCell(
				new Paragraph("Total Users : " + String.valueOf(total), FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.GRAY);
		table2.addCell(cell);

		document.add(table2);
	}

	/**
	 * Checks if the file with the specified path is opened or not. returns false if
	 * and only if the file is not opened. Otherwise returns true.
	 * 
	 * @param path
	 * @return true if the file is open , otherwise false
	 */
	private boolean isFileOpen(String path) {
		File file = new File(path);

		if (file.exists() == false)
			return false;
		else {
			// try to rename the file with the same name
			File sameFileName = new File(path);
			if (file.renameTo(sameFileName) == true) {
				// if the file is renamed then it's not open
				return false;
			}
			// then the file is open
			return true;
		}

	}

	/**
	 * Distribute's a table according to the max with a calculated algorithm to
	 * distribute the max delay days to 10 cells in the table. Display the table in
	 * the PDF file. display the content if the delays array into the created table.
	 * 
	 * @param document - the writer to the pdf file.
	 * @param max      - the max delays.
	 * @param delays   = the delays array for a specific book
	 * @throws DocumentException
	 */
	public static void maxIsGreaterThan10(Document document, int max, long[] delays) throws DocumentException {

		PdfPCell cell;
		PdfPTable table = new PdfPTable(3);

		int[] count = new int[10];
		int c = 0, mode = 0;
		double j = 10.00;

		for (int i = 1; i < 11; i++, j = j + 10.00) {
			Per[i] = (int) Math.ceil(max * j / 100.00);
			for (int k = 0; k < delays.length; k++) {
				if (((int) delays[k]) >= Per[i - 1] && ((int) delays[k]) < Per[i]) {
					count[c]++;
				}
				if (mode == 0)
					if (((int) delays[k]) == max)
						count[9]++;
			}
			mode = 1;
			cell = new PdfPCell(new Paragraph(String.valueOf(Per[i - 1]) + " - " + String.valueOf(Per[i])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(Math.round((count[c] * 100.00) / delays.length)) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Paragraph(String.valueOf(count[c])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			c++;
		}
		document.add(table);

		PdfPTable table2 = new PdfPTable(2);

		cell = new PdfPCell(new Paragraph("Average (Lates)", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Median (Lates)", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		double avg = 0;
		for (int i = 0; i < delays.length; i++)
			avg = avg + delays[i];
		cell = new PdfPCell(new Paragraph(String.valueOf(avg / delays.length)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		int median;
		if (delays.length % 2 == 0)
			median = ((int) delays[delays.length / 2] + (int) delays[delays.length / 2 - 1]) / 2;
		else
			median = (int) delays[delays.length / 2];

		cell = new PdfPCell(new Paragraph(String.valueOf(median)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		document.add(table2);
	}

	/**
	 * Distributes a table according to the max with a 1 day difference. Display the
	 * table in the PDF file. display the content if the delays array into the
	 * created table.
	 * 
	 * @param document - the writer to the pdf file.
	 * @param max      - the max delays.
	 * @param delays   = the delays array for a specific book
	 * @throws DocumentException
	 */
	public static void maxIsLesserThan10(Document document, int max, long[] delays) throws DocumentException {

		PdfPCell cell;
		PdfPTable table = new PdfPTable(3);

		int[] array = new int[max + 1];

		for (int i = 0; i < array.length; i++)
			array[i] = i;

		int[] count = new int[max];
		int c = 0, mode = 0;

		for (int i = 1; i < array.length; i++) {
			for (int k = 0; k < delays.length; k++) {
				if ((int) delays[k] >= array[i - 1] && (int) delays[k] < array[i]) {
					count[c]++;
				}
				if (mode == 0)
					if (delays[k] == max)
						count[max - 1]++;

			}
			mode = 1;
			cell = new PdfPCell(new Paragraph(String.valueOf(array[i - 1]) + " - " + String.valueOf(array[i])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(String.valueOf(Math.round((count[c] * 100.00) / delays.length)) + "%"));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(String.valueOf(count[c])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			c++;
		}
		document.add(table);
		PdfPTable table2 = new PdfPTable(2);

		cell = new PdfPCell(new Paragraph("Average (Lates)", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		cell = new PdfPCell(new Paragraph("Median (Lates)", FontFactory.getFont(FontFactory.TIMES_BOLD)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table2.addCell(cell);

		double avg = 0;
		for (int i = 0; i < delays.length; i++)
			avg = avg + delays[i];
		cell = new PdfPCell(new Paragraph(String.valueOf(avg / delays.length)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		int median;
		if (delays.length % 2 == 0)
			median = ((int) delays[delays.length / 2] + (int) delays[delays.length / 2 - 1]) / 2;
		else
			median = (int) delays[delays.length / 2];
		cell = new PdfPCell(new Paragraph(String.valueOf(median)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table2.addCell(cell);

		document.add(table2);
	}
}
