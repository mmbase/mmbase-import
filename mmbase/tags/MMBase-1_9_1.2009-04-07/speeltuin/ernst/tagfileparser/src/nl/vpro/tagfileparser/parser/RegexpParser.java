package nl.vpro.tagfileparser.parser;

import java.util.Iterator;
import org.apache.commons.logging.*;

import nl.vpro.tagfileparser.model.TagInfo;
import nl.vpro.util.StringUtil;

/**
 * Matches the lines of the iterator with a regular expression, and calls
 * template methods according to the result. This class is not thread safe!
 * TODO: test
 * @author Ernst Bunders
 * 
 */
public abstract class RegexpParser implements ElementParser {
	
	private final Log log = LogFactory.getLog(RegexpParser.class.getName()); 

	private String startPattern;
	private String endPattern;
	/**
	 * When this goes to 'true' the parser stops iterating and returns. at that
	 * time the field is set back to false for the next run. It should be set to
	 * 'true' bij specializations of this class
	 */
	
	//if this is true, and one match is found the parser quits.
	private boolean findOne = false;

	protected TagInfo tag;

	public RegexpParser(String startPattern, String endPattern, boolean findOne) {
		this.startPattern = startPattern;
		this.endPattern = endPattern;
		this.findOne = findOne;
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.vpro.tagfileparser.parser.ElementParser#parse(java.util.Iterator,
	 *      nl.vpro.tagfileparser.model.TagInfo)
	 */
	public final void parse(Iterator<String> lines, TagInfo tag) {
		String construction = "";
		boolean reading = false;
		while (lines.hasNext()) {
			this.tag = tag;
			String line = lines.next();
			log.info("considering line: ["+line+"]");
			if( reading == false){
				//look for the start pattern
				if (StringUtil.startsWith(line, startPattern)) {
					construction = line;
					if (StringUtil.endsWith(line, endPattern)) {
						//construction complete
						use(construction, tag);
						if(findOne){
							return;
						}else{
							//start again;
							construction = "";
						}
					}else{
						//end pattern not on same line
						reading = true;
					}
				}
			}else{
				//add this line to the construction, preserving the newline
				construction = construction + "\n" + line;
				if (StringUtil.endsWith(line, endPattern)) {
					//construction complete
					use(construction, tag);
					if(findOne){
						return;
					}else{
						//start again
						construction = "";
						reading = false;
					}
				}
			}
		}
	}


	protected abstract void use(String line, TagInfo tag);
}
