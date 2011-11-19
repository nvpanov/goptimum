package solvers;


import java.util.Random;
import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import testharness.TestHarness;
import testharness.TestData;
import algorithms.Algorithm;

public class Bisection_SrtL_CBtC_AllEqSTest {
	protected Random rnd = new Random();
	protected TestHarness test = new TestHarness();
	protected TestData result = null;
	
	@BeforeClass 
	public static void printHeader() {
		TestData.printHeader();
	}
	@After
    public void printResult() {
		System.out.println(result);
    }
	
	@Test(timeout=12*1000) //12 sec
	public final void test1() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		final int dim = rnd.nextInt(5)+1;
		RealInterval area = new RealInterval(-700, 200);
		result = test.f_DeJong_Zero(a, dim, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test11() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		final int dim = rnd.nextInt(5)+1;
		result = test.f_DeJong_NotSim(a, dim);
	}
//	@Ignore
	@Test(timeout=12*1000) //12 sec
	public final void test2() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		RealInterval area = new RealInterval(-900, 999);
		result = test.f_Price5_Zero(a, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test3() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		RealInterval area = new RealInterval(-1000, 1000);
		result = test.f_Rastrigin10(a, area);
	}
	@Test(timeout=12*1000) //12 sec
	public final void test4() {
		Algorithm a = new Bisection_SrtL_CBtC_AllEqS();
		RealInterval area = new RealInterval(-1000, 1000);
		result = test.f_SixHumpCamelBack(a, area);
	}
}
