import java.util.ArrayList;
import java.util.Iterator;


public class UnSortedWorkList extends WorkList {

	public UnSortedWorkList(Box area) {
		super(area);
	}

	@Override
	public void add(ArrayList<Box> boxes) {
		Iterator<Box> i = boxes.iterator();
	    while (i.hasNext())
	    	add( i.next() );
	}
	
	@Override
	public void add(Box box) {
		list.add(box);
	}

	@Override
	public Box getCurrentBest() {
		double lowestBorder = Double.MAX_VALUE;
		int pos = -1;

		// found current best
		for (int i = 0; i < list.size(); i++) {
	    	Box b = list.get(i);
			if (lowestBorder >  b.getFunctionValue().lo() ) {
				lowestBorder = b.getFunctionValue().lo();
				pos = i;
			}
		}
		// return it
		Box curOptimum = list.get(pos);
		list.remove(pos);
		return curOptimum;
	}

	@Override
	ArrayList<Box> getOptimum() {
		// TODO  hi border could be better 
		ArrayList<Box> tmp = new ArrayList<Box>();
		tmp.add( getCurrentBest() );
		return tmp;
		
	}

}
