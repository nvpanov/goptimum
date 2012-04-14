package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.7
// g.min: f(0) = 0
// [-5.12, 5.12]
public class Function_MichalewiczG_nD extends FunctionNEW {
	public Function_MichalewiczG_nD(int dim) {
		StringBuilder sb = new StringBuilder("10*");
		sb.append(dim);
		for (int i = 1; i <= dim; i++) {
			sb.append(" + ");
			sb.append("(x");
			sb.append(i);
			sb.append("^2-10*cos(2*Pi*x");
			sb.append(i);
			sb.append("))");
		}
		String equation = sb.toString();
		init(dim, equation);
	}
}