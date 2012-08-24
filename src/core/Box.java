package core;
import java.util.ArrayList;

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
	 * when we don't know all the intervals 
	 * also it is used used in clone()
	 */
	public Box(int dimension) {
		//dim = dimension;
		functionValue = new RealInterval();
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
	public void setInterval(int n, double val) {
		RealInterval iVal = new RealInterval(val);
		setInterval(n, iVal);		
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
		
		RealInterval side = this.getInterval(sideNum);
		double cutPoint = side.wid() / 2 + side.lo();

		return splitSideByPoint(sideNum, cutPoint);
	}
	private Box[] splitSideByPoint(int sideNum, double cutPoint) {
		RealInterval side = getInterval(sideNum);
		assert (side.contains(cutPoint));

		Box one = this.clone();
		Box two = this.clone();
		one.setFunctionValue(unset); // Flush function values.
		two.setFunctionValue(unset); // On new sub-boxes it is not calculated yet.

		RealInterval left  = new RealInterval( side.lo(),  cutPoint );
		RealInterval right = new RealInterval( cutPoint, side.hi()  );

		one.setInterval(sideNum, left);
		two.setInterval(sideNum, right);

		Box result[] = {one, two};
		return result;		
	}

	/*
	 * returns true if this box has at least one common edge point
	 * with @box@. Used in screening by derivative in @Screener@ 
	 */
	public boolean hasAtLeastOneCommonSide(Box box) {
		final int dim = getDimension();
		assert (dim == box.getDimension());
		for (int i = 0; i < dim; i++) {
			RealInterval a = this.getInterval(i);
			RealInterval b = box.getInterval(i);
			if (a.isIntersects(b))
				return true;
		}
		return false;
	}

	/*
	 * returns true if the box arguments (not function) contains this point 
	 */
	public boolean contains(double... point) {
		final int dim = getDimension();
		assert dim == point.length : "Dimension mismatch"; 
		for (int i = 0; i < dim; i++) {
			if ( !getInterval(i).contains(point[i]) )
				return false;
		}
		return true;
	}

	/*
	 * in some cases we could suspect that some point inside the box is an optimum
	 * that we are looking for. (For example when a point algorithm found it).
	 * Than we want to check this. To do this we cut the box on a small box
	 * around this point and everything else
	 */
	public Box[] cutOutBoxAroundThisPoint(double[] potentialOptPoint) {
		final double epsilon = 5e-4;
		
		final int dim = getDimension();
		assert(potentialOptPoint.length == dim);
		if (!this.contains(potentialOptPoint)) {
			// probably it is due to rounding error and it is close
			// save original point for diagnostic
			double[] origPoint = potentialOptPoint.clone();
			if (setToClosestAreaPoint(potentialOptPoint) < epsilon * dim) {
				// let it be "close enough"
				// continue
			} else {
				Box arr[] = new Box[1];
				arr[0] = this;
				// possible bug. Lets fail in debug. See origPoint[].
				assert (false); 
				return arr;
			}
		}
		ArrayList<Box> parts = new ArrayList<>();
		Box boxOfInterest = this;
		Box boxes[] = null;
		for (int i = 0; i < dim; i++) {
			double cutPoint = potentialOptPoint[i] - epsilon;
			if ( boxOfInterest.getInterval(i).contains(cutPoint) ) {
				boxes = boxOfInterest.splitSideByPoint(i, cutPoint);
				assert (boxes[1].contains(potentialOptPoint)); // "our" box is right
				parts.add(boxes[0]);
				boxOfInterest = boxes[1]; 
			}
			cutPoint = potentialOptPoint[i] + epsilon; // <
			if ( boxOfInterest.getInterval(i).contains(cutPoint) ) {
				boxes = boxOfInterest.splitSideByPoint(i, cutPoint);
				assert (boxes[0].contains(potentialOptPoint)); // "our" box is left now
				parts.add(boxes[1]);
				boxOfInterest = boxes[0]; 
			}			
		}
		assert (boxOfInterest.contains(potentialOptPoint));
		parts.add(boxOfInterest);
		return parts.toArray(new Box[parts.size()]);
	}
	
	/*
	 * if the point is outside of tha AREA (not function!)
	 * of the box it will change it to the nearest border point:
	 *      ____      ____
	 *     |    | => |    |
	 *     |____|    |__._|
	 * point->.         ^-point
	 * 
	 * returns the distance between original point and new one  
	 */     
	public double setToClosestAreaPoint(double[] point) {
		double distance = 0;
		final int dim = getDimension();
		assert(point.length == dim);
		
		for (int i = 0; i < dim; i++) {
			RealInterval ii = getInterval(i);
			if (ii.lo() > point[i]) {
				distance += Math.abs(point[i] - ii.lo());
				point[i] = ii.lo();
			} 
			else if (ii.hi() < point[i]) {
				distance += Math.abs(point[i] - ii.hi());
				point[i] = ii.hi();
			}
		}
		return distance;
	}
	

}
