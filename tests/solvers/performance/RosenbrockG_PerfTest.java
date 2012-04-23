package solvers.performance;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import solvers.*;
import testharness.TestHarness;
import algorithms.Algorithm;


public class RosenbrockG_PerfTest {
	@Rule
	public MethodRule benchmarkRun = new BenchmarkRule();
	  
	protected TestHarness test = new TestHarness();
	protected final int runs = 2;
	protected final int warmup = 1;
	int dim = 32;

	//(w=2(1, dim=32),i=2) 4/20/12 [-150, 60]
	// dim									2		4		8		16		32	
	// Bisection_SrtL_CBtC_AllEqS			0.14	1.02	28		--		--
	// Bisection_SrtL_CBtC_BigEqS			0.08	0.82	2.52	10		43	
	// PointIntervalBis_SrtL_CBtC_BigEqS	0.19	0.73	2.1		11		44

	@Ignore
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void RosenbrockPerf_BisectionAll() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		run(a);
	}
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void RosenbrockPerf_BisectionBig() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		run(a);
	}	
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void RosenbrockPerf_PointInterval() {
		Algorithm a = new PointIntervalBis_SrtL_CBtC_BigEqS();
		run(a);
	}	

	private final void run(Algorithm a) {
		RealInterval side = new RealInterval(-150, 60);
		//RealInterval side = new RealInterval(-2.048, 2.048);
		test.f_RosenbrockGn(a, dim, side);
	}
}
