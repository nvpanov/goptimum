package worklists;

import java.util.Comparator;
import java.util.TreeSet;
import net.sourceforge.interval.ia_math.RMath;
import net.sourceforge.interval.ia_math.RealInterval;
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
	private final static Comparator<Box> _sorter = new AscendingLowBoundBoxComparator();

	public SortedWorkList() {
		super(new TreeSet<Box>(_sorter), null);
	}
	public SortedWorkList(Box area) {
		super(new TreeSet<Box>(_sorter), area);
	}

	@Override
	protected void addChecked(Box box) {
		// TreeSet maintains sorting
		// automatically via our comparator
		collection.add(box);
	}

	@Override
	protected Box getLeadingBoxInternal() {
		if (collection.size() == 0)
			return null;
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
	@Override
	public int removeRejectedBoxes() {
		int size = size();
		if (size == 0)
			return 0;
		double threshold = rejector.getLowBoundMaxValue();
		if ( threshold >= Double.MAX_VALUE ) {
			return 0;
		}
		Box leader = getLeadingBoxInternal(); // it can't be null, we already checked the size
		
		// 'remove all' case
		if (!rejector.checkByValue(leader) ) {
			clearAll(threshold);
			return size;
		}
		// otherwise...
		// find where to cut: will remove all boxes witch FunctionValue.lo > threshold.
		Box mark = new Box(leader.getDimension(), new RealInterval());
		double aLittleBitMoreThanThreshold = RMath.add_hi(threshold, 0);
		mark.setFunctionValue(new RealInterval(aLittleBitMoreThanThreshold));
		assert (mark.getFunctionValue().wid() == 0); // just checking that Interval is not making an interval from a number. 
														//	otherwise the lower bound could become lower than needed.
		
		assert (collection instanceof TreeSet<?>);
		((TreeSet<Box>)collection).tailSet(mark).clear();
		
		int newSize = collection.size();
		//17:30,18-15,19-00,19-45
		
		return newSize - size;
	}
}