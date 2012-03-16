package functions;

/*
 * The class is kept for backward compatibility with previously written tests
 * And this function is not more than a \sum x_i^2
 */
public class Function_DeJong_nD extends FunctionNEW {

	public Function_DeJong_nD(int dim) {
		StringBuilder sb = new StringBuilder("x0^2");
		for (int i = 1; i < dim; i++) {
			sb.append(" + ");
			sb.append("x");
			sb.append(i);
			sb.append("^2");
		}
		String equation = sb.toString();
		init(dim, equation);
	}
}
