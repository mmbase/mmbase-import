package nl.vpro.tagfileparser.parser;

/**
 * @author Ernst Bunders
 *
 */
public class BasicTagParserException extends RuntimeException {

	public BasicTagParserException() {
	}

	public BasicTagParserException(String message) {
		super(message);
	}

	public BasicTagParserException(Throwable cause) {
		super(cause);
	}

	public BasicTagParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
