/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

public class Book {
	private int bookID;
	private String name;
	private String author;
	private String edition;
	private int  printYear;
	private String subject;
	private String description;
	private int catalog;
	private String tableOfContents;
	private String shelf;
	private int copiesNumber;
	private bookType type;
	private int availableCopies;
	private int bookOrders;
	
	public enum bookType {
		Wanted, Regular
	};	
	
	public Book(int bookID, String name, String author, String edition, int printYear, String subject,
			String description, int catalog, String tableOfContents, String shelf, int copiesNumber, bookType type, int availableCopies) {
		super();
		this.bookID = bookID;
		this.name = name;
		this.author = author;
		this.edition = edition;
		this.printYear = printYear;
		this.subject = subject;
		this.description = description;
		this.catalog = catalog;
		this.tableOfContents = tableOfContents;
		this.shelf = shelf;
		this.copiesNumber = copiesNumber;
		this.type = type;
		this.availableCopies = availableCopies;
		this.bookOrders = bookOrders;
	}
	
  	public Book() {
	}

	/**
  	 * Gets the book ID.
  	 * 
  	 * @return  bookID
  	 */
	public int getBookID() {
		return bookID;
	}
	
	/**
	 * Instantiates book ID
	 * @param  bookID 
	 */
	public void setBookID(int bookID) {
		this.bookID = bookID;
	}
	
  	/**
  	 * Gets the name.
  	 * 
  	 * @return  name
  	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Instantiates name
	 * @param  name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
  	/**
  	 * Gets the author.
  	 * 
  	 * @return  author.
  	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Set the author
	 * @param  author 
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
  	/**
  	 * Gets the edition.
  	 * 
  	 * @return  edition.
  	 */
	public String getEdition() {
		return edition;
	}
	/**
	 * Set the edition
	 * @param  edition 
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}

  	/**
	 * @return the printYear
	 */
	public int getPrintYear() {
		return printYear;
	}
	
	/**
	 * @param printYear
	 */
	public void setPrintYear(int printYear) {
		this.printYear = printYear;
	}
	/**
  	 * Gets the Subject.
  	 * 
  	 * @return  Subject.
  	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * Set subject
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
  	/**
  	 * Gets   description.
  	 * 
  	 * @return  description.
  	 */
	public String getDescription() {
		return description;
	}

	/**
	 * set description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
  	/**
  	 * Gets   catalog.
  	 * 
  	 * @return  catalog.
  	 */
	public int getCatalog() {
		return catalog;
	}
	/**
	 * set catalog
	 * @param catalog
	 */
	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}
	/**
	 * Get table of content
	 * @return table of contents path
	 */
	public String getTableOfContents() {
		return tableOfContents;
	}
	/**
	 * Set tableOfContents path
	 * @param tableOfContents
	 */
	public void setTableOfContents(String tableOfContents) {
		this.tableOfContents = tableOfContents;
	}
	/**
  	 * Gets   shelf.
  	 * 
  	 * @return  shelf
  	 */
	public String getShelf() {
		return shelf;
	}
	/**
	 * set shelf
	 * @param shelf
	 */
	public void setShelf(String shelf) {
		this.shelf = shelf;
	}
	/**
  	 * Gets   copies Number.
  	 * 
  	 * @return  copiesNumber.
  	 */
	public int getCopiesNumber() {
		return copiesNumber;
	}
	/**
	 * Set copiesNumber
	 * @param  copiesNumber 
	 */
	public void setCopiesNumber(int copiesNumber) {
		this.copiesNumber = copiesNumber;
	}
	
	public bookType getBookType() {
		return type;
	}
	
	/**
	 * set BookType
	 * @param type
	 */
	public void setBookType(bookType type) {
		this.type = type;
	}
	
	/**
	 * get available book copies
	 * @return available copies
	 */
	public int getAvailableCopies() {
		return availableCopies;
	}
	
	/**
	 * set available copies
	 * @param availableCopies
	 */
	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
	}
	
	/**
	 * get book orders number
	 * @return book orders number
	 */
	public int getBookOrders() {
		return bookOrders;
	}
	
	/**
	 * Set book orders number
	 * @param bookOrders
	 */
	public void setBookOrders(int bookOrders) {
		this.bookOrders = bookOrders;
	}

}