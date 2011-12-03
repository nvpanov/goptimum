package parallelization;


import static org.junit.Assert.*;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.Box;

import functions.Function;
import functions.Function_DeJong_nD;
import functions.Function_SixHumpCamelBack_2D;

import solvers.Bisection_SrtL_CBtC_BigEqS;
import solvers.Bisection_UnsL_RndC_BigEqS;

import algorithms.Algorithm;
import algorithms.BaseAlgorithm;

public class ParallelExecutorTest {
	private Algorithm /*ParallelExecutor*/ executor;

	@Ignore
	@Test(timeout=12*1000) //12 sec
	public void testDeJong() throws Exception {
		//fail("FIX me for IAMath2JInterval");
		int threads = 2* 3;
		BaseAlgorithm algorithms[] = new BaseAlgorithm[threads];
		for (int i = 0; i < threads; i+=2) {
			algorithms[i] = new Bisection_SrtL_CBtC_BigEqS();
			algorithms[i+1] = new Bisection_UnsL_RndC_BigEqS();
		}
		
		int dim;
		dim = 6; Function f = new Function_DeJong_nD(dim);
		Box area = new Box(dim, new RealInterval(-10, 100));
		
		executor = new ParallelExecutor(threads, algorithms);
		executor.setProblem(f, area);
		executor.solve();
		
		RealInterval opt = executor.getOptimumValue();
		Box[] optA = executor.getOptimumArea();
		System.out.println(" > OPTIMUM VALUE: " + opt);
		System.out.println(" >  AREA CONTAINS " + optA.length + " boxes");
		System.out.println(" >    " + optA[0]);
		assertTrue(opt.wid() < executor.getPrecision());
		assertTrue(Math.abs(opt.hi() - 0) < executor.getPrecision());
	}
	
	@Test
	public void testDeJong2() {
		int threads = 2* 3;

		int dim;
		dim = 16; Function f = new Function_DeJong_nD(dim);
		Box area = new Box(dim, new RealInterval(-100, 1000));
		
		executor = new ParallelExecutor(threads, new Bisection_SrtL_CBtC_BigEqS());
		executor.setProblem(f, area);
		executor.solve();
		
		RealInterval opt = executor.getOptimumValue();
		Box[] optA = executor.getOptimumArea();
		System.out.println(" > OPTIMUM VALUE: " + opt);
		System.out.println(" >  AREA CONTAINS " + optA.length + " boxes");
		System.out.println(" >    " + optA[0]);
		assertTrue(opt.wid() < executor.getPrecision());
		assertTrue(Math.abs(opt.hi() - 0) < executor.getPrecision());

		
		//dim = 2; f = new Function_SixHumpCamelBack_2D();
		//assertTrue(Math.abs(opt.hi() - (-1.03163)) < executor.getPrecision());
		
		
	}

}
