package functions;

/**
 * @author  nvpanov
 */
public class Function_SixHumpCamelBack_2D extends FunctionNEW {
	private static final String equation = "4*x^2 - 2.1*x^4 + 1/3*x^6 + x*y -4*y^2 + 4*y^4";
	// 4x^2 - 2.1x^4 + \frac{1}{3}x^6 + xy -4y^2 + 4y^4 
	// 2 g.min f = -1.03163, x = 0.08984, y = -0.71266; 
	// 						 x = -0.08984, y = 0.71266;

	public Function_SixHumpCamelBack_2D() {
		init(2, equation);
	}
}
