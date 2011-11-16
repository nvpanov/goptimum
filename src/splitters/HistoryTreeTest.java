package splitters;

import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Test;

import core.Box;

public class HistoryTreeTest {
	private HistoryTree history;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void test() {
		final int dim = 2;
		int sideNum = 0;
		Box b = new Box(dim, new RealInterval(-10, 10));
		history = new HistoryTree(b);

		Box[] children = b.splitSide(sideNum, 0.5);
		history.boxWasSplited(b, sideNum, children);
		
		assertTrue(history.activeNodes.size() == 2);
		assertTrue(history.activeNodes.first().parent == history.activeNodes.last().parent);
		assertTrue(history.activeNodes.first().parent.value == null);
		assertTrue(history.activeNodes.first().parent.coordinate == sideNum);
		assertTrue(history.activeNodes.first().children == null);
		assertTrue(history.activeNodes.last().children == null);
		
		ArrayList<Box> nb = history.getLeftNeighbors(children[0], sideNum);
		assertTrue(nb.size() == 0);
		nb = history.getRightNeighbors(children[0], sideNum);
		assertTrue(nb.size() == 1);
		assertTrue(nb.get(0) == children[1]);
		
/*		
		//// 2
		ArrayList<Box> neighbors;
		try {
			neighbors = history.getLeftNeighbors(new Box(1, new RealInterval()), 1);
//			assertTrue(neighbors == null);
			fail("assert expected");
		} catch (AssertionError e) {
			// OK
		}
		
		//// 3
		sideNum = 1;
		b = children[0];
		children = b.splitSide(sideNum, 0.5);
		history.boxWasSplited(b, sideNum, children);
		assertTrue(history.activeNodes.size() == 3);
*/		
	}

}
