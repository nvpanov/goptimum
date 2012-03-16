package solvers;

import splitters.AllSidesEquallySplitter;
import splitters.Splitter;
import worklists.UnSortedWorkList;
import worklists.WorkList;
import choosers.Chooser;
import choosers.CurrentBestChooser;
import algorithms.BaseAlgorithm;

/*
 * Naming convention for strategies:
 * Name contains: 
 *  Idea of the method (Bisection)
 *  Type of WorkList + "L" (UnsL stands for UnSorted WorkList)
 *  Type of Chooser + "C" (CBt stands for Current Best Chooser)
 *  Type of Splitter + "S" (AllEqS stands for AllSidesEquallySplitter)
 * 
 */
public class Bisection_UnsL_CBtC_AllEqS extends BaseAlgorithm implements IntervalSolver {

	public Bisection_UnsL_CBtC_AllEqS() {
		WorkList workList = new UnSortedWorkList();
		Chooser chooser  = new CurrentBestChooser(workList);
		Splitter splitter = new AllSidesEquallySplitter();
		
		setLogic(workList, chooser, splitter);
		// the algorithm is ready, but
		// area and function are  still not set. 
		// further call of init(Function f, Box area) is expected
	}
	
	
}
