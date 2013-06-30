package algorithms;

import net.sourceforge.interval.ia_math.RealInterval;
import choosers.Chooser;
import splitters.Splitter;
import worklists.WorkList;
import core.Box;
import functions.Function;
import static algorithms.OptimizationStatus.*;

public class BaseAlgorithm implements Algorithm {

	protected Chooser chooser;    // will somehow select next victim to be cut
	protected Splitter splitter;  // will somehow cut the box selected by the Chooser 
	protected Function targetFunction; // the function which optimum we are searching for 
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
	public void setProblem(Function f, Box area) {
		_dbg_iterationNum = 0;
		stopCriterion.reset();
		workList.init(area, f);
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
		if (workBox == null) {
			assert (workList.size() == 0);
			return EMPTY_WORKLIST;
		}
		
		newBoxes = splitter.splitIt(workBox);
		assert(newBoxes.length > 1);
		
		calculateIntervalExtensions(newBoxes);
		for (Box b : newBoxes) {
			// new <= old, not care about the last digits
			final double precision = 1e-6;
			assert ((int)(workBox.getFunctionValue().lo()/precision) <= (int)(b.getFunctionValue().lo()/precision));
			assert ((int)(workBox.getFunctionValue().hi()/precision) >= (int)(b.getFunctionValue().hi()/precision));
		}
		
		if (logging)
			for (Box b : newBoxes)
				System.out.println("  => " + b);
		
		workList.add(newBoxes);
		
		/*
		 * uncomment for debug. Will allow to catch when the last box containing optimum was removed from the list.
		 * Also set actual optimum value and optimum area 		
		 */
//*		
		Double checkValue = null;//-1.03163;
		double checkArg[] = new double[] {+0.08984, -0.71266};
		boolean wasLastBox = debug_catchLastOptimaBox(checkValue, checkArg);
		if (wasLastBox) {
			System.out.println("LAST BOX contains optima has been just LOST! (iteration #" + (_dbg_iterationNum-1) + ")");
		}
//*/
		/*
		 * calculated function extensions can contain infinity. what to do in such case?
		 */
		boolean shouldTerminate = processInfinityInExtensions(newBoxes);
		
		if ( shouldTerminate || isDone(workBox) )
			return STOP_CRITERION_SATISFIED;
		return RUNNING;
	}	
	
	private boolean processInfinityInExtensions(Box[] newBoxes) {
	    for (int i = 0; i < newBoxes.length; i++) {
	    	if (extensionContainsInfinity( newBoxes[i] ) ) {
	    		boolean terminationRequired = processBoxWithInfinityInFunctionEstimation(newBoxes[i]);
	    		if (terminationRequired)
	    			return true;
	    	}
	    }
	    return false; // Immediate abort of the iteration is not requested
	}
	private boolean processBoxWithInfinityInFunctionEstimation(Box box) {
		System.out.println("Got INFINITY while evaluating target function (" + targetFunction 
				+ ") on the following area:\n\t" + box.toStringArea() 
				+ "\nCurrent policy means stop computation after this.");
		return true; // true means stop iteration on this.
	}
	private boolean extensionContainsInfinity(Box box) {
		RealInterval extension = box.getFunctionValue();
		if (Double.isInfinite(extension.lo()) || Double.isInfinite(extension.hi()))
			return true;
		return false;
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
	@Override
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
	@Override
	public void setStopCriterion(StopCriterion stopCriterion) {
		this.stopCriterion = stopCriterion;
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


	/*
	 * for @IntervalAndPointAlgorithm@ 
	 * returns current leading box but do not remove it from the work list
	 */
	Box getCurrentLeadingBox() {
		return workList.getLeadingBox();
	}

	/*
	 * If a user suspects something or (more common use case)
	 * if a point algorithm found a local minimum
	 * this method can be used to draw attention to that point
	 * the found local optimum is unreliable due to rounding errors
	 * so we will calculate interval estimation around this point.
	 * Or just ignore this info:)
	 */
	public void giveHint(double[] potentialOptPoint, Box boxFromTheList) {
		final double epsilon = 5e-4;
		final int dim = boxFromTheList.getDimension();
		assert (potentialOptPoint.length == dim);

		//	1. check if the point is inside the box.
		if (!boxFromTheList.contains(potentialOptPoint)) {
			// try to search the list
			Box anotherBoxFromTheList = workList.getBoxContainsThisPoint(potentialOptPoint);
			if (anotherBoxFromTheList == null) {
				// probably it is due to rounding error and it is close
				// save original point for diagnostic
//				double[] _debug_origPoint = potentialOptPoint.clone();
				double distance = boxFromTheList.setToClosestAreaPoint(potentialOptPoint);
				if (distance > epsilon * dim) {
					// possible bug. Lets fail in debug. See _debug_origPoint[].
//					assert false : "distance = " + distance + ", threshold = " + epsilon * dim; 
					return;
				}
			} else 
				boxFromTheList = anotherBoxFromTheList;
		}
		assert (boxFromTheList.getFunctionValue().contains(getLowBoundMaxValue()));
		//	2.	extract this box from the list
		boolean success = workList.remove(boxFromTheList);
		if (!success) {
			assert(success);
			return;
		}
		//	3.	split this box on a small box around this point and everything else
		Box boxes[] = boxFromTheList.cutOutBoxAroundThisPoint(potentialOptPoint);
		assert (boxes.length > 0);
		//  4.0 calc function values -- otherwise, if the algorithm will be stopped before the
		//		function estimation will be calculated in regular order the low bound will be -inf.
		calculateIntervalExtensions(boxes);
		//	4.	add the sub-boxes to the list
		workList.add(boxes);
	}

	@SuppressWarnings("unused")
	private int _dbg_iterationNum = 0;	
	@SuppressWarnings("unused")
	/**
	 * the following block is for debugging.
	 * it allows to catch why last containing optimum box was rejected
	 * @param checkVal -- known optimum value, can be null if unknown
	 * @param checkArg -- known optimum arguments, can be null if unknown
	 * @return true if the work list doesn't contain boxes carry the known optimum
	 */
	private boolean debug_catchLastOptimaBox(Double checkVal, double[] checkArg) {
		_dbg_iterationNum++;
		boolean noMoreOptimumInTheList = false;
		if(checkVal != null && !workList.getOptimumValue().contains(checkVal) ) {
			noMoreOptimumInTheList = true;
		}
		if (checkArg != null && workList.getBoxContainsThisPoint(checkArg) == null) {
			noMoreOptimumInTheList = true;
		}
		return noMoreOptimumInTheList;
	}
}
