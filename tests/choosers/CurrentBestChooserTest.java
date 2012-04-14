package choosers;
import java.util.Random;

import org.junit.Test;

import core.*;
import choosers.*;
import worklists.*;

import net.sourceforge.interval.ia_math.RealInterval;
import junit.framework.TestCase;

public class CurrentBestChooserTest extends TestCase {
	@Test
	public void testWorkList() {
		Box b = new Box(1, new RealInterval(1, 2));
		WorkList wl = new UnSortedWorkList(b);
		Chooser c = new CurrentBestChooser(wl);

		assertTrue(c.extractNext() == b);
	}
	@Test
	public void testSetWorkList() {
		Box b = new Box(1, new RealInterval(1, 2));
		WorkList wl = new UnSortedWorkList(b);
//		WorkList wl1 = new UnSortedWorkList(b1);
//		Box b1 = new Box(1, new RealInterval(2, 3));

		Chooser c = new CurrentBestChooser(wl);
		assertTrue(c.extractNext() == b);
//		c.setWorkList(wl1);
//		assertTrue(c.extractNext() == b1);
	}
	
	@Test
	public void testGetBest() {
		Random r = new Random();
		int dim = r.nextInt(16)+1;
		double lowest = Double.MAX_VALUE; 
		WorkList wl = null; // we do not create list here as far as
							// it requires a box. 
		
		for (int i = 0; i < 123; i++) {
			double a = r.nextDouble(), b = r.nextDouble();
			RealInterval interval;
			Box box = null;
			try {
				interval = new RealInterval(a, b);
				box = new Box(dim, interval);
				a = r.nextDouble(); 
				b = r.nextDouble();
				interval = new RealInterval(a, b);
				box.setFunctionValue(interval);
				if (a < lowest)
					lowest = a;
				try {
					wl.add(box);
				} catch (NullPointerException e) {
					// first iteration: no list was created.
					// create it.
					wl = new UnSortedWorkList(box);
					wl.switchOffDerivativesCheck(); // 12/23/11
				}
			} catch (Exception e) {
				// wrong interval: a > b;
				// will try next one
			}
		}
		Chooser c = new CurrentBestChooser(wl);
		assertTrue(c.extractNext().getFunctionValue().lo() == lowest);
	}
	
}
