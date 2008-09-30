package nl.vpro.tagfileparser.parser.file;

import java.io.File;

import nl.vpro.tagfileparser.model.TagInfo;
import nl.vpro.tagfileparser.parser.TestBase;

public class BasicTagParserTest extends TestBase {

	public void testParser(){
		TagParser tagParser = new BasicTagParser();
		String root  = new File(".").getAbsolutePath();
		root = root.substring(0, root.lastIndexOf("/"));
		
		String tagFilePath = root + "/test/resources/tags/sometag.tag";
		File tagFile = new File(tagFilePath);
		if(!tagFile.isFile() || !tagFile.canRead()){
			throw new RuntimeException(String.format("can not find file: %s", tagFilePath));
		}
		
		TagInfo tagInfo = tagParser.parseTag(tagFile);
		
		//test the tag directive
		assertNotNull(tagInfo.getTagDirective());
		assertEquals("empty", tagInfo.getTagDirective().getBodyContent());
		assertEquals("This is a description.\nAnd there is a second line.\nAnd a third...", tagInfo.getTagDirective().getDescription());
		assertNull(tagInfo.getTagDirective().getDisplayName());
		System.out.println(tagInfo.getTagDirective().getDescription());
		
		//test the attribute directives
		assertEquals("name", tagInfo.getAttributes().get(0).getName());
		assertEquals("The title and so on", tagInfo.getAttributes().get(0).getDescription());
		assertEquals("description", tagInfo.getAttributes().get(1).getName());
		assertEquals("some describing words", tagInfo.getAttributes().get(1).getDescription());
		
		
		
		//test the variable directives
		
		//assertEquals(1, tagInfo.getVariables().size());
		
		//test the tag 
	}
}
