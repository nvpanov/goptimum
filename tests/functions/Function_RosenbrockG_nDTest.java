package functions;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import static net.sourceforge.interval.ia_math.IAMath.*;
import net.sourceforge.interval.ia_math.RealInterval;

import core.Box;


public class Function_RosenbrockG_nDTest {
	Random rnd = new Random();

	@Test
    public void testFunc() {
		Function f = new Function_RosenbrockG_nD(2);
		assertEquals("0 + (100*(x1-x0^2)^2 + (x0-1)^2)", f.toStringFull());
		f = new Function_RosenbrockG_nD(3);
		assertEquals("0 + (100*(x1-x0^2)^2 + (x0-1)^2) + (100*(x2-x1^2)^2 + (x1-1)^2)", f.toStringFull());
	}
	@Test
    public void testPoints() {
    	rnd.setSeed(System.currentTimeMillis());    	
    	int dim = rnd.nextInt(10) + 2;
    	Function f = new Function_RosenbrockG_nD(dim); 
    	double point[] = new double[dim];
    	Box b = new Box(dim, new RealInterval());
        for (int i = 0; i < 100; i++) { // 100 tests
        	for (int j = 0; j < dim; j++) { // init box: set points as intervals
        		point[j] = rnd.nextDouble() * rnd.nextInt(10);
        		b.setInterval(j, new RealInterval(point[j]) );
        	}
      	
        	f.calculate(b);
	
        	assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-6);
        	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);
        }
        ////// known-point: 0
        double checkVal;
        checkVal = dim-1;
    	for (int j = 0; j < dim; j++) { 
    		point[j] = 0;
    		b.setInterval(j, new RealInterval(point[j]) );
    	}
  	
    	f.calculate(b);
    	
//    	System.out.println("dim = " + dim + "; " + f.calculatePoint(point) + "; " + b.getFunctionValue());

    	assertTrue(Math.abs(f.calculatePoint(point) - checkVal) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);

        ////// known-point: 1
    	checkVal = 0;
    	for (int j = 0; j < dim; j++) { 
    		point[j] = 1;
    		b.setInterval(j, new RealInterval(point[j]) );
    	}
  	
    	f.calculate(b);

    	assertTrue(Math.abs( f.calculatePoint(point) - checkVal) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - f.calculatePoint(point) < 1e-6);
    	
    }

    @Test
    public void test1Der_x1x0() {
    	long seed = System.currentTimeMillis();
//   	seed = 1309165436839L;
    	rnd.setSeed(seed);
    	int dim = 2;
    	Function f = new Function_RosenbrockG_nD(dim);
    	Box box = new Box(dim, new RealInterval(1));
    	for (int i = 0; i < dim; i++)
    		box.setInterval(rnd.nextInt(dim), new RealInterval(rnd.nextInt(6) - rnd.nextInt(3)));
    	
    	RealInterval d1;
    	RealInterval checkVal;
    	int argNum = 1; //x[i+1];
    	d1 = f.calc1Derivative(box, argNum);
//    	System.out.println("d1 = " + f.getGradient().getPartialDerivative(argNum) + " = " + d1);
    	assertTrue("" + seed, d1 != null);
//    	System.out.println("d1 = " + d1);
    	// adiff.com: (20000 * (x1 - (x0^2)))
    	assertTrue(dim == 2 && argNum == 1);
    	checkVal = sub( box.getInterval(argNum), pow(box.getInterval(0), 2) );
    	checkVal = mul(20000, checkVal);
//    	System.out.println("checkVal = " + checkVal);
    	
    	assertTrue("" + seed, Math.abs(d1.lo() - checkVal.lo()) < 1e-4);
    	assertTrue("" + seed, Math.abs(d1.hi() - checkVal.hi()) < 1e-4);
    	
////////////
    	argNum = 0;
    	d1 = f.calc1Derivative(box, argNum);
    	RealInterval x0 = box.getInterval(0), x1 = box.getInterval(1);
//    	System.out.println("d1 = " + f.getGradient().getPartialDerivative(argNum) + " = "+ d1 + " | x0 = " + x0 + ", x1 = " + x1);
    	assertTrue("" + seed, d1 != null);
    	// adiff.com: (-(40000 * x0 * (x1 - (x0^2))) + (2 * (-1 + x0)))
    	assertTrue(dim == 2 && argNum == 0);
    	checkVal = mul(-40000, mul(x0, sub(x1, pow(x0, 2) ) ) );
    	checkVal = add(checkVal, mul(2, sub(x0, 1) ));
    	System.out.println("checkVal = " + checkVal);
    	
    	assertTrue("" + seed, d1.lo() <= checkVal.lo());
    	assertTrue("" + seed, d1.hi() >= checkVal.hi());

    	assertTrue("" + seed, Math.abs(d1.hi() - d1.lo()) < Math.abs(checkVal.hi())/100.0 ); // 1%
    	
    	
    }
    
}