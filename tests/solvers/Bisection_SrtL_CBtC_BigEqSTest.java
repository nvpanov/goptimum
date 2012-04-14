package solvers;

import java.util.Random;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Test;

import testharness.TestHarness;
import testharness.TestData;
import algorithms.Algorithm;

public class Bisection_SrtL_CBtC_BigEqSTest {
	protected Random rnd = new Random();
	protected TestHarness test = new TestHarness();
	protected TestData result = null;

//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test1() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		final int dim = rnd.nextInt(9)+1;
		RealInterval area = new RealInterval(-rnd.nextInt(70), rnd.nextInt(200));
		result = test.f_DeJong_Zero(a, dim, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test1p() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		final int dim = 10;
		RealInterval area = new RealInterval(-700, 200);
		result = test.f_DeJong_Zero(a, dim, area);
	}
//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test11() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		final int dim = rnd.nextInt(9)+1;
		result = test.f_DeJong_NotSim(a, dim);
	}
//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test2() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-1000, 2000);
		result = test.f_Price5_Zero(a, area);
	}
//	@Ignore
	@Test//(timeout=12*1000) //12 sec
	public final void test3() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-1000, 2000);
		result = test.f_Rastrigin10(a, area);
	}
	
//	@Ignore
	@Test//(timeout=12*1000) //12 sec
	public final void test4() {
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		//RealInterval area = new RealInterval(-1000, 2000);
		RealInterval area = new RealInterval(-1, 1);
		result = test.f_SixHumpCamelBack(a, area);
	}
/*	
	@Test(timeout=30*1000) //30 sec
	public final void test5() {
		int dim = 7;
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-1000, 2000);
		result = test.f_RosenbrockGn(a, dim, area);
	}
	@Test//(timeout=30*1000) //30 sec
	public final void test5_1() {
		int dim = 2;
		Algorithm a = new Bisection_SrtL_CBtC_BigEqS();
		RealInterval area = new RealInterval(-10, 10);
		result = test.f_RosenbrockGn(a, dim, area);
	}
*/	
}
