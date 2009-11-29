import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;


public class TestProblem1 implements TargetFunction {
	public RealInterval calculateValue(RealInterval interval) {
		return IAMath.sin(interval);
	}

}
