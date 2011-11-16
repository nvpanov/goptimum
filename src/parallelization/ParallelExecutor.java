package parallelization;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;
import functions.Function;

import splitters.BiggestSideEquallySplitter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import algorithms.Algorithm;
import algorithms.BaseAlgorithm;
import algorithms.ParallelAlgorithm;



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
		throw new NotImplementedException();
		/*
		BaseAlgorithm newArr[] = new BaseAlgorithm[threadNum];
		//Arrays.copyOf can't help here
		System.arraycopy(algorithms, 0, newArr, 0, algorithms.length);
		for (int i = algorithms.length; i < newArr.length; i++) {
			int algNumToClone = (i - algorithms.length) % algorithms.length;
			newArr[i] = new BaseAlgorithm(algorithms[algNumToClone]);
		}
		return newArr;  
		 */		
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
		return communicator.getArea();
	}
	@Override
	public RealInterval getOptimumValue() {
		return communicator.getOptimum();
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
	public void setProblem(Function f, Box area) {
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
}

