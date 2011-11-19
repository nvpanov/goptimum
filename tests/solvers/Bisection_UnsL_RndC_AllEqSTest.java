package solvers;

import java.util.Random;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Ignore;
import org.junit.Test;

import testharness.TestHarness;
import algorithms.Algorithm;

public class Bisection_UnsL_RndC_AllEqSTest {
	protected Random rnd = new Random();
	protected TestHarness test = new TestHarness();
	
//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test1() {
		Algorithm a = new Bisection_UnsL_RndC_AllEqS();
		final int dim = rnd.nextInt(5)+1;
		RealInterval area = new RealInterval(-10, 10);
		test.f_DeJong_Zero(a, dim, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test11() {
		Algorithm a = new Bisection_UnsL_RndC_AllEqS();
		final int dim = rnd.nextInt(5)+1;
		test.f_DeJong_NotSim(a, dim);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test2() {
		Algorithm a = new Bisection_UnsL_RndC_AllEqS();
		RealInterval area = new RealInterval(-10, 10);
		test.f_Price5_Zero(a, area);
	}
//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test3() {
		Algorithm a = new Bisection_UnsL_RndC_AllEqS();
		RealInterval area = new RealInterval(-10, 10);
		test.f_Rastrigin10(a, area);
	}
//	@Ignore
	@Test 
	//(timeout=12*1000) //12 sec
	public final void test4() {
		Algorithm a = new Bisection_UnsL_RndC_AllEqS();
		RealInterval area = new RealInterval(-10, 10);
		test.f_SixHumpCamelBack(a, area);
	}
}
