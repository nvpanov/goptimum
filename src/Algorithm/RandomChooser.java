package Algorithm;
import java.util.Random;

/**
 * 
 */

public class RandomChooser extends Chooser {
	
	private Random random;
	
	public RandomChooser(WorkList list) {
		super(list);
		random = new Random();
	}
	
	public RandomChooser() {
		super();
		random = new Random();
	}

	@Override
	public Box extractNext() {
		int n = random.nextInt(list.size() );
		return list.extract(n);
	}
	
	@Override
	public RandomChooser clone() {
		RandomChooser clone = new RandomChooser(null);
		clone.list = list.clone();
		return clone;
	}

}
