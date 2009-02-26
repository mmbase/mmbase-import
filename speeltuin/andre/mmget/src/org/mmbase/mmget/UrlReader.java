package org.mmbase.mmget;

import java.io.*;
import java.net.*;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Reads a web resource an returns its tags that may contain links to other resources. 
 * Originally made for webpages, altered to be able to parse css-files.
 *
 * @author  &lt;&gt;
 * @version $Rev$
 */
public final class UrlReader {
	private static final Logger log = Logging.getLoggerInstance(UrlReader.class);
	
	protected URL url = null;
	protected BufferedReader inrdr = null;
	protected static int contenttype = -1;
		
	/** 
	 * Constructor
	 */
	public UrlReader(String str) throws IOException, MalformedURLException {
		this(new URL(str));
	}

	public UrlReader(URL url) throws IOException {
	    this.url = url;
		
		// open the URL for reading
		URLConnection uc = url.openConnection();
		contenttype = MMGet.contentType(uc);
		if (contenttype == MMGet.CONTENTTYPE_HTML) {
		    return HTMLReader(url);
		} else {
		    return CSSReader(url);
		}
		//log.debug("contenttype: " + contenttype);
		//inrdr = new BufferedReader(new InputStreamReader(url.openStream()));
	}
	
	protected int getContentType() {
	    return contenttype;
	}

	/** 
	 * Gets all links that look they can contain to resources
	 * @return        list contain links
	 */
	public ArrayList<String> getLinks() throws IOException {
	    return new ArrayList<String>();
	}
	
	public void close() throws IOException {
		//inrdr.close();
	}
	
	/* return a String representation of this object */
	public String toString() {
		return "UrlReader[" + url.toString() + "]";
	}
	
	/** 
	 * Main method for command-line invocation.
	 * @param argv    the argument String array
	 */
	public static void main (String[] args) throws MalformedURLException, IOException {
		if (args.length == 0) {
			System.out.println("Usage: UrlReaders [...]");
			return;
		}
		for (int i = 0; i < args.length; i++) {
			UrlReader ur = new UrlReader(args[0]);
			String tag;
			while ((tag = ur.nextTag()) != null ) {
				System.out.println(tag);
			}
			ur.close();
		}		
	}

}
