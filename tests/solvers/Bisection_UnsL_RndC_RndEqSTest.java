package solvers;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import testharness.TestHarness;
import algorithms.Algorithm;

public class Bisection_UnsL_RndC_RndEqSTest {
	protected TestHarness test = new TestHarness();

	@Test(timeout=12*1000) //12 sec
	public final void test1() {
		Algorithm a = new Bisection_UnsL_RndC_RndEqS();
		final int dim = 2;
		RealInterval area = new RealInterval(-10, 2);
		test.f_DeJong_Zero(a, dim, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test11() {
		Algorithm a = new Bisection_UnsL_RndC_RndEqS();
		final int dim = 1;
		test.f_DeJong_NotSim(a, dim);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test2() {
		Algorithm a = new Bisection_UnsL_RndC_RndEqS();
		RealInterval area = new RealInterval(-9, 1.6);
		test.f_Price5_Zero(a, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test3() {
		Algorithm a = new Bisection_UnsL_RndC_RndEqS();
		RealInterval area = new RealInterval(-1, 2);
		test.f_Rastrigin10(a, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test4() {
		Algorithm a = new Bisection_UnsL_RndC_RndEqS();
		RealInterval area = new RealInterval(-1, 2);
		test.f_SixHumpCamelBack(a, area); // lots of chances that it will fail
	}
	
}
