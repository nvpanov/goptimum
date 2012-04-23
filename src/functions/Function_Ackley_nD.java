package functions;

// http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
// p.15
// g.min: f(0) = 0
// [-32,768, 32,768]
public class Function_Ackley_nD extends FunctionNEW {
	
	protected double a = 20, b = 0.2, c = 2*Math.PI;
	
	public Function_Ackley_nD(int dim) {
		StringBuilder sb = new StringBuilder("-");
		sb.append(a).append("*exp(-").append(b).append("*sqrt(1/"); // exp(, sqrt(
		sb.append(dim);
		sb.append("*(0"); 											// (
		for (int i = 1; i <= dim; i++) {
			sb.append("+x");
			sb.append(i);
			sb.append("^2");
		}
		sb.append(")))-exp(1/");  									//)	exp), sqrt), exp(
		sb.append(dim);
		sb.append("*(0"); 								// (
		for (int i = 1; i <= dim; i++) {
			sb.append("+cos(c*x");
			sb.append(i);
			sb.append(")");
		}
		sb.append("))+");								// )						exp)
		sb.append(a).append("+exp(1");
		
		String equation = sb.toString();
		init(dim, equation);
	}
}