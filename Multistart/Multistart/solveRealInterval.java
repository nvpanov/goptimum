package Multistart;

import net.sourceforge.interval.ia_math.*;

public class solveRealInterval extends Thread {
  private double eps;
  private double h;
  private double opt;
  private RealInterval interval;
  private double point;
  private TargetFunction function;
  	
  	public solveRealInterval(RealInterval input, double ipoint, double ih, double ieps, TargetFunction function) throws RuntimeException{
  		this.eps = ieps;
  		if( ih>(input.hi()-input.lo()) ) System.err.println("Warring: h is greater than interval size");
  		this.h = ih;
  		this.interval = input;
  		this.point = ipoint;
  		if(!isBelongToInterval(this.point)) throw new RuntimeException("point doesn't belong to an interval");
  		this.function = function;  		
  	}
  	
  	@Override
  	public void run(){
  		solver();
  	}
  	
  	public double solver(){
  	  double tmph = this.h;
  	  double xmin = this.point, x;
  		while( tmph>this.eps ){
  			x = xmin;
  			xmin = getmin(xmin, tmph);
  			if( x == xmin || this.function.calculateValue(x) == this.function.calculateValue(xmin) ) tmph /= 10;
  		}
  	  return (opt = xmin);
  	}
  	
  	private double getmin(double x, double h){
  		if( isBelongToInterval(x+h) && isBelongToInterval(x-h) ){
  			if( this.function.calculateValue(x-h) <= this.function.calculateValue(x+h) ){
  				if( this.function.calculateValue(x-h)<=this.function.calculateValue(x) )	return x-h;
  				else return x;
  			}
  			else{
  				if( this.function.calculateValue(x)<=this.function.calculateValue(x+h) )    return x;
  				else return x+h;
  			}
  		}
  		if( isBelongToInterval(x+h) ){
  			if( this.function.calculateValue(x) <= this.function.calculateValue(x+h) ) return x;
  			else return x+h;  			
  		}
  		if( isBelongToInterval(x-h) ){
  			if( this.function.calculateValue(x-h) <= this.function.calculateValue(x) ) return x-h;
  			else return x;  			
  		}
  	  return 0;
  	}
  	
  	private boolean isBelongToInterval(double x){
  		if( x>=this.interval.lo() && x<=this.interval.hi() ) return true;
  	  return false;
  	}
  	
  	public double getOpt(){
  	  return this.opt;
  	}
}
