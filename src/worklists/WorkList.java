package worklists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.interval.ia_math.RealInterval;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import worklists.SortedWorkList.DescendingAreaWidthBoxComparator;

//import net.sourceforge.interval.ia_math.RealInterval;

import choosers.Chooser;

import core.Box;


public abstract class WorkList {
	protected Collection<Box> collection;
	protected Screener screener;
	protected Chooser chooser;
	protected Cleaner cleaner;
	private int maxSize = -1;
	private int avgSize = -1;
	private int boxAdded = -1;

	/*
	 * Has a protected constructor only
	 */
	protected WorkList(Collection<Box> collectionToUse, Box area) {
		/*
		 *  child has to instantiate particular collection
		 *  sorted and unsorted list uses different collections
		*/ 
		collection = collectionToUse;
		chooser = null;

		double limit = Double.MAX_VALUE; 
		if (area != null) {
			collection.add(area);
			limit = area.getFunctionValue().hi();
		}
		screener = new Screener(limit);
		cleaner = new Cleaner(collection, screener);
	}
/*
	@Override
	protected void finalize() {
		if (debug){
			System.out.println("_____________________");
			System.out.println("WorkList statistiks:");
			System.out.println("  Max size of the list was " + maxSize);
			System.out.println("  Average size of the list " + avgSize);
			System.out.println("  Num of boxes inserted    " + boxAdded);
			System.out.println("  Attempts to GC the list  " + attemptsToClean);
			System.out.println("  Successfully cleaned     " + listCleanedTimes);
			System.out.println("----------------------");
		}
	}
*/
	public void setChooser(Chooser c) {
		chooser = c;
	}
	
	/*
	 * this method indeed adds box to the list.
	 * add(Box) and add(Box[]) lock the list, perform
	 * checks with Screener and call this method for
	 * actual adding. Is the list sorted or not depends on 
	 * implementation of this method.
	 */
	protected abstract void add_checked(Box box);
	
	/*
	 * this method returns a box with the lowest
	 * low border - so called the leading box.
	 * The method is used in getOptimumValue()
	 * Also can be used in some strategies to 
	 * split the most suspicious box.
	 * We do not know anything about box order
	 * so we have to ask a child
	 * that implements the actual behavior 
	 */
	public abstract Box getLeadingBox();
	
	/*
	 * extracts next box. which box will be extracted depends on
	 * what Chooser is used.
	 */
	public final Box extractNext() {
		if (collection.size() < 1) {
			return null;
		}
		Box b;
		do {
			b = chooser.extractNext();
		} while (b != null && screener.checkByValue(b) == false); // we do not clean the list all the time
																	// but we do not return bad boxes:)
		return b;
	}
	
	/*
	 * adds a bunch of boxes.
	 * current implementation just calls
	 * add(Box) for each element.
	 * So, no checks with screener here,
	 * no list looks, no checks if 
	 * ListCleaner has to be resurrected.
	 */
	public void add(Box[] newBoxes) {
		for (Box b : newBoxes)
			add(b); // add(Box) locks the list
	}

	/*
	 * check input boxes with screener
	 * and calls add_checked()
	 */
	public final void add(Box box) {
		if (screener.checkPassed(box)) {
			add_checked(box);
		}
	}
	/*
	 * receives a new set of boxes and their estimation of the optimum
	 * used by ParallelAlgorithm
	 */
	public void add(Box[] newBoxes, double minLo, double minHi) {
		if (minLo > screener.getLowBoundMaxValue())
			return; 					// they hasn't passed screening
		if (this.size() == 0 || minHi < this.getOptimumValue().lo()) { // getOptimumValue() could be expensive on unsorted lists!
			// we are an empty list or 		we haven't passed the screening
			this.clean(minHi);
			collection.addAll(Arrays.asList(newBoxes));
		}
		this.probeNewLowBoundMaxValueAndDoNotClean(minHi);
		this.add(newBoxes);
	}
	public final Box extract(int n) {
		Box b = extractInternal(n);
		return b;
	}
	
	protected Box extractInternal(int n) {
		// default implementation
		// we do not have a random access in Collection interface
		// so, we have to use iterators.
		// derived worklist can override this function for
		// more effective work
		Iterator<Box> it = collection.iterator();
		Box b = null;
		for (int i = 0; i < n-1; i++)
			it.next();
		b = it.next();
		boolean success = remove(b);
		assert(success);
		return b;
	}

	public final boolean remove(Box toRemove) {
		return collection.remove(toRemove);
	}
	public final int size() {
		int i = collection.size();
		return i;
	}
	
	public final RealInterval getOptimumValue() {
		if (collection.size() == 0)
			return null;
		double hiBorder = screener.getLowBoundMaxValue();
		double loBorder = getLeadingBox().getFunctionValue().lo();
		return new RealInterval(loBorder, hiBorder);
	}
	public final Box[] getOptimumArea() {
		cleaner.cleanList();
		
		//ArrayList<Box> opt = (ArrayList<Box>)list.clone();
		Box[] opt = collection.toArray(new Box[collection.size()]);
		
		// TODO: combine near intervals into one if possible!
//		IntervalMerger merger = new IntervalMerger(opt);
//		opt = merger.merge();
		return opt;
	}

	public void probeNewLowBoundMaxValue(double possibleNewMax) {
		if (screener.probeNewLimit(possibleNewMax) )
			cleaner.triggerScreeningIfWorth();
	}
	public void probeNewLowBoundMaxValueAndClean(double possibleNewMax) {
		if (screener.probeNewLimit(possibleNewMax) )
			cleaner.cleanList();
	}
	public void probeNewLowBoundMaxValueAndDoNotClean(double possibleNewMax) {
		screener.probeNewLimit(possibleNewMax);
	}

	public void clean() {
		clean(Double.MAX_VALUE);
	}
	private void clean(double threshold) {
		collection.clear();
		screener = new Screener(threshold);
	}
	
	public final void getWorkFrom(WorkList otherWL) {
		System.out.println("WorkList::getWorkFrom() {{{");
		double threshold = Math.min(this.getLowBoundMaxValue(), otherWL.getLowBoundMaxValue() );
		assert(threshold <= this.getLowBoundMaxValue());
		clean(threshold);
		
		Collection<Box> oCol = otherWL.collection;
		Box b;
		int otherSize = otherWL.size();
		for (int i = 0; i < otherSize/2; i++) {
			Iterator<Box> oI = oCol.iterator();
			b = oI.next(); 
			oI.remove();
			collection.add(b);
		}
	
		System.out.println("WorkList::getWorkFrom() otherWL has size " + 
				otherSize + ", now this WL has size " + collection.size());
		System.out.println("WorkList::getWorkFrom() }}}");
	}
	public double getLowBoundMaxValue() {
		return screener.getLowBoundMaxValue();
	}
	/////////////////////////////// for rnd prove only ///////////////////
	private DescendingAreaWidthBoxComparator widthComparator = new DescendingAreaWidthBoxComparator();
									
	// DIRTY CASTS BELLOW!!!!!!!!!!!!!!!!!!!!
	// OK, OK, This is BAD.... 
	// But this code is only to check the prove and not supposed to be reused 
	public void printStats(boolean print) {
		double sumW = 0;
		double minW = Double.MAX_VALUE;
		double maxW = -1;
		for (Box b : collection) {
			for (int i = b.getDimension() - 1; i >= 0; i--) {
				double wid = b.getInterval(i).wid();
				if (wid > maxW)
					maxW = wid;
				if (wid < minW)
					minW = wid;
				sumW += wid;					
			}
		}
		sumW = sumW / (collection.size() * collection.iterator().next().getDimension());
		
		// MIN, AVERAGE, MAX
		if (print) System.out.println(minW + "\t" + sumW + "\t" + maxW);
	}
	
	private ArrayList<ArrayList<Double>> statMin; 
	private ArrayList<ArrayList<Double>> statMax; 
	private ArrayList<ArrayList<Double>> statAvg;
	
	public void initStats(int runsInSeria) {
		int size = runsInSeria;
		statMin = new ArrayList<ArrayList<Double>>(size);
		statMax = new ArrayList<ArrayList<Double>>(size);
		statAvg = new ArrayList<ArrayList<Double>>(size);
		for (int i= 0; i < size; i++) {
			statMin.add( new ArrayList<Double>() );
			statMax.add( new ArrayList<Double>() );
			statAvg.add( new ArrayList<Double>() );
		}
	}
	// copy-paste!
	public void collectStats(int runNum) {
		double sumW = 0;
		double minW = Double.MAX_VALUE;
		double maxW = -1;
		for (Box b : collection) {
			for (int i = b.getDimension() - 1; i >= 0; i--) {
				double wid = b.getInterval(i).wid();
				if (wid > maxW)
					maxW = wid;
				if (wid < minW)
					minW = wid;
				sumW += wid;					
			}
		}
		sumW = sumW / (collection.size() * collection.iterator().next().getDimension());
		statMin.get(runNum).add(minW);
		statMax.get(runNum).add(maxW);
		statAvg.get(runNum).add(sumW);
	}
	public void printCollectedStats() {
		for (int i = 0; i < statMax.size(); i++) {
			for (ArrayList<Double> list : statMax) {
				for (double d : list)
					System.out.print(d + "\t");
			}
			System.out.print("\n");
		}
	}
}
