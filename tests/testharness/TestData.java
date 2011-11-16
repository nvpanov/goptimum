package testharness;

import net.sourceforge.interval.ia_math.RealInterval;

class TestCase {
	final String algName;
	final String functionName;
	final RealInterval searchArea;
	final int dim;
	
	public TestCase(String algName, String functionName,
			RealInterval searchArea, int dim) {
		this.algName = algName;
		this.functionName = functionName;
		this.searchArea = searchArea;
		this.dim = dim;
	}
	@Override
	public String toString() {
		return functionName + "\t" + dim + "\t" + algName + "\t" + searchArea;
	}
	public static String getHeader() {
		return "Function\tdimension\talgName\tsearchInterval";
	}
	
	@Override
	public boolean equals(Object thata) {
	    if ( !(thata instanceof TestCase) ) 
	    	return false;
	    TestCase that = (TestCase)thata;
	    
	    if (dim == that.dim && algName.equals(that.algName) && 
	    		functionName.equals(that.functionName) && 
	    		searchArea.equals(that.searchArea) 
	    	) 
	    	return true;
		return false;
	}
	@Override
	public int hashCode(){
		return dim + algName.hashCode() + functionName.hashCode() + searchArea.hashCode();
	}
}

class TestResult {
	final long time;
	final RealInterval optVal;
	final Error error;
	
	public TestResult(long time, RealInterval optVal) {
		this.time = time;
		this.optVal = optVal;
		error = null;
	}
	public TestResult(Error err) {
		time = -1;
		optVal = null;
		error = err;
	}
	public long getElapsedTime() {
		return time;
	}
	public RealInterval getOptVal() {
		return optVal;
	}
	public Error getError() {
		return error;
	}	

	@Override
	public String toString() {
		if (error != null)
			return -1 + "\t" + error.getMessage();
		return time + "\t" + optVal.wid() + "\t" + optVal;
	}
	public static String getHeader() {
		return "time\twidth\toptimum";
	}
}

public class TestData {
	private final TestCase problem;
	private final TestResult result;
	
	public TestData(String functionName, String algName, long time, RealInterval optVal, RealInterval area, int dim, Error err) {
		problem = new TestCase(algName, functionName, area, dim);
		if (time < 0) // something got wrong;
			result = new TestResult(err);
		else 
			result = new TestResult(time, optVal);
	}

	@Override
	public String toString() {
		return problem.toString() + "\t" + result.toString();
	}
	public static void pritnHeader() {
		System.out.println(TestCase.getHeader() + "\t" + TestResult.getHeader());
	}

	public TestCase getProblem() {
		return problem;
	}

	public TestResult getResult() {
		return result;
	}
}
