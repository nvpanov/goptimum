package symboldiff;

import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.Box;

import symboldiff.exceptions.ExpressionException;

public class ExpressionTest {
	Expression exp;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testGetCoords() throws Exception {
		exp = new Expression("x + xyz0 * y^x + Y/sin(0)+cos(a)");
		ArrayList<String> variables = exp.getVariables();
		assertEquals(variables.get(0), "a");
		assertEquals(variables.get(1), "x");
		assertEquals(variables.get(2), "xyz0");
		assertEquals(variables.get(3), "y");
		assertTrue(variables.size() == 4);
	}
	@Test
	public final void testIsConstant() throws Exception {
		exp = new Expression("x");
		assertFalse(exp.isConstant());
		exp = new Expression("0");
		assertTrue(exp.isConstant());
	}

	@Test
	public final void testIsVariable() throws Exception {
		exp = new Expression("x");
		assertTrue(exp.isVariable());
		exp = new Expression("0");
		assertFalse(exp.isVariable());
	}
	@Test
	public void testEvaluate() throws Exception {
		RealInterval iVal = new RealInterval(-4, 6);
		Box area = new Box(3, iVal);
		exp = new Expression("x+y+z");
		RealInterval res = exp.evaluate(area);
		RealInterval check = IAMath.mul(iVal,3);
		assertTrue(Math.abs(res.lo() - check.lo()) < 1e-3);
		assertTrue(Math.abs(res.hi() - check.hi()) < 1e-3);
		
		exp = new Expression("x*y+3");
		area = new Box(2, iVal);
		res = exp.evaluate(area);
		check = IAMath.add(IAMath.mul(iVal,iVal), 3);
		assertTrue(Math.abs(res.lo() - check.lo()) < 1e-3);
		assertTrue(Math.abs(res.hi() - check.hi()) < 1e-3);
		
//		res = exp.evaluate((Box)null);

		try {
			exp = new Expression("f(x)");
			fail("exception expected");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("Unsupported function f(x)"));			
		}
		double[] p = {2,0};		
		exp = new Expression("x/0");
		try {
			exp.evaluate(p);
			fail("exception expected");
		} catch (AssertionError e) { }		

		exp = new Expression("x/y");
		try {
			exp.evaluate(p);
			fail("exception expected");
		} catch (AssertionError e) { }		

	}
	@Test
	public final void testBrackets() throws Exception {
		exp = new Expression("1-2*3/4");
		assertEquals("1-2*3/4", exp.toString());
		exp = new Expression("1-(2*3/4)-(5+6*(7-8+9))");
		assertEquals("1-2*3/4-(5+6*(7-8+9))", exp.toString());
	}
	@Test
	public final void test0() throws Exception {
		exp = new Expression("a/b*c");
		assertEquals("a/b*c", exp.toString());
		exp = new Expression("a/(b*c)");
		assertEquals("a/(b*c)", exp.toString());
		exp = new Expression("a/b/c");
		assertEquals("a/b/c", exp.toString());
		exp = new Expression("a/(b/c)");
		assertEquals("a/(b/c)", exp.toString());
		exp = new Expression("a^b^c");
		assertEquals("a^b^c", exp.toString());
		exp = new Expression("a^(b^c)");
		assertEquals("a^(b^c)", exp.toString());
	}
	@Test
	public void test_e() throws ExpressionException {
		exp = new Expression("1.25E-4*x1^2");
		assertEquals("1.25e-4*x1^2", exp.toString());
		exp = new Expression("Z*1.25E-4*x1^2");
		assertEquals("z*1.25e-4*x1^2", exp.toString());
	}	
	
	@Test
	public void test1() throws ExpressionException {
		exp = new Expression("(a+b)/(c+d)");
		assertEquals("(a+b)/(c+d)", exp.toString());		
	}

    @Test
    public void testEvaluate1_1() throws ExpressionException {
    	Expression exp = new Expression("(2*x^3*y-y^3)^2");
    	Box bigBox = new Box(2, new RealInterval(-1.05, 0.28));
    	bigBox.setInterval(1, new RealInterval(-3.70, 1.60));
    	Box smallBox = bigBox.splitSide(0, 0.5)[0];
    	RealInterval bigVal = exp.evaluate(bigBox);
    	RealInterval smallVal = exp.evaluate(smallBox);
    	assertTrue (bigVal.contains(smallVal) );
    }
    @Test
    public void testEvaluate1_2() throws ExpressionException {
    	Expression exp = new Expression("(6*x+y-y^2)^2");
    	Box bigBox = new Box(2, new RealInterval(-1.05, 0.28));
    	bigBox.setInterval(1, new RealInterval(-3.70, 1.60));
    	Box smallBox = bigBox.splitSide(0, 0.5)[0];
    	RealInterval bigVal = exp.evaluate(bigBox);
    	RealInterval smallVal = exp.evaluate(smallBox);
    	assertTrue (bigVal.contains(smallVal) );
    }
    @Ignore
    @Test
    public void testEvaluate1_2_1() throws ExpressionException {
    	Expression exp = new Expression("(x-y^2)^2");
    	Box bigBox = new Box(2, new RealInterval(-1, 0.2));
    	bigBox.setInterval(1, new RealInterval(-2, 1));
    	Box smallBox = bigBox.splitSide(0, 0.5)[0];
    	RealInterval bigVal = exp.evaluate(bigBox);
    	RealInterval smallVal = exp.evaluate(smallBox);
    	assertTrue (bigVal.contains(smallVal) );
    }
    @Test
    public void testEvaluate1_2_2() throws ExpressionException {
    	Expression exp = new Expression("(a)^2");
    	Box b = new Box(1, new RealInterval(-5, 0.1));
    	RealInterval val = exp.evaluate(b);
    	assertTrue (val.contains(25) );
    }
	
}
