package splitters;

import core.Box;


public class BiggestSideEquallySplitter extends Splitter {

	@Override
	public Box[] splitIt(Box box) {
		int dim = box.getDimension();
		
		// fiend the biggest side
		double w = -1; 
		int pos = -1;
		for (int i = 0; i < dim; i++) {
			// we are using patched IAMath, it has wid() function
			double wid = box.getInterval(i).wid();
			if (wid > w) {
				w = wid;
				pos = i;
			}
		}
		return splitSide(box, pos);
	}
}
