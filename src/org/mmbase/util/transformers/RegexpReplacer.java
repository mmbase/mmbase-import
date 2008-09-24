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
import org.mmbase.util.Casting;

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
        protected Collection  patterns;
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

        Collection  regs = (Collection) utilReader.getMaps().get("regexps");
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
                Object next = i.next();
                Pattern p;
                String result;
                if (next == null) {
                    log.warn("Found null in " + list);
                    continue;
                } else if (next instanceof Map.Entry) {
                    Map.Entry entry  = (Map.Entry) next;
                    p        = Pattern.compile(Casting.toString(entry.getKey()));
                    Object value = entry.getValue();
                    if (value instanceof Collection) {
                        result = null;
                        Iterator  j = ((Collection) value).iterator();
                        while (j.hasNext()) {
                            Object n = j.next();
                            if (! (n instanceof Map.Entry)) {
                                log.warn("Could not understand " + n.getClass() + " '" + n + "' (in collection " + value + "). It should be a Map.Entry.");
                                continue;
                            }
                            Map.Entry  subEntry = (Map.Entry) n;
                            Object key = subEntry.getKey();
                            if ("key".equals(key)) {
                                p        = Pattern.compile(Casting.toString(subEntry.getValue()));
                                continue;
                            }
                            if ("value".equals(key)) {
                                result   = Casting.toString(subEntry.getValue());
                            }
                        }
                        if (result == null) result = "";
                    } else {
                        result   = Casting.toString(value);
                    }
                } else {
                    log.warn("Could not understand " + next.getClass() + " '" + next + "'. It should be a Map.Entry.");
                    continue;
                }
                patterns.add(new Entry(p, result));
            }
        }
    }

    private class Chunk {
        String string;
        boolean replaced = false;
        Chunk(String s) {
            string = s;
        }
        Chunk(String s, boolean r) {
            string = s; replaced = r;
        }
        public String toString() { return "'" + string + "'" + (replaced ? "." : ""); }

    }

    protected boolean replace(String string, Writer w, Status status) throws IOException {

        boolean r = false; // result value

        List chunks;
        if (onlyFirstPattern) {
            // linked list while we're going to do a lot of changing:
            chunks = new LinkedList();
        } else {
            // will not make any changes
            chunks = new ArrayList(1);
        }
        chunks.add(new Chunk(string));


        Iterator j = getPatterns().iterator();
        while (j.hasNext()) {
            Map.Entry entry = (Map.Entry) j.next();
            Pattern p = (Pattern) entry.getKey();


            if (onlyFirstMatch && status.used.contains(p)) continue;

            ListIterator  i = chunks.listIterator();
            while (i.hasNext()) {
                Chunk chunk = (Chunk) i.next();
                if (onlyFirstPattern && chunk.replaced) {
                    continue;
                }
                Matcher m = p.matcher(chunk.string);
                String replacement = (String) entry.getValue();
                boolean result = false;
                if (to == ChunkedTransformer.XMLTEXT_WORDS || to == ChunkedTransformer.WORDS) {
                    result = m.matches(); // try for a full match, as string is one word.
                } else {
                    result = m.find();
                }
                if (result) {
                    r = true;
                    StringBuffer sb = new StringBuffer();
                    do {
                        status.replaced++;
                        m.appendReplacement(sb, replacement);
                        if (onlyFirstMatch || onlyFirstPattern ||
                            to == ChunkedTransformer.XMLTEXT_WORDS ||
                            to == ChunkedTransformer.WORDS) break;
                        result = m.find();
                    } while (result);

                    if (onlyFirstPattern) {
                        // make a new chunk.
                        i.remove();
                        int s = m.start();
                        if (s > 0) {
                            i.add(new Chunk(sb.toString().substring(0, s)));
                            sb.delete(0, s);
                        }
                        i.add(new Chunk(sb.toString(), true));
                        sb.setLength(0);
                        m.appendTail(sb);
                        i.add(new Chunk(sb.toString()));
                        i.previous();
                    } else {
                        m.appendTail(sb);
                        i.set(new Chunk(sb.toString()));
                    }
                    if (onlyFirstMatch ||
                        to == ChunkedTransformer.XMLTEXT_WORDS ||
                        to == ChunkedTransformer.WORDS) {
                        // next pattern
                        break;
                    }
                }
            }
        }
        Iterator k = chunks.iterator();
        while (k.hasNext()) {
            Chunk s = (Chunk) k.next();
            w.write(s.string);
        }
        return r;

    }
    protected final String base() {
        return "REGEXPS";
    }

    public String toString() {
        return getEncoding() + " " + getPatterns();
    }

    public static void main(String[] arg) {
        StringBuffer b = new StringBuffer();
        Pattern p = Pattern.compile(arg[0]);
        String input = arg[1];
        Matcher m = p.matcher(input);
        while (m.find()) {
            b.append("'");
            m.appendReplacement(b, m.group().toUpperCase());
            b.append("'");
            System.out.println("s: " + m.start() + " e: " + m.end() + "g: " + m.group());
        }
        b.append("X");
        m.appendTail(b);
        System.out.println("buf : " + b);
    }



}
