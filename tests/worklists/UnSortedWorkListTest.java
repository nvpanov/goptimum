package worklists;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import junit.framework.TestCase;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Ignore;
import org.junit.Test;

import core.*;

public class UnSortedWorkListTest extends TestCase {
	Random rnd = new Random();

	
	@Test
//	@Ignore
	public void testCheckValue() {
		int dim = rnd.nextInt(20) + 1;
		Box b1 = new Box(dim, new RealInterval(0,0));
		Box b2 = new Box(dim, new RealInterval(0,0));
		// create two intervals in a way that a.hi < b.lo
		b1.setFunctionValue(new RealInterval(- rnd.nextDouble(), 0.0) );
		b2.setFunctionValue(new RealInterval(10 * rnd.nextDouble(), 11 + rnd.nextDouble()) );
		// set area intervals in some rnd values 
		for (int i = 0; i < dim; i++) {
			b1.setInterval(i, new RealInterval(-200 * rnd.nextDouble(), 1000 * rnd.nextDouble()));
			b2.setInterval(i, new RealInterval(-200 * rnd.nextDouble(), 1000 * rnd.nextDouble()));
		}
		WorkList wl = new UnSortedWorkList(b2);
		wl.switchOff1DerivativeCheck(); // 12/23/11
		int size = wl.size();
		assertTrue(size == 1 + 2*dim); // because we add all ages of search area as well. 12/23/11
		wl.add(b1);
		assertTrue(wl.size() == size + 1);
		
		WorkList wl1 = new UnSortedWorkList(b1);
		wl1.switchOff1DerivativeCheck(); //12/23/11
		int s = wl1.size(); // 12/23/11: it doesn't equal to 1. 
		wl1.add(b2); // Shouldn't be added because b2's function value is higher
		assertTrue(wl1.size() == 1 + s);
	}
	/* it is not relivant any more
	@Test(timeout=3*1000) // 3 sec
	public void testListCleanup() {
		System.gc();
		int dim = rnd.nextInt(20) + 100; // more memory
		int infinity = 500000;
		
		WorkList wl = new UnSortedWorkList(new Box(dim, new RealInterval(0)));
		for (int i = 0; i < 2; i++) {
			// cycle two times as far as we need to test that 
			// ListCleaner was resurrected after GC
			int iteration = 0, prevSize = 0;
			do {
				try {
					prevSize = wl.size();
					RealInterval f = new RealInterval(rnd.nextDouble(), rnd.nextDouble());
					RealInterval arg = new RealInterval(-rnd.nextDouble(), +rnd.nextDouble());
					Box b = new Box(dim, arg);
					b.setFunctionValue(f);
					wl.add(b);
					iteration++;
				} catch (Exception e) {
					// wrong interval. it's ok.
				}
			} while (wl.size() >= prevSize && iteration <= infinity);
			System.out.println("now size = " + wl.size() + ", prevSize = " + prevSize);
			assertTrue(iteration < infinity);
		}		
 	}
 	*/
	
	@Test
	public final void testGetLeadingBox() {
		WorkList wl = null;
		RealInterval i;
		int dim = rnd.nextInt(9)+1;
		double minimum = Double.MAX_VALUE;
		Box leader = null;
		for (int c = 0; c < 100; c++) {
			double lo = rnd.nextDouble();
			double hi = rnd.nextDouble();
			try {
				i = new RealInterval(lo, hi);
			} catch (Exception e) {
				continue;
			}
			Box b = new Box(dim, new RealInterval(-rnd.nextDouble(), rnd.nextDouble()));
			b.setFunctionValue(i);

			if (minimum > lo) {
				minimum = lo;
				leader = b;
			}
			if (wl == null) {
				 wl = new UnSortedWorkList(b);
				 wl.switchOff1DerivativeCheck();
			}
			wl.add(b);
		}
		// end of initialization
		int s1 = wl.size();
		Box res = wl.getLeadingBox();
		int s2 = wl.size();
		assertTrue(s1 == s2);		
		assertTrue(leader.equals(res));
		assertTrue(res.getFunctionValue().lo() == minimum);
	}
}