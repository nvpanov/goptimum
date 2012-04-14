package solvers.performance;


import org.junit.Ignore;
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
	int dim = 32;

	//(w=1,i=3) 4/9/12
	// dim									1		2		4		8		16		32	
	// Bisection_SrtL_CBtC_BigEqS			0		0.03	0.12	0.56	4.48	42.45	
	// Bisection_SrtL_CBtC_AllEqS			0		0.17	8.7		55.45	--		--
	// PointIntervalBis_SrtL_CBtC_BigEqS	0		0.02	0.02	0.06	0.15	0.3
	
	@Ignore
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
		run(a, dim);
	}	
	
	@BenchmarkOptions(benchmarkRounds = runs, warmupRounds = warmup)
	@Test
	public final void DeJongNotSimPerf_PointInterval() {
		Algorithm a = new PointIntervalBis_SrtL_CBtC_BigEqS();
		run(a, dim);
	}	

	private final void run(Algorithm a, int dim) {
		test.f_DeJong_NotSim(a, dim);
	}
}
