import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.interval.ia_math.RealInterval;


public class GOptimum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Function f = new TestFunction1();
		Box area = new Box(3, new RealInterval(-1, 1) );
		Algorithm algo = new BaseAlgorithm(area, f);
		ArrayList<Box> optimums = algo.solve(); // could be more than one global optimum 
	    
		Iterator<Box> i = optimums.iterator();
	    while (i.hasNext()) {
	    	System.out.println( i.next() );
	    }
		

	}

}
