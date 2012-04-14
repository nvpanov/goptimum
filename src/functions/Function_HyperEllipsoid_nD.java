package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.4
// G.min: 	f(0) = 0
// [-5.12, 5,12]
public class Function_HyperEllipsoid_nD extends FunctionNEW {

	public Function_HyperEllipsoid_nD(int dim) {
		StringBuilder sb = new StringBuilder("x1^2");
		for (int i = 2; i <= dim; i++) {
			sb.append("+");
			sb.append(i);
			sb.append("*x");
			sb.append(i);
			sb.append("^2");
		}
		String equation = sb.toString();
		init(dim, equation);
	}
}
