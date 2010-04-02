package Algorithm;
import net.sourceforge.interval.ia_math.RealInterval;


public class Box implements Cloneable {

	private int dim;  // the dimension of this multidimensional interval
	private RealInterval intervals[]; // multidimensional interval is a vector of intervals 
	private RealInterval functionValue;

	/*
	 * Creates a square box where each side is equals to @value@ 
	 */
	public Box(int dimension, RealInterval value) { 
		functionValue = new RealInterval(); // [-inf, +inf]
		dim = dimension;
		intervals = new RealInterval[dimension];
		for (int i = 0; i < dim; i++) {
			intervals[i] = (RealInterval)value.clone(); // make enough copies of interval specified and add them in the list  
		}
	}

	public Box(int dimension) {
		dim = dimension;
		intervals = new RealInterval[dimension];
	}

	public int getDimension() {
		return dim;
	}
	
	public RealInterval getInterval(int n) {
		return intervals[n]; // out of range checking will be automatically done by JVM
	}
	public void setInterval(int n, RealInterval i) {
		intervals[n] = i; // out of range checking will be automatically done by JVM
	}

	public void setFunctionValue(RealInterval i) {
		functionValue = i;		
	}	
	public RealInterval getFunctionValue() {
		return functionValue;		
	}	
	
	@Override
	public String toString() {
		return toStringValue() + " (" + toStringArea() + ")";
	}
	public String toStringArea() {
		String str = "";
		for (int i = 0; i < dim; i++) {
			str += intervals[i].toString();
			if (i != dim - 1)
				str += " x ";
		}
		return str;		
	}
	public String toStringValue() {
		return functionValue.toString();
	}

	@Override
	public Box clone() {
		Box b = new Box(dim);
		
		for (int i = 0; i < dim; i++) {
			b.intervals[i] = (RealInterval)intervals[i].clone();
		}
		b.functionValue = (RealInterval)functionValue.clone();
		
		return b;
	}
}
