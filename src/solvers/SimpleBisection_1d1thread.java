package solvers;
import java.util.ArrayList;

import functions.TargetFunction;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

/*
 * This is the first algorithm implemented.
 * One dimensional functions only. One thread.
 * For education purposes.    
 */

public class SimpleBisection_1d1thread {

	private TargetFunction function;
	private ArrayList<RealInterval> workList;
	private double epsilonI = 1e-5;
	private double epsilonF = 1e-5;
	private int iterations = 0;
	private int maxIterations = 1000;

	public SimpleBisection_1d1thread(RealInterval area, TargetFunction function) {
		this.function = function;
		workList = new ArrayList<RealInterval>();
		workList.add(area);		
	}

	/*
	 * the main method to be called for solving an optimization problem
	 * @see BaseAlgorithm#solve()
	 * returns a vector because a function can have more than one global optimum
	 */	
	public RealInterval solve() {
		RealInterval optimum;// = new RealInterval(); 
		do {
			optimum = extractBestInterval();
			
		} while ( doIteration(optimum) );
		
		return function.calculateValue(optimum);
	}

	/*
	 * Main logic is here.
	 * 0) check if stop criteria is reached
	 * 1) cat interval on two halves
	 * 2) calculate interval extensions of a target function
	 * 3) add them to a worklist 
	 */
	private boolean doIteration(RealInterval interval) {
		/*//midpoint is missed now...
		if (isDone(interval))
			return false;
	
		RealInterval mp = IAMath.midpoint(interval);
		
		workList.add( new RealInterval(interval.lo(), mp.lo()) );
		workList.add( new RealInterval(mp.hi(), interval.hi()) );
		*/
		return true;
	}

	/*
	 * stop criteria:
	 * returns true (=yes, we are done) if
	 * 1) width of an interval is less then a threshold
	 * 2) width of an interval estimation of a 
	 * 		target function is less than a threshold
	 * 3) num of iteration performed is greater than a threshold
	 *      (for prevent infinite looping if the algorithm has a bug
	 *      and does not make p. 1 or 2 :)   
	 */
	private boolean isDone(RealInterval i) { 
		if (width(i) < epsilonI)
			return true;
		if ( width(function.calculateValue(i) ) < epsilonF)
			return true;
		if( iterations++ > maxIterations)
			return true;
		
		return false;
	}

	/*
	 * support function. actually shouldn't be here.
	 */
	private double width(RealInterval i) {
		return i.hi() - i.lo();
	}

	/*
	 * calculates the optimum estimation.
	 */
	private RealInterval extractBestInterval() {
		double lowestBorder = Double.MAX_VALUE;
		int pos = -1;
		// found current best
		for(int i = 0; i <workList.size(); i++) {
			if (lowestBorder > function.calculateValue(workList.get(i)).lo() ) {
				lowestBorder = function.calculateValue(workList.get(i)).lo();
				pos = i;
			}
		}
		// return it
		RealInterval curOptimum = workList.get(pos);
		workList.remove(pos);
		return curOptimum;
	}
}
