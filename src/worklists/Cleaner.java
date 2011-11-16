package worklists;

import java.util.Collection;
import java.util.HashSet;

import core.Box;

class Cleaner {
	private long memoryThreshold = 10 * 1024*1024; // 10 Mb;
	//protected double threshold = 1;
	private Collection<Box> collection;
	private Screener screener;
	private static final int lengthThreshold = 20;
	private static final int updatesThreshold = 10;
	
	public Cleaner(Collection<Box> collection, Screener screener) {
		assert(collection != null && screener != null);
		this.collection = collection;
		this.screener = screener;
	}

	// could start screening. not sure that will  
	public int triggerScreeningIfWorth() {
		int res = -1;
		if (isWorthScreening())
			res = cleanList();
		return res;
	}
	public int cleanList() { 
		double valueLimit = screener.getLowBoundMaxValue();
		int removedCount = 0;


		// first variant
/*
 		Iterator<Box> it = collection.iterator();
		while(it.hasNext()) {
	    	b = it.next();
			if (b.getFunctionValue().lo() > valueLimit) {
	    		it.remove();
	    		removedCount++;
	    	}
	    }
		
*/
		// second variant
		HashSet<Box> toRemove = new HashSet<Box>();
		for(Box b : collection) {
	    	if (b.getFunctionValue().lo() > valueLimit) {
	    		toRemove.add(b);
				removedCount++;
	    	}
	    }
		collection.removeAll(toRemove);

		// third variant
/*		
		// WorkList will use the old collection!
		ArrayList<Box> newCollection = new ArrayList<Box>();
		for(Box b : collection) {
	    	if (b.getFunctionValue().lo() < valueLimit) {
	    		newCollection.add(b);
	    	}
	    }
		removedCount = collection.size() - newCollection.size();
		collection = newCollection;
*/
/*		
		System.out.println("WorkList:  -- Cleaned " + removedCount + 
				" boxes. Actual size is " + collection.size());
*/		
		screener.resetStatistics();
		
		return removedCount;
	}
	
	private boolean isWorthScreening() {
		long usedMem = Runtime.getRuntime().totalMemory();
		if (usedMem > memoryThreshold)
			return true;
		if (collection.size() < lengthThreshold)
			return false;
		if (screener.getValueLimitUpdatesCount() > updatesThreshold)
			return true;
//		if (screener.getLowBoundMaxValueLimitDelta() > collection.iterator().next().getFunctionValue().wid()/10) return true;
		// some other heuristics 
		//if() return true;
		return false;			
	}
	
}
