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
	
	public static Pattern urlPattern;
	public static Pattern importPattern;
	/*
	 list-style-image: url("images/bullet.png");
	 @import "form.css";
     @import url("mystyle.css");
	*/
	public static final String URL_PATTERN = "[\\w\\s?]url\\((.*)\\)[\\s;]";
	public static final String IMPORT_PATTERN = "@import\\s+[\"\'](.*)[\"\']";

	/** 
	 * Tags to be looking for.
	 */
	public final static String[] wantTags = {
		"<a ", "<A ",
		"<applet", "<APPLET",
		"<area", "<AREA",
		"<embed", "<EMBED",
		"<frame", "<FRAME",
		//"<input", "<INPUT",		// TODO: <input type="image" src=".." />
		"<iframe", "<IFRAME",
		"<img", "<IMG",
		"<link", "<LINK",
		"<object", "<OBJECT",
		"<script", "<SCRIPT",
	};
	
	/** 
	 * Constructor
	 */
	public UrlReader(String str) throws IOException, MalformedURLException {
		this(new URL(str));
	}

	public UrlReader(URL url) throws IOException {
	    this.url = url;
		urlPattern = Pattern.compile(URL_PATTERN);
		importPattern = Pattern.compile(IMPORT_PATTERN);
		
		// open the URL for reading
		URLConnection uc = url.openConnection();
		contenttype = MMGet.contentType(uc);
		//log.debug("contenttype: " + contenttype);
		inrdr = new BufferedReader(new InputStreamReader(url.openStream()));
	}
	
	protected int getContentType() {
	    return contenttype;
	}
	
	/**
	 * Reads a css file and passes it to a regexp parser. 
	 * Does that twice: first looking for url(..), second for @import "...".
	 * @param  
	 * @return 
	 */
	public ArrayList<String> readCSS() throws IOException {
		String line;
		ArrayList<String> l = new ArrayList<String>();
		while((line = inrdr.readLine()) != null) {
			l.addAll(parseCSS(line, urlPattern));
			l.addAll(parseCSS(line, importPattern));
		}
		return l;
	}

	/**
	 * Parses a css file and passes it to a regexp parser
	 * @param  
	 * @return 
	 */
    private ArrayList<String> parseCSS(String line, Pattern p) {
        ArrayList<String> list = new ArrayList<String>();
        Matcher m = p.matcher(line);
        while (m.find()) {
            String match = m.group(1);
            if (match == null) {
                break;
            }
            //if (log.isDebugEnabled()) log.debug("Found match: " + match);
            // remove first and last " if any
            if (match.indexOf("\"") > -1) match = match.replace("\"", "");
            list.add(match);
        }
        return list;
    }

	/** 
	 * Gets all links that look they can contain to resources
	 * @return        list contain links
	 */
	public ArrayList<String> getLinks() throws IOException {
		if (contenttype == MMGet.CONTENTTYPE_CSS) {
		    return readCSS();
		} else {
            ArrayList<String> al = new ArrayList<String>();
            String tag;
            while ((tag = nextTag()) != null) {
                for (int i = 0; i < wantTags.length; i++) {
                    if (tag.startsWith(wantTags[i])) {
                        String link = readLinkformTag(tag);
                        if (link != null) al.add(link);
                        continue;	// optimization
                    }
                }
            }
            return al;
        }
	}
	
    /**
      * Read the next tag 
      * @return a complete tag, like &lt;img scr="foo.gif" /&gt;  
      */ 
	public String nextTag() throws IOException {
		int i;
		while ((i = inrdr.read()) != -1) {
			char c = (char)i;
			if (c == '<') {
				String tag = readTag();
				return tag;
			}
		}
		return null;
	}
	
    public static String readLinkformTag(String tag) {
        String href = null;
        href = MMGet.extractHREF(tag);
        /*
        try {
            href = SiteExport.extractHREF(tag);
        } catch (MalformedURLException e) {
            log.error(e);
            return null;
        } 
        */
        if (href.startsWith("mailto") || href.startsWith("#") || href.startsWith("javascript")) {
            //log.info(href + " -- NOT FOLLOWING (yet)");	// Can't be used (for now), TODO: todo's here?
            return null;
        }
        return href;
    }

	/** 
	 * Reads a tags and its contents.
	 * @return  the tag
	 */
	protected String readTag() throws IOException {
    	StringBuffer theTag = new StringBuffer("<");
    	int i = '<';
    	while (i != '>' && (i = inrdr.read()) != -1) {
    		theTag.append((char)i);
    	}
		return theTag.toString();
	}
	
	public void close() throws IOException {
		inrdr.close();
	}
	
	/* return a String represantation of this object */
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
