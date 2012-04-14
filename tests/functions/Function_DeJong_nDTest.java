package functions;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import core.Box;


public class Function_DeJong_nDTest {
	Random rnd = new Random();
	
   //@Ignore
	@Test
    public void testPoints() {
    	rnd.setSeed(System.currentTimeMillis());    	
    	int dim = rnd.nextInt(10) + 1;
    	Function_DeJong_nD f = new Function_DeJong_nD(dim); 
    	double point[] = new double[dim];
    	Box b = new Box(dim, new RealInterval());
        for (int i = 0; i < 10; i++) { // 10 tests
        	for (int j = 0; j < dim; j++) { // init box: set points as intervals
        		point[j] = rnd.nextDouble() * rnd.nextInt(10);
        		b.setInterval(j, new RealInterval(point[j]) );
        	}
      	
        	f.calculate(b);
	
        	assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-6);
        	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);
        }
        ////// known-point: 0
    	for (int j = 0; j < dim; j++) { 
    		point[j] = 0;
    		b.setInterval(j, new RealInterval(point[j]) );
    	}
  	
    	f.calculate(b);

    	assertTrue(Math.abs( f.calculatePoint(point) ) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);

        ////// known-point: 1
    	for (int j = 0; j < dim; j++) { 
    		point[j] = 1;
    		b.setInterval(j, new RealInterval(point[j]) );
    	}
  	
    	f.calculate(b);

    	assertTrue(Math.abs( f.calculatePoint(point) - dim) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);
    	
    }
    
    @Test
    public void test1Der() {
    	long seed = System.currentTimeMillis();
//    	seed = 1295619079035L;
    	rnd.setSeed(seed);
    	int dim = rnd.nextInt(10) + 1;
    	Function_DeJong_nD f = new Function_DeJong_nD(dim);
    	Box box = new Box(dim, new RealInterval(1));
    	for (int i = 0; i < dim; i++)
    		box.setInterval(rnd.nextInt(dim), new RealInterval(rnd.nextInt(6) - rnd.nextInt(3)));
    	
    	RealInterval d1;
    	int argNum = rnd.nextInt(dim);
    	d1 = f.calculate1Derivative(box, argNum);
    	System.out.println(d1);
    	assertTrue("" + seed, d1 != null);
    	RealInterval checkVal = IAMath.mul(2, box.getInterval(argNum));
    	
    	assertTrue("" + seed, Math.abs(d1.lo() - checkVal.lo()) < 1e-4);
    	assertTrue("" + seed, Math.abs(d1.hi() - checkVal.hi()) < 1e-4);
    }
}
