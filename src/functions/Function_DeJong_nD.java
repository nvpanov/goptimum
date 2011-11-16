package functions;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;
import symboldiff.exceptions.IncorrectExpression;
import core.Box;


/*
 * This function is not more than a \sum x_i^2
 */
public class Function_DeJong_nD extends Function {

	public Function_DeJong_nD(int dim) {
		this.dim = dim;
		try {
			super.init(toStringFull());
		} catch (IncorrectExpression e) {
			// actually everything should be OK,
			// otherwise we will work w/o derivatives
			e.printStackTrace();
		}
	}

	public void calculate(Box b) {
		int dim = b.getDimension();
		assert(dim == this.getDimension());
		RealInterval result = new RealInterval(0);
		for(int i = 0; i < dim; i++) {
			result = IAMath.add( result, IAMath.power(b.getInterval(i), 2) );
		}
		b.setFunctionValue(result);
	}

	@Override
	public double calculatePoint(double... point) {
		int dim = point.length;
		assert(dim == this.getDimension());
		double res = 0;
		for (int i = 0; i < dim; i++) {
			res += Math.pow(point[i], 2);
		}
		return res;
	}

	@Override
	protected String toStringFull() {
		StringBuilder sb = new StringBuilder("x0^2");
		for (int i = 1; i < dim; i++) {
			sb.append(" + ");
			sb.append("x");
			sb.append(i);
			sb.append("^2");
		}
		return sb.toString();
	}

	@Override
	protected String toStringHuman() {
		return "sum_{i=1}^{"+dim + "} x_i^2";
	}
}
