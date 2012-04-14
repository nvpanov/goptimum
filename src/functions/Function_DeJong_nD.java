package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.2
// f(x) = 0, x = 0
// [-5.12, 5.12]

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
