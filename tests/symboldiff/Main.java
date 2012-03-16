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
		assertEquals("-3.0", exp.toString());
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
		assertEquals("x3^2.0", g.getPartialDerivative(0).toString());
		assertEquals("2.0*x2*x3", g.getPartialDerivative(1).toString());
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
	    assertEquals("2.0*x1+2.0*x2+x3^2.0+2.0*x2*x3", grad.toString() );
	}
	
	@Test
	public void testDeJongdiff() throws ExpressionException {
		int dim = 2;
		Function_DeJong_nD deJong_ = new Function_DeJong_nD(dim);
		Gradient d1f = new Gradient(new Expression(deJong_.toString()));
		for (int i = 0; i < dim; i++)
			Simplifier.simplify(d1f.getPartialDerivative(i));
		assertEquals("2.0*x0+2.0*x1", d1f.toString());
		
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
		assertEquals("8.4*x^3.0", df_dx.toString());
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
		assertEquals("-4.0*x^3.0", df_dx);
	}
	@Test
	public void testSixHumpDiff_2() throws Exception {
		Expression f = new Expression("0.0+6*1.0*x^(6-1)*0.3333333333333333+0.0+2*1.0*x^(2-1)*4.0-0.0+4*1.0*x^(4-1)*2.1+y*1.0+0.0-0.0+0.0");
		Simplifier.removeZeros(f);
		assertEquals("6*1.0*x^(6-1)*0.3333333333333333+2*1.0*x^(2-1)*4.0+4*1.0*x^(4-1)*2.1+y*1.0", f.toString());		
	}	
	
	@Test
	public void testSixHumpDiff() throws Exception {
		FunctionNEW f_ = new Function_SixHumpCamelBack_2D(); // 4*x^2 - 2.1*x^4 + 1/3*x^6 + x*y -4*y^2 + 4*y^4
		Expression df_dx = (new Gradient(f_.toString())).getPartialDerivative(0);
		Simplifier.simplify(df_dx);

		// adiff.com: (y + ((1/3) * (6 * (x^5))) + (-2.1 * (4 * (x^3))) + (4 * (2 * x)))
		assertEquals("2.0*x^5.0+8.0*x-8.4*x^3.0+y", df_dx.toString());
		
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

}
