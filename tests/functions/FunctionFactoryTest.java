package functions;

import static org.junit.Assert.*;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import core.Box;

import symboldiff.exceptions.ExpressionException;
import symboldiff.exceptions.IncorrectExpression;

public class FunctionFactoryTest {
	FunctionNEW f;
	RealInterval ONE = new RealInterval(1);
	RealInterval ZERO= new RealInterval(0);
	
	@Test
	public void testNewFunction() {
		try {
			// 1d
			RealInterval i = new RealInterval(0, 1);
			Box b1 = new Box(1, i);
			f = FunctionFactory.newFunction("x");
			f.calculate(b1);
			assertTrue(b1.getFunctionValue().almostEquals(i));
			double r = f.calculatePoint(i.lo());
			assertTrue(Math.abs(r-i.lo()) < 1e-3);
			r = f.calculatePoint(i.hi());
			assertTrue(Math.abs(r-i.hi()) < 1e-3);
			RealInterval d = f.calculate1Derivative(b1, 0);
			assertTrue(d.almostEquals(ONE));
			d = f.calculate2Derivative(b1, 0);
			assertTrue(d.almostEquals(ZERO));

			// 0d
			f = FunctionFactory.newFunction("-0.00+1");
			f.calculate(b1);
			assertTrue(b1.getFunctionValue().almostEquals(ONE));
			r = f.calculatePoint(i.lo());
			assertTrue(Math.abs(r-1) < 1e-3);
			r = f.calculatePoint(i.hi());
			assertTrue(Math.abs(r-1) < 1e-3);
			d = f.calculate1Derivative(b1, 0);
			assertTrue(d.almostEquals(ZERO));
			d = f.calculate2Derivative(b1, 0);
			assertTrue(d.almostEquals(ZERO));
			
			//errors
			try {
				f = FunctionFactory.newFunction("(");
				fail("exception expected");
			} catch (IncorrectExpression e) {
				assertTrue(e.getMessage().contains("1 unclosed bracket"));
			}
			try {
				f = FunctionFactory.newFunction("((");
				fail("exception expected");
			} catch (IncorrectExpression e) {
				assertTrue(e.getMessage().contains("2 unclosed brackets"));
			}
			try {
				f = FunctionFactory.newFunction(")");
				fail("exception expected");
			} catch (IncorrectExpression e) {
				assertTrue(e.getMessage().contains("1 extra closing bracket"));
			}
			try {
				f = FunctionFactory.newFunction("ssin(x)");
				fail("exception expected");
			} catch (IncorrectExpression e) {
				assertTrue(e.getMessage().contains("Unsupported function") && e.getMessage().contains("ssin"));
			}
			try {
				f = FunctionFactory.newFunction("sin()");
				fail("exception expected");
			} catch (IncorrectExpression e) {
				System.out.println(e.getMessage());
				assertTrue(e.getMessage().contains("Expression >sin(???)< is incorrect. Something should be instead of '???'"));
			}
			try {
				f = FunctionFactory.newFunction("+");
				fail("exception expected");
			} catch (IncorrectExpression e) {
				System.out.println(e.getMessage());
				assertTrue(e.getMessage().contains("Expression >???+???< is incorrect. Something should be instead of '???'"));
			}
			
			f = FunctionFactory.newFunction("sin(x*cos(-y)+2)^2+z+1");
			assertEquals(3, f.getDimension());
			Box b3 = new Box(3, i);
/*			try {
				f.calculate(b1);
				fail("wrong dimension -- assert expected");
			}catch(AssertionError e) {
				// OK.
			}
*/			
			f.calculate(b3);
		} catch (ExpressionException e) {
			fail("these tests are correct");
		}
	}
	
	@Test
	public void t1() throws ExpressionException {
		String equation = "1e-8 + 2+ee-2 - 3.14e+9 - 2.1e2";//*x^(1/10)";
		f = FunctionFactory.newFunction(equation);
		System.out.println(f);
		assertEquals(1, f.getDimension());
		
		equation = "1e-8*x^(1/10)";
		f = FunctionFactory.newFunction(equation);
		double rd;
		Box b = new Box(1, ZERO);
		rd = f.calculatePoint(0);
		f.calculate(b);
		assertTrue(Math.abs(rd-0) < 1e-6);
		assertTrue(b.getFunctionValue().almostEquals(ZERO));
		rd = f.calculatePoint(1e100);
		b.setInterval(0, 1e100);
		f.calculate(b);
		assertTrue(Math.abs(rd-0) < 1e-6);
		assertTrue(b.getFunctionValue().almostEquals(ZERO));
	}

}
