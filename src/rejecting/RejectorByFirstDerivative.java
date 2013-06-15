package rejecting;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionFactory;
import functions.Function;

/** FIRST derivative
 * A point could be a minimum or a maximum if and only if the derivative
 * is equal to zero in this point. 
 * Therefore interval extensions of all partial derivatives have to contain zero.
 * The only exception are border points. Consider the following case:
 * f(x) = x, min_{0<x<1}(f) = f(0), but f'(x) != 0.
 * BUT instead of performing such checks each time we just have to add all ages to the
 * working list from the very beginning! Much simple and less code: )
 * Because of this it doesn't screen out boxes with at least one side width = 0
 */

class RejectorByFirstDerivative implements BaseRejector {

	@Override
	public boolean checkPassed(Box box) {
		return check1Derivative(box);
	}

	/** FIRST derivative
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
		Function function = FunctionFactory.getTargetFunction();
		for (int i = box.getDimension()-1; i >= 0; --i) {
			// A workaround for edges. Worklist adds zero-width
			// edges for initial search area. 
			// See Worklist.addAreaAndAllEges()			
			if (box.getInterval(i).wid() == 0) {
				return true;
			}
			
			RealInterval f1d = function.calculate1Derivative(box, i);
			if (f1d == null)
				break;
			if (!f1d.contains(0))
				return false;
		}
		return true; // check passed
	}
}
