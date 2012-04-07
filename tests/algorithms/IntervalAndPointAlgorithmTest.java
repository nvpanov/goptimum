package algorithms;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.Box;

import point.PointAlgorithm;
import point.SteepestDescent;

import solvers.Bisection_SrtL_CBtC_BigEqS;
import solvers.IntervalSolver;
import splitters.AllSidesEquallySplitter;
import splitters.BiggestSideEquallySplitter;
import splitters.Splitter;
import testharness.TestHarness;
import worklists.SortedWorkList;
import worklists.WorkList;
import choosers.Chooser;
import choosers.CurrentBestChooser;
import functions.FunctionNEW;
import functions.Function_DeJong_nD;
import functions.Function_RavineSurface;
import functions.Function_RosenbrockG_nD;

public class IntervalAndPointAlgorithmTest {
	protected Random rnd = new Random();
	protected TestHarness test = new TestHarness();
	

	@Before
	public void setUp() throws Exception {
	}

	//@Ignore
	@Test
	public final void test_DeJong() {
		int dim = 12;
		Box area = new Box(dim, new RealInterval(-10, 100));
		Function_DeJong_nD f = new Function_DeJong_nD(dim);
		IntervalSolver iSolver = new Bisection_SrtL_CBtC_BigEqS();
		//iSolver.setProblem(f, area);
		
		PointAlgorithm pointAlg = new SteepestDescent();
		Algorithm algo = new IntervalAndPointAlgorithm(iSolver, pointAlg);
		algo.setProblem(f, area);
		
		algo.solve();
		
		RealInterval opt = algo.getOptimumValue();
		
		assertTrue(opt.wid() < 1e-3);
		assertTrue(opt.lo() > -1e-3);
		assertTrue(opt.hi() < 1e-3);		
	}

//	@Ignore
	@Test//(timeout=3*1000) //3 sec
	public final void test_RosenbrockGn() {
		int dim = 8;
		RealInterval area = new RealInterval(-100, 100);
		//d=8	- 1s, (with debug output) !!! now it 3!!!!
		// 	16	- 8s,
		//  32	- 90
		
		//RealInterval area = new RealInterval(-rnd.nextInt(10)*100 - 1, rnd.nextInt(10)*200 + 1);

		IntervalSolver iSolver = new Bisection_SrtL_CBtC_BigEqS();
		PointAlgorithm pointAlg = new SteepestDescent();

		Algorithm algo = new IntervalAndPointAlgorithm(iSolver, pointAlg);
		long start = System.currentTimeMillis();
		test.f_RosenbrockGn(algo, dim, area);
		long stop = System.currentTimeMillis();
		System.out.println("\n===============\nOptVal = " + algo.getOptimumValue() + 
				"\nTime = " + ((stop-start)/1000.0 + "s."));
	}
	
	@Test
	public final void test_RavineSurface() {
		int dim = 2;
		Box area = new Box(dim, new RealInterval(-10, 100));
		FunctionNEW f = new Function_RavineSurface(dim);
		IntervalSolver iSolver = new Bisection_SrtL_CBtC_BigEqS();
		PointAlgorithm pointAlg = new SteepestDescent();
		Algorithm algo = new IntervalAndPointAlgorithm(iSolver, pointAlg);
		algo.setProblem(f, area);
		
		algo.solve();
		
		RealInterval opt = algo.getOptimumValue();
		
		System.out.println("RavineSurface: opt = " + opt);
		assertTrue(opt.wid() < 1);
		assertTrue(opt.lo() > -1e-3);
	}

}
