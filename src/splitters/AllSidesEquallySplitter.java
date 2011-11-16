package splitters;

import core.Box;


public class AllSidesEquallySplitter extends Splitter {

	@Override
	public Box[] splitIt(Box box) {
		final int dim = box.getDimension();
		final int numOfSubBoxes = (int)Math.pow(2, dim);
		//Box[] result = new Box[numOfSubBoxes];
		
		Box[] subBoxes = new Box[1];
		subBoxes[0] = box;
	
		Box[] result = splitAllBoxesByOneSide(subBoxes, dim, 0);
		if (result.length != numOfSubBoxes)
			throw new RuntimeException("Internal error in " + this.getClass());
		
		return result;
	}

	private Box[] splitAllBoxesByOneSide(Box[] subBoxes, int dim, int sideNum) {
		if (sideNum == dim)
			return subBoxes;
		Box[] result = new Box[subBoxes.length*2];
		for (int i = 0; i < subBoxes.length; i++) {
			Box[] tmp = splitSide(subBoxes[i], sideNum);
			result[2*i]   = tmp[0];
			result[2*i+1] = tmp[1];			
		}
		subBoxes = result;
		sideNum++;
		return splitAllBoxesByOneSide(subBoxes, dim, sideNum);
	}
}
