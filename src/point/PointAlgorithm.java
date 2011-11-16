package point;

import core.Box;
import functions.Function;

public abstract class PointAlgorithm /*implements Runnable*/ {
	protected double alpha = 0.9;
	protected final double epsilon = 1e-6;
	protected Function function;
	protected final int maxSteps = 100;
	protected final double stepFromSearchAreaSizeFactor = 0.25;
	
//	protected double optVal;
//	protected double optArg[];
	
	public PointAlgorithm() {
//		optVal = Double.MAX_VALUE;
	}
	public void setFunction(Function f) {
		function = f;
	}
	public abstract double localMinimum(Box area);
}
