package parallelization;

import static algorithms.OptimizationStatus.*;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;

import solvers.Bisection_SrtL_CBtC_AllEqS;

import worklists.SortedWorkList;
import worklists.UnSortedWorkList;
import worklists.WorkList;

import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;
//import net.sourceforge.interval.ia_math.exceptions.IAIntersectionException;
import algorithms.Algorithm;
import algorithms.BaseAlgorithm;
import algorithms.OptimizationStatus;
import algorithms.ParallelAlgorithm;


public class AlgorithmsCommunicator extends Thread {
	// just a shortcuts to executor fields
	Thread threadsToWatch[];
	ParallelAlgorithm algorithms[];
	// executor itself
	ParallelExecutor executor;
	double globalScreeningValue = Double.MAX_VALUE;

	private RealInterval optTmp = new RealInterval(); // -inf, + inf
	private volatile Box[] optArea;
	private volatile RealInterval optVal;
	
	private SortedWorkList sortedWorkList;

	public AlgorithmsCommunicator(ParallelExecutor executor) {
		this.executor = executor;
		threadsToWatch = executor.getThreads();
		algorithms = executor.getAlgorithms();
		
		setPriority(NORM_PRIORITY-2);
		setName("AlgorithmsCommunicator");
		
		sortedWorkList = new SortedWorkList();
	}
	
	public void run() {
		double r;
		int failCounter = 0;
		while (failCounter < threadsToWatch.length) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				interrupt();
			}
			for (int i = 0; i < threadsToWatch.length; i++) {
				// check if thread finished his work
				OptimizationStatus state = algorithms[i].getState();
				System.out.println("Communicator: Checking thread #" + i);
				if (state != RUNNING ) {
					System.out.println("Communicator: It's NOT runing. " +
							"It's in " + state + " state");
					switch (state) {
						case EXTERNAL_INTERRUPTED:
					 		continue;
						case STOP_CRITERION_SATISFIED:
							saveFoundOptimumAndArea(algorithms[i]);
							// no break here: continue as with empty worklist
						case EMPTY_WORKLIST: 
							if (!loadThreadFromNeighbor(i)) {
								failCounter++;
							}
							break;
						default:
							throw new RuntimeException("Unknown OptimizationStatus " + state);
					}
				}
				else { // RUNNING
					r = getScreeningValue(i);
					System.out.println("Communicator: It's runing. It's ScreeningValue=" +
							r + ", global ScreeningValue=" + globalScreeningValue);
					updateScreeningValue(i, r);
					failCounter = 0;
				}
			}
		}
		prepareFinalResult();
		System.out.println("Communicator: -- DONE --");
		assert(false); // JUNit parallel execution and asserts
	}
	
	private void prepareFinalResult() {
		if (shouldResultBeRefined())
			refineResult();
		optArea = sortedWorkList.getOptimumArea();
		optVal = sortedWorkList.getOptimumValue();
	}
	private boolean shouldResultBeRefined() {
		// TODO Auto-generated method stub
		return false;
	}
	private void refineResult() {
/*		BaseAlgorithm a = new Bisection_SrtL_CBtC_AllEqS();
		a.setProblem(f, sortedWorkList);
		
		a.setProblem(algorithms[0].getProblem());
		optArea = a.getOptimumArea();
		optVal  = a.getOptimumValue();
		System.out.println("composeResult() optValue=" + optVal + ", area size=" + optArea.length);
		if (optVal.wid() > a.getPrecision()) {
			a.solve();		
		}
		optArea = a.getOptimumArea();
		optVal  = a.getOptimumValue();
		System.out.println("   \\=> optValue=" + optVal + ", area size=" + optArea.length);
*/		
	}

	private void saveFoundOptimumAndArea(ParallelAlgorithm parallelAlgorithm) {
		//throw new RuntimeException("FIX ME for IAMath2JInterval");
		
		RealInterval newOpt = parallelAlgorithm.getOptimumValue();
		if (newOpt == null)
			return; // list was cleared before getOptimum call 
		System.out.println("{{{ Communicator: saveFoundOptimumAndArea() : potential new optimum: " + newOpt);

		if (newOpt.lo() > optTmp.hi()) {
			// throw out new result...
			System.out.println("->  Communicator: saveFoundOptimumAndArea() opt DISCARDED");
			return;
		}
		sortedWorkList.add( parallelAlgorithm.getOptimumArea(), newOpt.lo(), newOpt.hi() );
			
		if (optTmp.isIntersects(newOpt))
			System.out.println("->  Communicator: saveFoundOptimumAndArea() opt COMBINED. area size is " + sortedWorkList.size());
		else
			System.out.println("->  Communicator: saveFoundOptimumAndArea() opt RENEWED. area size is " + sortedWorkList.size());
		
		globalScreeningValue = sortedWorkList.getLowBoundMaxValue();
	}

	private double getScreeningValue(int threadNum) {
		return algorithms[threadNum].getLowBoundMaxValue();
	}
	private void updateScreeningValue(int threadNum, double r) {
		if (r > globalScreeningValue) {
			algorithms[threadNum].probeNewLowBoundMaxValue(globalScreeningValue);
		} else
			globalScreeningValue = r;
	}

	private boolean loadThreadFromNeighbor(int threadNum) {
		System.out.println("Communicator: loadThreadFromNeighbor for thread# " + threadNum + " {{{");

		// find a working neighbor to get work for
		int neighborNum = findWorkingNeighbourAndPauseIt(threadNum);
		if (neighborNum < 0) {
			System.out.println("Communicator: loadThreadFromNeighbor for thread# " 
					+ threadNum + " FAILED: no working algs }}}");
			return false; 
		}
		ParallelAlgorithm neighbor = algorithms[neighborNum];
		ParallelAlgorithm finished = algorithms[threadNum];

		finished.getWorkFromStopped(neighbor);

		resumeAlgorithms(threadNum, neighborNum);

		System.out.println("Communicator: loadThreadFromNeighbor for thread# " + threadNum + " }}}");
		return true;
	}

	private void resumeAlgorithms(int threadNum, int neighborNum) {
		ParallelAlgorithm neighbor = algorithms[neighborNum];

		neighbor.resumeIterations();
		restartThread(threadNum);
	}

	/*
	 * checks all algorithms in a round till find working one
	 * than returns it number. returns -1 if nobody running  
	 */
	private int findWorkingNeighbourAndPauseIt(int threadNum) {
		int neighborNum = threadNum;
		int count = 0;
		System.out.println("Communicator: findWorkingNeighbour for thread# " 
				+ threadNum + " {{{");
		
		do {
			neighborNum = (neighborNum + 1) % threadsToWatch.length;
			if (/*algorithms[neighborNum].getState() == RUNNING &&*/ 
				algorithms[neighborNum].pauseIterations() // pause() checks that state == RUNNING 
				) {
				System.out.println("Communicator: findWorkingNeighbour for thread# " 
						+ threadNum + ". neighbor found and stopped: thread #" + neighborNum + " }}}");
				return neighborNum;
			}
		} while (threadsToWatch.length < count++);

		// if we here it means there are no more alive threads
		System.out.println("Communicator: findWorkingNeighbour for thread# " + threadNum +
					" failed. }}}");
		return -1;
	}

	private void restartThread(int threadNum) {
		executor.restartThread(threadNum);		
	}
	
	public RealInterval getOptimumValue() {
		return sortedWorkList.getOptimumValue();
	}

	public Box[] getOptimumArea() {
		return sortedWorkList.getOptimumArea();
	}
	
}
