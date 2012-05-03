package algorithms;


import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionNEW;


public interface Algorithm {
	/*
	 * the main function. finds the global optimum 
	 * (optimums in case if the target function has more than one global optimum) 
	 * One should call getOptimumValue() or getOptimumArea()
	 * to get minimum value or its arguments.
	 */
	public void solve();
	public RealInterval getOptimumValue();
	public Box[] getOptimumArea();
	
	/*
	 * sets the target function and search area
	 */
	public void setProblem(FunctionNEW f, Box area);

	/*
	 * sets stop criterion -- when the iteration should be stopped.
	 */
	public void setStopCriterion(StopCriterion stopCriterion);	
	
	/*
	 * Width of interval extension is a stop criteria.
	 * Algorithm stops iteration when a box, selected to be split,
	 * has more precise estimation of a target function F than
	 * getPrecision() returns.   
	 */
	public void   setPrecision( double pres );
	public double getPrecision();
	
	/*
	 * Current upper estimation of the optimum 
	 */
	public double getLowBoundMaxValue();
}