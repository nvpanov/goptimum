package Multistart;

import net.sourceforge.interval.ia_math.RealInterval;


public class MultistartRealInterval extends Thread{
  private double opt;
  private double[] points;
  private RealInterval interval;
  private double h;
  private double eps;
  private TargetFunction function;
  
  	public MultistartRealInterval(RealInterval interval, double[] points, double h, double eps, TargetFunction function) {
  	  int size = points.length;
  		this.points = new double[size];
  		this.interval = interval;
  		this.function = function;
		this.h = h;
  		this.eps = eps;  		
  		for(int i=0; i<size; i++){
  			this.points[i] = points[i];
  		}
	}
  	
  	public void run(){
  		solver();
  	}
  	
  	public void solver(int nthrds) throws RuntimeException{
  		if( nthrds<1 ) throw new RuntimeException("number of threads below than zero");
  		if( nthrds>this.points.length ) nthrds = this.points.length;
  	  solveRealInterval[] algorithm = new solveRealInterval[this.points.length];
	  double[] optpoints = new double[this.points.length];
  	  	for(int i=0; i<nthrds; i++){
  		  	algorithm[i] = new solveRealInterval(this.interval, this.points[i], this.h, this.eps, this.function);
  	  		algorithm[i].start();
  	  	} 	
  	  	for(int i=nthrds; i<this.points.length; i++){
  	  		algorithm[i] = new solveRealInterval(this.interval, this.points[i], this.h, this.eps, this.function);
  	  		algorithm[i].solver();
  	  		optpoints[i] = algorithm[i].getOpt();
  	  	}
  	  	
  	  	try{
  	  		for(int i=0; i<algorithm.length; i++){
  	  			algorithm[i].join();
  	  			optpoints[i] = algorithm[i].getOpt();
  	  		}
  	  		opt = minimum(optpoints);
  	  	}catch(Exception e){
  	  		System.err.println(e.getMessage());
  	  		return;
  	  	}
  	}
  	
  	public void solver(){
  	  solveRealInterval algorithm;;
  	  double optpoints[] = new double[this.points.length];
  	  	for(int i=0; i<this.points.length; i++){
  	  		algorithm = new solveRealInterval(this.interval, this.points[i], this.h, this.eps, this.function);
  	  		algorithm.solver();
  	  		optpoints[i] = algorithm.getOpt();
  	  	}
  	  	opt = minimum(optpoints);
  	}

  	private double minimum(double[] a){
  	  double min = a[0];
  	  	for(int i=1; i<a.length; i++)
  	  		if( this.function.calculateValue(a[i])<this.function.calculateValue(min) ) min = a[i];
  	  return min; 
  	}
  	
  	public double getOpt(){
  	  return opt;
  	}
}
