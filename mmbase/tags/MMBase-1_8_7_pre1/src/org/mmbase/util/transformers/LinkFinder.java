/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.util.regex.*;
import org.mmbase.util.Entry;

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

    protected static Collection  urlPatterns = new ArrayList();

    static {
        new LinkFinder().readPatterns(urlPatterns);
    }

    public LinkFinder() {
        super(XMLTEXT_WORDS);
        onlyFirstPattern = true;
    }


    protected String getConfigFile() {
        return "linkfinder.xml";
    }

    protected Collection getPatterns() {
        return urlPatterns;
    }


    protected void readDefaultPatterns(Collection patterns) {
        patterns.add(new Entry(Pattern.compile(".+@.+"),      "<a href=\"mailto:$0\">$0</a>"));
        patterns.add(new Entry(Pattern.compile("http://.+"),  "<a href=\"$0\">$0</a>"));
        patterns.add(new Entry(Pattern.compile("https://.+"), "<a href=\"$0\">$0</a>"));
        patterns.add(new Entry(Pattern.compile("ftp://.+"),   "<a href=\"$0\">$0</a>"));
        patterns.add(new Entry(Pattern.compile("www\\..+"),   "<a href=\"http://$0\">$0</a>"));
        return;
    }


    public String toString() {
        return "LINKFINDER";
    }

}
