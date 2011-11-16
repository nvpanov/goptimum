package functions;

import symboldiff.exceptions.IncorrectExpression;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;

/**
 * @author  nvpanov
 */
public class Function_SixHumpCamelBack_2D extends Function {
	private static final String equation = "4*x^2 - 2.1*x^4 + 1/3*x^6 + x*y -4*y^2 + 4*y^4";
	// 4x^2 - 2.1x^4 + \frac{1}{3}x^6 + xy -4y^2 + 4y^4 
	// 2 g.min f = -1.03163, x = 0.08984, y = -0.71266; 
	// 						 x = -0.08984, y = 0.71266;

	public Function_SixHumpCamelBack_2D() {
		dim = 2;
		try {
			super.init(toStringFull());
		} catch (IncorrectExpression e) {
			// actually everything should be OK,
			// otherwise we will work w/o derivatives
			e.printStackTrace();
		}
	}
	
	@Override
	public void calculate(Box b) {
		assert(b.getDimension() != getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + b.getDimension() + "d box.");
		
		RealInterval x = b.getInterval(0), y = b.getInterval(1);
		
		final double c1 = 4, c2 = 2.1, c3 = 1.0/3;

		RealInterval t1 = IAMath.mul(IAMath.power(x, 2), c1);
		RealInterval t2 = IAMath.mul(IAMath.power(x, 4), c2);
		RealInterval t3_dbg = IAMath.power(x, 6);
		RealInterval t3 = IAMath.mul(t3_dbg, c3);
		RealInterval t4 = IAMath.mul(x, y);
		RealInterval t5 = IAMath.mul(IAMath.power(y, 2), c1);
		RealInterval t6 = IAMath.mul(IAMath.power(y, 4), c1);
		RealInterval t01 = IAMath.sub(t1, t2);
		RealInterval t02 = IAMath.add(t3, t4);
		RealInterval t03 = IAMath.sub(t6, t5);
		RealInterval r = IAMath.add(IAMath.add(t01, t02), t03);
			
		b.setFunctionValue(r);
/*// debug...		
		double dt1 = 4*Math.pow(x.lo(), 2);
		double dt2 = 2.1 * Math.pow(x.lo(), 4);
		double dt3 = c3*Math.pow(x.lo(), 6);
		double dt4 = x.lo()*y.lo();
		double dt5 = 4* Math.pow(y.lo(), 2);
		double dt6 = 4* Math.pow(y.lo(), 4);
		double dt01= dt1-dt2;
		double dt02= dt3+dt4;
		double dt03= dt6-dt5;
		double dr  = dt01 + dt02 + dt03;
		double drr = calculatePoint(x.lo(), y.lo());
*/		
	}
	@Override
	public double calculatePoint(double... point) {
		assert(point.length == getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + 
			//		" is 2d function while it called for " + point.length + "d point.");
		
		double x = point[0], y = point[1];
		double f = 4*Math.pow(x, 2) - 2.1 * Math.pow(x, 4) + 1.0/3*Math.pow(x, 6) + 
					x*y - 4* Math.pow(y, 2) + 4* Math.pow(y, 4);

		return f; 
	}
	
/*	// a workaround for IAMath that can multiply intervals only // solved, nvpanov
	private final static RealInterval c1 = new RealInterval(4); 
	private final static RealInterval c2 = new RealInterval(2.1);
	private final static RealInterval c3 = new RealInterval(1.0/3);
*/	
	@Override
	protected String toStringHuman() {
		return equation;
	}
}
