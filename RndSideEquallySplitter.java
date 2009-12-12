import java.util.ArrayList;
import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

/*
 * Just a stub. Do nothing.
 */
public class RndSideEquallySplitter extends Splitter {

	private Random rnd = new Random();
	
	public Box[] splitIt(Box box) {
		int dim = box.getDimension();
		int sideNum = rnd.nextInt(dim);

		return splitSide(box, sideNum);
	}

}
