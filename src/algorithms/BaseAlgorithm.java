package algorithms;

import net.sourceforge.interval.ia_math.RealInterval;
import choosers.Chooser;
import splitters.Splitter;
import worklists.WorkList;
import core.Box;
import functions.FunctionNEW;
import static algorithms.OptimizationStatus.*;

public class BaseAlgorithm implements Algorithm {

	protected Chooser chooser;    // will somehow select next victim to be cut
	protected Splitter splitter;  // will somehow cut the box selected by the Chooser 
	protected FunctionNEW targetFunction; // the function which optimum we are searching for 
	protected WorkList workList;  // will somehow maintains the list of subboxes  
	protected StopCriterion stopCriterion; // decides when it is enough.

	protected boolean logging = false;
	
	protected Box workBox;

	
	/*
	 * Construct an instance of the algorithm. 
	 * The behavior of the alg. depends on how workList, 
	 * chooser and splitter works.
	 * BaseAlgorithm can't be instantiated -- its just a
	 * container for common functionality so 
	 * it has only protected constructors
	 */
	
	// algorithm will setup everything by itself  
	protected BaseAlgorithm() {
		stopCriterion = new StopCriterion(this);
	}
/*	
	// a copy-constructor.
	// should be used instead of @clone@
	public BaseAlgorithm(BaseAlgorithm original) {
		this.chooser = new Chooser(original.chooser);
		this.logging = original.logging;
		this.splitter = new Splitter(original.splitter);
		this.stopCriterion = new StopCriterion(original.stopCriterion);
		this.targetFunction = new Function(original.targetFunction);
		this.workList = new WorkList(original.workList);
	}
*/	
	

	/*	
	// full initialization for other cases 
	protected BaseAlgorithm(Function f, WorkList wl, Chooser ch, Splitter sp) {
		stopCriterion = new StopCriterion();
		targetFunction = f;
		setLogic(wl, ch, sp);
	}
*/ 
/*	
	// setup functions
	public void setArea(Box area) {
		if (workList.size() != 0)
			throw new IllegalArgumentException("setArea called while the worklist is not empty");
		workList.add(area);
	}
*/	
	@Override
	public void setProblem(FunctionNEW f, Box area) {
		if (workList.size() != 0) { // we were solving other problem and got this one.
			workList.clearAll(); 
			stopCriterion.reset();
		}
		workList.addSearchArea(area);
		targetFunction = f;
	}
/*	
	public void setProblem(Function f, Box[] optArea) {
		if (targetFunction != null || workList == null || workList.size() != 0)
			throw new IllegalArgumentException("Algorithm is in inappropriate state " +
					"for this initialization");
		targetFunction = f;
		workList.addSearchArea(optArea);
	}
*/	
	
	// this function is used to set particular behavior of the algorithm.  
	protected void setLogic(WorkList wl, Chooser ch, Splitter sp) {
		assert(wl.size() == 0);
		workList = wl;
		chooser  = ch;
		splitter = sp;
	}

	/*
	 * the main method to be called for solving an optimization problem
	 * @see Algorithm#solve()
	 * One should call getOptimumValue() or getOptimumArea()
	 * to get minimum value or its arguments.
	 */
	public void solve() {
		OptimizationStatus status;
		do {
			status = iterate();
		} while (status == RUNNING);
	}

	protected OptimizationStatus iterate() {
		Box[] newBoxes;

		if (logging)
			System.out.println("WorkList size = " + workList.size());
		workBox = workList.extractNext();
		if (logging)
			System.out.println(workBox + " => ");
		if (workBox == null)
			return EMPTY_WORKLIST;
		
		newBoxes = splitter.splitIt(workBox);
		assert(newBoxes.length > 1);
		
		calculateIntervalExtensions(newBoxes);
		if (logging)
			for (Box b : newBoxes)
				System.out.println("  => " + b);

		workList.add(newBoxes);

		if ( isDone(workBox) )
			return STOP_CRITERION_SATISFIED;
		return RUNNING;
	}	
	
	public RealInterval getOptimumValue() {
		return workList.getOptimumValue();
	}
	public Box[] getOptimumArea() {
		return workList.getOptimumArea();
	}

	public void probeNewLowBoundMaxValueAndDoNotClean(double localMin) {
		workList.probeNewLowBoundMaxValueAndDoNotClean(localMin);
	}
	public void probeNewLowBoundMaxValueAndClean(double localMin) {
		workList.probeNewLowBoundMaxValueAndClean(localMin);
	}
	public void probeNewLowBoundMaxValue(double localMin) {
		workList.probeNewLowBoundMaxValue(localMin);
	}
	
	public double getLowBoundMaxValue() {
		return workList.getLowBoundMaxValue();
	}	

	/*
	 * this function decides when it is enough.
	 * separated in another class.
	 */
	protected boolean isDone(Box workBox) {
		return stopCriterion.isDone(workBox);
	}

	/*
	 * recalculate function value on new sub-boxes
	 */
	protected void calculateIntervalExtensions(Box[] newBoxes) {
	    for (int i = 0; i < newBoxes.length; i++) {
	    	targetFunction.calculate( newBoxes[i] );
	    }
	}

	public double getPrecision(){
		return stopCriterion.getFMaxPrecision();
	}
	public void setPrecision(double pres){
		stopCriterion.setFMaxPrecision(pres);
	}


	public String toString() {
		String fullName = this.getClass().getName();
		return fullName.substring(fullName.lastIndexOf('.')+1); // removes packages
	}
}
