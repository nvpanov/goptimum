package worklists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import net.sourceforge.interval.ia_math.RealInterval;

import core.Box;

// not a public class
class IntervalMerger {
	protected ArrayList<Box> boxes; 

	public IntervalMerger(Box[] boxes) {
		assert (boxes.length != 0);
		this.boxes = new ArrayList<>(Arrays.asList(boxes));
	}

	public Box[] merge() {
		int size;
		do {
			size = boxes.size();
			assert (size != 0);
			mergeOnce();
		} while (boxes.size() != size);
		Collections.sort(boxes, new AllSidesSorter()); // just for more nice output
		return boxes.toArray(new Box[0]);
	}

	private void mergeOnce() {
		Box prevBox = null, thisBox = boxes.get(0);
		final int dim = thisBox.getDimension(); 
		for (int side = 0; side < dim; side++) {
			Collections.sort(boxes, new SideSorter(side));
			thisBox = boxes.get(0);
			for (int i = 1; i < boxes.size(); /**/) {
				prevBox = thisBox;
				thisBox = boxes.get(i);
				if (boxesFollowedOneAnother(thisBox, prevBox, side) &&
						allTouchingSidesAreEqual(thisBox, prevBox, side) ) {
					Box newBox = mergeBoxes(thisBox, prevBox, side);
					boxes.add(i+1, newBox);
					boxes.remove(i);
					boxes.remove(i-1);
					thisBox = newBox;
				} else
					i++;
			}
		}
	}

	boolean boxesFollowedOneAnother(Box two, Box one, int side) {
		RealInterval i1 = one.getInterval(side);
		RealInterval i2 = two.getInterval(side);
		if (i1.hi() == i2.lo() || 
				i1.lo() == i2.hi() ) { // this part just in case somebody would call this function
										// with another order of boxes
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if all other sides (except specified int side) are equal.
	 * @param b1 -- first box
	 * @param b2 -- second box
	 * @param side -- number of dimension in which they are consecutive.
	 * @return true if all other sides except specified are the same
	 * 
	 *     ____    ________
	 *  2 /___/|  /_______/| 
	 * 0  |___|/  |_______|/  , side #0 => true, side #1 => false, side #2 => false 
	 *     1
	 */
	boolean allTouchingSidesAreEqual(Box b1, Box b2, int side) {
		final int dim = b1.getDimension();
		for (int i = 0; i < dim; i++) {
			if (i == side) // we don't compare sides which are "not touching". i.e. sides that going to be merged in one. 
				continue;  //   (side #1 in the picture above)
			// all other sides:
			// (the surface which is going to be glued. 
			if ( !b1.getInterval(i).equals( b2.getInterval(i) ) ) {
				return false;
			}
		}
		return true;
	}
	Box mergeBoxes(Box b1, Box b2, int side) {
		Box box = b1.clone();
		box.setFunctionValue(new RealInterval()); // flush function estimation
		RealInterval s1 = b1.getInterval(side);
		RealInterval s2 = b2.getInterval(side);
		double l, h;
		if (s1.lo() < s2.lo()) {
			l = s1.lo();
			h = s2.hi();
		} else {
			l = s2.lo();
			h = s1.hi();
		}
		box.setInterval(side, new RealInterval(l, h));
		return box;
	}
}
