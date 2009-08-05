package nl.vpro.tagfileparser.parser.file;

import nl.vpro.tagfileparser.parser.AttributeDirectiveParser;
import nl.vpro.tagfileparser.parser.TagDirectiveParser;
import nl.vpro.tagfileparser.parser.VariableDirectiveParser;

/**
 * This tag parser parses simple tagfiles (xx.tag).
 * It parses the following directives:
 * <ul>
 * <li>tag
 * <li>attribute
 * <li>variable
 * </ul>
 * It has one special feature: before the tag file is parsed, a scan will be made for 
 * an include of a tag fragment. it should be something like:
 * <code><%@ include file="fieldinit.tagf" %></code>
 * The include will than be resolved (if possible) and included before the tag is parsed
 * 
 * @author Ernst bunders
 *
 */
public class BasicTagParser extends AbstractTagParser {

		public BasicTagParser(){
			setIncludeExtension("tagf");
			setTagExtension("tag");
			addElementParser(new TagDirectiveParser());
			addElementParser(new VariableDirectiveParser());
			addElementParser(new AttributeDirectiveParser());
		}

	

}
