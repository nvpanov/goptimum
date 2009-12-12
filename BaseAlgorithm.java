import java.util.ArrayList;
import java.util.Iterator;


public class BaseAlgorithm implements Algorithm {

	private Chooser chooser;    // will somehow select next victim to be cut
	private Splitter splitter;  // will somehow cut the box selected by the Chooser 
	private Function targetFunction; // the function which optimum we are searching for 
	private WorkList workList;  // will somehow maintains the list of subboxes  
	
	// Stop criterion 
	// TODO: other criterion has to be added! 
	private int maxIterations = (int) 1e3; // do not more than 10^3 iterations
	private int iterations = 0; // iteration counter. required for maxIterations stop criteria 
	
	
	/*
	 * Construct an instance of the algorithm. 
	 * To crate another algorithm with different behavior you need to change only this constructor.
	 * The behavior of the method depends on how workList, chooser and splitter works.
	 */
	public BaseAlgorithm(Box area, Function f) {
		workList = new UnSortedWorkList(area);
		chooser  = new CurrentBestChooser(workList);
		splitter = new RndSideEquallySplitter();
		targetFunction = f;
	}
	/*
	 * the main method to be called for solving the optimization problem
	 * (actually the only public method yet)
	 * @see Algorithm#solve()
	 * returns a vector because a function can have more than one global optimum
	 */
	public ArrayList<Box> solve() {
		Box workBox;
		Box[] newBoxes;
		
		do {
			workBox = chooser.extractNext();
			newBoxes = splitter.splitIt(workBox);
			calculateIntervalExtensions(newBoxes);
			workList.add(newBoxes);
		} while (isDone(workBox) == false);
		
		return workList.getOptimum();
	}

	/*
	 * this function decides when it is enough.
	 * it could be separated in another class but as far as the functionality is quite simple let it be here as a function
	 */
	private boolean isDone(Box workBox) {
		// TODO Add more stop options
		if (iterations++ > maxIterations)
			return true;
		
		return false;
	}

	/*
	 * recalculate function value on new sub-boxes
	 */
	private void calculateIntervalExtensions(Box[] newBoxes) {
	    for (int i = 0; i < newBoxes.length; i++) {
	    	targetFunction.calculateF( newBoxes[i] );
	    }
	}
}
