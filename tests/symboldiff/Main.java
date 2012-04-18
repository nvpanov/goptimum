package symboldiff;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import symboldiff.exceptions.ExpressionException;
import symboldiff.exceptions.IncorrectExpression;

import core.Box;
import functions.FunctionNEW;
import functions.Function_DeJong_nD;
import functions.Function_SixHumpCamelBack_2D;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

public class Main {
	Random rnd = new Random();

	@Test
	public void t2() throws Exception {
		Expression e = new Expression("x");
		assertEquals("x", e.toString());
	}

	@Test
	public void t3_0() throws Exception {
		Expression exp = null;
		exp = new Expression("(1-2)");
		assertEquals("1-2", exp.toString());
		exp = new Expression("(-1-2)");
		assertEquals("-1-2", exp.toString());
		Simplifier.simplify(exp);
		assertEquals("-3", exp.toString());
	}
	@Test
	public void t3() throws Exception {
		Expression exp = new Expression("x^-1");
		Gradient g = new Gradient(exp);
		g.getPartialDerivative(0);
		Simplifier.simplify(g.getPartialDerivative(0));
	}
	
	@Test
	public void t0() throws Exception {
		Expression exp = new Expression("x3*x3*x2");
		Gradient g = new Gradient(exp);
		Simplifier.simplify(g.getPartialDerivative(0)); //nvp 01/19/12 -- Gradient doesn't call simplify() anymore
		Simplifier.simplify(g.getPartialDerivative(1));
		assertEquals("x3^2", g.getPartialDerivative(0).toString());
		assertEquals("2*x2*x3", g.getPartialDerivative(1).toString());
	}
	@Test
	public void t1() throws Exception {
	  RPN rpn;
	  Expression expr;
	  Gradient grad;
	    
	    //rpn = new RPN("arcctg(x_1+1)*sqrt(x_3)-asdasdasdasdasd + x_4");
	    rpn = new RPN("x1*x1+x2*x2+x3*x3*x2");
	    expr = new Expression(rpn);
	    grad = new Gradient(expr);
	    for (int i = 0; i < 3; i++)
	    	Simplifier.simplify(grad.getPartialDerivative(i));
	    assertEquals("x1*x1+x2*x2+x3*x3*x2", expr.toString());
	    assertEquals("2*x1+2*x2+x3^2+2*x2*x3", grad.toString() );
	}
	
	@Test
	public void testDeJongdiff() throws ExpressionException {
		int dim = 2;
		Function_DeJong_nD deJong_ = new Function_DeJong_nD(dim);
		Gradient d1f = new Gradient(new Expression(deJong_.toString()));
		for (int i = 0; i < dim; i++)
			Simplifier.simplify(d1f.getPartialDerivative(i));
		assertEquals("2*x0+2*x1", d1f.toString());
		
		Box b = new Box(dim, new RealInterval(1.2640918036031814) );
		RealInterval df_dx1_val = d1f.getPartialDerivative(1).evaluate(b);
		assertTrue(df_dx1_val.wid() < 1e-4);
		assertTrue(Math.abs(df_dx1_val.hi() - b.getInterval(1).hi()*2) < 1e-4);
		
		b.setInterval(0, new RealInterval(2.374262669340239E-6));
		//df_dx1_val = deJong.calc1Derivative(b, 1);
		df_dx1_val = d1f.getPartialDerivative(1).evaluate(b);
		assertTrue(df_dx1_val.wid() < 1e-4);
		assertTrue(Math.abs(df_dx1_val.hi() - b.getInterval(1).hi()*2) < 1e-4);
	}
	
	@Test
	public void testSixHumpDiff_1() throws Exception {
		Expression exp = new Expression("2.1*x^4");
		assertEquals("2.1*x^4", exp.toString() );
		Gradient g = new Gradient(exp);
		Simplifier.simplify(g.getPartialDerivative(0));

//		System.out.println(g);
		Expression df_dx = g.getPartialDerivative(0); // adiff.com: (2.1 * (4 * (x^3))) => (8.4 * (x^3))
		assertEquals("8.4*x^3", df_dx.toString());
		//System.out.println(df_dx);
	}

	@Test
	public void testSixHumpDiff_0() throws Exception {
		String s = "0-1*x^4";
		Gradient g = new Gradient(s);
		Expression df_dx = g.getPartialDerivative(0);
		System.out.println("df: " + df_dx);
		Simplifier.simplify(df_dx);
		System.out.println("simple: " + df_dx);
		assertEquals("-4*x^3", df_dx.toString());
	}
	@Test
	public void testSixHumpDiff_2() throws Exception {
		Expression f = new Expression("0+6*1*x^(6-1)*0.3333333333333333+0+2*1*x^(2-1)*4-0+4*1*x^(4-1)*2.1+y*1+0-0+0");
		Simplifier.removeZeros(f);
		assertEquals("6*1*x^(6-1)*0.3333333333333333+2*1*x^(2-1)*4+4*1*x^(4-1)*2.1+y*1", f.toString());		
	}	
	
	@Test
	public void testSixHumpDiff() throws Exception {
		FunctionNEW f_ = new Function_SixHumpCamelBack_2D(); // 4*x^2 - 2.1*x^4 + 1/3*x^6 + x*y -4*y^2 + 4*y^4
		Expression df_dx = (new Gradient(f_.toString())).getPartialDerivative(0);
		Simplifier.simplify(df_dx);

		// adiff.com: (y + ((1/3) * (6 * (x^5))) + (-2.1 * (4 * (x^3))) + (4 * (2 * x)))
		assertEquals("2*x^5+8*x-8.4*x^3+y", df_dx.toString());
		
		Expression adiff_x = new Expression("(y + ((1/3) * (6 * (x^5))) + (-2.1 * (4 * (x^3))) + (4 * (2 * x)))");
		for (int i = 0; i < 100; i++) {
			double l = 10 * rnd.nextDouble()*(1 - rnd.nextInt(1));
			double h = l + 10 * rnd.nextDouble();
			RealInterval iVal = new RealInterval(l, h);
			Box b = new Box(2, iVal);
			
			RealInterval a = adiff_x.evaluate(b);
			RealInterval r = df_dx.evaluate(b);
			assertTrue(r != null);
			assertTrue(b.toString(), Math.abs(a.lo() - r.lo()) < 1e-3 );
			assertTrue(b.toString(), Math.abs(a.hi() - r.hi()) < 1e-3 );
		}
	}	
	@Test
	public void testDiff() throws Exception {
		Expression exp, df;
		Gradient g;
		exp = new Expression("a/b*c");
		g = new Gradient(exp);
		df = g.getPartialDerivative(0);
		Simplifier.simplify(df);
		assertEquals("c/b", df.toString());
		df = g.getPartialDerivative(1);
		Simplifier.simplify(df);
		assertEquals("-a/b^2*c", df.toString());
		df = g.getPartialDerivative(2);
		Simplifier.simplify(df);
		assertEquals("a/b", df.toString());

		exp = new Expression("a/(b*c)");
		g = new Gradient(exp);
		df = g.getPartialDerivative(0);
		Simplifier.simplify(df);
		assertEquals("c^-1/b", df.toString());
		df = g.getPartialDerivative(1);
		Simplifier.simplify(df);
		assertEquals("-a/(b*c)^2*c", df.toString()); //(-a * c * ((b * c)^(-2)))
		df = g.getPartialDerivative(2);
		Simplifier.simplify(df);
		assertEquals("-a*b/(b*c)^2", df.toString());

		exp = new Expression("a^(b)");
		g = new Gradient(exp);
		df = g.getPartialDerivative(0);
		Simplifier.simplify(df);
		assertEquals("a^(b-1)*b", df.toString());  // b * (a^(-1 + b))
		df = g.getPartialDerivative(1);
		Simplifier.simplify(df);
		assertEquals("a^b*ln(a)", df.toString());  // ((a^b) * log(a))

		exp = new Expression("b^b");
		g = new Gradient(exp);
		df = g.getPartialDerivative(0);
		Simplifier.simplify(df);
		assertEquals("(1+ln(b))*b^b", df.toString());

		exp = new Expression("a^(b^c)");
		g = new Gradient(exp);
		df = g.getPartialDerivative(0);
		Simplifier.simplify(df);
		assertEquals("a^(b^c-1)*b^c", df.toString());
		df = g.getPartialDerivative(1);
		Simplifier.simplify(df);
		assertEquals("a^b^c*b^(c-1)*c*ln(a)", df.toString());  // c * (a^(b^c)) * (b^(-1 + c)) * log(a)

// fails:
		exp = new Expression("(a^b)^c");
		g = new Gradient(exp);
		df = g.getPartialDerivative(0);
		Simplifier.simplify(df);
		assertEquals("a^(b-1)*a^b^(c-1)*b*c", df.toString());  // TODO: actually this is equal to 'a^b^c*b*c/a' !!
		df = g.getPartialDerivative(1);
		Simplifier.simplify(df);
		assertEquals("a^b*a^b^(c-1)*c*ln(a)", df.toString());  // TODO: = c * ln(a) * a^b^c
	}
	@Test
	public void testDiff2() throws ExpressionException {
		// http://www.analyzemath.com/calculus/multivariable/partial_derivatives.html
		checkDerivatives("x^2*y + 2*x + y", "(1+x*y)*2", "1+x^2");
		checkDerivatives("sin(x* y) + cos( x )", "cos(x*y)*y-sin(x)", "cos(x*y)*x");
		checkDerivatives("x*exp(x* y)", "(1+x*y)*exp(x*y)", "exp(x*y)*x^2"); 
		checkDerivatives("ln ( x^2 + 2* y)", "2/(2*y+x^2)*x", "2/(2*y+x^2)");
		checkDerivatives("y* x^2 + 2* y", "2*x*y", "2+x^2");
		checkDerivatives("x *exp(x + y)","(1+x)*exp(x+y)","exp(x+y)*x"); 
		checkDerivatives("x *sin(x - y)", "cos(x-y)*x+sin(x-y)", "-cos(x-y)*x");
		
		// http://www.analyzemath.com/calculus/multivariable/second_order_derivative.html
		checkDerivatives2("sin (x *y)", "cos(x*y)*y", "-sin(x*y)*y^2", "cos(x*y)*x", "-sin(x*y)*x^2");
		checkDerivatives2("x^3 + 2* x* y", "2*y+3*x^2", "6*x", "2*x", "0");
		checkDerivatives2("x^3*y^4 + x^2*y", "2*x*y+3*x^2*y^4", "2*y+6*x*y^4", "4*x^3*y^3+x^2 ", "12*x^3*y^2");
		
		// ....
//		checkDerivatives("sin(x)/(2*x+1)", "((1+2*x)*cos(x)-2*sin(x))/(1+2*x)^2");
		// ^^^ orig: (2*x*cos(x)+cos(x)-2*sin(x))/(2*x+1)^2  
		//           ((1+2*x)*cos(x)   -2*sin(x))/(1+2*x)^2
		checkDerivatives("(2*x+1)^2", "(1+2*x)*4");
		checkDerivatives("sin(x)^2", "2*cos(x)*sin(x)");
		checkDerivatives("sin(x^2)", "2*cos(x^2)*x");
		
		checkDerivatives("3^(x+1)", "3^(1+x)*ln(3)");
		checkDerivatives("x^3+3^(x+1)", "3*x^2+3^(1+x)*ln(3)");

//		checkDerivatives("", "");
	}
	@Test
	public void testDiff2_diffOKSimplificationFails() throws ExpressionException {
		checkDerivatives("ln( x^ln(y) )", "ln(y)/x", "ln(x)/y");
	}
	@Test
	public void testDiff2_diffOKSimplificationFails1() throws ExpressionException {
		checkDerivatives("ln ( 2 *x + y* x)", "1/x", "1/(y+2)");
	}
	@Test
	public void testDiff2_fails() throws ExpressionException {
		checkDerivatives("(1+x)*sin(x)", "(1+x)*cos(x)+sin(x)");
		checkDerivatives("ln(x)*sin(x)", "cos(x)*ln(x)+sin(x)/x");
		checkDerivatives("(1+x)*sin(x)*ln(x)", "(sin(x)+cos(x)+x*cos(x)*ln(x))*((1+x)*sin(x)/x)/x");
		// ^^^ orig: (sin(x)+cos(x)+x*cos(x)*ln(x))*((1+x)*sin(x)/x)/x
		//   actual: ((1+x)*cos(x)+sin(x))*ln(x)+(1+x)*sin(x)/x   
		// actual: (cos+x*cos+sin)*ln+sin/x+sin
		// orig: (sin+cos+x*cos*ln)/x*(sin/x+sin)  
	}
	@Test
	public void testDiff2_fails1() throws ExpressionException {
		checkDerivatives("exp(x)/(x^(2))", "exp(x)*(x-2)/x^3"); 
	}
	@Test
	public void test_Griewangk() throws ExpressionException {
		String f = "1-cos(7*x2)*cos(x1)+2.5E-4*(x1^2+x2^2)";
		f = "2.5E-4*(x1^2+x2^2)";
		checkDerivatives(f, "5.0E-4*x1", "5.0E-4*x2");
		f = "cos(7*x)";
		checkDerivatives(f, "-7*sin(7*x)");
		f = "cos(x2)*cos(x1)";
		checkDerivatives(f, "-cos(x2)*sin(x1)", "-cos(x1)*sin(x2)");
		f = "cos(7*x2)*cos(x1)";
		checkDerivatives(f, "-cos(7*x2)*sin(x1)", "-7*cos(x1)*sin(7*x2)");
	}

	public static void checkDerivatives(String expression, String... diffs) throws ExpressionException {
		Expression exp, df;
		Gradient g;
		exp = new Expression(expression);
		g = new Gradient(exp);
		for (int i = 0; i < diffs.length; i++) {
			df = g.getPartialDerivative(i);
			Simplifier.simplify(df);
			assertEquals(diffs[i], df.toString());
		}
	}
	public static void checkDerivatives2(String expression, String... diffs) throws ExpressionException {
		Expression exp, df;
		Gradient g, g2;
		exp = new Expression(expression);
		g = new Gradient(exp);
		for (int i = 0; i < diffs.length/2; i+=2) {
			df = g.getPartialDerivative(i);
			Simplifier.simplify(df);
			assertEquals(diffs[i], df.toString());
			g2 = new Gradient(df);
			df = g2.getPartialDerivative(0);
			Simplifier.simplify(df);
			assertEquals(diffs[i+1], df.toString());			
		}
	}
}
