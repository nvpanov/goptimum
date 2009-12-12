import java.util.ArrayList;


public abstract class WorkList {
	abstract void add(ArrayList<Box> boxes);
	abstract void add(Box box);
	abstract Box getCurrentBest();
	abstract ArrayList<Box> getOptimum();
	
	public WorkList(Box area) {
		list = new ArrayList<Box>();
		list.add(area);
	}
	protected ArrayList<Box> list;
}
