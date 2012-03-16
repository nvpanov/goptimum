package functions;

/**
 * @author  nvpanov
 * The class is kept for solvers tests that use it
 */
public class Function_Rastrigin10_2D extends FunctionNEW {
	protected static final String equation = "x^2 + y^2 - cos(18*x) - cos(18*y)";
// min f = -2, x = y = 0 
	
	public Function_Rastrigin10_2D() {
		init(2, equation);
	}

}