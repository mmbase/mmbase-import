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
import org.mmbase.util.ResourceWatcher;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.util.Entry;

import org.mmbase.util.logging.*;


/**
 * Finds regexps in the Character String, and replaces them. The replaced regexps can be found in a configuration file 'regexps.xml' (if it is present).
 * It ignores existing XML markup, and also avoids trailing dots and comments and surrounding quotes and parentheses.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class RegexpReplacer extends ChunkedTransformer {
    private static final Logger log = Logging.getLoggerInstance(RegexpReplacer.class);

    /**
     * Every extension of regexp-replacer can make use of this.
     */
    private static final Map utilReaders = new HashMap();     // class -> utilreader

    /**
     * The regexps for the unextended RegexpReplacer
     */
    protected static final Collection regexps = new ArrayList();

    protected static abstract class PatternWatcher extends ResourceWatcher {
        protected Collection patterns;
        PatternWatcher(Collection p) {
            patterns = p;
        }
    }



    static {
        new RegexpReplacer().readPatterns(regexps);
    }

    public RegexpReplacer(int i) {
        super(i);
    }

    public RegexpReplacer() {
        super(WORDS);
    }
    /**
     * This on default gives the regexps configured for the base-class (a static member). You can
     * override this method to return another Collection.
     */
    protected Collection getPatterns() {
        return regexps;
    }

    /**
     * This can be overridden if the implementation must use its own configuration file.
     */
    protected String getConfigFile() {
        return "regexps.xml";
    }

    /**
     * Reads defaults translation patterns into the given collection patterns. Override this for
     * other default patterns.
     */
    protected void readDefaultPatterns(Collection patterns) {
    }

    /**
     * Reads patterns from config-file into given Collection
     */
    protected final void readPatterns(Collection patterns) {
        UtilReader utilReader = (UtilReader) utilReaders.get(this.getClass().getName());
        if (utilReader == null) {
            utilReader = new UtilReader(getConfigFile(),
                                        new PatternWatcher(patterns) {
                                            public void onChange(String file) {
                                                readPatterns(patterns);
                                            }
                                        });
            utilReaders.put(this.getClass().getName(), utilReader);
        }

        patterns.clear();

        Collection regs = (Collection) utilReader.getProperties().get("regexps");
        if (regs != null) {
            addPatterns(regs, patterns);
        } else {
            readDefaultPatterns(patterns);
        }
    }

    /**
     * Utility function to create a bunch of patterns.
     * @param list A Collection of Map.Entry (like {@link java.util.Map#entrySet()}), containing
     *        pairs of Strings
     * @param patterns This the Collection of Entries. The key of every entry is a compiled regular
     *        expression. The value is still a String. New entries will be added to this collection
     *        by this function.
     */
    protected static void addPatterns(Collection list, Collection patterns) {
        if (list != null) {
            Iterator i = list.iterator();
            while (i.hasNext()) {
                Map.Entry entry  = (Map.Entry) i.next();
                Pattern p        = Pattern.compile((String) entry.getKey());
                String  result   = (String) entry.getValue();
                patterns.add(new Entry(p, result));
            }
        }
    }

    protected boolean replace(String string, Writer w) throws IOException {
        Iterator i  = getPatterns().iterator();

        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            Pattern p = (Pattern) entry.getKey();
            Matcher m = p.matcher(string);
            if (m.matches()) {
                String result = (String) entry.getValue();
                for (int j = m.groupCount(); j >= 0; j--) {
                    result = result.replaceAll("\\$" + j, m.group(j));
                }
                w.write(result);
                return true;
            }
        }
        w.write(string);
        return false;

    }
    protected final String base() {
        return "REGEXPS";
    }

    public String toString() {
        return getEncoding() + " " + getPatterns();
    }



}
