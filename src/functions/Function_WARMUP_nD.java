package functions;

import core.Box;


/*
 * This function is just a constant and uses for WARMUP only
 */
public class Function_WARMUP_nD extends Function {

	public Function_WARMUP_nD(int dim) {
		this.dim = dim;
	}

	public void calculate(Box b) {
		b.setFunctionValue(b.getInterval(0));
	}

	@Override
	public double calculatePoint(double... point) {
		return point[0];
	}

	@Override
	protected String toStringFull() {
		return "x";
	}

	@Override
	protected String toStringHuman() {
		return toStringFull();
	}
}
