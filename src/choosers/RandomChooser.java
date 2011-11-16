package choosers;
import java.util.Random;

import core.Box;

import worklists.WorkList;


public class RandomChooser extends Chooser {
	
	private Random random;
	
	/*
	 * works with any type of the list
	 */
	public RandomChooser(WorkList list) {
		super(list);
		random = new Random();
	}
	
	protected int getRndIdx() {
		int size = list.size();
		int n = random.nextInt( size );
		return n;
	}
	@Override
	public Box extractNext() {
		return list.extract(getRndIdx());
	}
}
