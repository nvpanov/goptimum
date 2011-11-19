package solvers;

import net.sourceforge.interval.ia_math.RealInterval;
import org.junit.Ignore;
import org.junit.Test;

import testharness.TestHarness;
import algorithms.Algorithm;

public class Bisection_UnsL_CBtC_BigEqSTest {
	protected TestHarness test = new TestHarness();
	
//	@Ignore
	@Test(timeout=12*1000) //120 sec
	public final void test1() {
		Algorithm a = new Bisection_UnsL_CBtC_BigEqS();
		final int dim = 7;
		RealInterval area = new RealInterval(-70, 200);
		test.f_DeJong_Zero(a, dim, area);
	}
//	@Ignore
	@Test(timeout=12*1000) //120 sec
	public final void test11() {
		Algorithm a = new Bisection_UnsL_CBtC_BigEqS();
		final int dim = 6;
		test.f_DeJong_NotSim(a, dim);
	}
//	@Ignore
	@Test(timeout=2*1000) //2 sec
	public final void test2() {
		Algorithm a = new Bisection_UnsL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-90, 160);
		test.f_Price5_Zero(a, area);
	}
//	@Ignore
	@Test(timeout=2*1000) //2 sec
	public final void test3() {
		Algorithm a = new Bisection_UnsL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-1000, 2000);
		test.f_Rastrigin10(a, area);
	}
//	@Ignore
	@Test(timeout=2*1000) //2 sec
	public final void test4() {
		Algorithm a = new Bisection_UnsL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-1000, 200);
		test.f_SixHumpCamelBack(a, area);
	}
}
