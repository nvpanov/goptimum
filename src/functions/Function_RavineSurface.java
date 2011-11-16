package functions;

import static net.sourceforge.interval.ia_math.IAMath.pow;
import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;


public class Function_RavineSurface extends Function {
	private final int exp = 4;

	public Function_RavineSurface(int dim) {
		this.dim = dim;
	}
	@Override
	public void calculate(Box b) {
		assert(b.getDimension() == this.getDimension());
		RealInterval x = b.getInterval(0);
		b.setFunctionValue(pow(x, exp));
	}

	@Override
	public double calculatePoint(double... point) {
		assert(point.length == this.getDimension());
		return Math.pow(point[0], exp);
	}

	@Override
	protected String toStringHuman() {
		return "x^"+exp;
	}

}
