package symboldiff;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import symboldiff.exceptions.IncorrectExpression;

public class ExpressionTest {
	Expression exp;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testGetCoords() throws IncorrectExpression {
		exp = new Expression("x + xyz0 * y^x + Y/sin(0)+cos(a)");
		ArrayList<String> variables = exp.getVariables();
		assertEquals(variables.get(0), "a");
		assertEquals(variables.get(1), "x");
		assertEquals(variables.get(2), "xyz0");
		assertEquals(variables.get(3), "y");
		assertTrue(variables.size() == 4);
	}
	@Test
	public final void testIsConstant() throws IncorrectExpression {
		exp = new Expression("x");
		assertFalse(exp.isConstant());
		exp = new Expression("0");
		assertTrue(exp.isConstant());
	}

	@Test
	public final void testIsVariable() throws IncorrectExpression {
		exp = new Expression("x");
		assertTrue(exp.isVariable());
		exp = new Expression("0");
		assertFalse(exp.isVariable());
	}
	
}
