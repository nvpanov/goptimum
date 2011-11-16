package functions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import symboldiff.exceptions.IncorrectExpression;
import core.Box;
import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;

/*
public class Function_SumSin_nD extends Function {
	private Function_SumSin_nD() {
		throw new RuntimeException("Not Implemented");
	}	
	@Override
	public void calculate(Box b) {
		int dim = b.getDimension();
		RealInterval result = new RealInterval(0.0);
		
		for(int i = 0; i < dim; i++) {
			result = IAMath.add( result, IAMath.sin(b.getInterval(i)) );
		}
		
		b.setFunctionValue(result);
	}

	@Override
	public double calculatePoint(double... point) {
		throw new NotImplementedException();
	}

	@Override
	protected String toStringHuman() {
		// TODO Auto-generated method stub
		return null;
	}

}
*/