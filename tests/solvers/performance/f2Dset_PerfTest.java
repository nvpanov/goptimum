package solvers.performance;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import solvers.*;
import testharness.TestHarness;
import algorithms.Algorithm;


public class f2Dset_PerfTest {
	@Rule
	public MethodRule benchmarkRun = new BenchmarkRule();
	  
	protected TestHarness test = new TestHarness();
	protected final int runs = 3;
	protected final int warmup = 1;

	//(w=1,i=3) 4/9/12
	// dim									2	
	// Bisection_SrtL_CBtC_AllEqS			1
	// Bisection_SrtL_CBtC_BigEqS			1	
	// PointIntervalBis_SrtL_CBtC_BigEqS	1


	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void f2Dset_BisectionAll() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		run(a);
	}
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void f2Dset_BisectionBig() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		run(a);
	}	
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void f2Dset_PointInterval() {
		Algorithm a = new PointIntervalBis_SrtL_CBtC_BigEqS();
		run(a);
	}	

	private final void run(Algorithm a) {
		RealInterval side = new RealInterval(-150, 60);
		test.f_Price5_Zero(a, side);
		test.f_Rastrigin10(a, side);
		test.f_SixHumpCamelBack(a, side);
	}
}
