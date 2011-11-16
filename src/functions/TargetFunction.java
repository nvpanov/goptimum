package functions;
import net.sourceforge.interval.ia_math.RealInterval;

@Deprecated
// 1d function. Use Function instead
public interface TargetFunction {
	public RealInterval calculateValue(RealInterval interval);
}
