package splitters;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Test;

import core.Box;

public class AllSidesEquallySplitterTest {
	private Box box0, box;
	private Random rnd = new Random();
	private AllSidesEquallySplitter splitter = new AllSidesEquallySplitter();
	
	
	@Before
	public void setUp() {
		final int dim = rnd.nextInt(10)+1;
		box = new Box(dim, new RealInterval(0));
		for (int i = 0; i < rnd.nextInt(dim); i++) {
			box.setInterval(i, new RealInterval(-rnd.nextDouble(),rnd.nextDouble()));
		}
		box.setFunctionValue(new RealInterval(42));
		box0 = box.clone();
	}
	
//	@Before
	public void setUpDbg() {
		final int dim = 2;
		box = new Box(dim, new RealInterval(-2, 2));
		for (int i = 0; i < rnd.nextInt(dim); i++) {
//			box.setInterval(i, new RealInterval(-rnd.nextDouble(),rnd.nextDouble()));
		}
		box.setFunctionValue(new RealInterval(42));
		box0 = box.clone();
	}

	@Test
	public final void testSplitIt() {
		Box[] res = splitter.splitIt(box);
		assertTrue(res.length == Math.pow(2, box0.getDimension()));
		//////
		int bNum = rnd.nextInt(box0.getDimension());
		assertTrue(res[bNum].getFunctionValue().equals(new RealInterval())); // function value is reseted after splitting
		//////
		for (int i = 0; i < box0.getDimension(); i++) {
			RealInterval ii = res[bNum].getInterval(i);
			RealInterval ii0 = box0.getInterval(i);
			double midPoint = ii0.lo() + (ii0.hi() - ii0.lo())/2;
			if(ii.hi() == ii0.hi()) {
				assertTrue(ii.lo() == midPoint);
			} else if (ii.lo() == ii0.lo()) {
				assertTrue(ii.hi() == midPoint);
			} else
				fail();
		}
		///////
	}

}
