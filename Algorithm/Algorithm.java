package Algorithm;
import java.util.ArrayList;


public interface Algorithm {
	/*
	 * the main function. finds the global optimum (optimums in case if the target function has more than one global optimum) 
	 */
	public ArrayList<Box> solve();
}