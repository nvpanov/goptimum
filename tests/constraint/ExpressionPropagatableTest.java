package constraint;

import static org.junit.Assert.*;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import core.Box;
import functions.Function_DeJong_nD;

import symboldiff.exceptions.ExpressionException;

public class ExpressionPropagatableTest {

	private ExpressionPropagatable exp;
	private Box area;
	private RealInterval constrainingValue;
	boolean propagated;
	
	@Test
	public void testPropagateVariable() throws ExpressionException, RepugnantConditionException {
		exp = new ExpressionPropagatable("a");
		area = new Box(1, new RealInterval(-1, 1));
		constrainingValue = new RealInterval(0, 1);
		Box check = new Box(1, constrainingValue);
		check.setFunctionValue(constrainingValue);
		propagated = exp.propagate(area, constrainingValue);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue(propagated);
		assertEquals(check, area);
	}
	
	@Test
	public void testPropagateAdd() throws Exception {
		exp = new ExpressionPropagatable("a+b");
		area = new Box(2, new RealInterval(-1, 1));
		constrainingValue = new RealInterval(1, 1);
		Box check = new Box(2, new RealInterval(0, 1));
		check.setFunctionValue(constrainingValue);
		propagated = exp.propagate(area, constrainingValue);
		assertTrue(propagated);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue( check.almostEquals(area) );
	}
	@Test
	public void testPropagateAdd2() throws Exception {
		exp = new ExpressionPropagatable("a+b+c");
		area = new Box(3, new RealInterval(0, 1));
		constrainingValue = new RealInterval(0, 1);
		Box check = new Box(3, new RealInterval(0, 1));
		check.setFunctionValue(constrainingValue);
		propagated = exp.propagate(area, constrainingValue);
		assertTrue(propagated);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue( check.almostEquals(area) );
	}
	@Test
	public void testPropagateAdd3() throws Exception {
		exp = new ExpressionPropagatable("a+b+c");
		area = new Box(3, new RealInterval(0.1, 1));
		constrainingValue = new RealInterval(0, 0.299);
		try {
			propagated = exp.propagate(area, constrainingValue);
			fail();
		} catch (RepugnantConditionException e) {
			//OK
		}
	}
	@Test
	public void testPropagateSubAdd() throws Exception {
		exp = new ExpressionPropagatable("a-b+c");
		area = new Box(3, new RealInterval(10, 20));
		area.setInterval(1, new RealInterval(0,10));
		constrainingValue = new RealInterval(0,15);
		propagated = exp.propagate(area, constrainingValue);
		assertTrue(propagated);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue(new RealInterval(10, 15).almostEquals(area.getFunctionValue()) );
		assertTrue(new RealInterval(10, 15).almostEquals(area.getInterval(0)) );
		assertTrue(new RealInterval( 5, 10).almostEquals(area.getInterval(1)) );
		assertTrue(new RealInterval(10, 15).almostEquals(area.getInterval(2)) );
	}

	@Test
	public void testPropagateDiv() throws Exception {
		exp = new ExpressionPropagatable("a/b");
		area = new Box(2, new RealInterval(0, 100));
		area.setInterval(1, new RealInterval(5,100));
		constrainingValue = new RealInterval(0,0);
		propagated = exp.propagate(area, constrainingValue);
		assertTrue(propagated);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue(new RealInterval(0, 0).almostEquals(area.getFunctionValue()) );
		assertTrue(new RealInterval(0, 0).almostEquals(area.getInterval(0)) );
		assertTrue(new RealInterval(5, 100).almostEquals(area.getInterval(1)) );
	}
	@Test
	public void testPropagateDivMul() throws Exception {
		exp = new ExpressionPropagatable("a/b*c");
		area = new Box(3, new RealInterval(0, 100));
		area.setInterval(1, new RealInterval(5,100));
		constrainingValue = new RealInterval(0,0);
		propagated = exp.propagate(area, constrainingValue);
		assertFalse(propagated);
		
		area.setInterval(2, new RealInterval(0.1,100));
		propagated = exp.propagate(area, constrainingValue);
		assertTrue(propagated);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue(new RealInterval(0).almostEquals(area.getFunctionValue()) );
		assertTrue(new RealInterval(0).almostEquals(area.getInterval(0)) );
		assertTrue(new RealInterval(5, 100).almostEquals(area.getInterval(1)) );
		assertTrue(new RealInterval(0.1, 100).almostEquals(area.getInterval(2)) );
	}
	@Test
	public void testPropagateF() throws Exception {
		int dim = 10;
		Function_DeJong_nD f = new Function_DeJong_nD(dim);
		exp = new ExpressionPropagatable(f.toString());
		area = new Box(dim, new RealInterval(-100, 100));
		constrainingValue = new RealInterval(-10,10);
		propagated = exp.propagate(area, constrainingValue);
		assertTrue(propagated);
		area.setFunctionValue( exp.evaluate(area) );
		assertTrue(new RealInterval(0, 10).almostEquals(area.getFunctionValue() ));
		for (int i = 0; i < dim; i++) {
			assertTrue(new RealInterval(0, Math.sqrt(10)).almostEquals(area.getInterval(i) ));
		}
	}
}
