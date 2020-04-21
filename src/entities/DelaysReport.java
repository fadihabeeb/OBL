/**
 * entities package contains all entities used by the library controllers 
 */
package entities;

public class DelaysReport {
	private float lateNumberAverage;
	private float lateNumberMedian;
	private float lateNumberDecimalDistribution;
	private float lateDurationAverage;
	private float lateDurationMedian;
	private float lateDurationDecimalDistribution;
	private float bookAverage;
	private float bookMedian;
	private float bookDecimalDistribution;
	
	
	public DelaysReport(float lateNumberAverage, float lateNumberMedian, float lateNumberDecimalDistribution,
			float lateDurationAverage, float lateDurationMedian, float lateDurationDecimalDistribution,
			float bookAverage, float bookMedian, float bookDecimalDistribution) {
		this.lateNumberAverage = lateNumberAverage;
		this.lateNumberMedian = lateNumberMedian;
		this.lateNumberDecimalDistribution = lateNumberDecimalDistribution;
		this.lateDurationAverage = lateDurationAverage;
		this.lateDurationMedian = lateDurationMedian;
		this.lateDurationDecimalDistribution = lateDurationDecimalDistribution;
		this.bookAverage = bookAverage;
		this.bookMedian = bookMedian;
		this.bookDecimalDistribution = bookDecimalDistribution;
	}

	

	/**
	 * Gets the late Number Average.
	 * 
	 * @return lateNumberAverage
	 */
	public float getLateNumberAverage() {
		return lateNumberAverage;
	}

	/**
	 * Instantiates the late Number Average
	 * 
	 * @param lateNumberAverage set the late Number Average
	 */
	public void setLateNumberAverage(float lateNumberAverage) {
		this.lateNumberAverage = lateNumberAverage;
	}

	/**
	 * Gets the late Number median.
	 * 
	 * @return lateNumbermedian
	 */
	public float getLateNumberMedian() {
		return lateNumberMedian;
	}

	/**
	 * Instantiates the late Number median
	 * 
	 * @param lateNumbermedian set the late Number median
	 */
	public void setLateNumberMedian(float lateNumberMedian) {
		this.lateNumberMedian = lateNumberMedian;
	}

	/**
	 * Gets the late Number Decimal Distribution
	 * 
	 * @return lateNumberDecimalDistribution
	 */
	public float getLateNumberDecimalDistribution() {
		return lateNumberDecimalDistribution;
	}

	/**
	 * Instantiates the late Number Decimal Distribution
	 * 
	 * @param lateNumberDecimalDistribution
	 */
	public void setLateNumberDecimalDistribution(float lateNumberDecimalDistribution) {
		this.lateNumberDecimalDistribution = lateNumberDecimalDistribution;
	}

	/**
	 * Gets the late Duration Average
	 * 
	 * @return lateDurationAverage
	 */
	public float getLateDurationAverage() {
		return lateDurationAverage;
	}

	/**
	 * Instantiates the late Duration Average
	 * 
	 * @param lateDurationAverage
	 */
	public void setLateDurationAverage(float lateDurationAverage) {
		this.lateDurationAverage = lateDurationAverage;
	}

	/**
	 * Gets the late Duration Median
	 * 
	 * @return lateDurationMedian
	 */
	public float getLateDurationMedian() {
		return lateDurationMedian;
	}

	/**
	 * Instantiates the late Duration Median
	 * 
	 * @param lateDurationMedian
	 */
	public void setLateDurationMedian(float lateDurationMedian) {
		this.lateDurationMedian = lateDurationMedian;
	}

	/**
	 * Gets the late Duration Decimal Distribution
	 * 
	 * @return lateDurationDecimalDistribution
	 */
	public float getLateDurationDecimalDistribution() {
		return lateDurationDecimalDistribution;
	}

	/**
	 * Instantiates the late Duration Decimal Distribution
	 * 
	 * @param lateDurationDecimalDistribution
	 */
	public void setLateDurationDecimalDistribution(float lateDurationDecimalDistribution) {
		this.lateDurationDecimalDistribution = lateDurationDecimalDistribution;
	}

	/**
	 * Gets the Average books
	 * 
	 * @return bookAverage
	 */
	public float getBookAverage() {
		return bookAverage;
	}

	/**
	 * Instantiates the Average books
	 * 
	 * @param bookAverage
	 */
	public void setBookAverage(float bookAverage) {
		this.bookAverage = bookAverage;
	}

	/**
	 * Gets the Median book
	 * 
	 * @return bookMedian
	 */
	public float getBookMedian() {
		return bookMedian;
	}

	/**
	 * Instantiates the book Median
	 * 
	 * @param bookMedian
	 */
	public void setBookMedian(float bookMedian) {
		this.bookMedian = bookMedian;
	}

	/**
	 * Gets the book Decimal Distribution
	 * 
	 * @return bookDecimalDistribution
	 */
	public float getBookDecimalDistribution() {
		return bookDecimalDistribution;
	}

	/**
	 * Instantiates the book Decimal Distribution
	 * 
	 * @param bookDecimalDistribution
	 */
	public void setBookDecimalDistribution(float bookDecimalDistribution) {
		this.bookDecimalDistribution = bookDecimalDistribution;
	}

}
