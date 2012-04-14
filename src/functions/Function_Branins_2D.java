package functions;

//http://www.zsd.ict.pwr.wroc.pl/files/docs/functions.pdf
//p.23
//f(x1,x2)=0.397887; (x1,x2)=(-pi,12.275), (pi,2.275), (9.42478,2.475).
//a·(x2-b·x1^2+c·x1-d)^2+e·(1-f)·cos(x1)+e
//a=1, b=5.1/(4·pi^2), c=5/pi, d=6, e=10, f=1/(8·pi)
//-5<=x1<=10, 0<=x2<=15.
public class Function_Branins_2D extends FunctionNEW {
	protected static double a = 1.0, b = 5.1/(4.0*Math.pow(Math.PI, 2));
	protected static double c=5.0/Math.PI, d=6, e=10, f=1/(8*Math.PI);
	private static final String equation = a+"*(x2-"+b+"*x1^2+"+c+"*x1-"+d+")^2+"+e+"*(1-"+f+")*cos(x1)+"+e;
	
	public Function_Branins_2D() {
		init(2, equation);
	}
}
