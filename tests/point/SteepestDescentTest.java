package point;

import static org.junit.Assert.*;

import java.util.Random;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import solvers.Bisection_SrtL_CBtC_BigEqS;
import solvers.IntervalSolver;

import core.Box;

import functions.FunctionNEW;
import functions.Function_DeJong_nD;
import functions.Function_RosenbrockG_nD;

public class SteepestDescentTest {
	SteepestDescent solver;
	long seed;
	Random rnd = new Random();

	@Before
	public void setUp() throws Exception {
		seed = System.currentTimeMillis();
		rnd.setSeed(seed);
//		rnd.setSeed(1308989334661L);
		System.out.println(" > seed = " + seed);
	}

//	@Ignore
	@Test
	public final void testDeJong_small() {
		final int dim = 2;
		final double bound = 100;
		FunctionNEW f = new Function_DeJong_nD(dim);
		Box area = new Box(dim, new RealInterval(-bound, bound));
		solver = new SteepestDescent();
		solver.setProblem(f, area);
		double res = solver.localMinimum(area);
		assertTrue(res < 1e-4); // start from 0
		area = new Box(dim, new RealInterval(-bound-10, bound+2));
		res = solver.localMinimum(area);
		assertTrue(res < 1e-4);
	}
	@Ignore
	@Test(timeout=5*60*1000)
	public final void testDeJong_big() {
		final int dim = 40;
		final double bound = 100;
		long start = System.currentTimeMillis();
		FunctionNEW f = new Function_DeJong_nD(dim);
		System.out.println(System.currentTimeMillis() - start);
		/*
		 * 	20		40		80		160		
		 * 	600		2500	8000	65000
		 */

		Box area = new Box(dim, new RealInterval(-bound, bound));
		solver = new SteepestDescent(f, area);
		
		start = System.currentTimeMillis();
		double res = solver.localMinimum(area);
		System.out.println(System.currentTimeMillis() - start);
		
		assertTrue(res < 1e-4); // start from 0

		area = new Box(dim, new RealInterval(-bound-bound/2.0, bound-bound/5.0));
		res = solver.localMinimum(area);
		assertTrue(res < 1);

		area = new Box(dim, new RealInterval(-10, -9)); // bad point...
		res = solver.localMinimum(area);
		assertTrue(res < 1);
	}
	@Test
	public final void testRosenbrockG_nD() {
		final int dim = 4;
		final FunctionNEW f = new Function_RosenbrockG_nD(dim);
		//Box area = new Box(dim, new RealInterval(-rnd.nextInt(10)*10 - 1, rnd.nextInt(10)*20 + 1) );
		Box area = new Box(dim, new RealInterval(-100, 100) );

		PointAlgorithm pointAlg = new SteepestDescent(f, area);
		long start = System.currentTimeMillis();

		double res = pointAlg.localMinimum(area);
		System.out.println("time = " + (System.currentTimeMillis() - start) + "; loc.opt =  " + res);
		
		final double checkValue = 0;
//		final double checkArg = 1;		
		assertTrue(Math.abs(res - checkValue) < 10);
	}
}
