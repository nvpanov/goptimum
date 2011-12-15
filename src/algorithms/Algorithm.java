package algorithms;


import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.Function;


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
	public void setProblem(Function f, Box area);
	
	/*
	 * Width of interval extension is a stop criteria.
	 * Algorithm stops iteration when a box, selected to be split,
	 * has more precise estimation of a target function F than
	 * getPrecision() returns.   
	 */
	public void   setPrecision( double pres );
	public double getPrecision();
	
	
}