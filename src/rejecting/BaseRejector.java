package rejecting;

import core.Box;

/**
 * @author nvpanov
 * Internal class used as a base class for all simple (i.e. single criteria) rejectors.
 */

public abstract class BaseRejector {
	
	public abstract boolean checkPassed(Box box);
	
	protected boolean isBorder(Box box) {
		for (int i = box.getDimension()-1; i >= 0; --i) {
			if (box.getInterval(i).wid() == 0) // A workaround for edges. Worklist adds zero-width
												// edges for initial search area. 
												// See Worklist.addAreaAndAllEges()
				return true;
		}
		return false;			
	}

}
