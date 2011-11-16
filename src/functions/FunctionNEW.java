package functions;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;

public abstract class FunctionNEW {
	protected int dim;
	
	public abstract void calculate(Box b);
	public abstract double calculatePoint(double... point);
	
//	public abstract RealInterval calc1Derivative(Box box, int argNum);
//	public abstract RealInterval calc2Derivative(Box box, int argNum);
	
	public String toStringHuman() {
		return toString();
	}
	public int getDimension() {
		return dim;
	}
}
