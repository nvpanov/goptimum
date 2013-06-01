package splitters;

import java.util.Random;
import core.Box;


public class RndSideEquallySplitter extends Splitter {

	private Random rnd = new Random();
	
	public RndSideEquallySplitter() {
	}
	public RndSideEquallySplitter(long seed) {
		rnd.setSeed(seed);
		System.out.println("RndSideEquallySplitter: set seed = " + seed);
	}	
	
	@Override
	public Box[] splitIt(Box box) {
		int dim = box.getDimension();
		int sideNum = rnd.nextInt(dim);

		return splitSide(box, sideNum);
	}

}
