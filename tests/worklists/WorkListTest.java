package worklists;

import static org.junit.Assert.*;

import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import core.Box;
import functions.Function_RosenbrockG_nD;

public class WorkListTest {

	private static WorkList wl;
	private static Box area;
	private static int dim;
	private static Random rnd = new Random();
	
	@BeforeClass
	public static void before() {
		long seed = System.currentTimeMillis();
		rnd.setSeed(seed);
		dim = 2 + rnd.nextInt(10);
		area = new Box(dim, new RealInterval(-1, 10));
		new Function_RosenbrockG_nD(dim); // Screener uses it...

		wl = new WorkList(new ArrayList<Box>(), area) {
			@Override
			protected void addChecked(Box box) {
				collection.add(box);
			}
			@Override
			protected Box getLeadingBoxInternal() {
				return null;
			}
		};
	}
	
	@Test
	public void testAddAllAges() {
		//1
		assertTrue(wl.size() == 1 + dim*2);
		
		for (int i = 0; i < dim; i++) {
			int eqCnt = 0;
			int thinCnt = 0;
			for (Box b : wl.collection) {
				if (b.getInterval(i).equals(area.getInterval(i)))
					eqCnt++;
				if (b.getInterval(i).wid() == 0) {
					thinCnt++;
					assertTrue(b.getInterval(i).lo() == area.getInterval(i).lo() ||
							b.getInterval(i).lo() == area.getInterval(i).hi());
				}
			}
			assertTrue(eqCnt == 1 + dim*2 - 2);
			assertTrue(thinCnt == 2);
		}
		
		//2
		try {
			wl.addSearchArea(area);
			fail("assertion expected!");
		} catch (AssertionError e) {
			// OK.
		}
		
		//3
		wl.clearAll();
		wl.addSearchArea(area);
		assertTrue(wl.size() == 1 + dim*2);
		
		//4
		wl.clearAll();
		assertTrue(wl.size() == 0);
		wl.add(area);
		assertTrue(wl.size() == 1 + dim*2);
				
		//5
		WorkList wl = new WorkList(new ArrayList<Box>(), null) {
			@Override
			protected void addChecked(Box box) {
				collection.add(box);
			}
			@Override
			protected Box getLeadingBoxInternal() {
				return null;
			}
		};
		try {
			wl.extractNext();
			fail("Assertion expected: not inited area!");
		} catch (AssertionError e) {
			// OK
		}		
	}

	@Test
	public void testGetWorkFrom() {
		fail("Not yet implemented");
	}

}
