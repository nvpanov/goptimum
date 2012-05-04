package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.7
// also http://www.geatbx.com/docu/fcnindex-01.html
// f6(x)=10·n+sum(x(i)^2-10·cos(2·pi·x(i))), i=1:n; -5.12<=x(i)<=5.12.
// f(x)=0; x(i)=0, i=1:n.
public class Function_RastriginG_nD extends FunctionNEW {

	public Function_RastriginG_nD(int dim) {
		StringBuilder sb = new StringBuilder("10*");
		sb.append(dim);
		for (int i = 1; i <= dim; i++) {
			sb.append(" + ");
			sb.append("x");
			sb.append(i);
			sb.append("^2-10*cos(2/Pi*x");
			sb.append(i);
			sb.append(")");
		}
		String equation = sb.toString();
		init(dim, equation);
	}
}