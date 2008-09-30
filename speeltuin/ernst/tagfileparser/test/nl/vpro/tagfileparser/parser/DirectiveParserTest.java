package nl.vpro.tagfileparser.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.vpro.tagfileparser.model.TagInfo;
import nl.vpro.tagfileparser.parser.DirectiveParser.Attribute;

import junit.framework.TestCase;

public class DirectiveParserTest extends TestCase {

	public void testParser() {
		String line = "<%@   test foo =    \"bar\"  name= \"disco\" hi=\"ho	\" empty=\"\" %>";
		final List<Attribute> attributes = new ArrayList<Attribute>();

		// this is a bit weird, but the boolean has to be
		// final, and it is not mutable, so this is a workaround.
		final Boolean[] found = new Boolean[] { false };
		DirectiveParser tdp = createParser(attributes, found);

		// now parse the test line
		List<String> l = new ArrayList<String>();
		l.add(line);
		tdp.parse(l.iterator(), new TagInfo("test"));
		assertEquals(Boolean.TRUE, found[0]);
		assertEquals(4, attributes.size());
		assertEquals("foo", attributes.get(0).getName());
		assertEquals("name", attributes.get(1).getName());
		assertEquals("hi", attributes.get(2).getName());

		assertEquals("bar", attributes.get(0).getValue());
		assertEquals("disco", attributes.get(1).getValue());
		assertEquals("ho	", attributes.get(2).getValue());
		assertEquals("", attributes.get(3).getValue());

	}

	/**
	 * This method tests the bug where dash (-) characters are not valid as part
	 * of attribute names
	 */
	public void testDashBug() {
		final List<Attribute> attributes = new ArrayList<Attribute>();
		final Boolean[] found = new Boolean[] { false };
		DirectiveParser tdp = createParser(attributes, found);

		tdp.parse(Arrays.asList(new String[] { "<%@test name-dash=\"value\" %>" }).iterator(), new TagInfo("test"));

		assertEquals(1, attributes.size());
		assertEquals("name-dash", attributes.get(0).getName());
		assertEquals("value", attributes.get(0).getValue());

	}

	/**
	 * When the value of an attribute is has newlines (consists of more than one
	 * line, the lines are joined, and the newlines disappear.
	 */
	public void testNewlineInAttributeBug() {
		final List<Attribute> attributes = new ArrayList<Attribute>();
		final Boolean[] found = new Boolean[] { false };
		DirectiveParser tdp = createParser(attributes, found);

		tdp.parse(Arrays.asList(new String[] { "<%@test name=\"regel een\nregeltwee\" %>" }).iterator(), new TagInfo("test"));

		assertEquals(1, attributes.size());
		assertEquals("regel een\nregeltwee", attributes.get(0).getValue());
	}

	private DirectiveParser createParser(final List<Attribute> attributes, final Boolean[] found) {
		DirectiveParser tdp = new DirectiveParser("test") {
			protected void attributeFound(Attribute attribute) {
				attributes.add(attribute);
			}

			protected void directiveFound() {
				found[0] = Boolean.TRUE;
			}
		};
		return tdp;
	}
}
