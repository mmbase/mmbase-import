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

public class RegexpReplacer extends ConfigurableReaderTransformer implements CharTransformer {
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

    public final static int WORDS     = 1;
    public final static int LINES     = 2; // not yet supported
    public final static int ENTIRE    = 3; // not yet supported



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
     * @param list A Collection of Map.Entry (like {@link java.util.Map#entryList}), containing
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
    
    /**
     * Takes one word (as a StringBuffer), checks if it can be made clickable, and if so, does it.
     *
     * @return true if a replacement occured
     */
    protected boolean replaceWord(StringBuffer word, Writer writer) throws IOException {
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
            if (l > 0) {

                // to allow for . , and like in the end, we tear those of.
                char d = word.charAt(l - 1); 
                while (! Character.isLetterOrDigit(d)) {
                    postFix.insert(0, d);
                    word.setLength(--l);
                    if (l == 0) break;
                    d = word.charAt(l - 1); 
                }
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

        Iterator i  = getPatterns().iterator();

        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            Pattern p = (Pattern) entry.getKey();
            Matcher m = p.matcher(w);
            if (m.matches()) {
                String result = (String) entry.getValue();
                for (int j = m.groupCount(); j >= 0; j--) {
                    result = result.replaceAll("\\$" + j, m.group(j));
                }
                writer.write(result);
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
            log.trace("Starting regexp replacing");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (c == '<') {  // don't do it in existing tags and attributes
                    translating = false;
                    if (replaceWord(word, w)) replaced++;
                    w.write(c);
                } else if (c == '>') {
                    translating = true;
                    word.setLength(0);
                    w.write(c);
                } else if (! translating) {
                    w.write(c);
                } else {
                    if (Character.isWhitespace((char) c) || c == '\'' || c == '\"' || c == '(' || c == ')' ) {
                        if (replaceWord(word, w)) replaced++;
                        word.setLength(0);
                        w.write(c);
                    } else {       
                        word.append((char) c);
                    }
                }
            }
            // write last word
            if (translating && replaceWord(word, w)) replaced++;
            log.debug("Finished regexp replacing. Replaced " + replaced + " words");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public String getEncoding() {
        switch (to) {
        case WORDS:
            return "REGEXPS_WORDS";
        case LINES:
            return "REGEXPS_LINES";
        case ENTIRE:
            return "REGEXPS_ENTIRE";
        default :
            throw new UnknownCodingException(getClass(), to);
        }
    }
    

    public Map transformers() {
        Map h = new HashMap();
        h.put("REGEXPS_WORDS",  new Config(RegexpReplacer.class, WORDS,  "Search and replaces regexps word-by-word"));
        h.put("REGEXPS_ENTIRE",  new Config(RegexpReplacer.class, WORDS,  "Search and replaces regexps"));
        return h;
    }

    public String toString() {
        return getEncoding() + " " + getPatterns();
    }



}
