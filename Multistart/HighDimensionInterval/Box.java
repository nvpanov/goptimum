package HighDimensionInterval;

import net.sourceforge.interval.ia_math.RealInterval;


public class Box {
  private double[] lo;
  private double[] hi;
  private int dimension;
  
  	public Box(double inputlo[], double inputhi[]) throws BoxException{
  		if( inputlo.length != inputhi.length ){
  			throw new BoxException("Size of bounds incorrect");
  		}
  		for(int i=0; i<inputlo.length; i++){
  			if( inputlo[i]>inputhi[i] ) throw new BoxException("Constructor: low bounds must be lower or equal than high bounds");
  		}
  		this.dimension = inputlo.length;
  		this.lo = new double[inputlo.length];
  		this.hi = new double[inputhi.length];
  		//System.arraycopy(this.lo, 0, inputlo, 0, inputlo.length);
  		//System.arraycopy(this.hi, 0, inputhi, 0, inputhi.length);
  		for(int i=0; i<inputhi.length; i++){
  			this.lo[i] = inputlo[i];
  			this.hi[i] = inputhi[i];
  		}
  	}
  	
  	public Box(double inputarray[]) throws BoxException{
  		this.dimension = inputarray.length;
  		this.lo = new double[inputarray.length];
  		this.hi = new double[inputarray.length];
  		for(int i=0; i<inputarray.length; i++){
  			this.lo[i] = -Math.abs(inputarray[i]);
  			this.hi[i] = Math.abs(inputarray[i]);
  		}
  	}
  	
  	public Box(RealInterval[] array){
  		this.dimension = array.length;
  		this.lo = new double[this.dimension];
  		this.hi = new double[this.dimension];
  		for(int i=0; i<array.length; i++){
  			this.lo[i] = array[i].lo();
  			this.hi[i] = array[i].hi();
  		}
  		
  	}
  	
  	public Box(Box[] array){
  		this.dimension = 0;
  		for(int i=0; i<array.length; i++)
  			 this.dimension += array[i].getDimension();
  		
  		this.hi = new double[this.dimension];
  		this.lo = new double[this.dimension];
  	  int k=0;
  		for(int i=0; i<array.length; i++)
  			for(int j=0; j<array[i].getDimension(); j++){
  				this.lo[k] = array[i].getlo(j);
  				this.hi[k] = array[i].gethi(j);
  				k++;
  			}
  	}
  	
  	public String toString(){
  	  String S = "[" + this.lo[0] + ", " + this.hi[0] + "]";
  	  	for(int i=1; i<this.lo.length; i++)
  	  		S += "x[" + this.lo[i] + ", " + this.hi[i] + "]";
  	  return S;
  	}
  	
  	public double[] getlo(){
  	  return this.lo;
  	}
	
  	public double getlo(int index) throws BoxException{
  		if( index<0 || index>=lo.length ) throw new BoxException("getlo: Incorrect index");
  	  return this.lo[index];
  	}
  	
  	public double[] gethi(){
  	  return this.hi;
  	}
  	
  	public double gethi(int index) throws BoxException{
  		if( index<0 || index>=hi.length ) throw new BoxException("gethi: Incorrect index");
  	  return this.hi[index];
  	}
  	
  	public int getDimension(){
  	  return dimension;
  	}
  	
  	public RealInterval getRealInterval(int index) throws BoxException{
  		if( index<0 || index>=hi.length ) throw new BoxException("gethi: Incorrect index");
  	  return new RealInterval(this.lo[index], this.hi[index]);
  	}
}
