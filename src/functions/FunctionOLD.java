package functions;

import symboldiff.Expression;
import symboldiff.Gradient;
import symboldiff.MethodRunner;
import symboldiff.exceptions.ExpressionException;
import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;

public abstract class FunctionOLD {
	private static FunctionOLD targetFunction = null;

	/*
	 * for automatically derivative calculation
	 */
	private Gradient grad1, grad2; // first and second derivatives
	//private Expression gradient1, gradient2;
	private MethodRunner executor;

	protected final int dim;
	
	
	public static synchronized final void setTargetFunction(FunctionOLD f) {
		assert(targetFunction == null);
		targetFunction = f;
	}
	public static final FunctionOLD getTargetFunction() {
		return targetFunction;
	}
	
	protected FunctionOLD(int dim) {
		this.dim = dim;
		// FIXME!!
		targetFunction = null;
		setTargetFunction(this);
		try {
			init(toStringFull());
		} catch (ExpressionException e) {
			// actually everything should be OK,
			// otherwise we will work w/o derivatives
			e.printStackTrace();
		}
	}
		
	public int getDimension() {
		return dim;
	}

	/*
	 * main functions: first one calculates function's interval extension on
	 * given box and calls b.setFunctionalValue on it. So it returns nothing.
	 * The second function is used for point calculations and (sometimes can be
	 * used) for checking correctness of interval computations.
	 */
	abstract public void calculate(Box b);

	abstract public double calculatePoint(double... point);

	/*
	 * functions now can compute their derivatives. To hide all the details and
	 * do all this derivative computation as much transparent as possible to end
	 * user it was decided to hide all setup here. Thats why Function now is an
	 * abstract class, not an interface...
	 */
	protected void init(String equation) throws ExpressionException {
		Expression expr = new Expression(equation);
		grad1 = new Gradient(expr);
		executor = new MethodRunner(/*expr*/);
//		try {
//			executor.generateMethods(grad1.getGradient());
//		} catch (MethodGenerationException e) {
			// something was bad with first derivative
			grad1 = null;
			System.err.println("{");
			System.err.println("1st derivative initialization faild for f=" +
					equation + " (so no 2nd one as well).");
//			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
			System.err.println("}");
			return;
//		}
//		RPN rpn2 = new RPN(grad1.getGradient().toString());
//		Expression expr2 = new Expression(rpn2);
//		grad2 = new Gradient(expr2);

//		try {
//			executor.generateMethods(grad2.getGradient());
//		} catch (MethodGenerationException e) {
//			grad2 = null;
			// something was bad with first derivative
//			System.err.println("{");
//			System.err.println("2nd derivative initialization faild for f=" +
//					equation + " (but we have 1st one).");
//			System.err.println(e.getMessage());
//			e.printStackTrace(System.err);
//			System.err.println("}");
//		}
	}

	/*
	 * Next two functions computes and returns first and second partial
	 * derivatives correspondingly. @argNum@ shows which argument is interesting
	 * to us now.
	 * Returns @null@ in case of any error
	 */
	public RealInterval calc1Derivative(Box box, int argNum) {
		assert(grad1 != null && executor != null); // check that we don't forget to call @init()@
		RealInterval firstDerivative = null;
		try {
			firstDerivative = executor.invokeMethods(box, 
					grad1.getPartialDerivative(argNum));
		} catch (Exception e) {
			// NullPointerException, MethodInvocationException
			// Something went wrong... will return null.			
		}
		return firstDerivative;
	}

	public RealInterval calc2Derivative(Box box, int argNum) {
		assert(grad2 != null && executor != null); // check that we don't forget to call @init()@		
		RealInterval secondDerivative = null;
		try {
			secondDerivative = executor.invokeMethods(box,
					grad2.getPartialDerivative(argNum));
		} catch (Exception e) {
			// NullPointerException, MethodInvocationException
			// Something went wrong... will return null.
		}
		return secondDerivative;
	}
	
	// used by point local optimization methods
	public Gradient getGradient() {
		return grad1;
	}
	
	@Override
	public String toString() {
		return toStringHuman();
	}

	public abstract String toStringHuman(); // in human-readable format (sum and so on can be used)
	public String toStringFull() {  // in format that will allow create an rpn
		// default implementation 
		//as far as for most function human and full formats are the same
		return toStringHuman();		
	}

}
