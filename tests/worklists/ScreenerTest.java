package worklists;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.Box;
import functions.Function;
import functions.Function_Rastrigin10_2D;

public class ScreenerTest {
	Screener s;
	Box b;
	Random rnd = new Random();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testCheckByValue() {
		s = new Screener(1);
		b = new Box(rnd.nextInt(1000), new RealInterval(0));
		b.setFunctionValue(new RealInterval(-rnd.nextInt(10)-0.1, 0.99));
		assertTrue(s.checkByValue(b));
		assertTrue(s.getLowBoundMaxValue() == 0.99);

		b.setFunctionValue(new RealInterval(1, 2));
		assertFalse(s.checkByValue(b));
		b.setFunctionValue(new RealInterval(0.99));
		assertTrue(s.checkByValue(b));
		
		//s.setLowBoundMaxValue(10); -- assert
		s.setLowBoundMaxValue(-8);
		b.setFunctionValue(new RealInterval(-rnd.nextInt(8)));
		assertFalse(s.checkByValue(b));		
		b.setFunctionValue(new RealInterval(-9));
		assertTrue(s.checkByValue(b));		
		b.setFunctionValue(new RealInterval(-7));
		assertFalse(s.checkByValue(b));		
	}

	@Test
	public final void testProbeNewLimit() {
		double v0 = 1e100, v = v0;
		s = new Screener(v);
		assertFalse( s.probeNewLimit(v+1) );
		assertFalse( s.probeNewLimit(v) );
		assertTrue( s.getLowBoundMaxValue() == v );
		v = 1e10;
		assertTrue( s.probeNewLimit(v) );
		assertTrue( s.getLowBoundMaxValue() == v );
		
		assertTrue( v0 - v == s.getLowBoundMaxValueLimitDelta() );
		assertTrue( s.getValueLimitUpdatesCount() == 1 );
	}

	@Ignore
	@Test
	public final void check1Derivative() {
		Function f = new Function_Rastrigin10_2D();
		// f = x^2 + y^2 - cos(18*x) - cos(18*y)
		// f'x = 2x + 18sin(18x). 0 in 0
		s = new Screener(0);
		
		Box b;
		b = new Box(2, new RealInterval(-5, 5));
		assertTrue( s.check1Derivative(b) );
		b = new Box(2, new RealInterval(0));
		assertTrue( s.check1Derivative(b) );
		b = new Box(2, new RealInterval(-101, -100));
		assertFalse( s.check1Derivative(b) );
		b = new Box(2, new RealInterval(100, 101));
		assertFalse( s.check1Derivative(b) );
	}
	@Test
	public final void check2Derivative() {
		fail("Not implemented");
	}

	
}
