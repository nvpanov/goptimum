package userinterface;

public class InputDataException extends Exception {

	private static final long serialVersionUID = -2287717305938356982L;

	public InputDataException(String message) {
		super(message);
	}

	public InputDataException(Throwable cause) {
		super(cause);
	}

	public InputDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InputDataException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
