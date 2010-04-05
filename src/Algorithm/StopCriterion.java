package Algorithm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

public class StopCriterion {
	protected int maxIterations;
	protected int iteration;
	protected double fPrecision;
//	protected double argPrecision;
//	protected int maxTimeS;
//	protected int startTimeS;
	
	public StopCriterion() {
		maxIterations = (int)1e3;
		fPrecision = 1e-2;
//		argPrecision = 1e-2;
//		maxTimeS = 200;
	}
	public boolean isDone(Box b) {
		// 1. iterations
		// time or iterations counter is obligatory to protect from infinite iterations
		if (++iteration > maxIterations)
			return true;
		
		// 2. F width
		if (IAMath.wid( b.getFunctionValue() ) < fPrecision)
			return true;
		
		// 3. X width
		// F width is a function from arguments width. 
		// So we can omit checks the width of all arguments and save CPU time for this function

		// 4. time
		// again lets relay only on iteration counting and improve the performance of
		// this criterion by skipping time measurements.
		
		return false;
	}


	
	////////////////// Tests /////////////////////////////
	
	protected Box setupBox() {
		   Random rnd = new Random();
		   int dim = rnd.nextInt(6);
		   Box b = new Box(dim, new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   b.setFunctionValue(new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   return b;
	}
	
	@Test
	public void testIterations() {
		Box b = setupBox();
		StopCriterion c = new StopCriterion();
		c.iteration = c.maxIterations - 3;
		c.fPrecision = Double.MIN_VALUE;
		for (int i = 0; i < 3; i++) {
			if (c.isDone(b))
				assertFalse(1 == 1);
		}
		if (c.isDone(b)) 
			assertTrue(1 == 1);
		else
			assertFalse(1 == 1);
	}
	@Test
	public void testFWidth() {
		Box b = setupBox();
		StopCriterion c = new StopCriterion();
		c.fPrecision = IAMath.wid(b.getFunctionValue()) - 1e-4;
		if (c.isDone(b))
			assertFalse(1 == 1);
		c.fPrecision = IAMath.wid(b.getFunctionValue());
		if (c.isDone(b))
			assertTrue(1 == 1);
		c.fPrecision = IAMath.wid(b.getFunctionValue()) + 1e-4;
		if (c.isDone(b))
			assertTrue(1 == 1);
	}		   
		   
	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main("StopCriterion");
	}	
}
