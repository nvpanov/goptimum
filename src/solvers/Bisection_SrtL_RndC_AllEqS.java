package solvers;

import splitters.AllSidesEquallySplitter;
import splitters.Splitter;
import worklists.SortedWorkList;
import worklists.WorkList;
import choosers.Chooser;
import choosers.RandomChooser;
import algorithms.BaseAlgorithm;

/*
 * Naming convention for strategies:
 * Name contains: 
 *  Idea of the method (Bisection)
 *  Type of WorkList + "L" (SrtL stands for UnSorted WorkList)
 *  Type of Chooser + "C" (RndC stands for Random Chooser)
 *  Type of Splitter + "S" (AllEqS stands for AllSidesEquallySplitter)
 * 
 */
public class Bisection_SrtL_RndC_AllEqS extends BaseAlgorithm implements IntervalSolver {

	public Bisection_SrtL_RndC_AllEqS() {
		WorkList workList = new SortedWorkList();
		Chooser chooser  = new RandomChooser(workList);
		Splitter splitter = new AllSidesEquallySplitter();
		setLogic(workList, chooser, splitter);		
	}
}
