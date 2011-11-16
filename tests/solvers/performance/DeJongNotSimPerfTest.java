package solvers.performance;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;

import net.sourceforge.interval.ia_math.RealInterval;

import org.junit.Ignore;
import org.junit.Test;
import solvers.*;
import testharness.TestHarness;
import testharness.TestData;
import algorithms.Algorithm;

@Deprecated
public class DeJongNotSimPerfTest {
	protected Random rnd = new Random();
	protected TestHarness test = new TestHarness();

	@Deprecated
	@Ignore
	@Test//(timeout=120*1000) //120 sec
	public final void test1() {
		TreeSet<TestData> results = new TreeSet<TestData>();
		ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();
		algorithms.add(new Bisection_SrtL_CBtC_AllEqS());
		algorithms.add(new Bisection_SrtL_CBtC_BigEqS());
		algorithms.add(new Bisection_SrtL_CBtC_RndEqS());

		algorithms.add(new Bisection_SrtL_RndC_AllEqS());
		algorithms.add(new Bisection_SrtL_RndC_BigEqS());
		algorithms.add(new Bisection_SrtL_RndC_RndEqS());

		algorithms.add(new Bisection_UnsL_CBtC_AllEqS());
		algorithms.add(new Bisection_UnsL_CBtC_BigEqS());
		algorithms.add(new Bisection_UnsL_CBtC_RndEqS());

		algorithms.add(new Bisection_UnsL_RndC_AllEqS());
		algorithms.add(new Bisection_UnsL_RndC_BigEqS());
		algorithms.add(new Bisection_UnsL_RndC_RndEqS());
		
		final int dim = 5;

		for (Algorithm a : algorithms) {
			TestData r = test.f_DeJong_NotSim(a, dim);
			results.add(r);
			System.out.println("  -> " + r);
			System.gc();
		}
		for (TestData r : results)
			System.out.println(r);
	}
}
