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
}