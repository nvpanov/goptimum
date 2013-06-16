package worklists;

import static org.junit.Assert.*;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import core.Box;

public class IntervalMergerTest {

	private IntervalMerger merger;

	@Test
	public void testBoxesFollowedOneAnother() {
		Box b = new Box(4, new RealInterval(0.0001, 0.00011));
		Box boxes[] = b.splitSide(2, 0.5);
		merger = new IntervalMerger(boxes);
//		merger.merge();
		assertTrue(merger.boxesFollowedOneAnother(boxes[0], boxes[1], 2));
		assertFalse(merger.boxesFollowedOneAnother(boxes[0], boxes[1], 1));
		assertFalse(merger.boxesFollowedOneAnother(boxes[0], boxes[1], 0));
	}

	@Test
	public void testAllTouchingSidesAreEqual() {	
		Box b = new Box(40, new RealInterval(0.0001, 0.00011));
		final int side = 2;
		Box boxes[] = b.splitSide(side, 0.5);
		merger = new IntervalMerger(boxes);
		assertTrue(merger.allTouchingSidesAreEqual(boxes[0], boxes[1], side));
		for (int i = 0; i < b.getDimension(); i++) {
			if (i == side)
				continue;
			assertFalse(merger.allTouchingSidesAreEqual(boxes[0], boxes[1], i));
		}
	}
	@Test
	public void test2() {	
		merger = new IntervalMerger(new Box[]{null, null}); // it requires an array of boxes, but this test doesn't need it
		Box b = new Box(40, new RealInterval(0.0001, 0.00011));
		final int dim = b.getDimension();
		for (int sideToChange = 0; sideToChange < dim; sideToChange++) {
			Box b1 = b.clone(); 							// get exact copy
			RealInterval ii = b1.getInterval(sideToChange);	// but change a little bit one border of one of the sides 
			
			double x = (sideToChange%2 == 0) ? 1e-6 : 0;
			double y = (x == 0)   ? 1e-6 : 0;
			b1.setInterval(sideToChange, new RealInterval(ii.lo()-x, ii.hi()+y));
			for (int j = 0; j < dim; j++) {
				if (j != sideToChange)
					assertFalse(merger.allTouchingSidesAreEqual(b, b1, j));
				else
					assertTrue(merger.allTouchingSidesAreEqual(b, b1, j)); // j == sideToChange so all other sides are the same
			}
		}
	}

	@Test
	public void testMergeBoxes() {
		Box b = new Box(40, new RealInterval(-101, 11));
		final int side = 0;
		Box boxes[] = b.splitSide(side, 0.5);
		merger = new IntervalMerger(boxes);
		Box b1 = merger.mergeBoxes(boxes[0], boxes[1], side);
		assertEquals(b, b1);
	}

	@Test
	public void test1() {
		RealInterval i1 = new RealInterval(1, 10);
		RealInterval i2 = new RealInterval(20, 30);
		RealInterval i3 = new RealInterval(10, 10);
		Box b1 = new Box(2, i1);
		b1.setInterval(1, i2);
		Box b2 = new Box(2, i3);
		b2.setInterval(1, i2);
		Box b3 = b2.clone();
		Box boxes[] = {b1, b2, b3};
		
		merger = new IntervalMerger(boxes);
		Box res[] = merger.merge();
		assertEquals(1, res.length);
		assertEquals(b1, res[0]);
	}
}
