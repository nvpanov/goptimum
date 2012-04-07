package functions;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import symboldiff.Expression;
import symboldiff.Gradient;
import symboldiff.Simplifier;
import symboldiff.exceptions.ExpressionException;

import static net.sourceforge.interval.ia_math.IAMath.*;
import net.sourceforge.interval.ia_math.RealInterval;

import core.Box;


public class Function_RosenbrockG_nDTest {
	Random rnd = new Random();

	@Test
    public void testFunc() {
		FunctionNEW f = new Function_RosenbrockG_nD(2);
		assertEquals("100*(x1-x0^2)^2+(x0-1)^2", f.toString());
		f = new Function_RosenbrockG_nD(3);
		assertEquals("100*((x1-x0^2)^2+(x2-x1^2)^2)+(x0-1)^2+(x1-1)^2", f.toString());
	}
	@Test
    public void testPoints() {
    	rnd.setSeed(System.currentTimeMillis());    	
    	int dim = rnd.nextInt(4) + 2;
    	FunctionNEW f = new Function_RosenbrockG_nD(dim); 
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
    public void test1Der_x1x0() throws ExpressionException {
    	long seed = System.currentTimeMillis();
//   	seed = 1309165436839L;
//		seed = 1325769986456L;
    	seed = 1327058754199L; //actually: 158400.0000000003
    	
    	rnd.setSeed(seed);
    	int dim = 2;
    	FunctionNEW f = new Function_RosenbrockG_nD(dim);
    	Box box = new Box(dim, new RealInterval(1));
    	for (int i = 0; i < dim; i++)
    		box.setInterval(rnd.nextInt(dim), new RealInterval(rnd.nextInt(6) - rnd.nextInt(3)));
    	
    	RealInterval d1;
    	RealInterval checkVal;
    	int argNum = 1; //x[i+1];
    	d1 = f.calculate1Derivative(box, argNum);
    	assertTrue("" + seed, d1 != null);
    	
    	Gradient checkingGrad = new Gradient(f.toString());
    	Expression checkingD1 = checkingGrad.getPartialDerivative(argNum);
    	Simplifier.simplify(checkingD1);
    	assertTrue(dim == 2 && argNum == 1);
    	assertEquals("200*(x1-x0^2)", checkingD1.toString()); // dim = 2, argnum = 1!
    	RealInterval checkVal2 = checkingD1.evaluate(box);
    	
    	checkVal = sub( box.getInterval(1), pow(box.getInterval(0), 2) );
    	checkVal = mul(200, checkVal);
//    	System.out.println("d1="+d1);
//    	System.out.println("checkVal="+checkVal);
//    	System.out.println("checkVal2="+checkVal2);
    	
    	assertTrue(seed + " actually: " + Math.abs(d1.lo() - checkVal.lo()), 
    										Math.abs(d1.lo() - checkVal.lo()) < 1e-4);
    	assertTrue(seed + " actually: " + Math.abs(d1.hi() - checkVal.hi()), 
    										Math.abs(d1.hi() - checkVal.hi()) < 1e-4);
    	
    	assertTrue(seed + " actually: " + Math.abs(d1.hi() - checkVal2.hi()), 
											Math.abs(d1.hi() - checkVal2.hi()) < 1e-4);
    	assertTrue(seed + " actually: " + Math.abs(d1.lo() - checkVal2.lo()), 
    										Math.abs(d1.lo() - checkVal2.lo()) < 1e-4);
    	
////////////
    	argNum = 0;
    	d1 = f.calculate1Derivative(box, argNum);
    	RealInterval x0 = box.getInterval(0), x1 = box.getInterval(1);
//    	System.out.println("d1 = " + f.getGradient().getPartialDerivative(argNum) + " = "+ d1 + " | x0 = " + x0 + ", x1 = " + x1);
    	assertTrue("" + seed, d1 != null);
    	assertTrue(dim == 2 && argNum == 0);
    	checkingD1 = checkingGrad.getPartialDerivative(argNum);
    	Simplifier.simplify(checkingD1);
    	assertTrue(dim == 2 && argNum == 0);
    	assertEquals("-400*x0*(x1-x0^2)+2*(x0-1)", checkingD1.toString()); // dim = 2, argnum = 0!
    	checkVal2 = checkingD1.evaluate(box);
    	
    	checkVal = mul(-400, mul(x0, sub(x1, pow(x0, 2) ) ) );
    	checkVal = add(checkVal, mul(2, sub(x0, 1) ));
    	//System.out.println("checkVal = " + checkVal);
    	
    	assertTrue(seed + " actually: " + Math.abs(d1.lo() - checkVal.lo()), 
				Math.abs(d1.lo() - checkVal.lo()) < 1e-4);
    	assertTrue(seed + " actually: " + Math.abs(d1.hi() - checkVal.hi()), 
				Math.abs(d1.hi() - checkVal.hi()) < 1e-4);

    	assertTrue(seed + " actually: " + Math.abs(d1.hi() - checkVal2.hi()), 
				Math.abs(d1.hi() - checkVal2.hi()) < 1e-4);
    	assertTrue(seed + " actually: " + Math.abs(d1.lo() - checkVal2.lo()), 
				Math.abs(d1.lo() - checkVal2.lo()) < 1e-4);
    	
    	
    }
    
}