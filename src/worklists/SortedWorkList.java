package worklists;

import java.util.Comparator;
import java.util.TreeSet;

import core.Box;

public class SortedWorkList extends WorkList {

	/*
	 * *static* need only for passing a comporator into a treeset into
	 * the parent constructor.
	 * super() must be the first statement in a constructor
	 * so we have to write inplace new for a TreeSet.
	 * it is OK but we need to specify custom Comparator for
	 * appropriate sorting. And here we can't write 
	 * super(new(new))!!   
	 */
	private final static Comparator<Box> _sorter = new SortedWorkList.AscendingLowBoundBoxComparator();
	
	public SortedWorkList() {
		super(new TreeSet<Box>(_sorter), null);
	}
	public SortedWorkList(Box area) {
		super(new TreeSet<Box>(_sorter), area);
	}

	@Override
	protected void add_checked(Box box) {
		// TreeSet maintains sorting
		// automatically via our comparator
		collection.add(box);
	}

	@Override
	public Box getLeadingBox() {
		// we do know that it is a TreeSet
		//Box b = ((TreeSet<Box>)collection).first();
		Box b = collection.iterator().next(); // first element in a sorted list
		assert(collection.contains(b));
		return b;
	}
	
	@Override
	protected Box extractInternal(int n) {
		// just a reminder for other list developers
		// that there is such method and that it could be
		// implemented for some collections in more effective way.
		// but not for Set. So, default implementation
		return super.extractInternal(n);
	}
	
	
	
	
	//////// support Comparator class
	protected static class AscendingLowBoundBoxComparator implements Comparator<Box> {
	    /**
	     * Compare two Boxes for order.
	     * @return -1 if box1 has lower lo bound of function value than box2,
	                1 otherwise,
	                0 is returned ONLY IF BOX ARE EQUALS!!
	                otherwise TreeSet will treat all such boxes as equal items
	                and will keep only one of them!!!!!!!!!!  
	     */

		@Override
	    public int compare(Box b1, Box b2) {
			assert(b1.getDimension() == b2.getDimension());
			if (b1 == b2)
				return 0;
	    	double lo1 = b1.getFunctionValue().lo();
	    	double lo2 = b2.getFunctionValue().lo();
	    	if (lo1 == lo2) {
	    		if (b1.equals(b2)) 
	    			return 0;
		    	double hi1 = b1.getFunctionValue().hi();
		    	double hi2 = b2.getFunctionValue().hi();
		    	if (hi1 == hi2) {
		    		double sumWid1 = 0, sumWid2 = 0;
		    		for (int i = b1.getDimension()-1; i >= 0; i--) {
		    			sumWid1 += b1.getInterval(i).wid();
		    			sumWid2 += b2.getInterval(i).wid();
		    		}
		    		if (sumWid1 == sumWid2) { // ok. everything is absolutely equals, but these boxes are different. lets distinguish them somehow 
			    		for (int i = b1.getDimension()-1; i >= 0; i--) {
			    			if (b1.getInterval(i).lo() < b2.getInterval(i).lo() ||
			    				b1.getInterval(i).hi() < b2.getInterval(i).hi() )
			    					return -1;
			    			if (b1.getInterval(i).lo() > b2.getInterval(i).lo() ||
				    			b1.getInterval(i).hi() > b2.getInterval(i).hi() )
				    				return 1;
			    		}
		    		} else
		    			return sumWid1 > sumWid2 ? -1 : 1; // wider boxes goes first
		    	} else
		    		return hi1 > hi2 ? -1 : 1; // offer boxes with wider function estimation first 
	    	}
	        return (lo1 < lo2) ? -1 : 1;
	    }
	}



}
