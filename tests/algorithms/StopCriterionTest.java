package algorithms;

import java.util.Random;

import junit.framework.TestCase;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import solvers.Bisection_SrtL_CBtC_BigEqS;

import algorithms.StopCriterion;

import core.Box;

public class StopCriterionTest extends TestCase {
	protected Box setupBox() {
		   Random rnd = new Random();
		   int dim = rnd.nextInt(6)+1;
		   Box b = new Box(dim, new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   b.setFunctionValue(new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   return b;
	}
	
	@Test
	public void testIterations() {
		BaseAlgorithm alg = new Bisection_SrtL_CBtC_BigEqS();
		Box b = setupBox();
		StopCriterion c = new StopCriterion(alg);
		c.setMaxIterations(3);
		c.setFMaxPrecision(Double.MIN_VALUE);
		for (int i = 0; i < 3; i++)
			assertFalse(c.isDone(b)); 	// 1, 2, 3
		assertTrue(c.isDone(b)); 		// 4
	}
	@Test
	public void testFWidth() {
		Box b = setupBox();
		BaseAlgorithm alg = new Bisection_SrtL_CBtC_BigEqS();
		alg.probeNewLowBoundMaxValue(b.getFunctionValue().hi());
		StopCriterion c = new StopCriterion(alg);
		c.setFMaxPrecision(IAMath.wid(b.getFunctionValue()) - 1e-4);
		if (c.isDone(b))
			assertFalse(true);
		c.setFMaxPrecision(IAMath.wid(b.getFunctionValue()));
		if (c.isDone(b))
			assertTrue(true);
		c.setFMaxPrecision(IAMath.wid(b.getFunctionValue()) + 1e-4);
		if (c.isDone(b))
			assertTrue(true);
	}		   
}
