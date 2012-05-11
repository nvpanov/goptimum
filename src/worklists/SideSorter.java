package worklists;

import java.util.Comparator;

import net.sourceforge.interval.ia_math.RealInterval;

import core.Box;

class SideSorter implements Comparator<Box> {
	private final static AscendingLowBoundBoxComparator defaultComparator = 
			new AscendingLowBoundBoxComparator();
	private final int sideNumToSort;
	public SideSorter(int sideNum) {
		sideNumToSort = sideNum;
	}
	@Override
	public int compare(Box b1, Box b2) {
		assert (b1.getDimension() == b2.getDimension());
		RealInterval r1 = b1.getInterval(sideNumToSort);
		RealInterval r2 = b2.getInterval(sideNumToSort);
		if (r1.lo() != r2.lo())
			return (int)Math.signum(r1.lo() - r2.lo());
		if (r1.hi() != r2.hi())
			return (int)Math.signum(r1.hi() - r2.hi());
		// the boxes are equal in interesting dimension
		// return just some order
		return defaultComparator.compare(b1, b2);
	}
}

/*
 * for more beautiful output of search area
 */
class AllSidesSorter implements Comparator<Box> {
	@Override
	public int compare(Box b1, Box b2) {
		final int dim = b1.getDimension();
		assert (dim == b2.getDimension());
		for (int i = 0; i < dim; i++) {
			RealInterval r1 = b1.getInterval(i);
			RealInterval r2 = b2.getInterval(i);
			if (r1.lo() != r2.lo())
				return (int)Math.signum(r1.lo() - r2.lo());
			if (r1.hi() != r2.hi())
				return (int)Math.signum(r1.hi() - r2.hi());
		}
		RealInterval f1 = b1.getFunctionValue(), f2 = b2.getFunctionValue();
		if (f1.lo() != f2.lo())
			return (int)Math.signum(f1.lo() - f2.lo());
		return (int)Math.signum(f1.hi() - f2.hi());
	}
}
