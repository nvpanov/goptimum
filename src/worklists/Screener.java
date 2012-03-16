package worklists;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionFactory;
import functions.FunctionNEW;

class Screener {
	private int notImpl = 1; // stub
	private volatile double lowBoundMaxValue = Double.MAX_VALUE;
	private double lowBoundMaxValueDelta;
	private int updatesCount;
	private boolean use1Derivative = true;
	
	public Screener(double startLimit) {
		resetStatistics();
		lowBoundMaxValue = startLimit;		
	}
	public void switchOff1DerivativeCheck() {
		use1Derivative = false;
	}

	public boolean checkPassed(Box box) {
		return checkByValue(box) && 
				check1Derivative(box) && 
				check2Derivative();
	}

	protected boolean check2Derivative() {
		if(notImpl > 0) {
			notImpl--;
//			System.out.println("Screener.check2Derivative() not implemented and always returns true!");
		}
		return true;
	}

	/*
	 * A point could be a minimum or a maximum if and only if the derivative
	 * is equal to zero in this point. 
	 * Therefore interval extensions of all partial derivatives have to contain zero.
	 * The only exception are border points. Consider the following case:
	 * f(x) = x, min_{0<x<1}(f) = f(0), but f'(x) != 0.
	 * BUT instead of performing such checks each time we just have to add all ages to the
	 * working list from the very beginning! Much simple and less code: )
	 * Because of this it doesn't screen out boxes with at least 
	 */
	protected boolean check1Derivative(Box box) {
		if (!use1Derivative)
			return true;
		for (int i = box.getDimension()-1; i >= 0; --i) {
			if (box.getInterval(i).wid() == 0)
				return true; // a workaround for edges. the only chance for a side to got its width = 0
								// is to be added as a border edge. See WorkList.addSearchArea().  
		}
		FunctionNEW function = FunctionFactory.getTargetFunction();
		for (int i = box.getDimension()-1; i >= 0; --i) {
			RealInterval f1d = function.calculate1Derivative(box, i);
			if (f1d == null)
				break;
			if (!f1d.contains(0)) {
/*				
				boolean isBorder = initialSearchArea.hasAtLeastOneCommonSide(box);
				if (isBorder) {
					deflate(box, initialSearchArea, function);
					//return true; // we have to keep such box
					  return checkByValue(box); // @deflate@ updates interval estimation, 
					                            // so there still is a chance to get rid from this box.
				} else
					return false; // doesn't have edge point -- could be thrown away
*/
//				System.out.println("   - faild checkBy1Der: " + box);
				return false;
			}
		}
		return true; // check passed
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
