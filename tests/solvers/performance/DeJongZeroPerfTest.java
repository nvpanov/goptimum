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


public class DeJongZeroPerfTest {
	@Rule
	public MethodRule benchmarkRun = new BenchmarkRule();
	  
	protected TestHarness test = new TestHarness();
	protected final int runs = 2;
	protected final int warmup = 1;
	int dim = 32;

	//(w=1,i=3) 4/9/12
	// dim									1		2		4		8		16		32	
	// Bisection_SrtL_CBtC_AllEqS			0		0.02	0.09	1.54	--		--
	// Bisection_SrtL_CBtC_BigEqS			0		0.02	0.06	0.1		0.48	1.54	
	// PointIntervalBis_SrtL_CBtC_BigEqS	0		0.01	0.02	0.04	0.21	0.27

	@Ignore
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
		test.f_DeJong_Zero(a, dim, side);
	}
}
