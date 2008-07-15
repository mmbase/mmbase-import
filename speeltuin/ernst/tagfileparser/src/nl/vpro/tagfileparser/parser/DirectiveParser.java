package nl.vpro.tagfileparser.parser;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.vpro.tagfileparser.model.TagInfo;

/**
 * This abstract class parses tag directives. When it finds a given directive,
 * the template method directiveFound is called.
 * 
 * It than creates Attribute instances for each attribute, and calls the
 * attributeFound() method.
 * 
 * @author Ernst Bunders
 * 
 */
public abstract class DirectiveParser extends RegexpParser {

	/**
	 * @param directive
	 *            the name of the directive you want to parse
	 */
	public DirectiveParser(String directive) {
		super("^[\\s]*<%@[\\s]*" + fixDirective(directive), "%>[\\s]*$", false);
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

	/**
	 * Remove the <%\@ and %> bits from the line
	 * 
	 * @param line
	 */
	private final String cleanup(String line) {
		line = line.trim();
		if (line.startsWith("<%@")) {
			line = line.substring(3, line.length());
		} else {
			throw new BasicTagParserException("Line did not start with expected '<%@'. [" + line + "]");
		}
		if (line.endsWith("%>")) {
			line = line.substring(0, line.length() - 2);
		}
		line = line.trim();
		return line;
	}

	/**
	 * @param attributeString
	 * @return
	 * @throws IllegalAttributeException
	 *             when the give string could not be parsed to a name value
	 *             pair.
	 */
	private final Attribute createAttribute(String attributeString) {
		StringTokenizer tokenizer = new StringTokenizer(attributeString, "=", false);
		if (tokenizer.countTokens() == 2) {
			String aName = tokenizer.nextToken().trim();
			String aValue = tokenizer.nextToken().trim();
			// trim the quotes from the attribute value;
			aValue = aValue.substring(1, aValue.length() - 1);
			return new Attribute(aName, aValue);
		}
		throw new IllegalAttributeException("illegal string format for attribute and value: [" + attributeString + "]");
	}

	private static final String fixDirective(String directive) {
		if (!directive.startsWith(" ")) {
			return directive + " ";
		}
		return directive;
	}

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

	/**
	 * This interface is used to creat simple objects that can take the string
	 * value of an attribute, and convert it into a valid object value and
	 * inject it into the directive model object.
	 * 
	 * @author ebunders
	 * 
	 */
	public interface PropertySetter {
		/**
		 * @param value
		 *            some attribute string value.
		 * @throws IllegalDirectiveException
		 *             when the value can not be converted to a valid object.
		 */
		public void setProperty(String value);
	}

}
