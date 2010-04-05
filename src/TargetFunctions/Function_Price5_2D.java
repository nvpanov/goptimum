package TargetFunctions;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import Algorithm.Box;


public class Function_Price5_2D implements Function {
	//(2x^3y - y^3)^2 + (6x - y^2 + y)^2
	public void calculate(Box b) {
		if (b.getDimension() != 2)
			throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + b.getDimension() + "d box.");
		
		RealInterval x = b.getInterval(0), y = b.getInterval(1);

		RealInterval t1 = IAMath.oddPower(x, 3);
		RealInterval t2 = IAMath.mul(y, c2);
		RealInterval t4 = IAMath.oddPower(y, 3);
		RealInterval t3r = IAMath.mul(t1, t2);
		RealInterval t5 = IAMath.mul(x, c6);
		RealInterval t6r = IAMath.sub(t3r, t4);
		RealInterval t8r = IAMath.add(t5, y);
		RealInterval t7 = IAMath.evenPower(y, 2);
		RealInterval t11r = IAMath.evenPower(t6r, 2);
		RealInterval t10r = IAMath.sub(t8r, t7);
		RealInterval t12r = IAMath.evenPower(t10r, 2);
		RealInterval t13r = IAMath.add(t11r, t12r);
			
		b.setFunctionValue(t13r);
	}
	// a workaround for IAMath that can multiply intervals only
	private RealInterval c2 = new RealInterval(2); 	
	private RealInterval c6 = new RealInterval(6);

	@Override
	public double calculatePoint(double[] point) {
		if (point.length != 2)
			throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + point.length + "d point.");
		
		double x = point[0], y = point[1];
		//(2x^3y - y^3)^2 + (6x - y^2 + y)^2
		return Math.pow( (2 * Math.pow(x, 3) * y - Math.pow(y, 3)), 2) + Math.pow( (6*x - Math.pow(y, 2) + y), 2);
	}
	
	///////////////// Tests //////////////////////
	
    @Test
    public void testPoints() {
//    	Random rnd = new Random();
    	double point[] = new double[2];
    	Box b = new Box(2);
/*
    	for (int i = 0; i < 10; i++) {
        	point[0] = rnd.nextDouble() * rnd.nextInt(4);
        	point[1] = rnd.nextDouble() * rnd.nextInt(10);
        	b.setInterval(0, new RealInterval(point[0]) );
        	b.setInterval(1, new RealInterval(point[1]) );
        	
        	calculate(b);
	
        	assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-6);
        	assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-6);
        }
*/        
        // known point: 0
    	// IAMath issue: 0.10548578434087275	
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
		org.junit.runner.JUnitCore.main("TargetFunctions.Function_Price5_2D");
	}
}
