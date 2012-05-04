package parallelization;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.FunctionNEW;
import solvers.Bisection_SrtL_CBtC_BigEqS;
import splitters.BiggestSideEquallySplitter;
import algorithms.Algorithm;
import algorithms.BaseAlgorithm;
import algorithms.ParallelAlgorithm;
import algorithms.StopCriterion;



/*
 * java.util.concurent and its ThreadPools doesn't fit the task
 */

public class ParallelExecutor implements Algorithm {
	private Thread[] threads;
	private ParallelAlgorithm[] pAlgorithms;
	private AlgorithmsCommunicator communicator;
	
	public ParallelExecutor(int threadNum, BaseAlgorithm... algorithms) {
		threads = new Thread[threadNum];
		pAlgorithms = new ParallelAlgorithm[threadNum];
		if (algorithms.length != threadNum) {
			algorithms = replicateAlgorithms(threadNum, algorithms);
		}
		for (int i = 0; i < threadNum; i++) {
			pAlgorithms[i] = new ParallelAlgorithm(algorithms[i]);
			createThreadForAlgorithm(i, pAlgorithms[i]);
		}
		communicator = new AlgorithmsCommunicator(this);		
	}
	private BaseAlgorithm[] replicateAlgorithms(int threadNum, BaseAlgorithm[] algorithms) {
		BaseAlgorithm[] pool = new BaseAlgorithm[threadNum];
		int i = 0;
		for (; i < algorithms.length; i++) 
			pool[i] = algorithms[i];
		for (; i < threadNum; i++) {
			//pool[i] = algorithms[i-algorithms.length].clone();
			pool[i] = new Bisection_SrtL_CBtC_BigEqS();
			System.out.println("Parallelexecutor added " + pool[i].toString() + " algorithm to the pool");
		}
		

		return pool;
	}
	protected void createThreadForAlgorithm(int i, ParallelAlgorithm pAlg) {
		threads[i] = new Thread(pAlg);
		threads[i].setName(pAlg.toString() + "-th" + i + "-g" + pAlg.getGeneration());
		//threads[i].setPriority(NORM_PRIORITY+2);
	}
	
	protected void startThreads() {
		for (Thread t : threads)
			t.start();
	}
	/*
	 * interface for Communicator 
	 */
	public void restartThread(int threadNum) {
		ParallelAlgorithm pAlg = pAlgorithms[threadNum];
		createThreadForAlgorithm(threadNum, pAlg);
		threads[threadNum].start();
	}	
	Thread[] getThreads() {
		return threads;
	}
	ParallelAlgorithm[] getAlgorithms() {
		return pAlgorithms;
	}
	void updateThreadStructure(int threadNum, ParallelAlgorithm newAlg) {
		pAlgorithms[threadNum] = newAlg;
		createThreadForAlgorithm(threadNum, newAlg);		
	}
	public void solve() {
		startThreads();
		communicator.start();
		try {
			communicator.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public Box[] getOptimumArea() {
		return communicator.getOptimumArea();
	}
	@Override
	public RealInterval getOptimumValue() {
		return communicator.getOptimumValue();
	}
	@Override
	public double getPrecision() {
		// it must be at least one algorithm
		return pAlgorithms[0].getPrecision();
	}
	@Override
	public void setPrecision(double pres) {
		for (ParallelAlgorithm a : pAlgorithms)
			a.setPrecision(pres);
	}
	@Override
	public void setProblem(FunctionNEW f, Box area) {
		Queue<Box> areas = splitAreaAmongAlgorithms(area, threads.length);
		for (ParallelAlgorithm a : pAlgorithms) {
			Box box = areas.remove();
			a.setProblem(f, box);
			System.out.println("Next alg inited with this area: " + box);
		}
	}
	private static Queue<Box> splitAreaAmongAlgorithms(Box area, int parts) {
		BiggestSideEquallySplitter splitter = new BiggestSideEquallySplitter();
		Deque<Box> areas = new LinkedList<Box>();
		areas.addLast(area);
//		System.out.println("!!!!!!! splitAreaAmongAlgorithms uses removeLast() to unbalance work!!!");
		for (int i = 0; i < parts; i++) {
			Box box = areas.removeFirst();
			//Box box = areas.removeLast();
			Box[] tmp = splitter.splitIt(box);
			areas.addLast(tmp[0]);
			areas.addLast(tmp[1]);
		}
		return areas;
	}
	@Override
	public void setStopCriterion(StopCriterion stopCriterion) {
		for (ParallelAlgorithm a : pAlgorithms)
			a.setStopCriterion(stopCriterion);		
	}
	@Override
	public double getLowBoundMaxValue() {
		return communicator.globalScreeningValue;
	}
}

