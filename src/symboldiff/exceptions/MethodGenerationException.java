/**
 * 
 */
package symboldiff.exceptions;

/**
 * @author nvpanov
 *
 */
public class MethodGenerationException extends ExpressionException {
	private static final long serialVersionUID = 8060653589914032264L;
	
	public MethodGenerationException(String msg, Exception e) {
		super(msg, e);
	}
}
