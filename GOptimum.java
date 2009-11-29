import net.sourceforge.interval.ia_math.RealInterval;


public class GOptimum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RealInterval go_val, area;
		
		area = new RealInterval(-10, 10);
		TargetFunction function = new TestProblem1();
		Bisection algorithm = new Bisection(area, function);
		
		go_val = algorithm.solve();
		System.out.println("Global min value = " + go_val);

	}

}
