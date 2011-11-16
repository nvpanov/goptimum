/**
 * 
 */
package interval;

/**
 * @author nvpanov
 *
 */
public class IMath {
/*	
	// +
	public static Interval add(Interval a, Interval b) {
		return new Interval(a.lo() + b.lo(), a.hi() + b.hi());
	}
	public static Interval add(Interval a, double b) {
		return new Interval(a.lo() + b, a.hi() + b);
	}
	public static Interval add(double b, Interval a) {
		return add(a, b);
	}
	
	// -
	public static Interval sub(Interval a, Interval b) {
		return new Interval(a.lo() - b.hi(), a.hi() - b.lo());
	}
	public static Interval sub(Interval a, double b) {
		return new Interval(a.lo() - b, a.hi() - b);
	}
	public static Interval sub(double a, Interval b) {
		return new Interval(a - b.hi(), a - b.lo());
	}
/*	
	// *
	public static Interval mul(Interval x, Interval y) {
		final double xl = x.lo();
		final double xu = x.hi();
		final double yl = y.lo();
		final double yu = y.hi();

if (xl < 0)
if (xu>0)
if (yl<0)
if (yu>0) // M * M
return new Interval(min( (xl * yu), (xu * yl)), max( (xl * yl), (xu * yu)), true);
else                    // M * N
return I(rnd.mul_down(xu, yl), rnd.mul_up(xl, yl), true);
else
if (interval_lib::user::is_pos(yu)) // M * P
return I(rnd.mul_down(xl, yu), rnd.mul_up(xu, yu), true);
else                    // M * Z
return I(static_cast<T>(0), static_cast<T>(0), true);
else
if (interval_lib::user::is_neg(yl))
if (interval_lib::user::is_pos(yu)) // N * M
return I(rnd.mul_down(xl, yu), rnd.mul_up(xl, yl), true);
else                    // N * N
return I(rnd.mul_down(xu, yu), rnd.mul_up(xl, yl), true);
else
if (interval_lib::user::is_pos(yu)) // N * P
return I(rnd.mul_down(xl, yu), rnd.mul_up(xu, yl), true);
else                    // N * Z
return I(static_cast<T>(0), static_cast<T>(0), true);
else
if (interval_lib::user::is_pos(xu))
if (interval_lib::user::is_neg(yl))
if (interval_lib::user::is_pos(yu)) // P * M
return I(rnd.mul_down(xu, yl), rnd.mul_up(xu, yu), true);
else                    // P * N
return I(rnd.mul_down(xu, yl), rnd.mul_up(xl, yu), true);
else
if (interval_lib::user::is_pos(yu)) // P * P
return I(rnd.mul_down(xl, yl), rnd.mul_up(xu, yu), true);
else                    // P * Z
return I(static_cast<T>(0), static_cast<T>(0), true);
else                        // Z * ?
return I(static_cast<T>(0), static_cast<T>(0), true);
}
*/	
	
	
	
}
