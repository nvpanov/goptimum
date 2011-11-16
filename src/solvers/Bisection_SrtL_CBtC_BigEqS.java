package solvers;

import splitters.*;
import worklists.SortedWorkList;
import worklists.WorkList;
import choosers.Chooser;
import choosers.CurrentBestChooser;
import algorithms.BaseAlgorithm;

/*
 * Naming convention for strategies:
 * Name contains: 
 *  Idea of the method (Bisection)
 *  Type of WorkList + "L" (SrtL stands for UnSorted WorkList)
 *  Type of Chooser + "C" (RndC stands for Random Chooser)
 *  Type of Splitter + "S" (BigEqS stands for BiggestSideEquallySplitter)
 * 
 */
public class Bisection_SrtL_CBtC_BigEqS extends BaseAlgorithm implements IntervalSolver {

	public Bisection_SrtL_CBtC_BigEqS() {
		WorkList workList = new SortedWorkList();
		Chooser chooser  = new CurrentBestChooser(workList);
		Splitter splitter = new BiggestSideEquallySplitter();
		setLogic(workList, chooser, splitter);		
	}
}
