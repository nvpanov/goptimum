package symboldiff.exceptions;

public class IncorrectExpression extends ExpressionException {
	private static final long serialVersionUID = 663137676537907942L;

	public IncorrectExpression(String msg, Exception e) {
		super(msg, e);
	}

	public IncorrectExpression(String msg) {
		super(msg);
	}
}