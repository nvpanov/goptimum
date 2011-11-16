package splitters;

import core.Box;

public abstract class Splitter {
	public abstract Box[] splitIt(Box box);
	
	protected static final Box[] splitSide(Box box, int sideNum) {
		return box.splitSide(sideNum, 0.5);
	}
}
