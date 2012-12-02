/**
 * 
 */
package constraint;

import core.Box;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;
import net.sourceforge.interval.ia_math.exceptions.IANarrowingFaildException;
import symboldiff.*;

/**
 * @author nvpanov
 *
 */
public class ExpressionPropogatable extends Expression {

 	public boolean propagate(Box area, RealInterval constrainingValue) throws RepugnantConditionException {
 		RealInterval newValue = propogateExpression(area, constrainingValue, this);
 		if (constrainingValue.containsNotEqual(newValue)) { // reduced
 			propagate(area, constrainingValue);
 			return true;
 		}
 		return false; 
	}
 
 	
	private static RealInterval propogateExpression(Box area, RealInterval constrainingValue, Expression exp) {
		if (exp == null)
			return constrainingValue;
		RealInterval curValue = exp.evaluate(area);
		RealInterval newValue = IAMath.intersect(constrainingValue, curValue);
		if ( newValue.equals(curValue) )  
			return newValue;
		
		RealInterval lValue = exp.getLeftExpression()  == null ? null : exp.getLeftExpression(). evaluate(area);
		RealInterval rValue = exp.getRightExpression() == null ? null : exp.getRightExpression().evaluate(area);
		RealInterval ii[] = new RealInterval[] { newValue, lValue, rValue };
		boolean success = false;
		try {
			success = propagate(exp, ii);
		} catch (RepugnantConditionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!success)
			return newValue; // we already have improved something but the propagation to the sub functions doesn't give anything
		
		constrainingValue = IAMath.intersect(constrainingValue, ii[0]);
		newValue = propogateExpression(area, constrainingValue, exp.getLeftExpression());
		constrainingValue = IAMath.intersect(constrainingValue, newValue);
		newValue = propogateExpression(area, constrainingValue, exp.getRightExpression());
		newValue = IAMath.intersect(constrainingValue, newValue);

		return newValue;
	}

	private static boolean propagate(Expression exp, RealInterval[] aWayToPassPointersToFunction) throws RepugnantConditionException {
		try {
		if ( exp.isAdd() ) 
			return IANarrow.narrowAdd(aWayToPassPointersToFunction);
		else if (exp.isDiv())
			return IANarrow.narrowDiv(aWayToPassPointersToFunction);
		else if (exp.isMul())
			return IANarrow.narrowMul(aWayToPassPointersToFunction);
		else if (exp.isNegate())
			return IANarrow.narrowNegate(aWayToPassPointersToFunction);
		else if (exp.isPow())
			return IANarrow.narrowPow(aWayToPassPointersToFunction);
		else if (exp.isSub())
			return IANarrow.narrowSub(aWayToPassPointersToFunction);
		else if (exp.isVariable())
			return IANarrow.narrowEquals(aWayToPassPointersToFunction);
		if (exp.isConstant())
			return propagateConstant(aWayToPassPointersToFunction);
		} catch (IANarrowingFaildException e) {
			throw new RepugnantConditionException(e);
		}

		throw new RuntimeException("Unknown operation for propogate: " + exp.getOperation());			
	}

	private static boolean propagateConstant(RealInterval[] aWayToPassPointersToFunction) throws IANarrowingFaildException {
		assert aWayToPassPointersToFunction.length == 1;
			return IANarrow.narrowEquals(aWayToPassPointersToFunction);
	}

}
