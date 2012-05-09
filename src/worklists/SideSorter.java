package worklists;

import java.util.Comparator;

import net.sourceforge.interval.ia_math.RealInterval;

import core.Box;

public class SideSorter implements Comparator<Box> {
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
