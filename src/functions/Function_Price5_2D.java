package functions;

/**
 * The class is kept to have previously written tests work.
 */
public class Function_Price5_2D extends FunctionNEW {
	private static final String equation = "(2*x^3 * y - y^3)^2 + (6*x - y^2 + y)^2";
	//(2x^3y - y^3)^2 + (6x - y^2 + y)^2
	// min f = 0, x = y = 0
	
	public Function_Price5_2D() {
		init(2, equation);
	}
}
