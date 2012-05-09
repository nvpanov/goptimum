package worklists;

import java.util.Comparator;

import core.Box;

// minimal function estimation goes first 
class AscendingLowBoundBoxComparator implements Comparator<Box> {
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

/*
 * max low-values goes first
 * can be useful when we tries to remove such boxes by value
 */
class DescendingLowBoundBoxComparator extends AscendingLowBoundBoxComparator {
	@Override
    public int compare(Box b1, Box b2) {
		return super.compare(b2, b1);
	}
}

