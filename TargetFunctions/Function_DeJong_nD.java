package TargetFunctions;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import Algorithm.Box;


/*
 * This function is not more than a \sum x_i^2
 */
public class Function_DeJong_nD implements Function {


	public void calculate(Box b) {
		int dim = b.getDimension();
		RealInterval result = new RealInterval(0);
		for(int i = 0; i < dim; i++) {
			result = IAMath.add( result, IAMath.evenPower(b.getInterval(i), 2) );
		}
		b.setFunctionValue(result);
	}

	@Override
	public double calculatePoint(double[] point) {
		int dim = point.length;
		double res = 0;
		for (int i = 0; i < dim; i++) {
			res += Math.pow(point[i], 2);
		}
		return res;
	}
	
	///////////////// Tests //////////////////////
	
    @Test
    public void testPoints() {
    	Random rnd = new Random();
    	int dim = rnd.nextInt(10);
    	double point[] = new double[dim];
    	Box b = new Box(dim);
        for (int i = 0; i < 10; i++) { // 10 tests
        	for (int j = 0; j < dim; j++) { // init data
        		point[j] = rnd.nextDouble() * rnd.nextInt(10);
        		b.setInterval(j, new RealInterval(point[j]) );
        	}
      	
        	calculate(b);
	
        	assertTrue(b.getFunctionValue().hi() - b.getFunctionValue().lo() < 1e-6);
        	assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-6);
        }
        ////// known-point: 0
    	for (int j = 0; j < dim; j++) { 
    		point[j] = 0;
    		b.setInterval(j, new RealInterval(point[j]) );
    	}
  	
    	calculate(b);

    	assertTrue(Math.abs( calculatePoint(point) ) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-6);

        ////// known-point: 1
    	for (int j = 0; j < dim; j++) { 
    		point[j] = 1;
    		b.setInterval(j, new RealInterval(point[j]) );
    	}
  	
    	calculate(b);

    	assertTrue(Math.abs( calculatePoint(point) - dim) < 1e-6);
    	assertTrue(b.getFunctionValue().hi() - calculatePoint(point) < 1e-6);
    	
    }
    

	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main("Function_DeJong_nD");
	}
}
