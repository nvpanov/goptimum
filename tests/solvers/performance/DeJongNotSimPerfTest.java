package solvers.performance;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import solvers.*;
import testharness.TestHarness;
import algorithms.Algorithm;

public class DeJongNotSimPerfTest {
	@Rule
	public MethodRule benchmarkRun = new BenchmarkRule();
	  
	protected TestHarness test = new TestHarness();
	protected final int runs = 3;
	protected final int warmup = 1;
	int dim = 16;

	//(w=1,i=3)
	// dim									1		2		4		8	10	16
	// Bisection_SrtL_CBtC_AllEqS			0.2		0.2		0.2		0.2		--
	// Bisection_SrtL_CBtC_BigEqS			0		0		0.17	8.7		--
	// PointIntervalBis_SrtL_CBtC_BigEqS	0.2		0.2		0.2		0.18	0.3
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongNotSimPerf_BisectionAll() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		run(a, dim);
	}
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongNotSimPerf_BisectionBig() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		int dim = 5;
		run(a, dim);
	}	
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongNotSimPerf_PointInterval() {
		Algorithm a = new PointIntervalBis_SrtL_CBtC_BigEqS();
		int dim = 5;
		run(a, dim);
	}	
	
	
	
	private final void run(Algorithm a, int dim) {
		test.f_DeJong_NotSim(a, dim);
	}
}
