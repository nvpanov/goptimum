import algorithms.Algorithm;
import algorithms.RndProveBaseAlgorithm;


public class RandomProveMain {

	public static void main(String[] args) {
//		int dim = Integer.parseInt(args[0]);
//		System.out.println("DIMENSION = " + dim);
//		System.out.println("Time\tIterations\tMinWidth\tAverageWidth\tMaxWidth");
//		for (int i = 0; i < 1; i++) {
//			RndProveBaseAlgorithm a = new RndProveBaseAlgorithm(dim, 100);
//			a.solve();
//		}

		/*
		RndProveBaseAlgorithm a = new RndProveBaseAlgorithm(2, 100);
		a.solve();
		a.printCollectedStats();
		*/
		
		long i = 0;
		int pow = 1;
		double sum = 0, delta;
		final double epsilon = 1e-20;
		double threshold = 1e-6;
		do {
			i++;
			delta = sum;
			sum += 1.0/Math.pow(i, pow);
			delta = sum - delta; 
			if (delta < threshold) {
				System.out.println("Sum = " + sum);
				threshold /= 10.0;
			}
		} while (delta > epsilon);
		System.out.println("Sum = " + sum + ", i = " + i);
	}

}
