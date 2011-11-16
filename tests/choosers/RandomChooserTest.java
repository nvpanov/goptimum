package choosers;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import junit.framework.TestCase;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Test;

import choosers.*;
import worklists.*;
import core.Box;


public class RandomChooserTest extends TestCase {
	private WorkList wl;
	private Chooser c;
	private int size = 100;
	private Random rnd = new Random();
	
    @Before
    public void setUp() {
    	wl = new UnSortedWorkList(new Box(1, new RealInterval(0)));
		for (int i = 1; i < size; i++) {
			wl.add(new Box(1, new RealInterval(i)));
		}
		assertTrue(wl.size() == size);
		c = new RandomChooser(wl);
    }

	@Test
	public void testExtraction() {
		c.extractNext();
		assertTrue(wl.size() == size-1);
	}

	@Test
	public void testRnd() {
		ArrayList<ArrayList<Double>> results = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < size; i++) {
			results.add(getExtractSeries());
			setUp();
		}
		for (int i = 0; i < (int)size/4; i++) {
			int a1 = rnd.nextInt(size);
			int a2 = rnd.nextInt(size);
			int num = rnd.nextInt(size);
			int idx1 = (a1 < a2) ? a1 : a2;
			int idx2 = (idx1 == a1) ? a2 : a1;
			TreeSet<Double> values = new TreeSet<Double>(); 
			for (int t = idx1; t < idx2; t++) {
				double value = results.get(t).get(num);
				values.add(value);
			}
			assertTrue(values.size() > (int)((idx2-idx1)/2));
		}
	}
	
	private ArrayList<Double> getExtractSeries() {
		ArrayList<Double> numbers = new ArrayList<Double>();
		try {
			while (true) {
				Box b = c.extractNext();
				numbers.add(b.getInterval(0).lo());
			}
		} catch (Exception e) {
			// no more boxes in the list
		}
		return numbers;
	}
	
	
}
