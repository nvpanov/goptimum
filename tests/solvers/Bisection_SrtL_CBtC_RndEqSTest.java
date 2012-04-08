package solvers;

import java.util.Random;
import net.sourceforge.interval.ia_math.RealInterval;
import org.junit.Test;

import testharness.TestHarness;
import algorithms.Algorithm;

public class Bisection_SrtL_CBtC_RndEqSTest {
	protected Random rnd = new Random();
	protected TestHarness test = new TestHarness();

	//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test1() {
		Algorithm a = new Bisection_SrtL_CBtC_RndEqS();
		final int dim = rnd.nextInt(9)+1;
		RealInterval area = new RealInterval(-70, 200);
		test.f_DeJong_Zero(a, dim, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test11() {
		Algorithm a = new Bisection_SrtL_CBtC_RndEqS();
		final int dim = rnd.nextInt(9)+1;
		test.f_DeJong_NotSim(a, dim);
	}
//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test2() {
		Algorithm a = new Bisection_SrtL_CBtC_RndEqS();
		RealInterval area = new RealInterval(-1, 1);
		test.f_Price5_Zero(a, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test3() {
		Algorithm a = new Bisection_SrtL_CBtC_RndEqS();
		RealInterval area = new RealInterval(-10, 20);
		test.f_Rastrigin10(a, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test4() {
		Algorithm a = new Bisection_SrtL_CBtC_RndEqS();
		RealInterval area = new RealInterval(-1, 1);
		test.f_SixHumpCamelBack(a, area);
	}	
}
