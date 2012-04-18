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
	protected final int runs = 1;
	protected final int warmup = 1;
	int dim = 4;

	//(w=1,i=3) 4/9/12
	// dim									2		4		8		16		32	
	// Bisection_SrtL_CBtC_AllEqS			0.12	0.6		16.9	--		--
	// Bisection_SrtL_CBtC_BigEqS			0.08	0.3		1.52	6.53	30.1	
	// PointIntervalBis_SrtL_CBtC_BigEqS	0.17	0.5		1.71	8.55	38.2

//	@Ignore
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongZeroPerf_BisectionAll() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		run(a);
	}
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongZeroPerf_BisectionBig() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		run(a);
	}	
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongZeroPerf_PointInterval() {
		Algorithm a = new PointIntervalBis_SrtL_CBtC_BigEqS();
		run(a);
	}	

	private final void run(Algorithm a) {
		RealInterval side = new RealInterval(-150, 60);
		test.f_RosenbrockGn(a, dim, side);
	}
}
