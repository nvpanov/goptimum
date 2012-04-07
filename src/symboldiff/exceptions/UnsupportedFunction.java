package symboldiff.exceptions;

public class UnsupportedFunction extends IncorrectExpression {
	private static final long serialVersionUID = 613133669527907041L;

	public UnsupportedFunction(String msg, Exception e) {
		super(msg, e);
	}

	public UnsupportedFunction(String msg) {
		super(msg);
	}

	public UnsupportedFunction(UnsupportedOperationException e) {
		super(e.getMessage(), e);
	}
}