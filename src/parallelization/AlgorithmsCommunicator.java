package parallelization;

import static algorithms.OptimizationStatus.*;
import worklists.SortedWorkList;
import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;
import algorithms.OptimizationStatus;
import algorithms.ParallelAlgorithm;


public class AlgorithmsCommunicator extends Thread {
	ParallelAlgorithm algorithms[];
	// executor itself
	ParallelExecutor executor;
	double globalScreeningValue = Double.POSITIVE_INFINITY;

	private SortedWorkList optimumShortList;
	private boolean logging = true;
	private boolean logging2 = false;

	public AlgorithmsCommunicator(ParallelExecutor executor) {
		this.executor = executor;
		algorithms = executor.getAlgorithms();
		
		setPriority(NORM_PRIORITY-2);
		setName("AlgorithmsCommunicator");
		
		optimumShortList = new SortedWorkList();
	}
	
	public void run() {
		communicateAlgorithms();
		if (logging) System.out.println("Communicator: -- Alomost DONE --");
		collectResults();
		for (int i = 0; i < algorithms.length; i++) {
			assert (algorithms[i].getState() == EMPTY_WORKLIST);
		}
		assert (executor.dbg_noLiveThreads());
		prepareFinalResult();
		if (logging) System.out.println("Communicator: -- DONE --");
	}
	
	private void communicateAlgorithms() {
		int failCounter = 0;
		while (failCounter < algorithms.length+1) {
			sleep();
			for (int i = 0; i < algorithms.length; i++) {
				// check if algorithm has finished his work
				OptimizationStatus state = algorithms[i].getState();
				if (logging2) System.out.println("Communicator: Checking alg " + algorithms[i].getId() + ", alg#" + i);
				if (state != RUNNING ) {
					if (logging2) System.out.println("  ->Communicator: It's NOT runing. " +
							"It's in " + state + " state");
					switch (state) {
						case EXTERNAL_INTERRUPTED:
					 		continue;
						case STOP_CRITERION_SATISFIED:
							saveFoundOptimumAndArea(algorithms[i]);
							algorithms[i].dropWorkList();
							// no break here: continue as with EMPTY worklist
						case EMPTY_WORKLIST:
							if (!getWorkFromNeighborForThisAlgorithm(i)) {
								failCounter++;
							}
							break;
						default:
							throw new RuntimeException("Unknown OptimizationStatus " + state);
					}
				}
				else { // RUNNING
					updateScreeningValueForAlgorithm(i);
					failCounter = 0;
				}
			}
		}
	}
	private void sleep() {
		try {
			sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			interrupt();
		}
	}
	private void collectResults() {
		boolean restartDispatching = false;
		for (int i = 0; i < algorithms.length; i++) {
			OptimizationStatus state = algorithms[i].getState();
			switch (state) {
				case EXTERNAL_INTERRUPTED:
					algorithms[i].resumeIterations(); // some algorithm was paused.. it is really strange. resume it and continue computation
					assert (false);
					// no break here!
				case RUNNING:			// there are running algorithms!
					restartDispatching = true;
					break;
				case STOP_CRITERION_SATISFIED:
					saveFoundOptimumAndArea(algorithms[i]);
					break;
				case EMPTY_WORKLIST:
					// do nothing
					break;
				default:
					throw new RuntimeException("Unknown OptimizationStatus " + state);
			}
		}
		if (restartDispatching) {
			assert(false);
			communicateAlgorithms();	// unstopped algorithms has been found: we have to dispatch them 
			collectResults();	// and get its results back			
		}
	}

	private void prepareFinalResult() {
		if (shouldResultBeRefined())
			refineResult();
	}
	private boolean shouldResultBeRefined() {
		RealInterval optVal = getOptimumValue();
		if (optVal.wid() > executor.getPrecision())
			return true;
//		if (getOptimumArea().length > MAX_ACCEPTABLE_OPTIMUM_AREA_LENGTH)
//			return true;
		return false;
	}
	private void refineResult() {
		//restart algorithms
	}

	private void saveFoundOptimumAndArea(ParallelAlgorithm parallelAlgorithm) {
		//throw new RuntimeException("FIX ME for IAMath2JInterval");
		assert (parallelAlgorithm.getState() == STOP_CRITERION_SATISFIED);
		// rejecting by-value bound could has been updated 
		parallelAlgorithm.removeRejectedBoxes();
		if (parallelAlgorithm.getWorkListSize() == 0) {
			/*if (logging)*/ System.out.println("{{{ Communicator: saveFoundOptimumAndArea(alg=" + parallelAlgorithm.getId() + 
					") : worklist is empty");
			return;
		}
		RealInterval newOpt = parallelAlgorithm.getOptimumValue();
		/*if (logging)*/ System.out.println("{{{ Communicator: saveFoundOptimumAndArea(alg=" + parallelAlgorithm.getId() + 
				") : potential new optimum: " + newOpt);

		RealInterval storedOpt = this.getOptimumValue(); 
		if (newOpt.lo() > storedOpt.hi()) {
			// throw out new result...
			/*if (logging)*/ System.out.println(" ->  Communicator: saveFoundOptimumAndArea() opt DISCARDED");
			return;
		}
		optimumShortList.add( parallelAlgorithm.getOptimumArea(), newOpt.lo(), newOpt.hi() );

		if (logging) {			
			if (storedOpt.isIntersects(newOpt))
				System.out.println(" ->  Communicator: saveFoundOptimumAndArea() opt COMBINED. area size is " + optimumShortList.size());
			else
				System.out.println(" ->  Communicator: saveFoundOptimumAndArea() opt RENEWED. area size is " + optimumShortList.size());
		}
		updateGlobalScreeningValue(optimumShortList.getLowBoundMaxValue());
	}

	private double getAlgorithmScreeningValue(int algNum) {
		return algorithms[algNum].getLowBoundMaxValue();
	}
	private void updateScreeningValueForAlgorithm(int algNum) {
		double r = getAlgorithmScreeningValue(algNum);
		
		if (r > globalScreeningValue) {
			algorithms[algNum].probeNewLowBoundMaxValueAndDoNotClean(globalScreeningValue); 
														// !!! DO NOT CLEAN -- otherwise it can crash iterations
														// they are asynchronous
			if (logging) System.out.println("   " + globalScreeningValue + " => screeningValue for alg " + algNum);
		} else {
			updateGlobalScreeningValue(r, algNum);
		}
	}

	private void updateGlobalScreeningValue(double r) {
		updateGlobalScreeningValue(r, null);
	}
	private void updateGlobalScreeningValue(double r, Integer algNum) {
		if (r < globalScreeningValue) {
			if (logging) {
				String str = "    globalScreening <= " + r;
				if (algNum != null)
					str += " (from alg " + algNum + ")";
				System.out.println(str);
			}
			globalScreeningValue = r;
		}		
	}

	private boolean getWorkFromNeighborForThisAlgorithm(int algNum) {
		if (logging) System.out.println("Communicator: getWorkFromNeighborForThisAlgorithm for alg# " + algNum + " {{{");

		// find a working neighbor to get work for
		int neighborNum = findWorkingNeighbourAndPauseIt(algNum);
		if (neighborNum < 0) {
			if (logging) System.out.println("Communicator: getWorkFromNeighborForThisAlgorithm for alg# " 
					+ algNum + " FAILED: no working algs }}}");
			return false; 
		}
		ParallelAlgorithm neighbor = algorithms[neighborNum];
		ParallelAlgorithm drained = algorithms[algNum];

		drained.getWorkFromStopped(neighbor, globalScreeningValue);

		resumeAlgorithms(algNum, neighborNum);

		if (logging) System.out.println("Communicator: getWorkFromNeighborForThisAlgorithm for alg# " + algNum + " }}}");
		return true;
	}

	private void resumeAlgorithms(int algNum, int neighborNum) {
		ParallelAlgorithm neighbor = algorithms[neighborNum];

		neighbor.resumeIterations();
		restartThread(algNum);
	}

	/*
	 * checks all algorithms in a round till find working one
	 * than returns it number. returns -1 if nobody running  
	 */
	private int findWorkingNeighbourAndPauseIt(int algNum) {
		int neighbourNum = algNum;
		int count = 0;
		if (logging) System.out.println("Communicator: findWorkingNeighbour for alg# " 
				+ algNum + " {{{");
		
		do {
			neighbourNum = (neighbourNum + 1) % algorithms.length;
			if (/*algorithms[neighborNum].getState() == RUNNING &&*/ 
				algorithms[neighbourNum].pauseIterations() // pause() checks that state == RUNNING 
				) {
				if (logging) System.out.println("Communicator: findWorkingNeighbour for alg# " 
						+ algNum + ". neighbor found and stopped: alg #" + neighbourNum + " }}}");
				return neighbourNum;
			}
		} while (algorithms.length < count++);

		// if we here it means there are no more working algorithms
		if (logging) System.out.println("Communicator: findWorkingNeighbour for alg# " + algNum +
					" failed. }}}");
		return -1;
	}

	private void restartThread(int threadNum) {
		executor.restartThread(threadNum);		
	}
	
	public RealInterval getOptimumValue() {
		if (optimumShortList.size() != 0) // there is an assertion in getOptimumValue()... 
			return optimumShortList.getOptimumValue();
		return new RealInterval(); //-inf, +inf
	}

	public Box[] getOptimumArea() {
		return optimumShortList.getOptimumArea();
	}
	
}
