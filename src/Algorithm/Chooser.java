package Algorithm;
import net.sourceforge.interval.ia_math.IAException;

public abstract class Chooser implements Cloneable {
	/*
	 * the main functionality -- returns a box to be split next time
	 * has to be implemented in particular behavior.  
	 */
	abstract public Box extractNext(); 
	
	public Chooser(WorkList list) {
		this.list = list;
	}

	public Chooser() {
		// sometimes it is more useful to create an empty chooser and than attach it to the list by setWorkList()
	}

	public void setWorkList(WorkList wl) {
		if (list != null) {
			throw new IAException("Wrong usage. It is not supposed to change the list on the fly.");
		}
		list = wl;
	}
	public void setWorkList(WorkList wl, boolean force) {
		if (force)
			list = wl;
		else setWorkList(wl);
	}	
	
	@Override
	public Chooser clone() {
		return null;
	}
	
	protected WorkList list;
}
