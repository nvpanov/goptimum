package parallelization;

import static algorithms.OptimizationStatus.*;

import java.util.ArrayList;
import java.util.Arrays;

import solvers.Bisection_SrtL_CBtC_AllEqS;

import worklists.UnSortedWorkList;
import worklists.WorkList;

import core.Box;
import net.sourceforge.interval.ia_math.RealInterval;
import net.sourceforge.interval.ia_math.exceptions.IAIntersectionException;
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
	private ArrayList<Box> areaTmp = new ArrayList<Box>();
	private volatile Box[] optArea;
	private volatile RealInterval optVal;

	public AlgorithmsCommunicator(ParallelExecutor executor) {
		this.executor = executor;
		threadsToWatch = executor.getThreads();
		algorithms = executor.getAlgorithms();
		
		setPriority(NORM_PRIORITY-2);
		setName("AlgorithmsCommunicator");
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
		composeResult();
		System.out.println("Communicator: -- DONE --");
	}
	
	private void composeResult() {
//		cleanResult();
		BaseAlgorithm a = new Bisection_SrtL_CBtC_AllEqS();
		a.setProblem(algorithms[0].getProblem(), areaTmp.toArray(new Box[0]));
		optArea = a.getOptimumArea();
		optVal  = a.getOptimumValue();
		System.out.println("composeResult() optValue=" + optVal + ", area size=" + optArea.length);
		if (optVal.wid() > a.getPrecision()) {
			a.solve();		
		}
		optArea = a.getOptimumArea();
		optVal  = a.getOptimumValue();
		System.out.println("   \\=> optValue=" + optVal + ", area size=" + optArea.length);
	}

/*	
   private void cleanResult() {
		WorkList wl = new UnSortedWorkList();
		wl.add(areaTmp.toArray(new Box[0]));
		optArea = wl.getOptimumArea();
		optVal  = wl.getOptimumValue();		
	}
*/
	private void saveFoundOptimumAndArea(ParallelAlgorithm parallelAlgorithm) {
		RealInterval newOpt = parallelAlgorithm.getOptimumValue();
		if (newOpt == null)
			return; // list was cleared before getOptimum call 
		Box[] newArea = parallelAlgorithm.getOptimumArea();
		System.out.println("Communicator: saveFoundOptimumAndArea() : new optimum: " + newOpt);
		
		try {
			optTmp.intersect(newOpt);
			areaTmp.addAll(Arrays.asList(newArea));
			System.out.println("Communicator: saveFoundOptimumAndArea() opt ADDED. area size is " + areaTmp.size());
			
		} catch (IAIntersectionException e) {
		//if (optTmp == null || optTmp.isEmpty()) {
			// intervals doesn't have common values.
			if (newOpt.lo() < optTmp.lo()) {
				// new value is better	
				optTmp = newOpt;
				areaTmp.clear();
				areaTmp.addAll(Arrays.asList(newArea));
				System.out.println("Communicator: saveFoundOptimumAndArea() opt RENEWED. area size is " + areaTmp.size());
			} else {
				// throw out new result...
				System.out.println("Communicator: saveFoundOptimumAndArea() opt DISCARDED");
			}
		}
		globalScreeningValue = Math.min(globalScreeningValue, optTmp.hi());
	}

	private double getScreeningValue(int threadNum) {
		return algorithms[threadNum].getCurLowBoundMaxValue();
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
	
	public RealInterval getOptimum() {
		return optVal;
	}

	public Box[] getArea() {
		return optArea;
	}
	
}
