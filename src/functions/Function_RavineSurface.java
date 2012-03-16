package functions;


/*
 * This class is kept for legacy purposes
 * And the function is a special function that depends only
 * on one variable. 
 * Created to play with Splitting strategy
 */
public class Function_RavineSurface extends FunctionNEW {
	private final int exp = 4;
	private String equation;

	public Function_RavineSurface(int dim) {
		StringBuilder sb = new StringBuilder("x0^").append(exp);
		for (int i = 1; i < dim; i++) {
			sb.append(" + ");
			sb.append("1e-8*x");
			sb.append(i);
			sb.append("^(1/10)");
		}
		equation = sb.toString();
		init(dim, equation);
	}
}
