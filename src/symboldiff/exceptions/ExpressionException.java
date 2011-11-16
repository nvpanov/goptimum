package symboldiff.exceptions;

public class ExpressionException extends Exception {
	private static final long serialVersionUID = 473137576527987952L;
	
	public ExpressionException(String msg, Exception e) {
		super(msg, e);
	}

	public ExpressionException(String msg) {
		super(msg);
		fillInStackTrace();
	}
}
