package splitters;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Test;

import core.Box;


public class BigestSideEquallySplitterTest {
	BiggestSideEquallySplitter splitter = new BiggestSideEquallySplitter();
	Random rnd = new Random();
	int dim;
	Box b0, b;

	@Before
	public void setUp() {
		dim = rnd.nextInt(5) + 2;
		b = new Box(dim, new RealInterval());
		for (int i = 0; i < dim; i++) {
			b.setInterval(i, new RealInterval(-i-rnd.nextDouble(), 2*i+rnd.nextDouble()));
		}
		b0 = b.clone();
	}

	/**
	 * Test method for {@link splitters.BiggestSideEquallySplitter#splitIt(core.Box)}.
	 */
	@Test
	public final void testSplitIt() {
		Box[] res = splitter.splitIt(b);
		for (int i = 0; i < dim-1; i++) {
			assertTrue(res[0].getInterval(i).equals(res[1].getInterval(i)) );
			assertTrue(res[0].getInterval(i).equals(b0.getInterval(i)) );
			assertTrue(res[0].getInterval(i).equals(b.getInterval(i)) );
		}
		assertTrue(res[0].getInterval(dim-1).lo() == b0.getInterval(dim-1).lo());
		assertTrue(res[0].getInterval(dim-1).hi() == res[1].getInterval(dim-1).lo());
		assertTrue(res[1].getInterval(dim-1).hi() == b0.getInterval(dim-1).hi());
		assertTrue(res[0].getInterval(dim-1).hi() == b0.getInterval(dim-1).lo() + b0.getInterval(dim-1).wid()/2);
	}

}
