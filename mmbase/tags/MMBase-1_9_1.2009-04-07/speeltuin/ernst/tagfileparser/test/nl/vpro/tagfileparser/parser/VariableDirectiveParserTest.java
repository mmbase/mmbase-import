package nl.vpro.tagfileparser.parser;

import java.util.Iterator;
import java.util.List;

import nl.vpro.tagfileparser.model.TagInfo;
import nl.vpro.tagfileparser.model.VariableDirective;

public class VariableDirectiveParserTest extends TestBase {

	public void testParser(){
		Iterator<String> lines = createFileIterator("DirectivesParserTest1.tag");
		TagInfo tagInfo = new TagInfo("test");
		VariableDirectiveParser parser = new VariableDirectiveParser();
		parser.parse(lines, tagInfo);
		
		assertEquals("test", tagInfo.getName());
		
		assertEquals(1, tagInfo.getVariables().size());
		VariableDirective vd = tagInfo.getVariables().get(0);
		assertEquals("een", vd.getNameGiven());
		assertEquals("twee", vd.getNameFromAttribute());
		assertEquals("drie", vd.getAlias());
		assertEquals("vier", vd.getDescription());
		assertEquals(List.class, vd.getVariableClass());
		assertEquals(Boolean.TRUE, vd.getDeclare());
	}
}

