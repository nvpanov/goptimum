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
import functions.Function;

// one class -- one rejecting criterion
class RejectorConstraintValue extends RejectorConstraintPropogation {
	@Override
	public boolean checkPassed(Box box) {
		return checkPassedValue(box);
	}
}
class RejectorConstraint1stDerivative extends RejectorConstraintPropogation {
	@Override
	public boolean checkPassed(Box box) {
		return checkPassed1stDerivative(box);
	}
}
class RejectorConstraint2ndDerivative extends RejectorConstraintPropogation {
	@Override
	public boolean checkPassed(Box box) {
		return checkPassed2ndDerivative(box);
	}
}

/**
 * @author nvpanov
 * Reject of squeeze a box using constraint propagation. 
 * see @constraint package for details.
 * Uses the following constraints:
 * - 1st derivative should be 0
 * - 2nd derivative should be non-negative
 * - function value should be less or equal than known minimum  
 */
abstract class RejectorConstraintPropogation implements BaseRejector {
	private ExpressionPropagatable derivatives1[];
	private ExpressionPropagatable derivatives2[];
	private ExpressionPropagatable function; 
	private RejectorByValue rejectorByValue;
	private final static RealInterval firstDerivativeShouldBeEqualToZero = new RealInterval(0);
	private final static RealInterval secondDerivativeShouldBeNonNegative = new RealInterval(0, Double.MAX_VALUE);

	public RejectorConstraintPropogation() {
	}
	public RejectorConstraintPropogation(Function f) {
		init(f, null);
	}
	
	public void init(Function f, RejectorByValue rejectorByValue) {
		this.rejectorByValue = rejectorByValue;
		
		final int dim = f.getDimension();
		derivatives1 = new ExpressionPropagatable[dim];
		derivatives2 = new ExpressionPropagatable[dim];
		try {
			function = new ExpressionPropagatable(f.toString());
			for (int i = 0; i < dim; i++) {
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
					derivatives2[i] = new ExpressionPropagatable(
							tmpExpressionString);
				}
			}
		} catch (ExpressionException e) { // it should not happen
			System.out.println(e);
		}
	}

	protected boolean checkPassedValue(Box box) {
		double minimumEstimationUpperBound = rejectorByValue.getLowBoundMaxValue();
		final RealInterval currentMinimumEstimation = new RealInterval(Double.NEGATIVE_INFINITY, minimumEstimationUpperBound);
		try {
			boolean propagated = function.propagate(box, currentMinimumEstimation);
			if (propagated) {
				box.setFunctionValue( function.evaluate(box) );
			}
		} catch (RepugnantConditionException e) {
			return false;
		}
		return true;
	}
	
	protected boolean checkPassed1stDerivative(Box box) {
		return checkPassedDerivative(box, derivatives1, firstDerivativeShouldBeEqualToZero);
	}
	protected boolean checkPassed2ndDerivative(Box box) {
		return checkPassedDerivative(box, derivatives2, secondDerivativeShouldBeNonNegative);
	}
	
	private boolean checkPassedDerivative(Box box, ExpressionPropagatable[] derivatives, RealInterval constrainingValue) {
		if (isBorder(box))
			return true;
		try {
			boolean propagated = false;
			for (int i = 0; i < function.getDimension(); i++) {
				propagated |= derivatives[i].propagate(box, constrainingValue);
			}
			if (propagated) {
				box.setFunctionValue( function.evaluate(box) );
			}
		} catch (RepugnantConditionException e) {
			return false;
		}
		return true;
	}
	
	private boolean isBorder(Box box) {
		for (int i = box.getDimension()-1; i >= 0; --i) {
			if (box.getInterval(i).wid() == 0) // A workaround for edges. Worklist adds zero-width
												// edges for initial search area. 
												// See Worklist.addAreaAndAllEges()
				return true;
		}
		return false;			
	}
}
