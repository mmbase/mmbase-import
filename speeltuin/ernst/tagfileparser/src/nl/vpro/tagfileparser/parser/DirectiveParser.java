package nl.vpro.tagfileparser.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.vpro.tagfileparser.model.TagInfo;

public abstract class DirectiveParser extends RegexpParser {

	/**
	 * @param directive
	 *            the name of the directive you want to parse
	 */
	public DirectiveParser(String directive) {
		super("^[\\s]*<%@[\\s]*" + directive, "%>[\\s]*$", false);
	}

	@Override
	protected final void use(String line, TagInfo tag) {
		// trim the start and end bits off of the tag line (<%@ .. %>)
		line = cleanup(line);
		directiveFound();

		// make one string for each attribute-value pair, assuming
		// that there might be whitespaces surrounding the equals operator
		Pattern attPattern = Pattern.compile("[a-zA-Z0-9_\\-]+\\s*=\\s*\"[^\"]*\"");
		Matcher attMatcher = attPattern.matcher(line);
		int start = 0;

		while (attMatcher.find(start)) {
			String attString = line.substring(attMatcher.start(), attMatcher.end());
			attributeFound(createAttribute(attString));
			start = attMatcher.end();
		}
	}

	/**
	 * @param attributeString
	 * @return
	 * @throws IllegalAttributeException
	 *             when the give string could not be parsed to a name value pair.
	 */
	private Attribute createAttribute(String attributeString) {
		StringTokenizer tokenizer = new StringTokenizer(attributeString, "=", false);
		if (tokenizer.countTokens() == 2) {
			String aName = tokenizer.nextToken().trim();
			String aValue = tokenizer.nextToken().trim();
			//trim the quotes from the attribute value;
			aValue = aValue.substring(1, aValue.length() - 1);
			return new Attribute(aName, aValue);
		}
		throw new IllegalAttributeException("illegal string format for attribute and value: [" + attributeString + "]");
	}

	/**
	 * 
	 * @param line
	 */
	String cleanup(String line) {
		line = line.trim();
		if (line.startsWith("<%@")) {
			line = line.substring(3, line.length());
		} else {
			throw new RuntimeException("Line did not start with expected '<%@'. [" + line + "]");
		}
		if (line.endsWith("%>")) {
			line = line.substring(0, line.length() - 2);
		}
		line = line.trim();
		return line;
	}

	/**
	 * Template method that will be called when the directive is found.
	 */
	protected abstract void directiveFound();

	/**
	 * Template method that will be called when an attribute is found.
	 * 
	 * @param name
	 * @param value
	 */
	protected abstract void attributeFound(Attribute attribute);

	static class Attribute {
		private String name;
		private String value;

		public Attribute(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

	}
	

	
	public interface PropertySetter{
		public void setProperty(String value);
	}

}
