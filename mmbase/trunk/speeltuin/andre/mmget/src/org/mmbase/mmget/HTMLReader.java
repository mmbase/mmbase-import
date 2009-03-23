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
 * @version $Id: HTMLReader.java,v 1.5 2009-03-23 22:30:22 andre Exp $
 */
public final class HTMLReader extends UrlReader {
    private static final Logger log = Logging.getLoggerInstance(HTMLReader.class);
    
    private URLConnection uc = null;
    private BufferedReader inrdr = null;

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
                    String link = extractHREF(tag);
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
    
    protected URL getUrl() {
        return uc.getURL();
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
     * Extracts the link from a tag.
     *
     * @param tag    the first parameter
     * @return       a link to a resource hopefully
     */
    public static String extractHREF(String tag) {
        String lcTag = tag.toLowerCase(); 
        String attr;
        int p1, p2, p3, p4;
        
        if (lcTag.startsWith("<a ") || lcTag.startsWith("<link ") || lcTag.startsWith("<area ")) {
            attr = "href=";
        } else {
            attr = "src=";       // TODO: src's of css in html
        }
        
        p1 = lcTag.indexOf(attr);
        if (p1 < 0) {
            log.warn("Can't find attribute '" + attr + "' in '" + tag + "'");
            return null;
        }
        
        p2 = tag.indexOf("=", p1);
        p3 = tag.indexOf("\"", p2);
        p4 = tag.indexOf("\"", p3 + 1);
        if (p3 < 0 || p4 < 0) {
            log.warn("Invalide attribute '" + attr + "' in '" + tag + "'");
        }
        
        String href = tag.substring(p3 + 1, p4);
        if (href.startsWith("mailto") || href.startsWith("#") || href.startsWith("javascript")) {
            //log.info(href + " -- NOT FOLLOWING (yet)");   // Can't be used (for now), TODO: todo's here?
            return null;
        }
        
        return href;
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
        //log.debug("closing...");
        inrdr.close();
    }

}
