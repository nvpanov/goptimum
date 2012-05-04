package core;

import static org.junit.Assert.*;

import static net.sourceforge.interval.ia_math.IAMath.*;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

public class Interval {

	@Test
	public final void testInterval() {
		RealInterval x, r;
		RealInterval y = new RealInterval(1);
		
		x = new RealInterval(Double.NEGATIVE_INFINITY, 0.0);
		
		x = new RealInterval(-1, 1);
		y = new RealInterval(3, 3);
		r = intPow(x, y);
		//assertTrue(r.equals(x));
		// due to all this direct rounding R could be a little bit wider...
		assertTrue(r.contains(x));
		assertTrue(r.wid() - x.wid() < 1e-6);
	}

    @Test
    public void testRealInterval() {
    	String s = new RealInterval(1e8, 1e8).toString();
//    	System.out.println(s);
    	assertEquals("[100,000,000.00, 100,000,000.00]", s);

    	s = new RealInterval(123.456789, 123456789.987).toString();
//    	System.out.println(s);
    	assertEquals("[123.46, 123,456,789.99]", s);
    	
    	s = new RealInterval().toString();
//    	System.out.println(s);
    	assertEquals("[-inf, inf]", s);    	
    }

    @Test
    public void testIntersect() {
    	RealInterval a = new RealInterval(-0.00, 6993.75);
    	RealInterval b = new RealInterval(4050.00, 4050.01);
    	
		RealInterval c = intersect(a, b);
		assertTrue(c != null);
    	assertTrue(c.equals(new RealInterval(4050.00, 4050.01) ));
    }
    @Test 
    public void sincos() {
    	//sin(((x*cos(-y))+2)) 		
    	RealInterval x = new RealInterval(0,1);
    	RealInterval y = new RealInterval(1,2);
    	
    	RealInterval t1 = cos( sub(0,y) );
    	assertTrue(t1.almostEquals(new RealInterval(-0.416146837, 0.540302306)));
    	RealInterval t2_0 = mul(x,t1);
    	System.out.println(t2_0);
    	assertTrue(t2_0.almostEquals(t1));
    	RealInterval t2 = add(t2_0, 2);
//    	assertTrue(t2.almostEquals(new RealInterval(1.583853163, 2)));
    	RealInterval bad = new RealInterval(1.58, 2.54);
    	RealInterval t3 = sin(bad); // InternalException. was.
    	t3 = sin(t2);
    	assertTrue("actual value = " + t3.toString(), 
    						t3.almostEquals(new RealInterval(0.56595623, 0.999957646)));
    }
/*
    @Test
    public void test1() {
    	RealInterval a = new RealInterval(-1, 1);
    	RealInterval b = new RealInterval(-1, 1);
    	
		RealInterval c = IAMath.add( IAMath.pow(a, 2), IAMath.mul(a,  b) );
//		System.out.println(c);
//    	assertTrue(c.equals(new RealInterval(-0, 4050.00) ));
    }
*/
	

	@Test
	public void testValueOf() throws Exception {
		String badStrings[] = {"-[1.2,2.3]", "[1.2,2,3]", "[3]", "[0, 0,0]", "{0; 0}", "[0;0,0]", "s", "[1, q]", "[7,8; 9,1]", "[7,8, 9,1]"};
		RealInterval result;
		for (String value : badStrings)
			try {
				result = RealInterval.valueOf(value);
				fail ("exception expected");
			} catch (NumberFormatException e) {
				// ok
			}
		String goodStrings[] = {"[1.2,2.3]", "[-1, 2.3]", "5.6", "[7.8; 9.1]"};
		RealInterval checks[] = {new RealInterval(1.2,2.3), new RealInterval(-1, 2.3), 
				new RealInterval(5.6), new RealInterval(7.8, 9.1)};
		int i = 0;
		for (String value : goodStrings) {
			result = RealInterval.valueOf(value);
			assertEquals(result, checks[i++]);
		}		
	}
}
