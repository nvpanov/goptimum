package algorithms;

import static algorithms.OptimizationStatus.RUNNING;
import net.sourceforge.interval.ia_math.RealInterval;
//import com.sun.org.apache.bcel.internal.generic.IALOAD;

import choosers.Chooser;
//import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.Function;
import point.PointAlgorithm;
import solvers.IntervalSolver;
import splitters.Splitter;
import worklists.WorkList;

public class IntervalAndPointAlgorithm extends BaseAlgorithm {
	private BaseAlgorithm intervalAlg;
	private PointAlgorithm pointAlg;
	private int iterationCount = 0;
	public IntervalAndPointAlgorithm(IntervalSolver intervalAlg, PointAlgorithm pointAlg) {
		assert(intervalAlg != null && pointAlg != null);
		this.intervalAlg = (BaseAlgorithm)intervalAlg;
		this.pointAlg = pointAlg;		
	}
	
	@Override
	public void solve() {
		OptimizationStatus status;
		do {
			status = intervalAlg.iterate();
			if (worthToRunPoint()) {
				/*
				 * point alg. consumes some time but it isn't always gives any benefits 
				 * (see RosenbrockGn at any symmetric area, like [-100, 100])
				 * so we want to use some heuristics    
				 */
				Box searchAreaForPointAlg = getAreaForPoint();
				double localMin = pointAlg.localMinimum(searchAreaForPointAlg);
				intervalAlg.probeNewLowBoundMaxValue(localMin);
				//intervalAlg.probeNewLowBoundMaxValueAndClean(localMin);
			}
			iterationCount++;
		} while (status == RUNNING);		
	}
	
	private boolean worthToRunPoint() {
		if (iterationCount % 100 == 0)
			return true;
		return false;
		
		/*
		double wid = intervalAlg.workBox.getFunctionValue().wid();
		double loBound = intervalAlg.workBox.getFunctionValue().lo();
		double screeningVal = intervalAlg.getLowBoundMaxValue();

		assert(loBound > Double.NEGATIVE_INFINITY && wid < Double.POSITIVE_INFINITY && loBound < screeningVal);

		if (abs(screeningVal - loBound) < wid / 100)
		*/
	}

	private Box getAreaForPoint() {
		return intervalAlg.workBox;//intervalAlg.workList.getLeadingBox();
	}

	
	
////////////////////////////////////////
////////////////////////////////////////
	@Override
	public void setProblem(Function f, Box area) {
		intervalAlg.setProblem(f, area);
		pointAlg.setFunction(f);
	}
	@Override
	public void setProblem(Function f, Box[] optArea) {
		intervalAlg.setProblem(f, optArea);
	}
	@Override
	protected void setLogic(WorkList wl, Chooser ch, Splitter sp) {
		intervalAlg.setLogic(wl, ch, sp);
	}
	@Override
	public RealInterval getOptimumValue() {
		return intervalAlg.getOptimumValue();
	}
	@Override
	public Box[] getOptimumArea() {
		return intervalAlg.getOptimumArea();
	}
	@Override
	public void probeNewLowBoundMaxValueAndClean(double localMin) {
		intervalAlg.probeNewLowBoundMaxValueAndClean(localMin);
	}
	@Override
	public double getPrecision(){
		return intervalAlg.getPrecision();
	}
	@Override
	public void setPrecision(double pres){
		intervalAlg.setPrecision(pres);
	}
	
	@Override
	public String toString() {
		return intervalAlg.toString() + "+" + pointAlg.toString();
	}
}
