package choosers;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import worklists.WorkList;
import core.Box;


public abstract class Chooser {
	/*
	 * Chooser choose boxes from a WorkList
	 */
	protected WorkList list;

	/*
	 * the main functionality -- returns a box to be split next time
	 * has to be implemented in particular behavior.  
	 */
	abstract public Box extractNext();
	
	protected Chooser(WorkList wl) {
		list = wl;
		list.setChooser(this);
	}
/*	public void setWorkList(WorkList wl) {
		list = wl;
	}
*/
	protected Box getBoxWithLowestBorder() {
		/* This method should be implemented in a protected subclass 
		 * that knows about internal structure of the list and really 
		 * make choosing
		 */
		throw new NotImplementedException();
	}
}
