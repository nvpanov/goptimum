package functions;


// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.11
// g.min: f(x) = 0, x = 0.
// [-600, 600]
public class Function_Griewangk_nD extends FunctionNEW {
	public Function_Griewangk_nD(int dim) {
		StringBuilder sb = new StringBuilder("1/4000*(0");
		for (int i = 1; i <= dim; i++) {
			sb.append("+x");
			sb.append(i);
			sb.append("^2");
		}
		sb.append(")-(1");
		for (int i = 1; i <= dim; i++) {
			sb.append("*cos(x");
			sb.append(i);
			sb.append("/sqrt(");
			sb.append(i);
			sb.append("))");
		}
		sb.append(")+1");
		
		String equation = sb.toString();
		init(dim, equation);
	}
}