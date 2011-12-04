package core;
import net.sourceforge.interval.ia_math.RealInterval;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Box implements Cloneable {

	//private int dim;  // the dimension of this multidimensional interval
	/* removed.
	 * memory footprint optimization: this info can be extracted from
	 * intervals[]  
	 */
	private RealInterval intervals[]; // multidimensional interval is a vector of intervals 
	private RealInterval functionValue;
	
	/*
	 * Creates a square box where each side is equals to @value@ 
	 */
	public Box(int dimension, RealInterval value) { 
		if (dimension < 1)
			throw new IllegalArgumentException("Creating a box with dimension < 1");
		//dim = dimension;
		functionValue = new RealInterval(); // [-inf, +inf]
		intervals = new RealInterval[dimension];
		
		for (int i = 0; i < dimension; i++) {
			intervals[i] = (RealInterval)value.clone(); // make enough copies of interval specified and add them in the list  
		}
	}

	/*
	 * Creates a degenerate (thin) box, each side is a thin interval with value of corresponded @point[i]@ 
	 */
	public Box(double[] point) {
		functionValue = new RealInterval(); // [-inf, +inf]
		int dim = point.length;
		intervals = new RealInterval[dim];
		
		for (int i = 0; i < dim; i++) {
			intervals[i] = new RealInterval(point[i]);  
		}		
	}

	/*
	 * protected internal constructor. used in clone()
	 */
	protected Box(int dimension) {
		//dim = dimension;
		intervals = new RealInterval[dimension];
	}
	public int getDimension() {
		//return dim;
		return intervals.length;
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
		StringBuilder str = new StringBuilder();
		int dim = this.getDimension();
		for (int i = 0; i < dim; i++) {
			str.append(intervals[i].toString());
			if (i != dim - 1)
				str.append(" x ");
		}
		return str.toString();		
	}
	public String toStringValue() {
		return functionValue.toString();
	}

	@Override
	public Box clone() {
		int dim = this.getDimension();
		Box b = new Box(dim);
		
		for (int i = 0; i < dim; i++) {
			b.intervals[i] = (RealInterval)intervals[i].clone();
		}
		

		b.functionValue = (RealInterval)functionValue.clone();
		
		return b;
	}
	
	// as far as we implementing custom equals
	// we need to implement hashCode as well
	@Override
	public int hashCode() {
		// not very fast implementation
		// but simple. 
		// Anyway it uses the same fields as
		// equals() do, so it is correct.
		// TODO: rewrite on something like
		//        int hash = 37;
		//        hash = hash*17 + areaCode;
		//        hash = hash*17 + number;
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object thata) {
	    //check for self-comparison
	    if ( this == thata ) return true;

	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explicit check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
	    if ( !(thata instanceof Box) ) return false;
	    //Alternative to the above line :
	    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

	    //cast to native object is now safe
	    Box that = (Box)thata;

	    //now a proper field-by-field evaluation can be made
	    if (this.getDimension() != that.getDimension())
	    	return false;
	    if (! this.functionValue.equals(that.functionValue) )
	    	return false;
	    for (int i = getDimension()-1; i >= 0; i--) {
	    	if (!getInterval(i).equals(that.getInterval(i)))
	    		return false;
	    }
	    return true;
	}
	
	private static final RealInterval unset = new RealInterval(); 
	public Box[] splitSide(int sideNum, double proportion) {
		if (proportion != 0.5)
			throw new NotImplementedException();
		
		Box one = this.clone();
		Box two = this.clone();
		
		one.setFunctionValue(unset); // Flash function values.
		two.setFunctionValue(unset); // On new sub-boxes it is not calculated yet.
		
		RealInterval side = this.getInterval(sideNum);
		double cutPoint = side.wid() / 2 + side.lo();
		RealInterval left  = new RealInterval( side.lo(),  cutPoint);
		RealInterval right = new RealInterval( cutPoint, side.hi() );
		
		one.setInterval(sideNum, left);
		two.setInterval(sideNum, right); //

		Box result[] = {one, two};
		
		//updateHistory(result);
		return result;		
	}

	public double wid() {
	    double w = 0;
	    for (int i = getDimension()-1; i >= 0; i--)
	    	w += intervals[i].wid();
	    return w;
	}
}
