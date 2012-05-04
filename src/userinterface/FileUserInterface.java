package userinterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import net.sourceforge.interval.ia_math.RealInterval;
import solvers.PointIntervalBis_SrtL_CBtC_BigEqS;
import algorithms.Algorithm;
import algorithms.StopCriterion;
import core.Box;
import functions.FunctionFactory;
import functions.FunctionNEW;

public class FileUserInterface {
	private HashMap<String, String> hashMapArgs;
	private String fStr;
	private FunctionNEW f; 
	private StopCriterion stopCriterion;
	private Algorithm algo;
	private boolean reportOptVal;
	private boolean reportOptArea;
	private String[] args;
	private File inFile;
	private File outFile;
	private int lineCounter;

	public static void main(String args[]) {
		FileUserInterface ui = new FileUserInterface();
		ui.mainFunction(args);
	}
	public FileUserInterface() {
		lineCounter = 0;
		algo = new PointIntervalBis_SrtL_CBtC_BigEqS();
		stopCriterion = new StopCriterion(algo);
		hashMapArgs = new HashMap<String, String>();
		reportOptVal = true;
		reportOptArea = true;
	}
	public void mainFunction(String args[]) {
		try{
			getAllArgs(args);
			parseArgs();
			
			if (fStr == null) {
				showToUser("!!! Error. ", "Target function hasn't been specified.");
				return;
			}				
			f = FunctionFactory.newFunction(fStr);
			Box area = getArea();
			
			algo.setProblem(f, area);
			algo.setStopCriterion(stopCriterion);
			algo.solve();
			
			showResults(algo);
		} catch (Throwable e) {
			reportError(e);
		}
	}
	private void reportError(Throwable e) {
		showToUser("!!! Error. ", e.getMessage());
	}
	private void getAllArgs(String[] args) {
		this.args = args;		
	}
	private static String addCurDirectoryToFileName(String name) {
		String nameWithPath;
		if ( name.contains(File.separator) ) { 
			//full path specified
			nameWithPath = name;
		} else { // try to look in current folder
			nameWithPath= "." + File.separator + name;
		}
		return nameWithPath;
	}
	private void openFiles() throws InputDataException {
		String inputFilePath = args[0];
		String outputFilePath = args[1];
		inputFilePath = addCurDirectoryToFileName(inputFilePath);
		outputFilePath = addCurDirectoryToFileName(outputFilePath);
		
		try {
			inFile = new File(inputFilePath);
			outFile = new File(outputFilePath);
			
			
			if (!inFile.isFile())
				throw new InputDataException("Can't read input file '" + inputFilePath + "': there is no such file.");
			if (!inFile.canRead())
				throw new InputDataException("Can't read input file '" + inputFilePath + "': not enough permissions.");
			if (!outFile.createNewFile()) {
				// probably it exists
				if (outFile.delete())
					outFile.createNewFile();
				else
					throw new InputDataException("Can't create output file '" + outputFilePath + "'");
			}
		} catch (Exception e) {
			throw new InputDataException("File operation error: '" + e.getMessage() + "'");
		}		
	}
	private void parseArgs() throws InputDataException, FileNotFoundException {
		if (args.length != 2) 
			throw new InputDataException("Please specify two arguments: input and output file names.");
		openFiles();
		parseInputFile();

	}
	private void parseInputFile() throws InputDataException, FileNotFoundException {
		Scanner scanner = new Scanner(new FileReader(inFile));
		try {
			//first use a Scanner to get each line
		    while ( scanner.hasNextLine() ){
		    	String line = scanner.nextLine();
		    	if (line != null && !line.startsWith("#"))
		    		processLine(line);
		    }
		}
		finally {
			//ensure the underlying stream is always closed
		    //this only has any effect if the item passed to the Scanner
			//constructor implements Closeable (which it does in this case).
		    scanner.close();
		}		
	}

	/** 
	   Overridable method for processing lines in different ways.
	    
	   <P>This simple default implementation expects simple name-value pairs, separated by an 
	   '=' sign. Examples of valid input : 
	   <tt>height = 167cm</tt>
	   <tt>mass =  65kg</tt>
	   <tt>disposition =  "grumpy"</tt>
	   <tt>this is the name = this is the value</tt>
	 * @throws InputDataException 
	  */
	  protected void processLine(String aLine) throws InputDataException {
		lineCounter++;
		if (aLine == null)
			  return;
		aLine = aLine.trim().toLowerCase();
	    Scanner scanner = new Scanner(aLine);
	    scanner.useDelimiter("=");
	    String name, value;
	    if ( scanner.hasNext() ){
	    	try {
	    		name = scanner.next();
	    		value = scanner.next();
	    	} catch (Exception e) {
	    		throw new InputDataException("Invalid line #" + lineCounter + " '" + aLine + "'. Each line has to look like 'name = value'.");
	    	}
    		parseInput(name, value);
	    }
	    if ( scanner.hasNext() ) // second '='
    		throw new InputDataException("Invalid line #" + lineCounter + " '" + aLine + "'. Each line has to look like 'name = value'.");
	  }

	private void parseInput(String name, String value) throws InputDataException {
		if (name == null || value == null ||
				name.isEmpty() || value.isEmpty())
			throw new InputDataException("Invalid line #" + lineCounter + ". Each line has to look like 'name = value'.");
//System.out.println("name=" + name + "; value="+value+ ".");		
		switch (name) {
		case "f":
			if (fStr != null)
				throw new InputDataException("Invalid line #" + lineCounter + ". Redifinition of the function. Previous expression: " + fStr + ".");
			fStr = value;
			break;
		case "reportarea":
			setReportArea(value);
			break;
		case "reportvalue":
			setReportValue(value);
			break;
		case "maxsteps":
			setMaxSteps(value);
			break;
		case "epsilon":
			setEpsilon(value);
		default:
			Object wasValue = hashMapArgs.put(name, value);
			if (wasValue != null) {
				throw new InputDataException("Invalid line #" + lineCounter + ". Redifinition of variable '" + name + "'. Previous value: " + wasValue + ".");
			}
		}		
	}
	private void setEpsilon(String value) throws InputDataException {
		double epsilon;
		try {
			epsilon = Double.valueOf(value);
		} catch (NumberFormatException e) {
			throw new InputDataException("Invalid line #" + lineCounter + ". Epsilon can only be a double value.");
		}
		stopCriterion.setFMaxPrecision(epsilon);
	}
	private void setMaxSteps(String value) throws InputDataException {
		int steps;
		try {
			steps = Integer.valueOf(value);
		} catch (NumberFormatException e) {
			throw new InputDataException("Invalid line #" + lineCounter + ". Maximum steps can only be an integer value.");
		}
		stopCriterion.setMaxIterations(steps);		
	}
	private Box getArea() throws InputDataException {
		ArrayList<String> fArgs = f.getVariables();
		int dim = f.getDimension();
		Box area = new Box(dim);
//		checkDimensionsAreEqual();
		for(String arg : hashMapArgs.keySet()) {
			if (fArgs.contains(arg)) {
				setVariableRange(arg, area);
			} 
		}
		if (dim == 1 && f.getVariables().get(0).equals("0xDEADBEEF")) {
			if (hashMapArgs.size() == 0) {
				reportOptArea = false;
				return new Box(dim, new RealInterval(0));
			} else
				dim = 0;
		}
		for (int i = 0; i < dim; i++)
			if (area.getInterval(i) == null)
				throw new InputDataException("Range for variable '" + fArgs.get(i) + "' hasn't been set. Specify something like '" + fArgs.get(i) + " = [-1, 0.1]'.");
		hashMapArgs.keySet().removeAll(fArgs);
		if (hashMapArgs.keySet().size() > 0)
			throw new InputDataException("Extra variable (" + hashMapArgs.keySet().iterator().next() + ") which is not used in the target function is set. Treating this as a potential error and exiting.");
		return area;
	}
	private void setVariableRange(String variable, Box area) throws InputDataException {
		String value = hashMapArgs.get(variable);
		RealInterval interval;
		try {
			interval = RealInterval.valueOf(value);
		} catch (NumberFormatException e) {
			throw new InputDataException("Wrong value (" + value + ") for variable '" 
							+ variable + "' caused the following error: " + e.getMessage());
		}
		int argNum = f.getVariableNum(variable);
		area.setInterval(argNum, interval);
	}
/*	
	private void checkDimensionsAreEqual() throws InputDataException {
		int dim = f.getDimension();
		final int argSize = hashMapArgs.size();
		if (dim != argSize) {
			if (dim == 1 && f.getVariables().get(0).equals("0xDEADBEEF"))
				if (argSize == 0)
					return;
				else
					dim = 0;
			ArrayList<String> fArgs = f.getVariables();
			String msg = "The target function has " + dim + " variable";
			if ( dim != 1) 
				msg += "s";
			msg += " ( ";
			for (int i = 0; i < dim; i++) { 
				String s = fArgs.get(i);
				msg += s + " ";
			}
			msg += ") while " + argSize + " variable";
			if ( argSize != 1) 
				msg += "s";
			msg += " were provided: ";
			for(String arg : hashMapArgs.keySet())
				msg += arg + " ";			
			throw new InputDataException(msg);
		}
	}
*/

	private void setReportArea(String value) throws InputDataException {
		reportOptArea = getBinaryValue(value);		
	}
	private void setReportValue(String value) throws InputDataException {
		reportOptVal = getBinaryValue(value);		
	}
	private boolean getBinaryValue(String value) throws InputDataException {
		if (value.equals("false") || value.equals("0"))
			return false;
		else if (value.equals("true") || value.equals("1"))
			return true;
		else
			throw new InputDataException("Invalid line #" + lineCounter + ". Binary variable can only accept the following values: '0, 1, false, true'");
	}
	private void showResults(Algorithm algo) {
		if (reportOptVal) {
			RealInterval optVal = algo.getOptimumValue();
			showToUser("Optimum value:", optVal);
		}
		if (reportOptArea) {			
			Box[] optArea = algo.getOptimumArea();
			showToUser("Optimum area:", optArea);		
		}
	}
	private void showToUser(String label, Object... values) {
		StringBuilder sb = new StringBuilder(label).append("\n");
		for (Object obj : values)
			sb.append("   ").append(obj).append("\n");
		String msg = sb.toString();
		System.out.println(msg);
		if (outFile != null) {
			try {
				FileWriter out = new FileWriter(outFile, true); // append
				out.append(msg);
				out.close();
			} catch (Exception e) {
				System.out.println("CAN'T WRITE TO OUTPUT FILE: " + e.getMessage());
			}
		}
	}
}
