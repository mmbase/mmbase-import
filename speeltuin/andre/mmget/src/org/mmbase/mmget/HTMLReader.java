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
 *
 * @author Andr&eacute; van Toly
 * @version $Id: HTMLReader.java,v 1.2 2009-02-27 10:45:07 andre Exp $
 */
public class HTMLReader extends UrlReader {
    private static final Logger log = Logging.getLoggerInstance(HTMLReader.class);
    
    protected URLConnection uc = null;
    protected BufferedReader inrdr = null;

    /** 
     * Tags to be looking for.
     */
    public final static String[] wantTags = {
        "<a ", "<A ",
        "<applet", "<APPLET",
        "<area", "<AREA",
        "<embed", "<EMBED",
        "<frame", "<FRAME",
        //"<input", "<INPUT",       // TODO: <input type="image" src=".." />
        "<iframe", "<IFRAME",
        "<img", "<IMG",
        "<link", "<LINK",
        "<object", "<OBJECT",
        "<script", "<SCRIPT",
    };
    
    public HTMLReader(URLConnection uc) throws IOException {
        this.uc = uc;
        inrdr = new BufferedReader(new InputStreamReader(uc.getInputStream()));
    }
    
    /** 
     * Gets all links that look they can contain to resources
     * @return        list contain links
     */
    public ArrayList<String> getLinks() throws IOException {
        ArrayList<String> al = new ArrayList<String>();
        String tag;
        while ((tag = nextTag()) != null) {
            for (int i = 0; i < wantTags.length; i++) {
                if (tag.startsWith(wantTags[i])) {
                    String link = readLinkformTag(tag);
                    if (link != null) al.add(link);
                    continue;   // optimization
                }
            }
        }
        return al;
    }

    protected int getContentType() {
        return MMGet.contentType(uc);
    }
    
    public static String readLinkformTag(String tag) {
        String href = null;
        href = MMGet.extractHREF(tag);

        if (href.startsWith("mailto") || href.startsWith("#") || href.startsWith("javascript")) {
            //log.info(href + " -- NOT FOLLOWING (yet)");   // Can't be used (for now), TODO: todo's here?
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
    
    public void close() throws IOException {
        inrdr.close();
    }

}
