package point;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.Function;

public class SteepestDescent extends PointAlgorithm {

	double[] delta;  

	public SteepestDescent(Function f) {
		super();
		setFunction(f);
	}
	public SteepestDescent() {
		super();
	}
	public double localMinimum(Box area) {
		int dim = area.getDimension();
		
		double point[] = new double[dim];
		
		for (int i = 0; i < dim; i++) {
			RealInterval ii = area.getInterval(i);
			point[i] = ii.lo() + ii.wid()/2;
		}
		double step[] = new double[dim];
//		double prevDelta[] = new double[dim];
		
		for(int i = 0; i < dim; i++)
			step[i] = area.getInterval(i).wid() * stepFromSearchAreaSizeFactor;
		double epsilonNd = epsilon * dim;

		//System.out.println("SteepestDescent : --------\nSteepestDescent : area = " + area);
		
		return localMinimum(point, step, epsilonNd);
	}
	private double localMinimum(double[] point, double step[], double epsilonNd) {	
		double startingVal = function.calculatePoint(point);
		double curMinVal = startingVal;
		double[] curMinPoint = point.clone();
		double[] savedStep = step.clone(); 	// see the end of the function. 
											// if it performs good we can decide to run its again
											// but @step already contains reduced values.
				
//System.out.println("SteepestDescent : starting value " + startingVal);		
		
		final int dim = point.length;
		double delta[] = new double[dim];

		int c;
		for (c = 0; c < maxSteps; c++) {
			double deltaSumAllDim = 0;
			Box tmp = new Box(point);
			
			// calculate new coordinates
			for (int i = 0; i < dim; i++) {
				RealInterval iGradient = function.calc1Derivative(tmp, i);
				if (iGradient == null)
					return Double.MAX_VALUE;
				double gradient = iGradient.hi();
				delta[i] = step[i] * gradient;
				double delta_i_abs = Math.abs(delta[i]);  
				if (delta_i_abs > step[i] )
					delta[i] /= Math.abs(gradient); // we will move not father than @step@
				deltaSumAllDim += delta_i_abs;
			}
			// Apply new coordinates
			for (int i = 0; i < dim; i++) {
				point[i] -= delta[i];  // " - " because we are moving AGAINST gradient
				step[i] *= alpha;
			}
			// recalculate the value in new point
			double newVal = function.calculatePoint(point);
			// keep track of minimums
			if (newVal < curMinVal) {
				curMinVal = newVal;
				curMinPoint = point.clone();
			}
			if (deltaSumAllDim < epsilonNd)
				break;
		}
		//System.out.println("SteepestDescent :    final value " + curMinVal + "(" + c + " steps)" );
		
		if (c == maxSteps && 
				(startingVal - curMinVal) > (startingVal/2) ) { // better than 50% improvement
//System.out.println("SteepestDescent :    ONE MORE CYCLE" );	
			for (int i = 0; i < dim; i++) {
				savedStep[i] /= 4;
			}
			double refinedVal = localMinimum(curMinPoint, savedStep, epsilonNd);
			curMinVal = Math.min(curMinVal, refinedVal);
		}
		return curMinVal;
	}
/*
	@Override
	public void run() {
		localMinimum();		
	}
*/	
}
