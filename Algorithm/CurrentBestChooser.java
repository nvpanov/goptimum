package Algorithm;
import net.sourceforge.interval.ia_math.IAException;


/*
 * Basic implementation of the Chooser
 * Choose the current best box
 */

public class CurrentBestChooser extends Chooser {

	public CurrentBestChooser(WorkList list) {
		super(list);
	}

	public CurrentBestChooser() {
		super();
	}

	@Override
	public Box extractNext() {
		if (list.getClass() == UnSortedWorkList.class) {
			double lowestBorder = Double.MAX_VALUE;
			int pos = -1;

			// found current best
			for (int i = 0; i < list.size(); i++) {
		    	Box b = list.getButLeftInTheList(i);
				if (lowestBorder >  b.getFunctionValue().lo() ) {
					lowestBorder = b.getFunctionValue().lo();
					pos = i;
				}
			}
			// return it
			return list.extract(pos);
		} 
			throw new IAException("NOT IMPLEMENTED!");
	}
	
	@Override
	public CurrentBestChooser clone() {
		CurrentBestChooser clone = new CurrentBestChooser(null);
		clone.list = list.clone();
		return clone;
	}

}
