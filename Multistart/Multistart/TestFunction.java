package Multistart;

public class TestFunction implements TargetFunction{

	@Override
	public double calculateValue(double point) {
		//return (Math.cos(point)-Math.sin(point));
		return (Math.pow(point - 3, 3));
	}
}
