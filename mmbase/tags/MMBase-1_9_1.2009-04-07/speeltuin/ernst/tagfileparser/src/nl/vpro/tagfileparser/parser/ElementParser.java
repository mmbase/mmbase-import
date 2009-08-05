package nl.vpro.tagfileparser.parser;

import java.util.Iterator;

import nl.vpro.tagfileparser.model.TagInfo;

/**
 * This Interface represents a parser for some tag file element. this can
 * be an attribute directive or something else.
 * @author Ernst Bunders
 *
 */
public interface  ElementParser  {
	/**
	 * This method should 
	 * <ul>
	 * <li>decide if the current line in the iterator contains (the start of) the element it can parse
	 * <li>read as many lines as it needs to parse the element.
	 * <li>Add the parsed element's information to the tagInfo instance.
	 * @param <T>
	 */
	public void parse(Iterator<String> lines, TagInfo tag);
}
