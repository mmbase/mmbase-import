/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.util.regex.*;

/**
 * Finds links in the Character String, and makes them 'clickable' for HTML (using a-tags). This
 * implementation is very simple and straightforward. It contains a list of regular expression which
 * are matched on all 'words'. It ignores existing XML markup, and also avoids trailing dots and
 * comments and surrounding quotes and parentheses.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public class LinkFinder extends RegexpReplacer {

    protected static Map urlPatterns = new LinkedHashMap();
    
    static {        
        new LinkFinder().readPatterns(urlPatterns);   
    }

    protected String getConfigFile() {
        return "linkfinder.xml";
    }

    protected Map getPatterns() {        
        return urlPatterns;
    }


    protected void readDefaultPatterns(Map patterns) {

        patterns.put(Pattern.compile(".+@.+"),      "<a href=\"mailto:$0\">$0</a>");
        patterns.put(Pattern.compile("http://.+"),  "<a href=\"$0\">$0</a>"); 
        patterns.put(Pattern.compile("https://.+"), "<a href=\"$0\">$0</a>"); 
        patterns.put(Pattern.compile("ftp://.+"),   "<a href=\"$0\">$0</a>"); 
        patterns.put(Pattern.compile("www\\..+"),   "<a href=\"http://$0\">$0</a>");
        return;
    }


    public String toString() {
        return "LINKFINDER";
    }

}
