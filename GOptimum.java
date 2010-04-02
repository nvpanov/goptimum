import java.util.ArrayList;
import java.util.Iterator;

import Algorithm.Algorithm;
import Algorithm.BaseAlgorithm;
import Algorithm.Box;
import TargetFunctions.Function;
import TargetFunctions.Function_Rastrigin10_2D;

import net.sourceforge.interval.ia_math.RealInterval;


public class GOptimum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Function f = new Function_Rastrigin10_2D();
		Box area = new Box(2, new RealInterval(-2, 3) );
		Algorithm algo = new BaseAlgorithm(area, f);
		ArrayList<Box> optimums = algo.solve(); // could be more than one global optimum 
	    
		Iterator<Box> i = optimums.iterator();
	    while (i.hasNext()) {
	    	System.out.println( i.next() );
	    }

	}

}
