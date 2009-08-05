package nl.vpro.tagfileparser.util;

import nl.vpro.tagfileparser.parser.BasicTagParserException;

public class IllegalPathException extends BasicTagParserException {

	public IllegalPathException(String string) {
		super(string);
	}

}
