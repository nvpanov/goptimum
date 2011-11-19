package testharness;

import java.util.HashMap;
import java.util.LinkedList;

public class TestResultAveraging {
	private HashMap<TestCase, LinkedList<TestResult>> data = 
			new HashMap<TestCase, LinkedList<TestResult>>();
	private HashMap<TestCase, long[]> averageData = 
			new HashMap<TestCase, long[]>();
	private boolean calculated = false;	
	
	public void addResult(TestData record) {
		TestCase key = record.getProblem();
		TestResult r = record.getResult();
		LinkedList<TestResult> results = data.get(key);
		
		if (results != null) // there are some records for this problem
			assert(!results.contains(r));
		else
			results = new LinkedList<TestResult>();
		results.add(r);
		data.put(key, results);
	}
	private void calcAverage() {
		if (calculated)
			return;
		for (TestCase key : data.keySet()) {
			long average, sum = 0, max = -1, min = Long.MAX_VALUE;
			int cnt = 0;
			LinkedList<TestResult> results = data.get(key);
			for (TestResult r : results) {
				cnt++;
				long time = r.getElapsedTime();
				if (time < 0) {
					average = time;
					cnt = 1;
					break;
				}
				sum += time;
				if (time > max)
					max = time;
				if (time < min)
					min = time;
			}
			average = sum;
			if (cnt > 1)
				average = (sum - max) / (cnt-1);
			long times[] = new long[2];
			times[0] = min;
			times[1] = average;
			averageData.put(key, times);
		}
		calculated = true;
	}
	public void printAveregedResults() {
		calcAverage();
		for(TestCase key : averageData.keySet()) {
			System.out.print(key);
			long times[] = averageData.get(key);
			System.out.println("\t"+times[0]+"\t"+times[1]);
		}
	}
	public static void printHeader() {
		System.out.println(TestCase.getHeader() + "\tmin\taverage");
	}
	public int getNumOfTestCases() {
		calcAverage();
		return averageData.keySet().size();
	}

}
