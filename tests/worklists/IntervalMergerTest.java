package worklists;

import static org.junit.Assert.*;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import core.Box;

public class IntervalMergerTest {

	private IntervalMerger merger;

	public void testMerge() {
		fail("Not yet implemented"); // TODO
	}

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
		final int dim = b.getDimension();
		for (int i = 0; i < dim; i++) {
			Box b1 = boxes[1].clone();
			RealInterval ii = b1.getInterval(i);
			double x = (i%2 == 0) ? 1e-6 : 0;
			double y = (x == 0 ? 1e-6 : 0);
			b1.setInterval(i, new RealInterval(ii.lo()-x, ii.hi()+y));
			for (int j = 0; j < dim; j++) {
				assertFalse(merger.allTouchingSidesAreEqual(boxes[0], b1, i));
			}
		}
	}

	public void testMergeBoxes() {
		Box b = new Box(40, new RealInterval(-101, 11));
		final int side = 0;
		Box boxes[] = b.splitSide(side, 0.5);
		merger = new IntervalMerger(boxes);
		Box b1 = merger.mergeBoxes(boxes[0], boxes[1], side);
		assertEquals(b, b1);
	}
}
