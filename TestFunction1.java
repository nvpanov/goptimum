import java.util.ArrayList;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;


/*
 * This function is not more than a \sum x_i^2
 */
public class TestFunction1 implements Function {

	@Override
	public void calculateF(Box b) {
		int dim = b.getDimension();
		RealInterval result = new RealInterval(0);
		for(int i = 0; i < dim; i++) {
			IAMath.add( result, IAMath.evenPower(b.getInterval(i), 2) );
		}
		b.setFunctionValue(result);
	}

	
	// For testing purposes
	public static void main(String[] args) {


	}	
}
