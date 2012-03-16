package solvers;

import splitters.*;
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
 *  Type of Chooser + "C" (RndC stands for Random Chooser)
 *  Type of Splitter + "S" (BigEqS stands for BiggestSideEquallySplitter)
 * 
 */
public class Bisection_UnsL_CBtC_BigEqS extends BaseAlgorithm implements IntervalSolver {

	public Bisection_UnsL_CBtC_BigEqS() {
		WorkList workList = new UnSortedWorkList();
		Chooser chooser  = new CurrentBestChooser(workList);
		Splitter splitter = new BiggestSideEquallySplitter();
		setLogic(workList, chooser, splitter);		
	}
}
