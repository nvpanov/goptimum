package Algorithm;
import java.util.ArrayList;
import java.util.Arrays;

public class UnSortedWorkList extends WorkList {

	public UnSortedWorkList(Box area) {
		super(area);
	}

	@Override
	public void add(Box[] boxes) {
		list.addAll( Arrays.asList(boxes) );
	}
	
	@Override
	public void add(Box box) {
		list.add(box);
	}

	@Override
	ArrayList<Box> getOptimum() {
		// TODO: high border could be better
		// TODO: all boxes are candidates for optimum
		
		//TODO: tmp solution
		ArrayList<Box> tmp = new ArrayList<Box>();
		CurrentBestChooser c = new CurrentBestChooser(this);
		tmp.add( c.extractNext() );
		return tmp;
		
	}
	@Override
	@SuppressWarnings("unchecked")
	public UnSortedWorkList clone(){
		UnSortedWorkList clone = new UnSortedWorkList(null);
		clone.list = (ArrayList<Box>) list.clone();
		
		return clone;		
	}
}

