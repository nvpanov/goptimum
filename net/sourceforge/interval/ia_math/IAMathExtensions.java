package net.sourceforge.interval.ia_math;

/*
 * for stuff that absent in IAMath
 */
public class IAMathExtensions {
	public static double wid(RealInterval i) {
		return Math.abs(i.hi() - i.lo());
	}

}
