package functions;

import java.util.Random;
import junit.framework.TestCase;
import net.sourceforge.interval.ia_math.RealInterval;
import org.junit.Test;
import core.Box;

public class Function_SixHumpCamelBack_2DTest  extends TestCase {
    Random rnd = new Random();
	double point[] = new double[2];
	Box b = new Box(2, new RealInterval());
	Function f = new Function_SixHumpCamelBack_2D();

	@Test
    public void testPoints() {
        for (int i = 0; i < 10; i++) {
        	point[0] = rnd.nextDouble() * rnd.nextInt(4);
        	point[1] = rnd.nextDouble() * rnd.nextInt(10);
        	b.setInterval(0, new RealInterval(point[0]) );
        	b.setInterval(1, new RealInterval(point[1]) );
        	
        	f.calculate(b);
	
        	if (b.getFunctionValue().hi() - b.getFunctionValue().lo() > 1e-4)
       			System.out.println(b.getFunctionValue().hi() + " -> " + (b.getFunctionValue().hi() - b.getFunctionValue().lo() ) );
       		assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-4);
       		assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-4);
        }
	}        

	@Test
    public void testKnownPoints() {
        point[0] = point[1] = 0;
    	b.setInterval(0, new RealInterval(point[0]) );
    	b.setInterval(1, new RealInterval(point[1]) );
    	f.calculate(b);
    	// f(0) = 0;
    	assertTrue(b.getFunctionValue().hi() < 0 + 1e-6);
    	assertTrue(b.getFunctionValue().lo() > 0 - 1e-6);
    	// cmp with point calculation
    	assertTrue(Math.abs( f.calculatePoint(point) ) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);
    }

    @Test
    public void testKnownPoints1() {
    	// 2 g.min f = -1.03163, x = 0.08984, y = -0.71266; 
    	// 						 x = -0.08984, y = 0.71266;
    	double min = -1.03163;
    	for (int i = 0; i < 2; i++) {
	    	point[0] = 0.08984;
	    	point[1] = -0.71266;
	    	b.setInterval(0, new RealInterval(point[0]) );
	    	b.setInterval(1, new RealInterval(point[1]) );
	    	
	    	f.calculate(b);
	    	
//	    	System.out.println(b);
	    	
	    	assertTrue((Math.abs(b.getFunctionValue().hi() - min)) < 1e-5);
	    	assertTrue(b.getFunctionValue().wid() < 1e-5);
	    	
	    	if (b.getFunctionValue().hi() - b.getFunctionValue().lo() > 1e-6)
	   			System.out.println(b.getFunctionValue().hi() + " -> " + (b.getFunctionValue().hi() - b.getFunctionValue().lo() ) );
	    	if (Math.abs( f.calculatePoint(point) - min) > 1e-4)
	    		System.out.println("x = " + point[0] + ", y = " + point[1] + 
	    				", f = " + Math.abs( f.calculatePoint(point) ));

	    	// cmp with point calculation
	    	assertTrue(Math.abs( f.calculatePoint(point) - min) < 1e-5);
	    	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-5);
	    	point[0] = -0.08984;
	    	point[1] = 0.71266;
    	}
    }
    
    @Test
    public void testWrongDimension() {
        int dim;
        do {
        	dim = rnd.nextInt(100);
        } while (dim == 2);
        
        Box box = new Box(dim, new RealInterval(-1, 1));
        double point[] = new double[dim];
        
        try {
        	f.calculate(box);
        	fail("exception expected! Don't you forget to add -ea option to JavaVM arguments? (Window->Preferences->Jnstalled JREs->Edit->Default VM arguments)");
        } catch (AssertionError e) {
        	assertTrue(true);
        }
        try {
        	f.calculatePoint(point);
        	fail();
        } catch (AssertionError e) {
        	assertTrue(true);
        }
    }
/*
    @Test
    public void testSomethingStrange() {
    	// checkValue = -1.03163; //x = +/-0.08984 y = +/-0.71266
    	// [-0.974, -0.829] ([0.08, 0.1] x [0.71, 0.72])
    	b.setInterval(0, new RealInterval(0.08, 0.1) );
    	b.setInterval(1, new RealInterval(0.71, 0.72) );

    	
    	b.setInterval(0, new RealInterval(0.089, 0.09) );
    	b.setInterval(1, new RealInterval(0.712, 0.723) );
    	
    	f.calculate(b);
    	
    	System.out.println(b);
    	
    	assertTrue(b.getFunctionValue().wid() < 1);
   	
    }
*/
    @Test
    public void testRealInterval() {
    	String s = new RealInterval(1e8, 1e8).toString();
    	System.out.println(s);
    	assertEquals("[100,000,000.00, 100,000,000.00]", s);

    	s = new RealInterval(123.456789, 123456789.987).toString();
//    	System.out.println(s);
    	assertEquals("[123.46, 123,456,789.99]", s);
    	
    	s = new RealInterval().toString();
//    	System.out.println(s);
    	assertEquals("[-inf, inf]", s);    	
    }

    
    @Test
    public void testSomethingStrange1() {
    	b.setInterval(0, new RealInterval(100.0, 100.0) );
    	b.setInterval(1, new RealInterval(45.0, 45.0) );

    	f.calculate(b);
    	double p = f.calculatePoint(100, 45);
    	
//    	System.out.println(b + "    " + p);
    	
    	assertTrue(b.getFunctionValue().wid() < 1);
    	assertTrue(Math.abs(b.getFunctionValue().lo() - p) < 1);
   	
    }

}
