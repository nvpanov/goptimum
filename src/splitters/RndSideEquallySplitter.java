package splitters;

import java.util.Random;

import core.Box;


public class RndSideEquallySplitter extends Splitter {

	private Random rnd = new Random();
	
	@Override
	public Box[] splitIt(Box box) {
		int dim = box.getDimension();
		int sideNum = rnd.nextInt(dim);

		return splitSide(box, sideNum);
	}

}
