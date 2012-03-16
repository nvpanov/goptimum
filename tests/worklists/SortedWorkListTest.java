package worklists;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Test;

import core.Box;

public class SortedWorkListTest {
	Random rnd = new Random();
	SortedWorkList wl = null;
	int dim = -1;

	@Before
	public void setUp() throws Exception {
		dim = rnd.nextInt(20)+1;
		wl = null;
	}

	@Test
	public final void testGetLeadingBox() {
		double minVal = (rnd.nextInt(10)-5) * rnd.nextDouble();
		RealInterval it = new RealInterval( rnd.nextDouble() );
		Box area = new Box(dim, it);
		area.setFunctionValue( new RealInterval(minVal) );
		wl = new SortedWorkList(area);
		
		for (int i = 0; i < rnd.nextInt(222); i++) {
			Box b = new Box(dim, new RealInterval(rnd.nextDouble()) );
			double val = (rnd.nextInt(10)-5) * rnd.nextDouble();
			if (val < minVal)
				minVal = val;
			if (rnd.nextInt(10) == 1)
				it = new RealInterval(val);
			else {
				double val2 = 10 + rnd.nextInt(10) * rnd.nextDouble();
				it = new RealInterval(val, val2);
			}
			b.setFunctionValue(it);
			wl.add(b);
		}
		int s1 = wl.size();
		Box lead = wl.getLeadingBox();
		int s2 = wl.size();
		assertTrue(lead.getFunctionValue().lo() == minVal);
		assertTrue(s1 == s2);
	}

	@Test
	public final void testAddBoxExtractBox() {
		Box b1 = new Box(dim, new RealInterval( 10 ));
		b1.setFunctionValue(new RealInterval(rnd.nextDouble()) );
		wl = new SortedWorkList(b1);
		assertTrue(wl.size() == 1);
		
		Box b2 = new Box(dim, new RealInterval( 100 ));
		b2.setFunctionValue(new RealInterval( -rnd.nextDouble(), rnd.nextDouble()));
		
		wl.add(b2);
		assertTrue(wl.size() == 2);
//		Box[] tmp = wl.collection.toArray(new Box[0]);
		Box b = wl.extract(0);
		assertTrue(b.equals(b2));
		assertTrue(wl.size() == 1);
		b = wl.extract(0);
		assertTrue(b.equals(b1));
		assertTrue(wl.size() == 0);
	}

	@Test
	public final void testRemove() {
		RealInterval it = new RealInterval( -rnd.nextDouble(), rnd.nextDouble() );
		RealInterval it1 = new RealInterval( it.lo()-rnd.nextDouble(), rnd.nextDouble() );
		Box b1 = new Box(dim, it); b1.setFunctionValue(it);
		Box b2 = new Box(dim, it); b2.setFunctionValue(it1);
		wl = new SortedWorkList(b1);
		wl.switchOff1DerivativeCheck();
		// assertTrue(wl.size() == 1); now we adds edges from the search area see @WorkList@
		int s0 = wl.size();
		wl.add(b2);
		assertTrue(wl.size() == s0+1);
		wl.remove(b1);
		assertTrue(wl.size() == s0);
	}
}
