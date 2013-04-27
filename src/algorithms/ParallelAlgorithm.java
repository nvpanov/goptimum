package algorithms;

import worklists.WorkList;

import static algorithms.OptimizationStatus.*;
import functions.Function;

public class ParallelAlgorithm extends BaseAlgorithm implements Runnable {
	private int generation;

	private OptimizationStatus status = RUNNING;
	private boolean isPaused = false;
	private Object lock = new Object();
	private final int id;
//	private Object stateLock = new Object();
//   private ReentrantLock pauseLock = new ReentrantLock();
//   private Condition unpaused = pauseLock.newCondition();

	public ParallelAlgorithm(BaseAlgorithm baseAlgorithm, int id) {
		this.chooser = baseAlgorithm.chooser;
		this.generation = 0;
		this.logging = baseAlgorithm.logging;
		this.splitter = baseAlgorithm.splitter;
		this.stopCriterion = baseAlgorithm.stopCriterion;
		this.targetFunction = baseAlgorithm.targetFunction;
		this.workList = baseAlgorithm.workList;
		this.id = id;
	}
	@Override
	public final void run() {
		solve();
	}
	int t=0;
	@Override
	public void solve() {
		OptimizationStatus tStat;
		do {
//			if (logging) System.out.println("ParallelAlgorithm::solve() -- iterating : " + t);
			sleepIfPaused();
			tStat = iterate();
			//synchronized (stateLock) {
			synchronized (lock) {
				status = tStat;
			}
			t++;
		} while (status == RUNNING);
	}
	private void sleepIfPaused() {
//		if (logging) System.out.println("ParallelAlgorithm::sleepIfPaused() {{{");
		
		/*
		 * when we use nested synchronized blocks 
		 * the order always should be the same
		 * so, lets assume that pause ALLWAYS comes first 
		 */
		//synchronized (pauseLock) {
		synchronized (lock) {
			if (!isPaused)
				return;
			if (logging) System.out.println(getId() + 
				" ParallelAlgorithm::sleepIfPaused() -- PAUSED. " +
				"Iteration count = " + t);
				
//			synchronized (stateLock) {
				status = EXTERNAL_INTERRUPTED;
				if (logging) System.out.println(getId() + 
					" ParallelAlgorithm::sleepIfPaused() -- notifying pauseIterations()");
//				stateLock.notify(); // awake pauseIterations()
				lock.notify(); // awake pauseIterations()
//			}

			if (logging) System.out.println(getId() + " ParallelAlgorithm::sleepIfPaused() -- waiting...");
			try {
				while(isPaused) {
					//pauseLock.wait();
					lock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				// TODO: what to do?
			}
			if (logging) System.out.println(getId() + " ParallelAlgorithm::sleepIfPaused() -- wakedup!");
		}
	}
	public boolean pauseIterations() {
		/*
		 * when we use nested synchronized blocks 
		 * the order always should be the same
		 * so, lets assume that pause ALLWAYS comes first 
		 */
//		synchronized (pauseLock) {
//			synchronized (stateLock) {
		synchronized (lock) {		
				if (status != RUNNING) {
					if (logging) System.out.println(getId() + 
							" ParallelAlgorithm::pauseIterations() returning FALSE because status is " + status);		
					return false;
				}
				isPaused = true;
				if (logging) System.out.println(getId() + " ParallelAlgorithm::pauseIterations() -- waiting till iteration will be finished...");		
				try {
					while (status == RUNNING) {
						//stateLock.wait(); // waiting for sleepIfPaused
						lock.wait(); // waiting for sleepIfPaused
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (logging) System.out.println(getId() + " ParallelAlgorithm::pauseIterations() -- iteration is finished.");		
//				}
//			}
		}
		return true;
	}

	public boolean resumeIterations() {
		/*
		 * when we use nested synchronized blocks 
		 * the order always should be the same
		 * so, lets assume that pause ALLWAYS comes first 
		 */
//		synchronized (pauseLock) {
		synchronized (lock) {		
			if (isPaused != true) {
				if (logging) System.out.println(getId() + 
						" ParallelAlgorithm::resumeIterations() returning FALSE because thread was not paused");		
				return false;
			}
			isPaused = false;
			if (logging) System.out.println(getId() +
					" ParallelAlgorithm::resumeIterations() -- isPaused = false");		
			//synchronized (stateLock) {
			synchronized (lock) {
				status = RUNNING;
			}
			//pauseLock.notify(); // awake sleepIfPaused()
			lock.notify(); // awake sleepIfPaused()
			if (logging) System.out.println(getId() +
					" ParallelAlgorithm::resumeIterations() -- notify");		
		}
		return true;
	}	
	
	public int getGeneration() {
		return generation;		
	}
	public int getId() {
		return id;		
	}
	public long getThreadId() {
		return Thread.currentThread().getId();
	}

	public void dropWorkList() {
		workList.clearAll();
		status = EMPTY_WORKLIST;
	}
	public synchronized void getWorkFromStopped(ParallelAlgorithm neighbour, double globalThreshold) {
		if (logging) System.out.println(getId() + " ParallelAlgorithm::getWorkFromStopped(" + neighbour.getId() + ") {{{");
		assert (workList.size() == 0); //we use getWorkFromStoped to fill EMPTY worklists ONLY
										// otherwise some areas can be lost
		if (neighbour.getState() != EXTERNAL_INTERRUPTED) { // stopped not because interrupt
															// but due to empty worklist or
															// because completed the job
			throw new IllegalStateException("getWorkFromStopped found that" +
					"neighbour.getState() != EXTERNAL_INTERRUPTED. It is in " +
					neighbour.getState() + " state");
		}			
		getSomeBoxesFromThisAlg(neighbour, globalThreshold);
		generation++;
		if (logging) System.out.println(getId() + " ParallelAlgorithm::getWorkFromStopped() }}}");
	}
	private void getSomeBoxesFromThisAlg(ParallelAlgorithm neighbour, double globalThreshold) {
		WorkList otherWL = neighbour.workList;
		assert (neighbour.getState() == EXTERNAL_INTERRUPTED && otherWL.size() > 0);
		assert (workList.size() == 0);
		workList.getWorkFrom(otherWL, globalThreshold);		
	}
	public OptimizationStatus getState() {
		//synchronized (stateLock) {
		synchronized (lock) {		
			return status;
		}
	}
	public Function getProblem() {
		return targetFunction;
	}
	public void removeRejectedBoxes() {
		workList.removeRejectedBoxes();
	}
	public int getWorkListSize() {
		return workList.size();
	}

}