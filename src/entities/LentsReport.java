/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

public class LentsReport {
	
	
       public LentsReport(float bookLendTimeAverage, float bookMedianLendTime, float bookLendTimeDecimalDistribution) {
		super();
		this.bookLendTimeAverage = bookLendTimeAverage;
		this.bookMedianLendTime = bookMedianLendTime;
		this.bookLendTimeDecimalDistribution = bookLendTimeDecimalDistribution;
	}
	private float bookLendTimeAverage;
       private float bookMedianLendTime;
       private float bookLendTimeDecimalDistribution;
        
        
    	/**
    	 * Gets the book lend time average.
    	 * 
    	 * @return  bookLendTimeAverage
    	 */
		public float getBookLendTimeAverage() {
			return bookLendTimeAverage;
		}
		/**
		 * Instantiates the time average of book lend
		 * @param setId set the id
		 */
		public void setBookLendTimeAverage(float bookLendTimeAverage) {
			this.bookLendTimeAverage = bookLendTimeAverage;
		}
    	/**
    	 * Gets the Median Lend Time  .
    	 * 
    	 * @return  bookLendTimeAverage
    	 */
		public float getBookMedianLendTime() {
			return bookMedianLendTime;
		}
		/**
		 * Instantiates Median Lend Time.
		 * @param bookMedianLendTime set the median time
		 */
		public void setBookMedianLendTime(float bookMedianLendTime) {
			this.bookMedianLendTime = bookMedianLendTime;
		}
    	/**
    	 * Gets the  Decimal Distribution  .
    	 * 
    	 * @return  getBookLendTimeDecimalDistribution
    	 */
		public float getBookLendTimeDecimalDistribution() {
			return bookLendTimeDecimalDistribution;
		}
		/**
		 * Instantiates book Lend Time Decimal Distribution.
		 * @param bookLendTimeDecimalDistribution set the  Decimal Distribution
		 */
		public void setBookLendTimeDecimalDistribution(float bookLendTimeDecimalDistribution) {
			this.bookLendTimeDecimalDistribution = bookLendTimeDecimalDistribution;
		}
      
}
