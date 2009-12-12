
public abstract class Chooser {
	/*
	 * the main functionality -- returns a box to be split next time
	 * has to be implemented in particular behavior.  
	 */
	abstract public Box extractNext(); 
	
	private WorkList list;
}
