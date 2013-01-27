package rejecting;

/**
 * @author nvpanov
 * Internal class used for performance profiling. 
 * Stores rejectors that rejected a box.
 * Later this data is used to see the performance of rejectors, like
 * rejector1 rejected 100 boxes, rejector2 -- only 10. moreover, all boxes rejected by
 * rejector2 were also rejected by rejector1.
 */
class ProfilingDataRejectedByMatrix {
	boolean[] rejectedBy;
	int currentRejectorNum = 0;
	
	public ProfilingDataRejectedByMatrix(int numOfRejectors) {
		rejectedBy = new boolean[numOfRejectors];
	}
	public void recordRejectionResult(boolean checkPassed) {
		rejectedBy[currentRejectorNum++] = !checkPassed;
	}
	public boolean equals(ProfilingDataRejectedByMatrix that) {
		assert this.currentRejectorNum == that.currentRejectorNum;
		assert this.rejectedBy.length == that.rejectedBy.length;
		for (int i = 0; i < rejectedBy.length; i++) {
			if (this.rejectedBy[i] != that.rejectedBy[i]) {
				return false;
			}
		}
	return true;
	}	
}

/**
 * @author nvpanov
 * Internal class used for performance profiling. 
 * Stores how long did it take for each rejector to process a box.
 * It counts rejected (useful) and not rejected (wasted) times separately. 
 */
class ProfilingDataRejectorsTimes {
	long nanoTimeBoxRejected;
	long nanoTimeBoxKept;
	int boxRejectedCount;
	int boxKeptCount;
	
	public ProfilingDataRejectorsTimes() {
	}
	
	public ProfilingDataRejectorsTimes(long time, boolean checkPassed) {
		if (checkPassed) {
			nanoTimeBoxKept += time;
			++boxKeptCount;
		} else {
			nanoTimeBoxRejected += time;
			++boxRejectedCount;
		}
	}
	
	public void add(ProfilingDataRejectorsTimes data) {
		this.nanoTimeBoxKept 		+= data.nanoTimeBoxKept;
		this.nanoTimeBoxRejected 	+= data.nanoTimeBoxRejected;
		this.boxKeptCount 			+= data.boxKeptCount;
		this.boxRejectedCount 		+= data.boxRejectedCount;
	}
}
