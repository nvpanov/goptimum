package worklists;


import java.util.ArrayList;

import core.Box;

public class UnSortedWorkList extends WorkList {
	public UnSortedWorkList(Box area) {
		super(new ArrayList<Box>(), area);
	}
	public UnSortedWorkList() {
		super(new ArrayList<Box>(), null);
	}

	@Override
	public void addChecked(Box box) {
		// just add it to the list.
		// no sorting - nothing to bother about
		collection.add(box);
	}

	@Override
	protected Box extractInternal(int n) {
		// we do know that it uses ArrayList
		return ((ArrayList<Box>)collection).remove(n);
	}
	@Override
	protected Box getLeadingBoxInternal() {
		double lowest = Double.MAX_VALUE;
		Box leader = null;
		for (Box b : collection) {
			double lo = b.getFunctionValue().lo();
			if (lo < lowest) {
				leader = b;
				lowest = lo;				
			}
		}
		return leader;
	}

}

