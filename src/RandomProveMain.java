import algorithms.Algorithm;
import algorithms.RndProveBaseAlgorithm;


public class RandomProveMain {

	public static void main(String[] args) {
		int dim = Integer.parseInt(args[0]);
		System.out.println("DIMENSION = " + dim);
		System.out.println("Time\tIterations\tMinWidth\tAverageWidth\tMaxWidth");
		for (int i = 0; i < 1; i++) {
			Algorithm a = new RndProveBaseAlgorithm(dim, 100);
			a.solve();
		}
	}

}
