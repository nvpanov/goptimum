package worklists;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionFactory;
import functions.FunctionNEW;

@SuppressWarnings("unused") // for profiling variables
class Screener {
	private volatile double lowBoundMaxValue = Double.MAX_VALUE;
	private double lowBoundMaxValueDelta;
	private int updatesCount;
	private boolean useDerivativesCheck = true;
	private int byValueOnly = 0;
	private int byFirstOnly = 0;
	private int byValAndFirst = 0;
	private int bySecondOnly = 0;
	private int byValAndFirstAndSecond = 0;
	private int byValAndSecond = 0;
	private int byFirstAndSecond = 0;
	private int total = 0;
	private long time2Der = 0;
	private long time1Der = 0;
	private long timeBorder = 0;
	private long timeByValue = 0;
	private double epsilon = 1e-6; // epsilon for comparing doubles: d1 == d2 if abs(d1-d2) < epsilon
	private static final boolean profiling = false;
/*	
	// if not interested in profiling -- comment this out for performance
	// GC doesn't like classes with finalize
	@Override
	public void finalize() {
		assert profiling : "Comment this method out! Use it for profiling only.";
		assert total == byValueOnly + byValAndFirst + byValAndSecond + byValAndFirstAndSecond + 
				byFirstOnly + byFirstAndSecond + bySecondOnly : "Screening checksum faild";
		
		System.out.println("-------Screening stats {{");
		System.out.println(">"+FunctionFactory.getTargetFunction() + "<");
		System.out.println("  By Value  only: " + byValueOnly + "\t\t (" + (100*byValueOnly/total) + "%)");
		System.out.println("  By First  only: " + byFirstOnly + "\t\t (" + (100*byFirstOnly/total) + "%)");
		System.out.println("  By Second only: " + bySecondOnly + "\t\t (" + (100*bySecondOnly/total) + "%)");
		System.out.println("      val+1d:     " + byValAndFirst + "\t\t (" + (100*byValAndFirst/total) + "%)");
		System.out.println("      val+2d:     " + byValAndSecond + "\t\t (" + (100*byValAndSecond/total) + "%)");
		System.out.println("      1d+2d:      " + byFirstAndSecond + "\t\t (" + (100*byFirstAndSecond/total) + "%)");
		System.out.println("      val+1d+2d:  " + byValAndFirstAndSecond + "\t\t (" + (100*byValAndFirstAndSecond/total) + "%)");
		long totalT = time2Der + time1Der + timeBorder + timeByValue;
		System.out.println("   byVal Time:  " + timeByValue + "\t\t (" + (100*timeByValue/totalT) + "%)");		
		System.out.println("   by1D  Time:  " + time1Der + "\t\t (" + (100*time1Der/totalT) + "%)");		
		System.out.println("   by2D  Time:  " + time2Der + "\t\t (" + (100*time2Der/totalT) + "%)");
		System.out.println("   borderTime:  " + timeBorder + "\t\t (" + (100*timeBorder/totalT) + "%)");
		System.out.println("}} Screening stats ------\n");
	}
//*/	
	public Screener(double startLimit) {
		resetStatistics();
		lowBoundMaxValue = startLimit;		
	}
	// for test purposes mostly
	public void switchOffDerivativesCheck() {
		useDerivativesCheck = false;
	}

	public boolean checkPassed(Box box) {
		if (profiling )
			return checkPassed_Profiling(box);
		return checkByValue(box) && checkDerivatives(box); 
	}
	/*
	 * Logic is equal to @checkPassed()@ but this version is also
	 * collecting statistics about effectiveness of each screening type.
	 */
	public boolean checkPassed_Profiling(Box box) {
		boolean byV  = false;
		boolean by1D = false;
		boolean by2D = false;
		long startT = System.nanoTime();
		boolean check = checkByValue(box);
		long stopT = System.nanoTime();
		timeByValue += stopT-startT;
		if (!check) {
			byV = true;
			byValueOnly++;
		}
		startT = System.nanoTime();
		check = isBorder(box);
		stopT = System.nanoTime();
		timeBorder += stopT-startT;
		if (!check) {
			startT = System.nanoTime();
			check = check1Derivative(box);
			stopT = System.nanoTime();
			time1Der += stopT-startT;
			if (!check) {
				by1D = true;
				if (byV) {
					byValAndFirst++;
					byValueOnly--;
				} else
					byFirstOnly++;
			}
			startT = System.nanoTime();
			check = check2Derivative(box);
			stopT = System.nanoTime();
			time2Der += stopT-startT;
			if (!check) {
				by2D = true;
				if (byV) {
					if (by1D) {
						byValAndFirst--;
						byValAndFirstAndSecond++;
					} else {
						byValAndSecond++;
					}
				} else if (by1D) {
					byFirstOnly--;
					byFirstAndSecond++;
				} else 
					bySecondOnly++;
			}
		}
		if (byV || by1D || by2D) {
			total++; // check sum
			return false;
		}
		return true;
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
		if (Math.abs(lo - lowBoundMaxValue) < epsilon ) {
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
