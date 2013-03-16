package worklists;

public class ListCleaner {

	private WorkList workListToClean;
	
	public ListCleaner(WorkList wl) {
		workListToClean = wl;
	}
	
	@Override
	public void finalize() {
		workListToClean.removeRejectedBoxes();
		new ListCleaner(workListToClean);
	}

}
