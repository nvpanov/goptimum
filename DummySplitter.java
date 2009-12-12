import java.util.ArrayList;

import net.sourceforge.interval.ia_math.RealInterval;

/*
 * Just a stub. Do nothing.
 */
public class DummySplitter extends Splitter {

	public ArrayList<Box> splitIt(Box box) {
		ArrayList<Box> subBoxes = new ArrayList<Box>();
		
//		int dim = box.getDimension();
		
		
		
		subBoxes.add(box);
		return subBoxes;
	}

}
