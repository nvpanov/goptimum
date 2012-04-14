package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.6
// f(1) = 0
// [-2.048, 2.048]

public class Function_RosenbrockG_nD extends FunctionNEW {
	public Function_RosenbrockG_nD(int dim) {
		if (dim < 2)
			throw new IllegalArgumentException(this.getClass().getName() + 
					" can't be less than 1d function");
		StringBuilder sb = new StringBuilder("0");
		for (int i = 0; i < dim-1; i++) {
			sb.append(" + ");
			sb.append("(100*(x");
			sb.append(i+1);
			sb.append("-x");
			sb.append(i);
			sb.append("^2)^2 + (x");
			sb.append(i);
			sb.append("-1)^2)");
		}
		String equation = sb.toString();
		init(dim, equation);
	}
}