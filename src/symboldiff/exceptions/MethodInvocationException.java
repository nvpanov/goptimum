/**
 * 
 */
package symboldiff.exceptions;

/**
 * @author nvpanov
 *
 */
public class MethodInvocationException extends ExpressionException {
	private static final long serialVersionUID = 8060653589914032264L;
	
	public MethodInvocationException(String msg, Exception e) {
		super(msg, e);
	}
}
