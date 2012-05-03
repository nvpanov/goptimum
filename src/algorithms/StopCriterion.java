package algorithms;

import core.Box;

/**
 * @author  nvpanov
 */
public class StopCriterion {
	protected int maxIterations;
	private int iteration;
	private double fPrecision;
//	protected double argPrecision;
//	protected int maxTimeS;
//	protected int startTimeS;
	
	protected Algorithm algorithm;
	protected boolean logging;
	
	public StopCriterion(Algorithm alg) {
		logging = false;//alg.logging;
		algorithm = alg;
		maxIterations = (int)1e6;
		fPrecision = 1e-2;
//		argPrecision = 1e-2;
//		maxTimeS = 200;
	}
	public void reset() {
		iteration = 0;
	}
	public boolean isDone(Box b) {
		// 1. iterations
		// time or iterations counter is obligatory to protect from infinite iterations
		if (++iteration > maxIterations) {
			if(logging) System.out.println(" = Iterations is done: maxIterations = " + maxIterations);
			return true;
		}
		
		// 2. F precision (this is better than "F width")
		assert(b.getFunctionValue().lo() <= algorithm.getLowBoundMaxValue());
		if (algorithm.getLowBoundMaxValue() - b.getFunctionValue().lo() < getFMaxPrecision()) {
			// we use patched IAMath! bug in exponenta is solved and wid added
			if(logging) System.out.println(" = Iterations is done: getFMaxPrecision = " + getFMaxPrecision());
			return true;
		}
		
/*		// 2. F width
		if (b.getFunctionValue().wid() < getFMaxPrecision()) {
			// we use patched IAMath! bug in exponenta is solved and wid added
			if(logging) System.out.println(" = Iterations is done: getFMaxPrecision = " + getFMaxPrecision());
			return true;
		}
*/		
		// 3. X width
		// F width is a function from arguments width. 
		// So we can omit checks the width of all arguments and save CPU time for this function

		// 4. time
		// again lets relay only on iteration counting and improve the performance of
		// this criterion by skipping time measurements.
		
		return false;
	}
	
	public void setMaxIterations(int iteration) {
		this.maxIterations = iteration;
	}
	public int getMaxIterations() {
		return maxIterations;
	}
	public void setFMaxPrecision(double fPrecision) {
		this.fPrecision = fPrecision;
	}
	public double getFMaxPrecision() {
		return fPrecision;
	}
}
