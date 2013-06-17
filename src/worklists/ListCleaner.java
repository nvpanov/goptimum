package worklists;

public class ListCleaner {

	private WorkList workListToClean;
	
	public ListCleaner(WorkList wl) {
		workListToClean = wl;
	}
	
	@Override
	public void finalize() {
		workListToClean.requestCleaning(); // we can't clean it from here -- if other thread is working with the list right now 
											// we will receive concurrent modification exception
		new ListCleaner(workListToClean);
		System.out.println("ListCleaner: cleaning requested.");
	}

}
