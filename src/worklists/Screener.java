package worklists;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.Function;

class Screener {
	private int notImpl = 1; // stub
	private volatile double lowBoundMaxValue = Double.MAX_VALUE;
	private double lowBoundMaxValueDelta;
	private int updatesCount;
	private Function function;

	public Screener(double startLimit) {
		resetStatistics();
		lowBoundMaxValue = startLimit;		
	}
	public void setFunction(Function f) {
		function = f;
	}

	public boolean checkPassed(Box box) {
		return (checkByValue(box) && check1Derivative(box) && check2Derivative());
	}

	private boolean check2Derivative() {
		if(notImpl > 0) {
			notImpl--;
//			System.out.println("Screener.check2Derivative() not implemented and always returns true!");
		}
		return true;
	}

	protected boolean check1Derivative(Box box) {
		/*
		assert(function != null); // call setFunction()
		for (int i = box.getDimension()-1; i >= 0; --i) {
			RealInterval f1d = function.calc1Derivative(box, i);
			if (f1d == null)
				break;
			if (!f1d.contains(0))
				return false;
		}
		*/
		return true;
	}

	boolean checkByValue(Box box) {
		if (box.getFunctionValue().lo() > lowBoundMaxValue)
			return false;
	
		probeNewLimit(box.getFunctionValue().hi());
		return true;		
	}
	

	public boolean probeNewLimit(double possibleNewVal) {
		if (lowBoundMaxValue > possibleNewVal) {
			setLowBoundMaxValue(possibleNewVal);
			return true;
		}
		return false;
	}
	public double getLowBoundMaxValue() {
		return lowBoundMaxValue;
	}

	protected void setLowBoundMaxValue(double val) {
		assert(val < lowBoundMaxValue);
		double d = lowBoundMaxValue - val;
		lowBoundMaxValueDelta += d;
		lowBoundMaxValue = val;
		updatesCount++;
	}

	public double getLowBoundMaxValueLimitDelta() {
		return lowBoundMaxValueDelta;
	}
	public int getValueLimitUpdatesCount() {
		return updatesCount;
	}

	void resetStatistics() {
		lowBoundMaxValueDelta = 0;
		updatesCount = 0;
	}

	
}
