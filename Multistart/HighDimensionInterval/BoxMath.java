package HighDimensionInterval;

import net.sourceforge.interval.ia_math.*;

public class BoxMath {
	
	public static Box midpoint(Box input){
	  double tmparray[] = new double[input.getDimension()];
	  	for(int i=0; i<tmparray.length; i++){
	  		tmparray[i] = (input.gethi(i) + input.getlo(i))/2;
	  	}
	  return new Box(tmparray);
	}
	
	public static Box intersect(Box a, Box b) throws BoxException{
		if( a.getDimension() != b.getDimension() ) throw new BoxException("intersect: Dimension of interval aren't equal");
	  double tmplo[] = new double[a.getDimension()];
	  double tmphi[] = new double[a.getDimension()];
	  	for(int i=0; i<tmplo.length; i++){
	  		tmplo[i] = Math.max(a.getlo(i), b.getlo(i));
	  		tmphi[i] = Math.min(a.gethi(i), b.gethi(i));
	  	}
	  return new Box(tmplo, tmphi);
	}
	
	public static Box union(Box a, Box b) throws BoxException{
		if( a.getDimension() != b.getDimension() ) throw new BoxException("union: Dimension of interval aren't equal");
	  double tmplo[] = new double[a.getDimension()];
	  double tmphi[] = new double[a.getDimension()];
	  	for(int i=0; i<tmplo.length; i++){
	  		tmplo[i] = Math.min(a.getlo(i), b.getlo(i));
	  		tmphi[i] = Math.max(a.gethi(i), b.gethi(i));
	  	}
	  return new Box(tmplo, tmphi);		
	}
	
	public static Box exp(Box input){
	  double tmplo[] = new double[input.getDimension()];
	  double tmphi[] = new double[input.getDimension()];
	  	for(int i=0; i<tmplo.length; i++){
	  		tmplo[i] = Math.exp(input.getlo(i));
	  		tmphi[i] = Math.exp(input.gethi(i));
	  	}
	  return new Box(tmplo, tmphi);
	}
	
	public static Box log(Box a){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
	  	for(int i=0; i<a.getDimension(); i++)
	  		tmp[i] = IAMath.log(a.getRealInterval(i));
	  return new Box(tmp);
	}
	
	public static Box add(Box a, Box b) throws BoxException{
		if( a.getDimension() != b.getDimension() ) throw new BoxException("add: Dimension of interval aren't equal");
	  double tmplo[] = new double[a.getDimension()];
	  double tmphi[] = new double[a.getDimension()];
	  	for(int i=0; i<tmplo.length; i++){
	  		tmplo[i] = a.getlo(i) + b.getlo(i);
	  		tmphi[i] = a.gethi(i) + b.gethi(i);
	  	}
	  return new Box(tmplo, tmphi);
	}
	
	public static Box sub(Box a, Box b) throws BoxException{
		if( a.getDimension() != b.getDimension() ) throw new BoxException("sub: Dimension of interval aren't equal");
     double tmplo[] = new double[a.getDimension()];
	 double tmphi[] = new double[a.getDimension()];
	  	for(int i=0; i<tmplo.length; i++){
	  		tmplo[i] = Choosing.sub_tochooselow(a.getlo(i), b.getlo(i), a.gethi(i), b.gethi(i));
	  		tmphi[i] = Choosing.sub_tochoosehigh(a.getlo(i), b.getlo(i), a.gethi(i), b.gethi(i));
	  	}
	 return new Box(tmplo, tmphi);		
	}
	
	public static Box sin(Box a){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
	  	for(int i=0; i<a.getDimension(); i++)
	  		tmp[i] = IAMath.sin(a.getRealInterval(i));
	  return new Box(tmp);
	}
	
	public static Box cos(Box a){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
	  	for(int i=0; i<a.getDimension(); i++)
	  		tmp[i] = IAMath.cos(a.getRealInterval(i));
	  return new Box(tmp);		
	}
	
	public static Box tan(Box a){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
		for(int i=0; i<a.getDimension(); i++)
			tmp[i] = IAMath.tan(a.getRealInterval(i));
	  return new Box(tmp);	
	}
	
	public static Box power(Box a, Box pow){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
	  	for(int i=0; i<a.getDimension(); i++)
	  		tmp[i] = IAMath.power(a.getRealInterval(i), pow.getRealInterval(i));
	  return new Box(tmp);
	}
	
	public static Box evenRoot(Box a, double root){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
	  	for(int i=0; i<a.getDimension(); i++)
	  		tmp[i] = IAMath.evenRoot(a.getRealInterval(i), root);
	  return new Box(tmp);
	}
	
	public static Box oddRoot(Box a, double root){
	  RealInterval tmp[] = new RealInterval[a.getDimension()];
	  	for(int i=0; i<a.getDimension(); i++)
	  		tmp[i] = IAMath.oddRoot(a.getRealInterval(i), root);
	  return new Box(tmp);
	}
	
	public static Box evenPower(Box a, double pow){
	  double[] lo = new double[a.getDimension()];
	  double[] hi = new double[a.getDimension()];
		for(int i=0; i< a.getDimension(); i++){
			lo[i] = Math.min( Math.pow(Math.abs(a.getlo(i)), pow), Math.pow(Math.abs(a.gethi(i)), pow) );
			hi[i] = Math.min( Math.pow(Math.abs(a.getlo(i)), pow), Math.pow(Math.abs(a.gethi(i)), pow) );
		}
	  return new Box(lo, hi);
	}
		
	public static Box oddPower(Box a, double pow){
	  double[] lo = new double[a.getDimension()];
	  double[] hi = new double[a.getDimension()];
	  	for(int i=0; i< a.getDimension(); i++){
			lo[i] = Math.min( Math.pow(a.getlo(i), pow), Math.pow(a.gethi(i), pow) );
			hi[i] = Math.min( Math.pow(a.getlo(i), pow), Math.pow(a.gethi(i), pow) );
		}
	  return new Box(lo, hi);
	}
}

class Choosing {
	protected static double sub_tochooselow(double loa, double lob, double hia, double hib) {
	  double min1, min2;
	  	min1 = Math.min(loa - hib, hib - loa);
	  	min2 = Math.min(lob - hia, hia - lob);
	  return Math.min(min1, min2);
	}
	protected static double sub_tochoosehigh(double loa, double lob, double hia, double hib) {
		  double min1, min2;
		  	min1 = Math.min(loa - hib, hib - loa);
		  	min2 = Math.min(lob - hia, hia - lob);
		  return Math.max(min1, min2);
	}
}