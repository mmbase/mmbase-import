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
 * Reads a css web resource an returns its tags that may contain links to other resources. 
 *
 *   list-style-image: url("images/bullet.png");
 *   @import "form.css";
 *   @import url("mystyle.css");
 *
 * @author Andr&eacute; van Toly
 * @version $Id: CSSReader.java,v 1.6 2009-03-24 20:02:07 andre Exp $
 */
public final class CSSReader extends UrlReader {
    private static final Logger log = Logging.getLoggerInstance(CSSReader.class);
    
    private HttpURLConnection huc = null;
    private BufferedReader inrdr = null;

    /*
     list-style-image: url("images/bullet.png");
     @import "form.css";
     @import url("mystyle.css");
    */
    public static final String URL_PATTERN = "[\\w\\s?]url\\((.*)\\)[\\s;]";
    private static final Pattern urlPattern = Pattern.compile(URL_PATTERN);;
    public static final String IMPORT_PATTERN = "@import\\s+[\"\'](.*)[\"\']";
    private static final Pattern importPattern = Pattern.compile(IMPORT_PATTERN);
    
    public CSSReader(HttpURLConnection huc) throws IOException {
        this.huc = huc;
        inrdr = new BufferedReader(new InputStreamReader(huc.getInputStream()));
    }
    
    protected int getContentType() {
        return MMGet.contentType(huc);
    }
    
    protected URL getUrl() {
        return huc.getURL();
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
        String line;
        ArrayList<String> l = new ArrayList<String>();
        while((line = inrdr.readLine()) != null) {
            l.addAll( parseCSS(line, urlPattern) );
            l.addAll( parseCSS(line, importPattern) );
        }
        return l;
    }

    public void close() throws IOException {
        inrdr.close();
        if (huc != null) { 
            huc.disconnect(); 
        }
    }
    
}
