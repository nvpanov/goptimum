package functions;

import symboldiff.exceptions.IncorrectExpression;
import core.Box;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;


/**
 * @author  nvpanov
 */
public class Function_Rastrigin10_2D extends Function {
	protected static final String equation = "x^2 + y^2 - cos(18*x) - cos(18*y)";
// min f = -2, x = y = 0 
	
	public Function_Rastrigin10_2D() {
		dim = 2;
		try {
			super.init(toStringFull());
		} catch (IncorrectExpression e) {
			// actually everything should be OK,
			// otherwise we will work w/o derivatives
			e.printStackTrace();
		}
	}
	
	public void calculate(Box b) {
		assert(b.getDimension() == getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + b.getDimension() + "d box.");
		
		RealInterval x = b.getInterval(0), y = b.getInterval(1);

		RealInterval t1 = IAMath.power(x, 2);
		RealInterval t2 = IAMath.power(y, 2);
		RealInterval t3 = IAMath.cos( IAMath.mul(x, c1) );
		RealInterval t4 = IAMath.cos( IAMath.mul(y, c1) );
		
		RealInterval r0 = IAMath.add(t1, t2);
		RealInterval r1 = IAMath.add(t3, t4);
		RealInterval rr = IAMath.sub(r0, r1);
			
			
		b.setFunctionValue(rr);
	}
	// a workaround for IAMath that can multiply intervals only // solved, nvpanov
	private static final int c1 = 18; //new RealInterval(18); 	

	@Override
	public double calculatePoint(double... point) {
		assert(point.length == getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + point.length + "d point.");
		
		double x = point[0], y = point[1];
		return x*x + y*y - Math.cos(18*x) - Math.cos(18*y);
	}

	@Override
	protected String toStringHuman() {
		return equation;
	}
}