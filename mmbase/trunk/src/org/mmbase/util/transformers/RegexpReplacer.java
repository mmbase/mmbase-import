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
import org.mmbase.util.WrappedFileWatcher;
import org.mmbase.util.xml.UtilReader;

import org.mmbase.util.logging.*;


/**
 * Finds regexps in the Character String, and replaces them. The replaced regexps can be found in a configuration file 'regexps.xml' (if it is present).
 * It ignores existing XML markup, and also avoids trailing dots and comments and surrounding quotes and parentheses.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.8
 */

public class RegexpReplacer extends ReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(RegexpReplacer.class);


    private static final Map utilReaders = new HashMap();     // class -> utilreader

    protected static Map regexps = new LinkedHashMap();

    protected static abstract class PatternWatcher extends WrappedFileWatcher {
        protected Map patterns;
        PatternWatcher(Map p) {
            patterns = p;
        }
    }

    static {
        new RegexpReplacer().readPatterns(regexps);
    }

    /**
     * This needs overriding, (must give the static map for the extension).
     */
    protected Map getPatterns() {
        return regexps;
    }

    /**
     * This needs overriding, (must give the configuration file for the extension).
     */
    protected String getConfigFile() {
        return "regexps.xml";
    }

    protected void readDefaultPatterns(Map patterns) {
    }

    protected void readPatterns(Map patterns) {
        UtilReader utilReader = (UtilReader) utilReaders.get(this.getClass().getName());
        if (utilReader == null) {
            utilReader = new UtilReader(getConfigFile(), new PatternWatcher(patterns) { public void onChange(File file) { readPatterns(patterns); } });
            utilReaders.put(this.getClass().getName(), utilReader);
        }

        patterns.clear();
        
        Map regexps = (Map) utilReader.getProperties().get("regexps");
        if (regexps != null) {
            Iterator i = regexps.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                Pattern p       = Pattern.compile((String) entry.getKey());
                String  result  = (String) entry.getValue();
                patterns.put(p, result);
            }            
        } else {
            readDefaultPatterns(patterns);
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

        Iterator i  = getPatterns().entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Pattern p = (Pattern) entry.getKey();
            Matcher m = p.matcher(w);
            if (m.matches()) {
                String result = (String) entry.getValue();
                for (int j = m.groupCount(); j >= 0; j--) {
                    result = result.replaceAll("\\$" + 0, m.group(j));
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


}
