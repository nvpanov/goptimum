package Multistart;

import HighDimensionInterval.*;

public class MultistartBox {
  private Box box;
  private double opt[];
  private double h;
  private double eps;
  private double points[][];
  private TargetFunction function;
  
	public MultistartBox(Box inputBox, double[][] points, double h, double eps, TargetFunction function) throws RuntimeException{
	  int size = inputBox.getDimension();
		if( points.length != size ) throw new RuntimeException("wrong size");
	  	this.box = inputBox;
		this.opt = new double[size];
		this.eps = eps;
		this.h = h;
		this.function = function;

		this.points = new double[size][];
		for(int i=0; i<size; i++){
			this.points[i] = new double[points[i].length];
			for(int j=0; j<points[i].length; j++)
				this.points[i][j] = points[i][j];
		}
	}
	
	public void solver(){
	  MultistartRealInterval algorithm[] = new MultistartRealInterval[this.box.getDimension()];
	  	for(int i=0; i<algorithm.length; i++){
	  		algorithm[i] = new MultistartRealInterval(this.box.getRealInterval(i), this.points[i], this.h, this.eps, this.function);
	  		algorithm[i].solver();
	  		opt[i] = algorithm[i].getOpt();
	  	}
	}
	
	public void solverByPoints(int nthrds) throws RuntimeException{
		if( nthrds<1 ) throw new RuntimeException("number of threads below than zero");
		if( nthrds>this.points.length ) nthrds = this.points.length;
	  MultistartRealInterval algorithm[] = new MultistartRealInterval[this.box.getDimension()];
	  	for(int i=0; i<nthrds; i++){
	  		algorithm[i] = new MultistartRealInterval(this.box.getRealInterval(i), this.points[i], this.h, this.eps, this.function);
	  		algorithm[i].start();
	  	}
	  	
	  	for(int i=nthrds; i<this.box.getDimension(); i++){
	  		algorithm[i] = new MultistartRealInterval(this.box.getRealInterval(i), this.points[i], this.h, this.eps, this.function);	  		
	  		algorithm[i].solver();
	  		this.opt[i] = algorithm[i].getOpt(); 
	  	}

	  	try{
	  		for(int i=0; i<nthrds; i++){
	  			algorithm[i].join();
	  			this.opt[i] = algorithm[i].getOpt();
	  		}
	  	}catch(Exception e){
	  		System.err.println(e.getMessage());
	  		return;
	  	}
	}
	
	public void solverByCoordinates(int nthrds) throws RuntimeException{
		if( nthrds<1 ) throw new RuntimeException("number of threads below than zero");
		if( nthrds>this.points.length ) nthrds = this.points.length;
	  MultistartRealInterval algorithm[] = new MultistartRealInterval[this.box.getDimension()];
	  	for(int i=0; i<algorithm.length; i++){
	  		algorithm[i] = new MultistartRealInterval(this.box.getRealInterval(i), this.points[i], this.h, this.eps, this.function);
		  	algorithm[i].solver(nthrds);
		  	opt[i] = algorithm[i].getOpt();
	  	}	
	}
	
	public double[] getOpt(){
	  return this.opt;
	}
}
