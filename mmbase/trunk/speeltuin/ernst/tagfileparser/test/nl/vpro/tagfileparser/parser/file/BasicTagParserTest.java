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
		
		//test the attributes
		assertEquals(1, tagInfo.getVariables().size());
		//assertEquals("name", tagInfo.getAttributes().get(0).getName());
		
		//test the variables
		
		//test the tag 
	}
}
