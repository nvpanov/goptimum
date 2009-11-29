import net.sourceforge.interval.ia_math.RealInterval;


public interface TargetFunction {
	public RealInterval calculateValue(RealInterval interval);
}
