package TargetFunctions;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import Algorithm.Box;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;


public class Function_Rastrigin10_2D implements Function {
// x^2 + y^2 - \cos(18x) - \cos(18y) 
	public void calculate(Box b) {
		if (b.getDimension() != 2)
			throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + b.getDimension() + "d box.");
		
		RealInterval x = b.getInterval(0), y = b.getInterval(1);

		RealInterval t1 = IAMath.evenPower(x, 2);
		RealInterval t2 = IAMath.evenPower(y, 2);
		RealInterval t3 = IAMath.cos( IAMath.mul(x, c1) );
		RealInterval t4 = IAMath.cos( IAMath.mul(y, c1) );
		
		RealInterval r0 = IAMath.add(t1, t2);
		RealInterval r1 = IAMath.add(t3, t4);
		RealInterval rr = IAMath.sub(r0, r1);
			
			
		b.setFunctionValue(rr);
	}
	// a workaround for IAMath that can multiply intervals only
	private RealInterval c1 = new RealInterval(18); 	

	@Override
	public double calculatePoint(double[] point) {
		if (point.length != 2)
			throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + point.length + "d point.");
		
		double x = point[0], y = point[1];
		
		return x*x + y*y - Math.cos(18*x) - Math.cos(18*y);
	}
	
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
	
        	assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-6);
        	assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-6);
        }
        // known point: 0
        point[0] = point[1] = 0;
    	b.setInterval(0, new RealInterval(point[0]) );
    	b.setInterval(1, new RealInterval(point[1]) );
    	calculate(b);
    	assertTrue(Math.abs( calculatePoint(point) + 2) < 1e-6);
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
		org.junit.runner.JUnitCore.main("Function_Rastrigin10_2D");
	}
}