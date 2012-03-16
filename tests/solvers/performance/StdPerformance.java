package solvers.performance;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import point.SteepestDescent;

import core.Box;

import functions.FunctionNEW;
import functions.Function_DeJong_nD;
import functions.Function_Price5_2D;
import functions.Function_Rastrigin10_2D;
import functions.Function_RosenbrockG_nD;
import functions.Function_SixHumpCamelBack_2D;

import algorithms.Algorithm;
import algorithms.IntervalAndPointAlgorithm;

import solvers.Bisection_SrtL_CBtC_AllEqS;
import solvers.Bisection_SrtL_CBtC_BigEqS;
import solvers.Bisection_UnsL_RndC_AllEqS;
import solvers.Bisection_UnsL_RndC_BigEqS;
import testharness.TestData;
import testharness.TestResultAveraging;

@RunWith(Theories.class)
public class StdPerformance {
	private static final int RUNS = 1;
	private static boolean printEachResult = true;

	protected Algorithm a = null;
	protected TestData result = null;
	protected static TestResultAveraging averaging = new TestResultAveraging();

	
	public static @DataPoints Algorithm[] algorithms = {
//		new Bisection_SrtL_CBtC_AllEqS(),
		new Bisection_SrtL_CBtC_BigEqS(),
//		new Bisection_UnsL_RndC_BigEqS(),
//		new Bisection_UnsL_RndC_AllEqS(),
//		new IntervalAndPointAlgorithm(new Bisection_SrtL_CBtC_AllEqS(), new SteepestDescent()),
		new IntervalAndPointAlgorithm(new Bisection_SrtL_CBtC_BigEqS(), new SteepestDescent()),
	};
	public static @DataPoints RealInterval[] areas = {
		new RealInterval(-100, 100),
//		new RealInterval(-1000, -1),
//		new RealInterval(1000, 10000),
//		new RealInterval(-10000, 10000),
	};
	public static @DataPoints FunctionNEW[] targetFunctions = {
//		new Function_WARMUP_nD(4),
		new Function_Price5_2D(),
		new Function_Rastrigin10_2D(),
//		new Function_SixHumpCamelBack_2D(),
//		new Function_DeJong_nD(8),
//		new Function_DeJong_nD(32),
//		new Function_DeJong_nD(128),
//		new Function_RosenbrockG_nD(8),
//		new Function_RosenbrockG_nD(16),
//		new Function_RosenbrockG_nD(32),
//		new Function_RosenbrockG_nD(64),
//		new Function_RosenbrockG_nD(128),
	};
	public static @DataPoints boolean[] warmup = {
//		true, // warmup = no results printed
		false // run == results
	};

	@BeforeClass 
	public static void printHeader() {
		if (printEachResult)
			TestData.printHeader();
	}
	
	@AfterClass 
	public static void printResults() {
		assertTrue(averaging.getNumOfTestCases() == algorithms.length * areas.length * targetFunctions.length);
		
		System.out.println("\n==========================================================");
		printHeader();
		averaging.printAveregedResults();
	}

	@Theory
	public void performanceMeasurement(Algorithm alg, RealInterval area, FunctionNEW f, boolean warmup) {
		Box box = new Box(f.getDimension(), area);
		
		for (int i = 0; i < RUNS; i++) {
			Error err = null;
			alg.setProblem(f, box);
			//System.out.println(area + "  :  " + f);
			System.gc();
			long time, t0 = System.nanoTime();
			try {
				alg.solve();
				time = System.nanoTime() - t0;
			} catch (Error e) {
				time = -1;
				err = e;
				alg.setProblem(f, box); // drop the list to get some memory
			}
			if (warmup)
				continue;
			result = new TestData(f.toString(), alg.toString(), (time>>5), alg.getOptimumValue(), area, f.getDimension(), err);
			if (printEachResult)
				System.out.println(result);
			averaging.addResult(result);
		}
	}
}
