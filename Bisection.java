import java.util.ArrayList;

import net.sourceforge.interval.ia_math.IAMath;
import net.sourceforge.interval.ia_math.RealInterval;


public class Bisection {

	private TargetFunction function;
	private ArrayList<RealInterval> workList;
	private double epsilonI = 1e-5;
	private double epsilonF = 1e-5;
	private int iterations = 0;
	private int maxIterations = 1000;

	public Bisection(RealInterval area, TargetFunction function) {
		this.function = function;
		workList = new ArrayList<RealInterval>();
		workList.add(area);		
	}

	public RealInterval solve() {
		RealInterval optimum = new RealInterval(); 
		do {
			optimum = extractBestInterval();
			
		} while ( doIteration(optimum) );
		
		return function.calculateValue(optimum);
	}

	private boolean doIteration(RealInterval interval) {
		if (isDone(interval))
			return false;
		RealInterval mp = IAMath.midpoint(interval);
		
		workList.add( new RealInterval(interval.lo(), mp.lo()) );
		workList.add( new RealInterval(mp.hi(), interval.hi()) );
		return true;
	}

	private boolean isDone(RealInterval i) { 
		if (width(i) < epsilonI)
			return true;
		if ( width(function.calculateValue(i) ) < epsilonF)
			return true;
		if( iterations++ > maxIterations)
			return true;
		
		return false;
	}

	private double width(RealInterval i) {
		return i.hi() - i.lo();
	}

	private RealInterval extractBestInterval() {
		double lowestBorder = Double.MAX_VALUE;
		int pos = -1;
		// found current best
		for(int i = 0; i <workList.size(); i++) {
			if (lowestBorder > function.calculateValue(workList.get(i)).lo() ) {
				lowestBorder = function.calculateValue(workList.get(i)).lo();
				pos = i;
			}
		}
		// return it
		RealInterval curOptimum = workList.get(pos);
		workList.remove(pos);
		return curOptimum;
	}
}
