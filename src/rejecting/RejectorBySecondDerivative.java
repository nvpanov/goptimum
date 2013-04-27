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
class RejectorBySecondDerivative extends BaseRejector {
	
	private boolean doNotCheckAnythingAndAlwaysReturnTrue;

	@Override
	public boolean checkPassed(Box box) {
		if (isBorder(box)) // we can't screen out any border point of original search area
			return true;   // because of derivatives. 
		return check2Derivative(box);
	}

	/*
	 * A twice differentiable function is convex on an interval if and only if its 
	 * second derivative is non-negative there; this gives a practical test for convexity.
	 */
	protected boolean check2Derivative(Box box) {
		if (doNotCheckAnythingAndAlwaysReturnTrue) {
			return true;
		}
		Function function = FunctionFactory.getTargetFunction();
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

	public void switchOff() {
		doNotCheckAnythingAndAlwaysReturnTrue = true;		
	}

}
