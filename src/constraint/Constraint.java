/**
 * 
 */
package constraint;

import java.util.ArrayList;

import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;
import symboldiff.Expression;
import symboldiff.MethodRunner;
import symboldiff.exceptions.MethodGenerationException;
import symboldiff.exceptions.MethodInvocationException;

/*
 * Base class for Constraint entity
 * Contains expression (i.e. uses composition, not inheritance) 
 */
public abstract class Constraint {
	protected Expression leftPart;
	protected ConstraintType operation;
	protected Expression rightPart;
	private Box area;
	private static MethodRunner methodRunner;
	
	public static void init(/*ArrayList<String> coordinats*/) {
		methodRunner = new MethodRunner();//coordinats);
	}
	
	public void setArea(Box area) {
		this.area = area;
	}
/*	
 	public boolean propagate() throws MethodGenerationException, MethodInvocationException {
		RealInterval l = evaluate(leftPart);
		RealInterval r = evaluate(rightPart);
		boolean reduced = applayValues(l, r);
		return reduced;
	}

	private boolean applayValues(RealInterval l, RealInterval r) {
		switch (operation) {
		case equal:
			try {
				if (l.intersect(r) != 0) { // result interval is smaller than originals
					
				}
			} catch (IAIntersectionException e) {
				// intersection failed!
				// it means that 
				
			
				e.printStackTrace();
			}
			break;

		default:
			throw new IllegalStateException("Unsupported constraint type!");
		}
		return false;
	}
*/
	protected RealInterval evaluate(Expression exp) 
				throws MethodGenerationException, MethodInvocationException {
		// if not yet initialized
		if (exp.getMethod() == null) 
			methodRunner.generateMethods(exp);
		
		RealInterval val = methodRunner.invokeMethods(area, exp);
		return val;
	}
}

enum ConstraintType {equal};

