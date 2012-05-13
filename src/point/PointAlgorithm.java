package point;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionNEW;

public abstract class PointAlgorithm /*implements Runnable*/ {
	protected double alpha = 0.9;
	protected final double epsilon = 1e-6;
	protected FunctionNEW function;
	protected final int maxSteps = 100;
	protected final double stepFromSearchAreaSizeFactor = 0.25;
	protected Box initialSearchArea; // point algorithm doesn't allowed to return extremes outside this area!
										// |\_|
	 									// |  |\ <-- f(x)
										// ^  ^ \.
										// area  ^-minimum OUTSIDE the search area -- this will be WRONG result
	
	protected final boolean logging = false;
	
	protected double optVal;
	protected double optArg[];
	
	public PointAlgorithm() {
	}
	public void setProblem(FunctionNEW f, Box initialSearchArea) {
		function = f;
		this.initialSearchArea = initialSearchArea;
	}
	public double localMinimum(Box area) {
		minimize(area);
		if (!initialSearchArea.contains(optArg)) { // initialSearchArea, not area!
			if (logging) { 
				System.out.println("Point algorithm found local optima that is " +
						"outside the search area. Return closest point..");
			}
			// we spent quite significant efforts on point optimization so we will spend a little bit more
			// in order to keep the result at least some how useful. Returning a middle point in this case 
			// proved to be a bad idea.  
			//optArg = middleAreaPoint(area);
			initialSearchArea.setToClosestAreaPoint(optArg);
			optVal = function.calculatePoint(optArg);
		}
		return optVal;
	}
	public double[] getLocalOptPoint() {
		return optArg;
	}

	protected abstract void minimize(Box area); 
	
	@Override
	public String toString() {
		String fullName = this.getClass().getName();
		return fullName.substring(fullName.lastIndexOf('.')+1); // removes packages
	}
	
	/*
	 * returns n-dimensional point which is in the middle of the area 
	 */
	protected double[] middleAreaPoint(Box area) {
		int dim = area.getDimension();
		double point[] = new double[dim];
		
		for (int i = 0; i < dim; i++) {
			RealInterval ii = area.getInterval(i);
			point[i] = ii.lo() + ii.wid()/2;
		}
		return point;
	}
}
