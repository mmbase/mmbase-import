package nl.vpro.tagfileparser.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.vpro.tagfileparser.model.TagInfo;

public class RegexpParserTest extends TestBase {

	public void testParse() throws IOException {
		Iterator<String> iterator = createFileIterator("regexpTest1.tag");
		String start = "[\\s]*<%@";
		String end = "%>[\\s]*";
		final List<String> lines = new ArrayList<String>();
		
		RegexpParser parser = new RegexpParser(start, end, false){
			@Override
			protected void use(String line, TagInfo tag) {
				lines.add(line);
			}
			
		};
		parser.parse(iterator, null);
		assertEquals("Er moeten twee regels worden gevonden", 2, lines.size());
		assertEquals("eerste regel klopt niet", " <%@ attribute bladiebla %>    ",lines.get(0));
		assertEquals("tweede regel klopt niet", "	<%@= en nog ietsover meerdere regels%>",lines.get(1));
	}
}
