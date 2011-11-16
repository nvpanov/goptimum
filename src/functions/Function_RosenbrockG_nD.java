package functions;

import symboldiff.exceptions.IncorrectExpression;
import core.Box;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;
import static net.sourceforge.interval.ia_math.RealInterval.*;
import static net.sourceforge.interval.ia_math.IAMath.*;
import static java.lang.Math.pow;
/**
 * @author  nvpanov
 */
public class Function_RosenbrockG_nD extends Function {
	public Function_RosenbrockG_nD(int dim) {
		if (dim < 2)
			throw new IllegalArgumentException(this.getClass().getName() + 
					" can't be less than 1d function");
		this.dim = dim;
		try {
			super.init(toStringFull());
		} catch (IncorrectExpression e) {
			// actually everything should be OK,
			// otherwise we will work w/o derivatives
			System.out.println("ERROR: " + getClass().getName() + " failed to init derivatives. Continue w/o them");
			e.printStackTrace();
		}
	}
	public void calculate(Box b) {
		/*
		if (b.getDimension() != dim)
			throw new IllegalArgumentException(this.getClass().getName() + 
					" is " + dim +"d function while it called for " 
					+ b.getDimension() + "d box.");
		*/
		assert(b.getDimension() == getDimension());
		
		RealInterval f = new RealInterval(0);
		for (int i = 0; i < dim-1; i++) {
			RealInterval x = b.getInterval(i);
			RealInterval x1 = b.getInterval(i+1);
			
			RealInterval right = pow(sub(x, 1), 2);
			RealInterval cntr0 = sub(x1, pow(x, 2));
			RealInterval cnter = pow(cntr0, 2);
			RealInterval whole = add(mul(100, cnter), right);
			f = add(f, whole);						
		}
		b.setFunctionValue(f);
	}
	@Override
	public double calculatePoint(double... point) {
		assert(point.length == getDimension());
			//throw new IllegalArgumentException(this.getClass().getName() + 
			//		" is " + dim + "d function while it called for " 
			//		+ point.length + "d point.");
		//f(x) = \sum^{n-1}_{i=1} (100 \cdot (x_{i+1} - x_{i}^2)^{2} + (x_{i} - 1)^{2}).
		//                                      cnter                       right
		double x[] = point;
		double f = 0;
		for (int i = 0; i < dim-1; i++) {
			double right = pow((x[i] - 1), 2);
			double cnter = pow(x[i+1] - pow(x[i], 2), 2);
			double whole = 100 * cnter + right;
			f += whole;
		}
		return f;
	}

	@Override
	protected String toStringFull() {
		StringBuilder sb = new StringBuilder("0");
		for (int i = 0; i < dim-1; i++) {
			sb.append(" + ");
			sb.append("(100*(x");
			sb.append(i+1);
			sb.append("-x");
			sb.append(i);
			sb.append("^2)^2 + (x");
			sb.append(i);
			sb.append("-1)^2)");
		}
		return sb.toString();
	}

	@Override
	protected String toStringHuman() {
		return "sum_{i=1}^{"+dim + "-1} (100*(x_{i+1} - x_{i}^2)^2 + (x_i - 1)^2)";
	}}