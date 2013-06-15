package rejecting;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionFactory;
import functions.Function;

/**  
 * SECOND derivative
 * a function can reach a minimum on an interval only if
 * 1) this is a border point of initial search area
 * 2) the function is concave on this interval.
 */
class RejectorBySecondDerivative implements BaseRejector {
	
	@Override
	public boolean checkPassed(Box box) {
		return check2Derivative(box);
	}

	/*
	 * A twice differentiable function is convex on an interval if and only if its 
	 * second derivative is non-negative there; this gives a practical test for convexity.
	 */
	protected boolean check2Derivative(Box box) {
		Function function = FunctionFactory.getTargetFunction();
		for (int i = box.getDimension()-1; i >= 0; --i) {
			// A workaround for edges. Worklist adds zero-width
			// edges for initial search area. 
			// See Worklist.addAreaAndAllEges()
			if (box.getInterval(i).wid() == 0) {
				return true;
			}
			
			RealInterval f2d = function.calculate2Derivative(box, i);
			if (f2d == null)
				break;
			if (f2d.hi() < 0) 
				return false;
		}
		return true;
	}
}
