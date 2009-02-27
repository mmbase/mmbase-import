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
 * Initiates an UrlReader for the url's contenttype and passes it the initiated
 * UrlConnection.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: UrlReaders.java,v 1.1 2009-02-27 10:38:28 andre Exp $
 */
public class UrlReaders {
	private static final Logger log = Logging.getLoggerInstance(UrlReaders.class);
	
	protected static UrlReader reader;
	protected URL url = null;
	protected static int contenttype = -1;

	public static UrlReader getUrlReader(URL url) throws IOException {
		
		URLConnection uc = url.openConnection();
		contenttype = MMGet.contentType(uc);
		log.debug("contenttype: " + contenttype);
		
		if (contenttype == MMGet.CONTENTTYPE_CSS) {
		    reader = new CSSReader(uc);
		} else {
		    reader = new HTMLReader(uc);
		}
		return reader;
	}
	
	protected int getContentType() {
	    return contenttype;
	}
	
}
