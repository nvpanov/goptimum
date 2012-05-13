package point;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionNEW;

public class SteepestDescent extends PointAlgorithm {

	double[] delta;  

	public SteepestDescent(FunctionNEW f, Box area) {
		super();
		setProblem(f, area);
	}
	public SteepestDescent() {
		super();
	}
	@Override
	protected void minimize(Box area) {
		int dim = area.getDimension();
		
		double point[] = middleAreaPoint(area);
		double step[] = new double[dim];
//		double prevDelta[] = new double[dim];
		
		for(int i = 0; i < dim; i++)
			step[i] = area.getInterval(i).wid() * stepFromSearchAreaSizeFactor;
		double epsilonNd = epsilon * dim;

		//System.out.println("SteepestDescent : --------\nSteepestDescent : area = " + area);
		
		localMinimum(point, step, epsilonNd);
		
	}
	private void localMinimum(double[] point, double step[], double epsilonNd) {	
		double startingVal = function.calculatePoint(point);
		optVal = startingVal;
		optArg = point.clone();
		double[] savedStep = step.clone(); 	// see the end of the function. 
											// if it performs good we can decide to run its again
											// but @step already contains reduced values.
				
		if (logging) 
			System.out.println("SteepestDescent : starting value " + startingVal);		
		
		final int dim = point.length;
		double delta[] = new double[dim];

		int c;
		for (c = 0; c < maxSteps; c++) {
			double deltaSumAllDim = 0;
			Box tmp = new Box(point);
			
			// calculate new coordinates
			for (int i = 0; i < dim; i++) {
				RealInterval iGradient = function.calculate1Derivative(tmp, i);
				if (iGradient == null) {
					return; 
				}
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
			if (newVal < optVal) {
				optVal = newVal;
				optArg = point.clone();
			}
			if (deltaSumAllDim < epsilonNd)
				break;
		}
		if (logging)
			System.out.println("SteepestDescent :    final value " + optVal + "(" + c + " steps)" );
		
		// run its once more if it was so good
		if (c == maxSteps && 
				(startingVal - optVal) > (startingVal/2) ) { // better than 50% improvement
			if (logging)
				System.out.println("SteepestDescent :    ONE MORE CYCLE" );	
			for (int i = 0; i < dim; i++) {
				savedStep[i] /= 4;
			}
			localMinimum(optArg, savedStep, epsilonNd);
		}
	}
/*
	@Override
	public void run() {
		localMinimum();		
	}
*/	
}
