package algorithms;

import static algorithms.OptimizationStatus.RUNNING;
import splitters.BiggestSideEquallySplitter;
import worklists.UnSortedWorkList;
import choosers.RandomChooser;
import net.sourceforge.interval.ia_math.RealInterval;
import core.Box;

public class RndProveBaseAlgorithm extends BaseAlgorithm {
	private long prevTimeRecord;
	private RealInterval funcVal = new RealInterval(0);
    private Box area;

    public RndProveBaseAlgorithm(int dim, double size) {
    	area = new Box(dim, new RealInterval(-size/2, size/2) );
    	workList = new UnSortedWorkList(area);
    	chooser = new RandomChooser(workList);
    	splitter = new BiggestSideEquallySplitter();
    }
	private void reset() {
		workList = new UnSortedWorkList(area);
		chooser = new RandomChooser(workList);
		System.gc();
		prevTimeRecord = System.nanoTime();
	}

	@Override
	protected OptimizationStatus iterate() {
		Box[] newBoxes;

		workBox = workList.extractNext();
		assert(workBox != null);
		
		newBoxes = splitter.splitIt(workBox);
		assert(newBoxes.length > 1);
		
		calculateIntervalExtensions(newBoxes);
		
		workList.add(newBoxes);

		return RUNNING;
	}
	@Override
	protected void calculateIntervalExtensions(Box[] newBoxes) {
		for (int i = 0; i < newBoxes.length; i++)
	    	newBoxes[i].setFunctionValue(funcVal);
	}
	@Override
	public void solve() {
/*		
		final int POINTS = 20;
		int STEP = 2; 
		int ITERATIONS = (int)Math.pow(STEP, POINTS);
		
		//	warmup
		for (int i = 0; i < 11000; i++) {
			iterate();
			printStats(i, false);
		}
		reset();
		for (int i = 0; i <= ITERATIONS; i++) {
			if (i % STEP == 0) {
				printStats(i, true);
				STEP *= 2;
			}
			iterate();
		}
*/		
		int ITERATIONS = 4000;
		int RUNS = 100;
		for (int run = 0; run < RUNS; run++) {
			
			reset();
			workList.initStats(RUNS);
			
			for (int i = 0; i < ITERATIONS; i++) {
				collectStats(run);
				iterate();
			}
		}
	}
	private void printStats(int i, boolean print) {
		if (print) System.out.print((System.nanoTime() - prevTimeRecord) + "\t" + i + "\t");
		workList.printStats(print);
		prevTimeRecord = System.nanoTime();		
	}
	private void collectStats(int i) {
		workList.collectStats(i);
	}
	public void printCollectedStats() {
		workList.printCollectedStats();		
	}
	
}
