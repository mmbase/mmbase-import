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
    private static final long serialVersionUID = 0L;

    protected static Collection<Entry<Pattern,String>> urlPatterns = new ArrayList<Entry<Pattern,String>>();

    static {
        new LinkFinder().readPatterns(urlPatterns);
    }

    public LinkFinder() {
        super(XMLTEXT_WORDS);
        onlyFirstPattern = true;
        replaceInA = false;
    }


    @Override
    protected String getConfigFile() {
        return "linkfinder.xml";
    }

    @Override
    protected Collection<Entry<Pattern,String>> getPatterns() {
        return urlPatterns;
    }


    @Override
    protected void readDefaultPatterns(Collection<Entry<Pattern,String>> patterns) {

        patterns.add(new Entry<Pattern,String>(Pattern.compile(".+@.+"),      "<a href=\"mailto:$0\">$0</a>"));
        patterns.add(new Entry<Pattern,String>(Pattern.compile("http://.+"),  "<a href=\"$0\">$0</a>"));
        patterns.add(new Entry<Pattern,String>(Pattern.compile("https://.+"), "<a href=\"$0\">$0</a>"));
        patterns.add(new Entry<Pattern,String>(Pattern.compile("ftp://.+"),   "<a href=\"$0\">$0</a>"));
        patterns.add(new Entry<Pattern,String>(Pattern.compile("www\\..+"),   "<a href=\"http://$0\">$0</a>"));
        return;
    }


    @Override
    public String toString() {
        return "LINKFINDER";
    }

}
