package splitters;
import java.util.Random;

import junit.framework.TestCase;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import core.Box;
import splitters.RndSideEquallySplitter;

public class RndSideEquallySplitterTest extends TestCase {
	@Test
	public void testEqMultiple() {
		for (int i = 0; i < 20; i++) 
			testEq();
	}

	@Test
	public void testEq() {
		RndSideEquallySplitter splitter = new RndSideEquallySplitter();
		Random rnd = new Random();
		int dim = rnd.nextInt(20) + 1;
		Box b = new Box(dim, new RealInterval(0));
		for (int i = 0; i < dim; i++) {
			RealInterval ri = new RealInterval(-20 * rnd.nextDouble(), 1000 * rnd.nextDouble());
			b.setInterval(i, ri);
		}
		b.setFunctionValue(new RealInterval(-1 * rnd.nextDouble(), rnd.nextDouble()) );
		
		Box res[] = splitter.splitIt(b);
		
		assertTrue(res.length == 2);
		assertTrue(res[0].getDimension() == res[1].getDimension());		
		assertTrue(res[0].getDimension() == res[1].getDimension());
		
		assertTrue(res[0].getFunctionValue().equals(new RealInterval())); // the value is not set
		assertTrue(res[1].getFunctionValue().equals(new RealInterval()));
		
		int splitCount = 0;
		for (int i = 0; i < dim; i++) {
			if (res[0].getInterval(i) != res[1].getInterval(i) || res[1].getInterval(i) != b.getInterval(i) ) {
				if ( res[0].getInterval(i).lo() == res[1].getInterval(i).hi() &&
						res[0].getInterval(i).hi() == b.getInterval(i).hi() && 
						res[1].getInterval(i).lo() == b.getInterval(i).lo()		||
					 res[0].getInterval(i).hi() == res[1].getInterval(i).lo() &&
					 	res[0].getInterval(i).lo() == b.getInterval(i).lo() &&
					 	res[1].getInterval(i).hi() == b.getInterval(i).hi() ) {
					
					splitCount++;
				}
			}
		}
		assertTrue(splitCount == 1);
	}
}
