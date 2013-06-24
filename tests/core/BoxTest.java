package core;

import static org.junit.Assert.*;
import java.util.Random;
import org.junit.Test;
import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;


public class BoxTest {
	protected final double epsilon = 5e-4;
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
	public void testCutOutBoxAroundThisPoint_FunctionValue() {
		Box b = new Box(1);
		b.setInterval(0, new RealInterval(-10, 10));
		b.setFunctionValue(new RealInterval(111));
		Box boxes[] = b.cutOutBoxAroundThisPoint(new double[] {0});
		for (Box bb: boxes) {
			assertEquals(bb.getFunctionValue(), new RealInterval());
		}
	}	

	@Test
	public void testCutOutBoxAroundThisPoint_Bound() {
		Box b = new Box(1);
		b.setInterval(0, new RealInterval(-10, 10));
		Box boxes[] = b.cutOutBoxAroundThisPoint(new double[] {10});
		assertEquals(2, boxes.length);
		assertEquals(boxes[0].getInterval(0), new RealInterval(-10, 10-epsilon));
	}	

	@Test
	public void testCutOutBoxAroundThisPoint_OutOfBound() {
		Box b = new Box(1);
		b.setInterval(0, new RealInterval(-10, 10));
		b.setFunctionValue(new RealInterval(111));
		try {
			b.cutOutBoxAroundThisPoint(new double[] {11});
			fail("assert expected");
		} catch (AssertionError e) {
			// OK
		}
	}	
	
	@Test
	public void testCutOutBoxAroundThisPoint_SmallBox() {
		Box b = new Box(1);
		b.setInterval(0, new RealInterval(-epsilon/2, epsilon/2));
		Box[] bb = b.cutOutBoxAroundThisPoint(new double[] {0});
		assertEquals(1, bb.length);
		assertEquals(bb[0], b);
	}
	
	@Test
	public void testCutOutBoxAroundThisPoint1d() {
		Box b = new Box(1);
		b.setInterval(0, new RealInterval(-10, 10));
		Box boxes[] = b.cutOutBoxAroundThisPoint(new double[] {0});
		assertEquals(3, boxes.length);
		assertEquals(new RealInterval(-10, -epsilon), 		boxes[0].getInterval(0));
		assertEquals(new RealInterval(epsilon, 10),			boxes[1].getInterval(0));
		assertEquals(new RealInterval(-epsilon, +epsilon),	boxes[2].getInterval(0));
	}	
	
	/*                                3
	 *  _____________          _______________
	 * |             |        |      | |      |
	 * |             |        |      |_|      |
	 * |      .      |  ===>  |  0   |_|  1   |
	 * |             |        |      | |      |
	 * |_____________|        |______|_|______|
	 *                                2 
	 */
	@Test
	public void testCutOutBoxAroundThisPoint2d() {
		Box b = new Box(2);
		b.setInterval(0, new RealInterval(-10, 10));
		b.setInterval(1, new RealInterval(-2, 2));
		Box boxes[] = b.cutOutBoxAroundThisPoint(new double[] {0, 0});
		assertEquals(5, boxes.length);
		assertEquals(new RealInterval(-10, -epsilon), 		boxes[0].getInterval(0));
		assertEquals(new RealInterval(epsilon, 10),			boxes[1].getInterval(0));
		assertEquals(new RealInterval(-epsilon, +epsilon),	boxes[2].getInterval(0));
		assertEquals(new RealInterval(-epsilon, +epsilon),	boxes[3].getInterval(0));
		assertEquals(new RealInterval(-epsilon, +epsilon),	boxes[4].getInterval(0));

		assertEquals(new RealInterval(-2, 2), 				boxes[0].getInterval(1));
		assertEquals(new RealInterval(-2, 2),				boxes[1].getInterval(1));
		assertEquals(new RealInterval(-2, -epsilon),		boxes[2].getInterval(1));
		assertEquals(new RealInterval(epsilon, 2),			boxes[3].getInterval(1));
		assertEquals(new RealInterval(-epsilon, +epsilon),	boxes[4].getInterval(1));
	}	

	@Test
	public void testCutOutBoxAroundThisPoint3dNearOneBorder() {
		Box b = new Box(3);
		b.setInterval(0, new RealInterval(-6, 6));
		b.setInterval(1, new RealInterval(-4, 4));
		b.setInterval(2, new RealInterval(-2, 2));
		
		Box[] checkResults = new Box[9-3];
		for (int i = 0; i < checkResults.length; i++) {
			checkResults[i] = new Box(3);
		}
		Box tmp[] = b.splitSide(0, 0.5);
		tmp[0].setInterval(0, new RealInterval(-6, -epsilon));
		tmp[1].setInterval(0, new RealInterval(epsilon, 6));
		checkResults[0] = tmp[0];
		checkResults[1] = tmp[1];
		
		RealInterval ee = new RealInterval(-epsilon, epsilon);
		Box t = new Box(3);
		t.setInterval(0, ee);
		t.setInterval(1, new RealInterval(-4, -epsilon));
		t.setInterval(2, new RealInterval(-2, 2));
		checkResults[2] = t;
		
		t = t.clone();
		t.setInterval(1, new RealInterval(epsilon, 4));
		checkResults[3] = t;
		
		t = t.clone();
		t.setInterval(1, ee);
		t.setInterval(2, new RealInterval(-2, 2-epsilon));
		checkResults[4] = t;

		t = t.clone();
		t.setInterval(2, new RealInterval(2-epsilon, 2));
		checkResults[5] = t;

		Box boxes[] = b.cutOutBoxAroundThisPoint(new double[] {0, 0, 2});

		assertEquals(checkResults.length, boxes.length);
		for(int i = 0; i < checkResults.length; i++) {
			assertEquals("i = " + i, checkResults[i], boxes[i]);
		}
	}	
	@Test
	public void testCutOutBoxAroundThisPointRnd() {
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
