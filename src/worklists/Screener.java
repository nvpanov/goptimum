package worklists;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionFactory;
import functions.FunctionNEW;

class Screener {
	private volatile double lowBoundMaxValue = Double.MAX_VALUE;
	private double lowBoundMaxValueDelta;
	private int updatesCount;
	private boolean useDerivativesCheck = true;
	
	public Screener(double startLimit) {
		resetStatistics();
		lowBoundMaxValue = startLimit;		
	}
	// for test purposes mostly
	public void switchOffDerivativesCheck() {
		useDerivativesCheck = false;
	}

	public boolean checkPassed(Box box) {
		return checkByValue(box) && checkDerivatives(box); 
	}

	/*  
	 * SECOND derivative
	 * a function can reach a minimum on an interval only if
	 * 1) this is a border point of initial search area
	 * 2) the function is concave on this interval.
 	 */
	protected boolean checkDerivatives(Box box) {
		if (!useDerivativesCheck)
			return true;
		if (isBorder(box)) // we can't screen out any border point of original search area
			return true;   // because of derivatives. 
		return check1Derivative(box) & check2Derivative(box);
	}
	private boolean isBorder(Box box) {
		for (int i = box.getDimension()-1; i >= 0; --i) {
			if (box.getInterval(i).wid() == 0) // A workaround for edges. Worklist adds zero-width
												// edges for initial search area. 
												// See Worklist.addAreaAndAllEges()
				return true;
		}
		return false;			
	}
	/* FIRST derivative
	 * A point could be a minimum or a maximum if and only if the derivative
	 * is equal to zero in this point. 
	 * Therefore interval extensions of all partial derivatives have to contain zero.
	 * The only exception are border points. Consider the following case:
	 * f(x) = x, min_{0<x<1}(f) = f(0), but f'(x) != 0.
	 * BUT instead of performing such checks each time we just have to add all ages to the
	 * working list from the very beginning! Much simple and less code: )
	 * Because of this it doesn't screen out boxes with at least one side width = 0
	 */
	 
	protected boolean check1Derivative(Box box) {
		FunctionNEW function = FunctionFactory.getTargetFunction();
		for (int i = box.getDimension()-1; i >= 0; --i) {
			assert (box.getInterval(i).wid() != 0);
			
			RealInterval f1d = function.calculate1Derivative(box, i);
			if (f1d == null)
				break;
			if (!f1d.contains(0))
				return false;
		}
		return true; // check passed
	}
	/*
	 * A twice differentiable function is convex on an interval if and only if its 
	 * second derivative is non-negative there; this gives a practical test for convexity.
	 */
	protected boolean check2Derivative(Box box) {
		FunctionNEW function = FunctionFactory.getTargetFunction();
		for (int i = box.getDimension()-1; i >= 0; --i) {
			assert (box.getInterval(i).wid() != 0);
			
			RealInterval f2d = function.calculate2Derivative(box, i);
			if (f2d == null)
				break;
			if (f2d.hi() < 0) 
				return false;
		}
		return true;
	}
	boolean checkByValue(Box box) {
		double lo = box.getFunctionValue().lo();
		if (lo > lowBoundMaxValue) {
//			System.out.println("   - faild checkByValue: " + box);
			return false;
		}	
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
//System.out.println("     = new value limit is " + lowBoundMaxValue);		
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

	// NO need in such function! we just add all the ages to the list from the very beginning
	// much faster and less bugs :)
/*	protected void deflate(Box box, Box initialSearchArea, Function function) {
		final int dim = initialSearchArea.getDimension();
		assert(dim == box.getDimension());
		for (int i = 0; i < dim; i++) {
			RealInterval boxI = box.getInterval(i);
			final RealInterval borderI = initialSearchArea.getInterval(i);
			assert( borderI.contains(boxI) ); // all ages have to have at least one common point
			if (borderI.contains(boxI.lo()) && !borderI.contains(boxI.hi())) {
				box.setInterval(i, boxI.lo());
			} else if (!borderI.contains(boxI.lo()) && borderI.contains(boxI.hi())) {
				box.setInterval(i, boxI.hi());
			} else if (borderI.contains(boxI.lo()) && borderI.contains(boxI.hi())) {
				// do nothing -- can't deflate this side
			} else {
				assert(false);
			}
		}
		function.calculate(box);
	}
*/		
}
