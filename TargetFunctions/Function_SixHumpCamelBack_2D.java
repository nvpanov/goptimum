package TargetFunctions;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import Algorithm.Box;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Random;


public class Function_SixHumpCamelBack_2D implements Function {
	// 4x^2 - 2.1x^4 + \frac{1}{3}x^6 + xy -4y^2 + 4y^4 
	public void calculate(Box b) {
		if (b.getDimension() != 2)
			throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + b.getDimension() + "d box.");
		
		RealInterval x = b.getInterval(0), y = b.getInterval(1);


		RealInterval t1 = IAMath.mul(IAMath.evenPower(x, 2), c1);
		RealInterval t2 = IAMath.mul(IAMath.evenPower(x, 4), c2);
		RealInterval t3_dbg = IAMath.evenPower(x, 6);
		RealInterval t3 = IAMath.mul(t3_dbg, c3);
		RealInterval t4 = IAMath.mul(x, y);
		RealInterval t5 = IAMath.mul(IAMath.evenPower(y, 2), c1);
		RealInterval t6 = IAMath.mul(IAMath.evenPower(y, 4), c1);
		RealInterval t01 = IAMath.sub(t1, t2);
		RealInterval t02 = IAMath.add(t3, t4);
		RealInterval t03 = IAMath.sub(t6, t5);
		RealInterval r = IAMath.add(IAMath.add(t01, t02), t03);
			
		b.setFunctionValue(r);
	}
	public double calculatePoint(double[] point) {
		if (point.length != 2)
			throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + point.length + "d point.");
		
		double x = point[0], y = point[1];
		double f = 4*Math.pow(x, 2) - 2.1 * Math.pow(x, 4) + 1/3*Math.pow(x, 6) + x*y - 4* Math.pow(y, 2) + 4* Math.pow(y, 4);

		return f; 
	}
	
	// a workaround for IAMath that can multiply intervals only
	private RealInterval c1 = new RealInterval(4); 
	private RealInterval c2 = new RealInterval(2.1);
	private RealInterval c3 = new RealInterval(1/3 - 1e-8, 1/3 + 1e-8);


	///////////////// Tests //////////////////////
	
    @Test
    public void testPoints() {
    	Random rnd = new Random();
    	double point[] = new double[2];
    	Box b = new Box(2);
        for (int i = 0; i < 10; i++) {
        	point[0] = rnd.nextDouble() * rnd.nextInt(4);
        	point[1] = rnd.nextDouble() * rnd.nextInt(10);
        	b.setInterval(0, new RealInterval(point[0]) );
        	b.setInterval(1, new RealInterval(point[1]) );
        	
        	calculate(b);
	
        	if (b.getFunctionValue().hi() - b.getFunctionValue().lo() > 1e-4)
       			System.out.println(b.getFunctionValue().hi() + " -> " + (b.getFunctionValue().hi() - b.getFunctionValue().lo() ) );
       		assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-4);
       		assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-4);
        }
        
        point[0] = point[1] = 0;
    	b.setInterval(0, new RealInterval(point[0]) );
    	b.setInterval(1, new RealInterval(point[1]) );
    	calculate(b);
    	assertTrue(Math.abs( calculatePoint(point) ) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-6);
        
    }
    
    @Test
    public void testWrongDimension() {
        Random rnd = new Random();
        int dim;
        do {
        	dim = rnd.nextInt(100);
        } while (dim == 2);
        
        Box box = new Box(dim, new RealInterval(-1, 1));
        double point[] = new double[dim];
        
        try {
        	calculate(box);
        	assertFalse(1 == 1);
        } catch (IllegalArgumentException e) {
        	assertTrue(1 == 1);
        }
        try {
        	calculatePoint(point);
        	assertFalse(1 == 1);
        } catch (IllegalArgumentException e) {
        	assertTrue(1 == 1);
        }
    }
	
	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main("Function_SixHumpCamelBack_2D");
	}
}
