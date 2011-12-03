package algorithms;

import net.sourceforge.interval.ia_math.RealInterval;
import splitters.Splitter;
import choosers.Chooser;
import core.Box;
import worklists.WorkList;

import static algorithms.OptimizationStatus.*;
import functions.Function;

public class ParallelAlgorithm extends BaseAlgorithm implements Runnable {
	private int generation;

	private OptimizationStatus status = RUNNING;
	private boolean isPaused = false;
	private Object lock = new Object();
//	private Object stateLock = new Object();
//   private ReentrantLock pauseLock = new ReentrantLock();
//   private Condition unpaused = pauseLock.newCondition();

	public ParallelAlgorithm(BaseAlgorithm baseAlgorithm) {
		this.chooser = baseAlgorithm.chooser;
		this.generation = 0;
		this.logging = baseAlgorithm.logging;
		this.splitter = baseAlgorithm.splitter;
		this.stopCriterion = baseAlgorithm.stopCriterion;
		this.targetFunction = baseAlgorithm.targetFunction;
		this.workList = baseAlgorithm.workList;
	}
	// for ParallelPointStub
	protected ParallelAlgorithm() {
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
//			System.out.println("ParallelAlgorithm::solve() -- iterating : " + t);
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
//		System.out.println("ParallelAlgorithm::sleepIfPaused() {{{");
		
		/*
		 * when we use nested synchronized blocks 
		 * the order always should be the same
		 * so, lets assume that pause ALLWAYS comes first 
		 */
		//synchronized (pauseLock) {
		synchronized (lock) {
			if (!isPaused)
				return;
			System.out.println(Thread.currentThread().getId() + 
				" ParallelAlgorithm::sleepIfPaused() -- PAUSED. " +
				"Iteration count = " + t);
				
//			synchronized (stateLock) {
				status = EXTERNAL_INTERRUPTED;
				System.out.println(Thread.currentThread().getId() + 
					" ParallelAlgorithm::sleepIfPaused() -- notifying pauseIterations()");
//				stateLock.notify(); // awake pauseIterations()
				lock.notify(); // awake pauseIterations()
//			}

			System.out.println(Thread.currentThread().getId() + " ParallelAlgorithm::sleepIfPaused() -- waiting...");
			try {
				while(isPaused) {
					//pauseLock.wait();
					lock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				// TODO: what to do?
			}
			System.out.println(Thread.currentThread().getId() + " ParallelAlgorithm::sleepIfPaused() -- wakedup!");
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
					System.out.println(Thread.currentThread().getId() + 
							" ParallelAlgorithm::pauseIterations() returning FALSE because status is " + status);		
					return false;
				}
				isPaused = true;
				System.out.println(Thread.currentThread().getId() + " ParallelAlgorithm::pauseIterations() -- waiting till iteration will be finished...");		
				try {
					while (status == RUNNING) {
						//stateLock.wait(); // waiting for sleepIfPaused
						lock.wait(); // waiting for sleepIfPaused
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getId() + " ParallelAlgorithm::pauseIterations() -- iteration is finished.");		
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
				System.out.println(Thread.currentThread().getId() + 
						" ParallelAlgorithm::resumeIterations() returning FALSE because thread was not paused");		
				return false;
			}
			isPaused = false;
			System.out.println(Thread.currentThread().getId() +
					"ParallelAlgorithm::resumeIterations() -- isPaused = false");		
			//synchronized (stateLock) {
			synchronized (lock) {
				status = RUNNING;
			}
			//pauseLock.notify(); // awake sleepIfPaused()
			lock.notify(); // awake sleepIfPaused()
			System.out.println(Thread.currentThread().getId() +
					"ParallelAlgorithm::resumeIterations() -- notify");		
		}
		return true;
	}	
	
	public int getGeneration() {
		return generation;		
	}

	public synchronized void getWorkFromStopped(ParallelAlgorithm neighbour) {
		System.out.println("ParallelAlgorithm::getWorkFromStopped() {{{");
		if (neighbour.getState() != EXTERNAL_INTERRUPTED) { // stopped not because interrupt
															// but due to empty worklist or
															// because completed the job
			throw new IllegalStateException("getWorkFromStopped found that" +
					"neighbour.getState() != EXTERNAL_INTERRUPTED. It is in " +
					neighbour.getState() + " state");
		}			
		setWorkList(neighbour);
		generation++;
		System.out.println("ParallelAlgorithm::getWorkFromStopped() }}}");
	}
	private void setWorkList(ParallelAlgorithm neighbour) {
		WorkList otherWL = neighbour.workList;
		workList.getWorkFrom(otherWL);		
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

}