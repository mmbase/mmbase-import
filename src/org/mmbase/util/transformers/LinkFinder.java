/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.io.*;
import java.util.regex.*;

import org.mmbase.util.logging.*;


/**
 * Finds links in the Character String, and makes them 'clickable' for HTML (using a-tags). This
 * implementation is very simple and straightforward. It contains a list of regular expression which
 * are matched on all 'words'. It ignores existing XML markup, and also avoids trailing dots and
 * comments and surrounding quotes and parentheses.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public class LinkFinder extends ReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(LinkFinder.class);

    protected static Map urlPatterns;
    
    private final static String AHREF = "<a href=\"";
    static {
        // should perhaps be configurable
        urlPatterns = new HashMap();
        urlPatterns.put(Pattern.compile(".+@.+"),      AHREF + "mailto:"); 
        urlPatterns.put(Pattern.compile("http://.+"),  AHREF); 
        urlPatterns.put(Pattern.compile("https://.+"), AHREF); 
        urlPatterns.put(Pattern.compile("ftp://.+"),   AHREF); 
    }
    
    /**
     * Takes one word (as a StringBuffer), checks if it can be made clickable, and if so, does it.
     *
     * @return true if a replacement occured
     */
    protected boolean link(StringBuffer word, Writer writer) throws IOException {
        int l = word.length();
        StringBuffer postFix = null;
        String w;
        if (l > 0) {

            postFix = new StringBuffer();

            // surrounding quotes might look like &quot; because of earlier escaping, so we take those out of consideration.
            w = word.toString();
            while (w.endsWith("&quot;")) {
                postFix.insert(0, "&quot;");
                l -= 6;
                word.setLength(l);
                w = word.toString();
            }

            // to allow for . , and like in the end, we tear those of.
            char d = word.charAt(l - 1); 
            while (! Character.isLetterOrDigit(d)) {
                postFix.insert(0, d);
                word.setLength(--l);
                if (l == 0) break;
                d = word.charAt(l - 1); 
            }
        }

        w = word.toString();

        // stuff in the beginning:
        while(w.startsWith("&quot;")) {
            writer.write("&quot;");
            word.delete(0, 6);
            l -= 6;
            w = word.toString();
        }

        // ready to make the anchor now.

        Iterator i  = urlPatterns.entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Pattern p = (Pattern) entry.getKey();
            if (p.matcher(w).matches()) {
                writer.write((String) entry.getValue() + w + "\">" + w + "</a>");
                if (postFix != null) {
                    writer.write(postFix.toString());
                } 
                return true;
            }
        }

        writer.write(w);
        if (postFix != null) {
            writer.write(postFix.toString());
        } 
        return false;
    }

    public Writer transform(Reader r, Writer w) {
        int replaced = 0;
        StringBuffer word = new StringBuffer();  // current word
        boolean translating = true;
        try {
            log.trace("Starting linkfinder");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c == '<') {  // don't do it in existing tags and attributes
                    translating = false;
                    if (link(word, w)) replaced++;
                    w.write(c);
                } else if (c == '>') {
                    translating = true;
                    word.setLength(0);
                    w.write(c);
                } else if (! translating) {
                    w.write(c);
                } else {
                    if (Character.isWhitespace((char) c) || c == '\'' || c == '\"' || c == '(' || c == ')' ) {
                        if (link(word, w)) replaced++;
                        word.setLength(0);
                        w.write(c);
                    } else {       
                        word.append((char) c);
                    }
                }
            }
            // write last word
            if (translating && link(word, w)) replaced++;
            log.debug("Finished censor. Replaced " + replaced + " words");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }


    public String toString() {
        return "LINKFINDER";
    }

}
