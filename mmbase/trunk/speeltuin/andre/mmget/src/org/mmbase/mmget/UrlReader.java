package org.mmbase.mmget;

import java.io.IOException;
import java.net.*;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Reads a web resource an returns its tags that may contain links to other resources. 
 *
 * @author Andr&eacute; van Toly
 * @version $Id: UrlReader.java,v 1.3 2009-02-27 10:38:28 andre Exp $
 */
public abstract class UrlReader {
	private static final Logger log = Logging.getLoggerInstance(UrlReader.class);
	
	/** 
	 * Gets all links to resources
	 *
	 * @return  list with tags that can contain links
	 */
	protected abstract ArrayList<String> getLinks() throws IOException;
	
	/** 
	 * Contenttype from urlconection
	 *
	 * @return  contenttype constant
	 */
	protected abstract int getContentType();

	protected abstract void close() throws IOException;

}
