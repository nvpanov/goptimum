package Algorithm;
import java.util.ArrayList;
import java.util.Random;


public abstract class WorkList implements Cloneable {
	
	abstract ArrayList<Box> getOptimum();

	public WorkList(Box area) {
		list = new ArrayList<Box>();
		list.add(area);
	}

	abstract void add(Box[] newBoxes);
	abstract void add(Box box);
	
	public Box getButLeftInTheList(int i) {
		return list.get(i);
	}
	
	@Override
	public WorkList clone(){
		return null;	
	}
	public int size() {
		return list.size();
	}
	public void remove(int pos) {
		list.remove(pos);		
	}

	protected ArrayList<Box> list;

	public Box extract(int n) {
		return list.remove(n);
	}
	
}
