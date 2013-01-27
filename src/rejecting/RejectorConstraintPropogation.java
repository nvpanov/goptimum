/**
 * 
 */
package rejecting;

import net.sourceforge.interval.ia_math.RealInterval;
import symboldiff.Expression;
import symboldiff.exceptions.ExpressionException;
import constraint.ExpressionPropagatable;
import constraint.RepugnantConditionException;
import core.Box;
import functions.FunctionNEW;

/**
 * @author nvpanov
 * Reject of squeeze a box using constraint propagation. 
 * see @constraint package for details.
 * Uses the following constraints:
 * - 1st derivative should be 0
 * - 2nd derivative should be non-negative
 */
public class RejectorConstraintPropogation extends BaseRejector {
	private ExpressionPropagatable derivatives1[];
	private ExpressionPropagatable derivatives2[];
	private ExpressionPropagatable function; 
	private final static RealInterval firstDerivativeShouldBeEqualToZero = new RealInterval(0);
	private final static RealInterval secondDerivativeShouldBeNonNegative = new RealInterval(0, Double.MAX_VALUE);

	public RejectorConstraintPropogation() {
	}
	
	public RejectorConstraintPropogation(FunctionNEW f) {
		init(f);
	}
	
	public void init(FunctionNEW f) {
		try {
			function = new ExpressionPropagatable(f.toString());
			for (int i = 0; i < f.getDimension(); i++) {
				// TODO: Optimize me
				Expression tmpExp = f.get1Derivative(i);
				if (tmpExp != null) {
					String tmpExpressionString = tmpExp.toString();
					derivatives1[i] = new ExpressionPropagatable(
							tmpExpressionString);
				}
				tmpExp = f.get2Derivative(i);
				if (tmpExp != null) {
					String tmpExpressionString = tmpExp.toString();
					derivatives1[i] = new ExpressionPropagatable(
							tmpExpressionString);
				}
			}
		} catch (ExpressionException e) { // it should not happen
			System.out.println(e);
		}
	}


	@Override
	public boolean checkPassed(Box box) {
		if (isBorder(box))
			return true;
		try {
			for (int i = 0; i < function.getDimension(); i++) {
				derivatives1[i].propagate(box, firstDerivativeShouldBeEqualToZero);
				derivatives2[i].propagate(box, secondDerivativeShouldBeNonNegative);
			}
		} catch (RepugnantConditionException e) {
			return false;
		}
//		if (box != null)
			return true;
	}
	
	public boolean checkPassed(Box box, RealInterval currentMinimumEstimation) {
		if (!checkPassed(box))
			return false;
		try {
			function.propagate(box, currentMinimumEstimation);
		} catch (RepugnantConditionException e) {
			return false;
		}
		return true;
	}
}
