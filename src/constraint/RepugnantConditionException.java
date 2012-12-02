package constraint;

import net.sourceforge.interval.ia_math.exceptions.IANarrowingFaildException;

public class RepugnantConditionException extends Exception {

	public RepugnantConditionException(IANarrowingFaildException e) {
		super(e);
	}

	public RepugnantConditionException() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 796919569281925412L;

}
