/**
 * 
 */
package constraint;

import core.Box;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.IANarrow;
import net.sourceforge.interval.ia_math.RealInterval;
import net.sourceforge.interval.ia_math.exceptions.IANarrowingFaildException;
import symboldiff.Expression;
import symboldiff.MethodRunner;
import symboldiff.exceptions.MethodGenerationException;
import symboldiff.exceptions.MethodInvocationException;

enum ConstraintType {equal};

/*
 * Base class for Constraint entity
 * Contains expression (i.e. uses composition, not inheritance) 
 */
/*
public abstract class Constraint {
	protected Expression leftPart;
	protected ConstraintType operation;
	protected Expression rightPart;
	
 	public boolean propagate(Box area) {
		RealInterval l = leftPart.evaluate(area);
		RealInterval r = rightPart.evaluate(area);
		boolean reduced = applayValues(l, r);
		return reduced;
	}

	private boolean applayValues(RealInterval l, RealInterval r) throws RepugnantConditionException {
		try {
			switch (operation) {
				case equal:
					return IANarrow.narrowEquals(l, r);
			default:
				throw new RuntimeException("Unsupported constraint type!");
			}
		} catch (IANarrowingFaildException e) {
			throw new RepugnantConditionException(e);
		}
	}

}
*/