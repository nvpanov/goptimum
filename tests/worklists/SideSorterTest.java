package worklists;

import static org.junit.Assert.*;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import core.Box;

public class SideSorterTest {
	private SideSorter sorter;

	@Test
	public void testCompare() {
		sorter = new SideSorter(0);
		Box b1 = new Box(2, new RealInterval(-1, 1));
		Box b2 = new Box(3, new RealInterval(-1, 1));
		try {
			sorter.compare(b1, b2);
			fail("Assert expected");
		} catch (AssertionError e) {
			// ok
		}
		b1 = new Box(3, new RealInterval(-1, 1));
		assertEquals(0, sorter.compare(b1, b2) );
		b1.setInterval(0, new RealInterval(-10, 0.5));
		assertEquals(-1, sorter.compare(b1, b2) );
		b2.setInterval(1, new RealInterval(0, 0.5));
		sorter = new SideSorter(1);
		assertEquals(-1, sorter.compare(b1, b2) );
		b2.setInterval(2, new RealInterval(-100, -10));
		sorter = new SideSorter(2);
		assertEquals(1, sorter.compare(b1, b2) );
	}

}
