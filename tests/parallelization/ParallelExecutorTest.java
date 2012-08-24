package parallelization;


import static org.junit.Assert.*;
import net.sourceforge.interval.ia_math.RealInterval;
import org.junit.Test;
import core.Box;
import functions.Function_DeJong_nD;
import solvers.Bisection_SrtL_CBtC_BigEqS;
import solvers.PointIntervalBis_SrtL_CBtC_BigEqS;

import algorithms.Algorithm;
import algorithms.BaseAlgorithm;

public class ParallelExecutorTest {
	private Algorithm /*ParallelExecutor*/ executor;


	@Test//(timeout=12*1000) //12 sec
	public void testDeJong() throws Exception {
		//fail("FIX me for IAMath2JInterval");
		int threads = 2* 3;
		BaseAlgorithm algorithms[] = new BaseAlgorithm[threads];
		for (int i = 0; i < threads; ) {
			algorithms[i++] = new Bisection_SrtL_CBtC_BigEqS();
//			algorithms[i++] = new Bisection_UnsL_RndC_BigEqS();
			algorithms[i++] = new PointIntervalBis_SrtL_CBtC_BigEqS();
		}
		
		int dim;
		dim = 6; Function_DeJong_nD f = new Function_DeJong_nD(dim);
		Box area = new Box(dim, new RealInterval(-10, 100));
		
		executor = new ParallelExecutor(threads, algorithms);
		executor.setProblem(f, area);
//		executor.setPrecision(1e-12);
		executor.solve();
		
		RealInterval opt = executor.getOptimumValue();
		Box[] optA = executor.getOptimumArea();
		System.out.println(" > OPTIMUM VALUE: " + opt);
		System.out.println(" >  AREA CONTAINS " + optA.length + " boxes");
		assertTrue(opt.wid() < executor.getPrecision());
		assertTrue(Math.abs(opt.hi() - 0) < executor.getPrecision());
		boolean contains = false;
		double[] checkArg = new double[dim];
		for (Box b : optA)
			if (b.contains(checkArg)) {
				contains = true;
				break;
			}
		assertTrue(contains);		
	}
	
	@Test
	public void testDeJongBig() {
		int threads = 2* 3;

		int dim;
		dim = 30; Function_DeJong_nD f = new Function_DeJong_nD(dim);
		Box area = new Box(dim, new RealInterval(-100, 1000));
		
		executor = new ParallelExecutor(threads, new Bisection_SrtL_CBtC_BigEqS());
		executor.setProblem(f, area);
		executor.setPrecision(1e-12);
		executor.solve();
		
		RealInterval opt = executor.getOptimumValue();
		Box[] optA = executor.getOptimumArea();
		System.out.println(" > OPTIMUM VALUE: " + opt);
		System.out.println(" >  AREA CONTAINS " + optA.length + " boxes");
		assertTrue(opt.wid() < executor.getPrecision());
		assertTrue(Math.abs(opt.hi() - 0) < executor.getPrecision());
		boolean contains = false;
		double[] checkArg = new double[dim];
		for (Box b : optA)
			if (b.contains(checkArg)) {
				contains = true;
				break;
			}
		assertTrue(contains);		
	}

}
