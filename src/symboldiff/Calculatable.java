package symboldiff;

import symboldiff.exceptions.MethodGenerationException;
import symboldiff.exceptions.MethodInvocationException;
import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;

/*
public class Calculatable {
	private MethodRunner methodRunner;
	private Expression expr;
	
	public Calculatable(Expression expr) throws MethodGenerationException {
		assert(expr != null);
		this.expr = expr;
	}

	public RealInterval calculate(Box box) {
		RealInterval res = null;
		res = expr.evaluate(box);
		return res;
	}
	public Double calculate(double[] point) {
		Double res = null;
		try {
			res = methodRunner.invokeMethods(point);
		} catch (MethodInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
*/