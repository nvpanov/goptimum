package symboldiff.exceptions;

public class DifferentiateExpression extends ExpressionException {
	private static final long serialVersionUID = 8609092361923567L;

	public DifferentiateExpression(String msg, Exception e) {
		super(msg, e);
	}

	public DifferentiateExpression(String msg) {
		super(msg);
	}
}