/**
 * 
 */
package constraint;

import java.util.HashMap;

import core.Box;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;
import net.sourceforge.interval.ia_math.exceptions.IANarrowingFaildException;
import symboldiff.*;
import symboldiff.exceptions.ExpressionException;

/**
 * @author nvpanov
 *
 */
public class ExpressionPropagatable extends Expression {
	boolean logging = false;
	private HashMap<Expression, RealInterval> cachedValues = new HashMap<>();
	private Box area;
	
	public ExpressionPropagatable(String expression) throws ExpressionException {
		super(expression);
	}

	public boolean propagate(Box area, RealInterval newValue) throws RepugnantConditionException {
		boolean wasPropagated;
		int propagationCycles = 0;
		init(area);
		
		do {
			Box originalArea = area.clone();
	 		wasPropagated = propagate(this, newValue);
	 		if (wasPropagated) {
	 			if (logging) {
	 				System.out.println(" Propagated: " + originalArea.toStringArea() + " => " + area.toStringArea());
	 			}	 			
	 		}
		} while (wasPropagated && ++propagationCycles < 100);
 		clearResources();
		if (propagationCycles == 100)
			throw new ArithmeticException();
		
		return propagationCycles > 0;
 	}
	
	private void init(Box area) {
		this.area = area;
 		evaluateCaching(this, area, cachedValues);
 	}
	private void clearResources() {
		cachedValues.clear();
		area = null;
	}
 	private static RealInterval evaluateCaching(Expression exp, Box area, HashMap<Expression, RealInterval> cachedValues) {
 		RealInterval cachedValue;
		if (exp.isConstant()) {
			cachedValue = new RealInterval(exp.getConstantValue());
		} 
		else if (exp.isVariable()) {
			cachedValue = area.getInterval(exp.getNumberOfThisVariable());
		} else {
			// it is an expression
			RealInterval l = null, r;
			if (exp.getLeftExpression() != null) // left could be null for unary expressions
				l = evaluateCaching(exp.getLeftExpression(), area, cachedValues);
			r = evaluateCaching(exp.getRightExpression(), area, cachedValues);
			cachedValue = evaluate(exp.getOperation(), l, r);
		}
		assert( !Double.isNaN(cachedValue.lo()) && !Double.isNaN(cachedValue.hi()) );
//		RealInterval previousValue = cachedValues.get(exp); //dbg

		cachedValues.put(exp, cachedValue);
		return cachedValue;
 	}
	
	private boolean propagate(Expression exp, RealInterval newValue) throws RepugnantConditionException 
 	{
		assert (newValue != null);
 		if (exp.isConstant()) {
 			return false;
 		}
 		
 		RealInterval calculatedAndSavedValue = cachedValues.get(exp);
 		if (calculatedAndSavedValue.equals(newValue) || newValue.contains(calculatedAndSavedValue)) {
 			// ^^ new = old || new > old
 			return false; // nothing to do, nothing was done 
 		}
 		RealInterval reducedValue = IAMath.intersect(calculatedAndSavedValue, newValue);
 		if (reducedValue == null) {
 			throw new RepugnantConditionException() ; // incompatible values
 		}
 		
 		if (exp.isVariable()) {
 			// it was not equal, it is not null => it was reduced
 			int index = exp.getNumberOfThisVariable();
 			area.setInterval(index, reducedValue);
 			cachedValues.put(exp, reducedValue);
 			return true;
 		}

 		Expression lExp = exp.getLeftExpression();
 		Expression rExp = exp.getRightExpression();
 		RealInterval lValue = null, rValue = null;
		rValue = cachedValues.get(rExp); // this is an Operation, right != null
		if ( lExp != null ) { // left == null for unary operations
			lValue = cachedValues.get(lExp);
		}
 		RealInterval[] aWayToPassPointersToFunction = {reducedValue, lValue, rValue};
 		boolean wasNarrowed = narrow(exp, aWayToPassPointersToFunction);
 		assert (aWayToPassPointersToFunction[0] != null);
 		cachedValues.put(exp, aWayToPassPointersToFunction[0]);
 		if ( lExp != null ) {
 			wasNarrowed |= propagate(lExp, aWayToPassPointersToFunction[1]);
 	 	}
 		if ( rExp != null ) {
 			wasNarrowed |= propagate(rExp, aWayToPassPointersToFunction[2]);
 		}
		return wasNarrowed; 			
 	}
 	
	private static boolean narrow(Expression exp, RealInterval[] aWayToPassPointersToFunction) throws RepugnantConditionException {
		try {
			if ( exp.isAdd() )
				return IANarrow.narrowAdd(aWayToPassPointersToFunction);
			if (exp.isDiv())
				return IANarrow.narrowDiv(aWayToPassPointersToFunction);
			if (exp.isMul())
				return IANarrow.narrowMul(aWayToPassPointersToFunction);
			if (exp.isNegate())
				return IANarrow.narrowNegate(aWayToPassPointersToFunction);
			if (exp.isPow())
				return IANarrow.narrowPow(aWayToPassPointersToFunction);
			if (exp.isSub())
				return IANarrow.narrowSub(aWayToPassPointersToFunction);
			
			if (exp.isSin())
				return IANarrow.narrowSin(aWayToPassPointersToFunction);
			if (exp.isCos())
				return IANarrow.narrowCos(aWayToPassPointersToFunction);
			
			if (exp.isVariable())
				return IANarrow.narrowEquals(aWayToPassPointersToFunction);
			if (exp.isConstant())
				return false;
		} catch (IANarrowingFaildException e) {
			throw new RepugnantConditionException(); // incompatible values
		}
		System.out.println("!!! Unknown operation for propagate: '" + exp.getOperation() + "' !!!!");
		return false;
	}
}
