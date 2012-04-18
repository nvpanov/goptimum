package worklists;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import symboldiff.exceptions.ExpressionException;

import core.Box;
import functions.FunctionFactory;
import functions.FunctionNEW;
import functions.Function_DeJong_nD;
import functions.Function_Rastrigin10_2D;
import functions.Function_RosenbrockG_nD;

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

	@Test
	public final void check1Derivative() throws Exception {
		Box b;
		s = new Screener(0);
		String functions[] = {"x^2+y^2+z^3", "x^2", "-x^20", "x^9"};
		for (String f : functions) {
			FunctionFactory.newFunction(f);
			b = new Box(2, new RealInterval(-5, 5));
			assertTrue( s.check1Derivative(b) );
			b = new Box(2, new RealInterval(0));
			assertTrue( s.check1Derivative(b) );
			b = new Box(2, new RealInterval(-101, -100));
			assertFalse( s.check1Derivative(b) );
			b = new Box(2, new RealInterval(100, 101));
			assertFalse( s.check1Derivative(b) );
		}
		String functions1[] = {"-0.001", "0", "-x^20"};
		for (String f : functions1) {
			FunctionFactory.newFunction(f);
			b = new Box(2, new RealInterval(-50000, 50000));
			assertFalse( s.check1Derivative(b) );
		}
		
	}
/*	
	@Test
	public final void testDeflate() {
		double v0 = 1e100;
		s = new Screener(v0);
		Function f = new Function_RosenbrockG_nD(10);
		// 1
		Box a = new Box(1, new RealInterval());
		Box border = new Box(10, new RealInterval());
		
		try {
			s.deflate(a, border, f);
			fail("exception expected");
		} catch (AssertionError e) {
			// OK.
		}
		//2
		int n = 10;
		// if the box is a corner one
		// it can't be deflated:
		//  ______
		//     |__|
		//        |
		RealInterval bI = new RealInterval(-1, 1), I = new RealInterval(0, 1);
		border = new Box(n, bI);
		a = new Box(n, I);
		s.deflate(a, border, f);
		for (int i = 0; i < n; i++) { // no side should change
			assertTrue(border.getInterval(i).equals(bI));
			assertTrue(a.getInterval(i).equals(I));
		}
		//3
		a = new Box(n, new RealInterval(0, 0.5));
		try {
			s.deflate(a, border, f);
			fail("assertion expected: they do not have common edges");
		} catch (AssertionError e) {
			// OK.
		}
		//4
		a.setInterval(n-1, 1);
		s.deflate(a, border, f);
		for (int i = 0; i < n; i++) {
			assertTrue(border.getInterval(i).equals(bI));
			assertTrue(a.getInterval(i).wid() == 0);
			assertTrue(a.getInterval(i).lo() == 1);
		}
		//5
		n = 2;
		f = new Function_RosenbrockG_nD(n);
		border = new Box(n, new RealInterval(0, 10)); //    __________
		a = new Box(n, new RealInterval(2, 8));       //   |___|_|____|
		a.setInterval(0, new RealInterval(0, 10));    //
		Box ref = a.clone(), refB = border.clone();
		s.deflate(a, border, f);
		assertTrue(a.equals(ref));
		assertTrue(border.equals(refB));
		//6
		border = new Box(n, new RealInterval(0, 10));
		a = new Box(n, new RealInterval(0, 10));
		ref = a.clone();
		s.deflate(a, border, f);
		assertTrue(a.equals(ref));
		assertTrue(border.equals(refB));
		//7
		border = new Box(n, new RealInterval(0, 10));
		a = new Box(n, new RealInterval(0, 10));
		ref = a.clone();
		s.deflate(a, border, f);
		assertTrue(a.equals(ref));
		assertTrue(border.equals(refB));
	}
*/	
	@Test
	public final void check2Derivative() {
		fail("Not implemented");
	}

	public void testCheck1Derivative()
	 throws Exception {
	
	}

	
}
