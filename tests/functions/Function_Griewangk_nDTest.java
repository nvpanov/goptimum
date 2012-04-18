package functions;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import symboldiff.Expression;
import symboldiff.Gradient;
import symboldiff.Simplifier;
import symboldiff.exceptions.ExpressionException;

import core.Box;


public class Function_Griewangk_nDTest {

	@Test
	public void testFunction() {
		FunctionNEW f = new Function_Griewangk_nD(2);
		assertEquals("1-cos(0.7071067811865475*x2)*cos(x1)+2.5E-4*(x1^2+x2^2)", f.toString());	
	}
	@Test
	public void test1Derivative() throws ExpressionException {
		FunctionNEW f = new Function_Griewangk_nD(2);
		Expression equation = new Expression(f.toString());
		Gradient g = new Gradient(equation);
		assertTrue(g != null);
		Expression d1x = g.getPartialDerivative(0);
		Simplifier.simplify( d1x );
		assertEquals("5.0E-4*x1+cos(0.7071067811865475*x2)*sin(x1)", d1x.toString());
		Expression d1y = g.getPartialDerivative(1);
		Simplifier.simplify( d1y );
//		System.out.println(d1y.toString());
		//assertEquals("5.0E-4*x2+0.7071067811865475*cos(x1)*sin(0.7071067811865475*x2)", d1y.toString());
		assertEquals("5.0E-4*x2--0.7071067811865475*cos(x1)*sin(0.7071067811865475*x2)", d1y.toString());
		//                     ^^
		
		Random rnd = new Random();
		long seed = System.currentTimeMillis();
		rnd.setSeed(seed);
		for (int i = 0; i < 20; i++) {
			// create new box 
			Box box = null;
			do {
				try {
					box = new Box(2, new RealInterval(
											100*(rnd.nextDouble()-0.3), 200*(rnd.nextDouble()) )
								);
				} catch (Exception e) {
					// RealInterval can throw an exception if left bound > right
				}
			}while (box == null);
			
			// check calculation on this box
			// 1) -- d1x 
			RealInterval v1 = f.calculate1Derivative(box, 0);
			RealInterval v2 = d1x.evaluate(box);
			assertTrue("FAILD with seed="+seed, v1.almostEquals(v2));
			// 2) -- d1y
			v1 = f.calculate1Derivative(box, 1);
			v2 = d1y.evaluate(box);
			assertTrue("FAILD with seed="+seed, v1.almostEquals(v2));
			
			// check point
			// dx
			box = new Box(2, new RealInterval(rnd.nextDouble()) );
			v1 = d1x.evaluate(box);
			double vp = d1x.evaluate(box.getInterval(0).lo(), box.getInterval(1).lo());
			assertTrue("FAILD with seed="+seed, v1.almostEquals(new RealInterval(vp)));
			v2 = f.calculate1Derivative(box, 0);
			assertTrue("FAILD with seed="+seed, v1.almostEquals(v2));
			//dy
			v1 = d1y.evaluate(box);
			vp = d1y.evaluate(box.getInterval(0).lo(), box.getInterval(1).lo());
			assertTrue("FAILD with seed="+seed, v1.almostEquals(new RealInterval(vp)));
			v2 = f.calculate1Derivative(box, 1);
			assertTrue("FAILD with seed="+seed, v1.almostEquals(v2));
		}
		// known points
		//dx
		double x[] = {0,1,1,Math.PI, 100};
		double y[] = {0,0,1,Math.PI/2, 100};
		double z[] = {0,0.841971,0.640224,0.0015708, 0.062578557};
		for (int i = 0; i < x.length; i++) {
			Box box = new Box(2, new RealInterval(x[i]) );
			box.setInterval(1, y[i]);
			RealInterval v = d1x.evaluate(box);
			RealInterval v1 = f.calculate1Derivative(box, 0);
			double vp = d1x.evaluate(x[i], y[i]);
			assertTrue(new RealInterval(v).almostEquals(new RealInterval(z[i])));
			assertTrue(v.almostEquals(new RealInterval(vp)));
			assertTrue(v.almostEquals(v1));
		}
		//dy
		//100, 100 => 0.659563
		Box box = new Box(2, new RealInterval(100) );
		RealInterval v1 = d1y.evaluate(box);
		RealInterval v2 = f.calculate1Derivative(box, 1);
		double vp = d1y.evaluate(100, 100);
		assertTrue(v1.almostEquals(new RealInterval(0.659563)));
		assertTrue(v1.almostEquals(new RealInterval(vp)));
		assertTrue(v1.almostEquals(v2));
	}
	@Test
	public void test2Derivative() throws ExpressionException {
		FunctionNEW f = new Function_Griewangk_nD(2);
		Expression equation = new Expression(f.toString());
		Gradient g1 = new Gradient(equation);
		assertTrue(g1 != null);
		Expression d1x = g1.getPartialDerivative(0);
//		Simplifier.simplify( d1x );		
//		System.out.println(d1x);
		Gradient g2 = new Gradient(d1x, equation.getVariables());
		Expression d2x = g2.getPartialDerivative(0);
		Expression d1y = g1.getPartialDerivative(1);
		g2 = new Gradient(d1y, equation.getVariables());
		Expression d2y = g2.getPartialDerivative(1);
		
		Simplifier.simplify( d2x );
		Simplifier.simplify( d2y );
		assertEquals("5.0E-4+cos(0.7071067811865475*x2)*cos(x1)", d2x.toString());
		//assertEquals("5.0E-4--0.5*cos(0.7071067811865475*x2)*cos(x1)", d2y.toString());	
		assertEquals("5.0E-4--0.4999999999999999*cos(0.7071067811865475*x2)*cos(x1)", d2y.toString());	
	}

}
