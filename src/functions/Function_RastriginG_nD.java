package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.20
// g.min: f(??) = -4.687 (n=5); f(??) = -9.66 (n=10)
// 0<=x(i)<=pi
// -sum(sin(x(i))·(sin(i·x(i)^2/pi))^(2·m))
public class Function_RastriginG_nD extends FunctionNEW {
	
	protected double m = 10;
	
	public Function_RastriginG_nD(int dim) {
		StringBuilder sb = new StringBuilder("-(0");
		sb.append(dim);
		for (int i = 1; i <= dim; i++) {
			sb.append(" + ");
			sb.append("sin(x");
			sb.append(i);
			sb.append(")*(sin(");
			sb.append(i);
			sb.append("*x");
			sb.append(i);
			sb.append("^2/Pi))^2*");
			sb.append(m);
		}
		sb.append(")");
		String equation = sb.toString();
		init(dim, equation);
	}
}