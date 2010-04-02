import java.util.ArrayList;
import java.util.Iterator;

import Algorithm.*;
import net.sourceforge.interval.ia_math.*;
import TargetFunctions.*;

public class Benchmark {

	@SuppressWarnings("boxing")
	public static void init() {
		functions = new ArrayList<Function>();
		areas = new ArrayList<RealInterval>();
		dimensions = new ArrayList<Integer>();
		worklists = new ArrayList<WorkList>();
		choosers = new ArrayList<Chooser>();
		splitters = new ArrayList<Splitter>();
		
		
//		functions.add(new Function_DeJong_nD());
		functions.add(new Function_Rastrigin10_2D());
//		functions.add(new Function_Price5_2D());		
//		functions.add(new Function_SixHumpCamelBack_2D());
		
		areas.add(new RealInterval(-1, 1));
		areas.add(new RealInterval(-10, 10));
		areas.add(new RealInterval(-100, 100));
		areas.add(new RealInterval(-1000, 1000));
		
		dimensions.add(2);
/*
 		dimensions.add(4);		
		dimensions.add(8);		
		dimensions.add(16);		
		dimensions.add(32);
*/		
		splitters.add(new RndSideEquallySplitter() );
				
		for (int i = 0; i < areas.size(); i++) {
			for (int j = 0; j < dimensions.size(); j++) {
				Box box = new Box(dimensions.get(j), (RealInterval)areas.get(i).clone());
				worklists.add(new UnSortedWorkList(box));
				//worklists.add(new SortedWorkList(box));
			}
		}
		
//		for (int i = 0; i < worklists.size(); i++) {
			choosers.add(new RandomChooser());
			choosers.add(new CurrentBestChooser());
//		}
		
		results = new ArrayList<Result>();
	}
	
	

	
	public static void main(String[] argv) {
		
		Benchmark.init();

		Iterator<Function> functionIterator = functions.iterator();
	    while (functionIterator.hasNext()) {
	    	Function f = functionIterator.next();
			Iterator<Chooser> chooserIterator = choosers.iterator();
   			while (chooserIterator.hasNext()) {
   				Chooser c  = chooserIterator.next();
   				Iterator<Splitter> splitterIterator = splitters.iterator();
   				while (splitterIterator.hasNext()) {
   					Splitter s = splitterIterator.next();
   					Iterator<WorkList> worklistIterator = worklists.iterator(); 
   					while (worklistIterator.hasNext()) {
   						WorkList wl = worklistIterator.next().clone();
   						if(wl.size() != 1)
   							throw new IAException("Something is wrong...");

   						Result result = new Result(f, wl.getButLeftInTheList(0).getDimension(), 
   													wl.getButLeftInTheList(0).getInterval(0), c, s, wl);
   						
   						Algorithm alg = new BaseAlgorithm(wl, c, s, f);
   						long start = System.nanoTime();
	    				ArrayList<Box> optimum = alg.solve();
	    				long time = System.nanoTime() - start;
	    				
	    				result.addResult(time, optimum);
	    				results.add(result);
	    			}
	    		}
	    	}
	    }
	    
	    Iterator<Result> ri = results.iterator();
	    System.out.println( Result.getTitle() );
	    while (ri.hasNext()) {
	    	System.out.println( ri.next() );
	    }
	}

		private static ArrayList<Function> functions;
		private static ArrayList<RealInterval> areas;
		private static ArrayList<Integer> dimensions;
		private static ArrayList<Chooser> choosers;
		private static ArrayList<Splitter> splitters;
		private static ArrayList<WorkList> worklists;

		private static ArrayList<Result> results;

		private static class Result {
			private String function;
			private int dim;
			private RealInterval area; 
			private String chooser;
			private String splitter;
			private String worklist;
			private long time;
			ArrayList<Box> optimum;
			
			
			private static final String delimiter = "\t";
			
			public Result(Function f, int dim, RealInterval area, Chooser c, Splitter s, WorkList wl) {
				function = f.getClass().getName();
				this.dim = dim;
				this.area = area;
				chooser = c.getClass().getName();
				splitter = s.getClass().getName();
				worklist = wl.getClass().getName();
			}
			
			public void addResult(long time, ArrayList<Box> optimum) {
				this.time = time;
				this.optimum = optimum;				
			}

			public static String getTitle() {
				String str =  "function" + delimiter + "dimension" + delimiter + "searchArea" + delimiter + 
						"chooser" + delimiter + "splitter" + delimiter + "workList" + delimiter + "time" + delimiter + "optimum1VAL";
				return str;
			}
			@Override
			public String toString() {
				String str = function + delimiter + dim + delimiter + area + delimiter + chooser + delimiter + splitter + delimiter + worklist + delimiter + time;
				Iterator<Box> i = optimum.iterator();
			    while (i.hasNext()) {
			    	Box opt = i.next();
			    	str +=  delimiter + opt.getFunctionValue();
			    }
			    return str;				
			}
			
		}		
	
}
