package rejecting;

import core.Box;

/**
 * @author nvp
 * rejects a box if its lower bound is higher than a known minimum value of the function
 */
class RejectorByValue extends BaseRejector {
	private volatile double lowBoundMaxValue = Double.POSITIVE_INFINITY;
	private double lowBoundMaxValueDelta;
	private int updatesCount;

	public RejectorByValue() {
		this(Double.MAX_VALUE);
	}
	
	public RejectorByValue(double startLimit) {
		resetStatistics();
		lowBoundMaxValue = startLimit;		
	}

	/**
	 * The main function. 
	 * Returns true if the low bound of the function value estimation is lower than known minimum value.
	 */
	@Override
	public boolean checkPassed(Box box) {
		return checkByValue(box); 
	}
	boolean checkByValue(Box box) {
		double lo = box.getFunctionValue().lo();
		if (lo > lowBoundMaxValue) {
			return false;
		}	
		probeNewLimit(box.getFunctionValue().hi());
		return true;		
	}
	
	/**
	 * Sets new threshold value. All boxes with higher low bound will be rejected  
	 * @param possibleNewVal -- new potential value for @lowBoundMaxValue 
	 * @return true if @lowBoundMaxValue was improved
	 */
	public boolean probeNewLimit(double possibleNewVal) {
		if (lowBoundMaxValue > possibleNewVal) {
			setLowBoundMaxValue(possibleNewVal);
			return true;
		}
		return false;
	}

	void setLowBoundMaxValue(double val) {
		assert(val < lowBoundMaxValue);
		double d = lowBoundMaxValue - val;
		lowBoundMaxValueDelta += d;
		lowBoundMaxValue = val;
		updatesCount++;
	}

	public double getLowBoundMaxValue() {
		return lowBoundMaxValue;
	}
	public double getLowBoundMaxValueLimitDelta() {
		return lowBoundMaxValueDelta;
	}
	public int getValueLimitUpdatesCount() {
		return updatesCount;
	}

	public void resetStatistics() {
		lowBoundMaxValueDelta = 0;
		updatesCount = 0;
	}
}
