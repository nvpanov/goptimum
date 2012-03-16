package symboldiff;

import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Test;

import core.Box;

import symboldiff.exceptions.IncorrectExpression;

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
	}
	@Test
	public final void testBrackets() throws Exception {
		exp = new Expression("1-2*3/4");
		assertEquals("1-2*3/4", exp.toString());
		exp = new Expression("1-(2*3/4)-(5+6*(7-8+9))");
		assertEquals("1-2*3/4-(5+6*(7-8+9))", exp.toString());
	}	
}
