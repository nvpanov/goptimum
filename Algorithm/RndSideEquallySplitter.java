package Algorithm;
import java.util.Random;

/*
 * Just a stub. Do nothing.
 */
public class RndSideEquallySplitter extends Splitter {

	private Random rnd = new Random();
	
	@Override
	public Box[] splitIt(Box box) {
		int dim = box.getDimension();
		int sideNum = rnd.nextInt(dim);

		return splitSide(box, sideNum);
	}

}
