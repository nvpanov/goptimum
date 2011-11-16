package core;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;
import core.Box;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;


public class BoxTest {
	protected Random rnd = new Random();
	protected Box setupBox() {
		   int dim = rnd.nextInt(6) + 1;
		   Box b = new Box(dim, new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   b.setFunctionValue(new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   return b;
	}
	
	//
	@Test
	public final void testInterval() {
/*		RealInterval x, r;
		RealInterval y = new RealInterval(1);
		
		x = new RealInterval(Double.NEGATIVE_INFINITY, 0.0);
		
		x = new RealInterval(-1, 1);
		y = new RealInterval(3, 3);
		r = IAMath.power(x, y);
		assertTrue(r.equals(x));
*/		
	}
	
	@Test
	public void intervalWid() {
	//Infinity : solvers.Bisection_SrtL_RndC_AllEqS@1a1c887 : [-2147483.648, 48.828]
		RealInterval i = new RealInterval(-2147483.648, 48.828);
		assertTrue(i.wid() == 2147483.648 + 48.828);
	}
	@Test
	public void testConstructors() {
		Box b;
		RealInterval i = new RealInterval(-1, 1);
		for (int dim = -1; dim < 1; dim ++) {
			try {
				b = new Box(dim, i);
				fail();
			} catch (Exception e) {
				// test passed
			}
		}
		int dim = rnd.nextInt(1000);
		b = new Box(dim, i);
		assertTrue(b.getDimension() == dim);
		assertTrue(b.getInterval(rnd.nextInt(dim)).equals(i));
	}
	@Test
	public final void testGetInterval() {
		int dim = rnd.nextInt(100) + 1;
		RealInterval i = new RealInterval(-rnd.nextDouble()*1e32, rnd.nextDouble()*1e-28);
		Box b = new Box(dim, i);
		for (int t = 0; t < dim; t++)
			assertTrue("dim="+dim+", i=" + i + ", t=" + t, i.equals(b.getInterval(t)));
	}	
	@Test
	public final void testSetInterval() {
		int dim = rnd.nextInt(100) + 1;
		Box b = new Box(dim, new RealInterval(0, 0));

		for (int t = 0; t < dim; t++) {
			int n = rnd.nextInt(dim);
			RealInterval i = new RealInterval(-rnd.nextDouble()*1e-8, rnd.nextDouble()*1e16);
			b.setInterval(n, i);
			RealInterval ii = b.getInterval(n);
			assertTrue("dim="+dim+", i=" + i + ", n=" + n, i.equals(ii));
		}
	}	
	@Test
	public final void testSetGetFunctionValue() {
		int dim = rnd.nextInt(100) + 1;
		Box b = new Box(dim, new RealInterval(0, 0));
		assertTrue(b.getFunctionValue().equals(new RealInterval()));
		RealInterval i = new RealInterval(-rnd.nextDouble()*1e6, rnd.nextDouble()*1e6);
		b.setFunctionValue(i);
		assertTrue(b.getFunctionValue().equals(i));
	}
	@Test
	public void testCloneAndToString() {
		Box b = setupBox();
		Box c = b.clone();
		assertTrue( b.toString().equals( c.toString() ) );
	}

}
