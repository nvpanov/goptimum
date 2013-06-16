package core;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;
import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;


public class BoxTest {
	protected Random rnd = new Random();
	protected Box setupBox() {
		   int dim = rnd.nextInt(6) + 1;
		   Box b = new Box(dim, new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   b.setFunctionValue(new RealInterval(-10 * rnd.nextDouble() - 1, 10 * rnd.nextDouble() + 1));
		   return b;
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
	
	@Test
	public void testHasAtLeastOneCommonSide() {
		// 1
		Box a = new Box(1, new RealInterval(0,1) );
		Box b = new Box(2, new RealInterval(0,1) );
		try {
			a.hasAtLeastOneCommonSide(b);
			fail("Assertion expected");
		} catch (AssertionError e) {
			// OK.
		}
		
		// 2
		a = new Box(1, new RealInterval(0,1) );
		b = new Box(1, new RealInterval(0,1) );
		assertTrue(a.hasAtLeastOneCommonSide(b));
		
		b = new Box(1, new RealInterval(0) );
		assertTrue(a.hasAtLeastOneCommonSide(b));

		b = new Box(1, new RealInterval(-1) );
		assertFalse(a.hasAtLeastOneCommonSide(b));
		
		// 3
		a = new Box(2, new RealInterval(-10,10) );
		b = new Box(2, new RealInterval(0,1) );
		assertTrue(a.hasAtLeastOneCommonSide(b));

		// 4
		a = new Box(2, new RealInterval(-1,1) );
		b = new Box(2, new RealInterval(2,3) );
		assertFalse(a.hasAtLeastOneCommonSide(b));

		b.setInterval(1, new RealInterval(0, 0.1));
		assertTrue(a.hasAtLeastOneCommonSide(b));
		
		b.setInterval(1, new RealInterval(-10, -1));
		assertTrue(a.hasAtLeastOneCommonSide(b));		
	}

	@Test
	public void testCutOutBoxAroundThisPoint() {
		int dim = 6;
		dim = rnd.nextInt(10);
		if (dim == 0) 
			dim = 1;
		Box b = new Box(dim, new RealInterval(0));
		double[] point = new double[dim];
		for (int i = 0; i < dim; i++)
			point[i] = i;
		try {
			b.cutOutBoxAroundThisPoint(point);
			fail("Assertion expected");
		} catch (AssertionError e) {
			// ok
		}
		///////////////////////////////
		for (int i = 0; i < dim; i++)
			point[i] = 0.0001;
		Box[] boxes = b.cutOutBoxAroundThisPoint(point);
		assertEquals(1, boxes.length);
		assertEquals(b, boxes[0]);
		///////////////////////////////
		b = new Box(dim, new RealInterval(0));
		boxes = b.cutOutBoxAroundThisPoint(point);
		assertEquals(1, boxes.length);
		assertEquals(b, boxes[0]);
		///////////////////////////////
		b = new Box(dim, new RealInterval(-100, 100));
		boxes = b.cutOutBoxAroundThisPoint(point);
		assertEquals(dim*2+1, boxes.length);
		int contains = 0;
		for (int i = 0; i < boxes.length; i++)
			if ( boxes[i].contains(point) )
				contains++;
		assertEquals(1, contains);
		///////////////////////////////
		for (int i = 0; i < dim; i++)
			point[i] = 100;
		boxes = b.cutOutBoxAroundThisPoint(point);
		assertEquals(dim+1, boxes.length);
		contains = 0;
		for (int i = 0; i < boxes.length; i++)
			if ( boxes[i].contains(point) )
				contains++;
		assertEquals(1, contains);
		///////////////////////////////
		point[0] = 0;
		boxes = b.cutOutBoxAroundThisPoint(point);
		assertEquals(dim+1+1, boxes.length);
		contains = 0;
		for (int i = 0; i < boxes.length; i++)
			if ( boxes[i].contains(point) )
				contains++;
		assertEquals(1, contains);
		///////////////////////////////
	}	
}
