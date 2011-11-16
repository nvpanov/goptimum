package choosers;

import core.Box;

import worklists.WorkList;


public class CurrentBestChooser extends Chooser {
	
	/*
	 * works with any type of the list
	 */
	public CurrentBestChooser(WorkList list) {
		super(list);
	}
	
	@Override
	public Box extractNext() {		
		Box b = list.getLeadingBox();
		boolean success = list.remove(b);
		assert( (b != null && success) || (b == null && !success));
		return b;
	}
}
