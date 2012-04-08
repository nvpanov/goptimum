package solvers;

import point.PointAlgorithm;
import point.SteepestDescent;

import algorithms.BaseAlgorithm;
import algorithms.IntervalAndPointAlgorithm;

/*
 * Naming convention for strategies:
 * Name contains: 
 *  Idea of the method ( Point and Interval(Bisection) )
 *  Type of WorkList + "L" (SrtL stands for UnSorted WorkList)
 *  Type of Chooser + "C" (RndC stands for Random Chooser)
 *  Type of Splitter + "S" (BigEqS stands for BiggestSideEquallySplitter)
 * 
 */
public class PointIntervalBis_SrtL_CBtC_BigEqS extends IntervalAndPointAlgorithm implements IntervalSolver {

	public PointIntervalBis_SrtL_CBtC_BigEqS() {
		BaseAlgorithm intervalAlg = new Bisection_SrtL_CBtC_BigEqS();
		PointAlgorithm pointAlg = new SteepestDescent();
		this.intervalAlg = intervalAlg;
		this.pointAlg = pointAlg;		
	}
}
