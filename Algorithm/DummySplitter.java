package Algorithm;

/*
 * Just a stub. Do nothing.
 */
public class DummySplitter extends Splitter {

	@Override
	public Box[] splitIt(Box box) {
		Box subBoxes[] = new Box[1];
		subBoxes[0] = box;
		
		return subBoxes;
	}

}
