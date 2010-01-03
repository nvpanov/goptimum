package HighDimensionInterval;

public class BoxException extends RuntimeException{
	public BoxException(String errormessage){
		super(errormessage);
	}	
	public BoxException(){
		super("Something wrong");
	}
}
