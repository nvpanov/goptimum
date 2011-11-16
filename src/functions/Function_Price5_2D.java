package functions;

import symboldiff.exceptions.IncorrectExpression;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;


/**
 * @author  nvpanov
 */
public class Function_Price5_2D extends Function {
	private static final String equation = "(2*x^3 * y - y^3)^2 + (6*x - y^2 + y)^2";
	//(2x^3y - y^3)^2 + (6x - y^2 + y)^2
	// min f = 0, x = y = 0
	
	public Function_Price5_2D() {
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
		assert(b.getDimension() == this.getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + b.getDimension() + "d box.");
		
		RealInterval x = b.getInterval(0), y = b.getInterval(1);

		RealInterval t1 = IAMath.power(x, 3);
		RealInterval t2 = IAMath.mul(y, c2);
		RealInterval t4 = IAMath.power(y, 3);
		RealInterval t3r = IAMath.mul(t1, t2);
		RealInterval t5 = IAMath.mul(x, c6);
		RealInterval t6r = IAMath.sub(t3r, t4);
		RealInterval t8r = IAMath.add(t5, y);
		RealInterval t7 = IAMath.power(y, 2);
		RealInterval t11r = IAMath.power(t6r, 2);
		RealInterval t10r = IAMath.sub(t8r, t7);
		RealInterval t12r = IAMath.power(t10r, 2);
		RealInterval t13r = IAMath.add(t11r, t12r);
			
		b.setFunctionValue(t13r);
	}
	// a workaround for IAMath that can multiply intervals only // solved, nvpanov
	private final static int c2 = 2; //new RealInterval(2); 	
	private final static int c6 = 6 ;// new RealInterval(6);

	@Override
	public double calculatePoint(double... point) {
		assert(point.length == this.getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + " is 2d function while it called for " + point.length + "d point.");
		
		double x = point[0], y = point[1];
		//(2x^3y - y^3)^2 + (6x - y^2 + y)^2
		return Math.pow( (2 * Math.pow(x, 3) * y - Math.pow(y, 3)), 2) + Math.pow( (6*x - Math.pow(y, 2) + y), 2);
	}

	@Override
	protected String toStringHuman() {
		return equation;
	}
}
