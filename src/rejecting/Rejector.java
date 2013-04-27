package rejecting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import core.Box;
import functions.Function;

/*
 * Main class in Rejecting package. Actually the only one which is public.
 * Basically just a container for simple rejectors
 */
public class Rejector {
	/**
	 * list of all actual rejectors 
	 */
	List<BaseRejector> rejectors = new LinkedList<>();
	
	/**
	 * profiling support
	 */
	private boolean profilingLight;
	private boolean profilingFull;
	private HashMap<BaseRejector, ProfilingDataRejectorsTimes> profilingTimes;
	private HashMap<ProfilingDataRejectedByMatrix, Integer> profilingRejectedByStatistic;

	/**
	 * links for some rejectors 
	 */
	private RejectorByValue rejectorByValue;
	private RejectorByFirstDerivative rejectorByFirstDerivative;
	private RejectorBySecondDerivative rejectorBySecondDerivative;
	private RejectorConstraintPropogation rejectorConstraintPropogation;
		
	public Rejector() {
		rejectorByValue = new RejectorByValue();
		rejectorByFirstDerivative = new RejectorByFirstDerivative();
		rejectorBySecondDerivative = new RejectorBySecondDerivative();
		rejectorConstraintPropogation = new RejectorConstraintPropogation();
		// the order of adding is important. the rejectors will be called in this order,
		// so more effective rejectors should go first.
		add(rejectorByValue);
		add(rejectorByFirstDerivative);
		add(rejectorBySecondDerivative);
		add(rejectorConstraintPropogation);
	}
	
	public void init(Function f) {
		rejectorConstraintPropogation.init(f);
	}

	private void add(BaseRejector nextRejector) {
		rejectors.add(nextRejector);
	}

	public void setFullProfiling() {
		setProfiling(true);
	}
	public void setLightProfiling() {
		setProfiling(false);
	}
	private void setProfiling(boolean full) {
		profilingLight = !full;
		profilingFull  =  full;
		profilingTimes = new HashMap<>();
		for (BaseRejector r : rejectors) {
			profilingTimes.put(r, new ProfilingDataRejectorsTimes());
		}
		profilingRejectedByStatistic = new HashMap<>();
	}

	public boolean checkPassed(Box box) {
		if (profilingTimes != null)
			return checkWithProfiling(box);
		return checkNoProfiling(box);
	}
	
	private boolean checkNoProfiling(Box box) {
		for (BaseRejector nextRejector : rejectors) {
			if (!nextRejector.checkPassed(box)) {
				return false;
			}
		}
		return true;
	}
	private boolean checkWithProfiling(Box box) {
		long time;
		boolean passed = true;
		ProfilingDataRejectedByMatrix rejectedBy = new ProfilingDataRejectedByMatrix(rejectors.size());

		
		for (BaseRejector nextRejector : rejectors) {

			time = System.nanoTime();
			passed &= nextRejector.checkPassed(box);
			time = System.nanoTime() - time;
			
			ProfilingDataRejectorsTimes record = new ProfilingDataRejectorsTimes(time, passed);
			profilingTimes.get(nextRejector).add(record);
			
			if (!passed && profilingLight) {				
				return false;
			}
			rejectedBy.recordRejectionResult(passed);			
		}
		if (profilingFull) {
			Integer numberOfSuchResults = profilingRejectedByStatistic.get(rejectedBy);
			if (numberOfSuchResults == null) {
				numberOfSuchResults = 0;
			}
			profilingRejectedByStatistic.put(rejectedBy, ++numberOfSuchResults);
		}
		return passed;
	}

	public boolean checkByValue(Box b) {
		return rejectorByValue.checkByValue(b);
	}
	public double getLowBoundMaxValue() {
		return rejectorByValue.getLowBoundMaxValue();
	}
	
	
	public String getProfilingData() {
		if (!(profilingFull || profilingLight)) {
			return "Profiling was turned off.";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("----- Rejection stats ");
		if (profilingFull) sb.append("FULL ");
		sb.append("{\n");
		
		for (BaseRejector r : rejectors) {
			ProfilingDataRejectorsTimes stats = profilingTimes.get(r);
			double totalTimeMsKept = stats.nanoTimeBoxKept/100;
			double totalTimeMsRejected = stats.nanoTimeBoxRejected/100;
			sb.append(r.toString() + ":");
			sb.append("\n\tTotal time:\t" + totalTimeMsKept + totalTimeMsRejected);
			sb.append("\n\tTotal usefull time:\t" + totalTimeMsRejected);
			sb.append("\t("+ 100.0*totalTimeMsRejected/(totalTimeMsKept+totalTimeMsRejected) + "%)");
			sb.append("\n\tTotal rejections:\t" + stats.boxRejectedCount);
			sb.append("\t(" + 100.0*stats.boxRejectedCount/(stats.boxKeptCount+stats.boxRejectedCount) + "%)");
		}
		sb.append("======\t");
		for (ProfilingDataRejectedByMatrix rejectedBy : profilingRejectedByStatistic.keySet()) {
			for (int i = 0; i < rejectors.size(); i++) {
				if (rejectedBy.rejectedBy[i]) {
					sb.append(rejectors.get(i).toString() + "\t");
				}
			}
			sb.append(profilingRejectedByStatistic.get(rejectedBy));
		}
     	sb.append("}\n");
		return sb.toString().replaceAll("\n", "\n\t");
	}

	public boolean probeNewLimit(double possibleNewMax) {
		return rejectorByValue.probeNewLimit(possibleNewMax);
	}

	public void reset(double threshold) {
		rejectorByValue.setLowBoundMaxValue(threshold);
		this.reset();
	}
	public void reset() {
		rejectorByValue.resetStatistics();
	}
	// for tests
	public void switchOffDerivativesCheck() {
		rejectorByFirstDerivative.switchOff();
		rejectorBySecondDerivative.switchOff();
	}
}

