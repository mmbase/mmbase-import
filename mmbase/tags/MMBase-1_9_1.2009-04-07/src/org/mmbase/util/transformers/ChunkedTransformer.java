/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import java.io.*;

import org.mmbase.util.logging.*;


/**
 * A chunked transformer is a transformer that transforms on a 'chunk by chunk' base. A chunck is
 * typically a word or a line or so. The type of the 'chunks' is controled by the 'mode' parameter.
 *
 * It can ignored existing XML markup (the 'XMLTEXT' modes), and also avoids trailing dots and
 * comments and surrounding quotes and parentheses.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public abstract class ChunkedTransformer<P> extends ConfigurableReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(ChunkedTransformer.class);

    /**
     * Match word by word, but only in PCDATA of xml elements.
     */
    public final static int XMLTEXT_WORDS     = 1;

    /**
     * Match in PCDATA of xml elements.
     */
    public final static int XMLTEXT     = 2;

    /**
     * Match word by word.
     */
    public final static int WORDS    = 3;

    /**
     * Match line by line.
     */
    public final static int LINES    =  4;

    /**
     * Match the entire stream (so, one String must be created).
     */
    public final static int ENTIRE    =  5;

    // about 3 bits used now.

    /**
     * If this is added to the config-int, then only the first match of any one pattern should be used.
     */
    public final static int ONLY_USE_FIRST_MATCHING_PATTERN = 1 << 5;

    /**
     * If this is added to the config-int, then only the first match of all patterns should be used.
     */
    public final static int ONLY_REPLACE_FIRST_MATCH        = 1 << 6;


    protected boolean onlyFirstPattern    = false;
    protected boolean onlyFirstMatch      = false;

    public void configure(int i) {
        onlyFirstMatch = ((i & ONLY_REPLACE_FIRST_MATCH) > 0);
        onlyFirstPattern = ((i & ONLY_USE_FIRST_MATCHING_PATTERN) > 0);
        // set corresponding bits to 0, they will not be needed any more.
        i &= ~ONLY_USE_FIRST_MATCHING_PATTERN;
        i &= ~ONLY_REPLACE_FIRST_MATCH;

        super.configure(i);
    }

    protected ChunkedTransformer(int i) {
        super(i);
    }

    public ChunkedTransformer() {
        this(WORDS);
    }

    protected class Status {
        int replaced = 0;
        boolean inA = false;
        final Set<P> used = onlyFirstMatch ? new HashSet<P>() : null;
    }
    protected Status newStatus() {
        return new Status();

    }
    /**
     * Implement this. Return true if a replacement done.
     */
    protected abstract boolean replace(String string, Writer w, Status status) throws IOException;

    protected boolean replaceWord(StringBuilder word, Writer writer, Status status) throws IOException {
        int l = word.length();
        StringBuilder postFix = null;
        String w;
        if (l > 0) {

            postFix = new StringBuilder();

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

        // ready to make the replacements now.
        boolean result = replace(w, writer, status);

        if (postFix != null) {
            writer.write(postFix.toString());
        }
        return result;
    }

    /**
     * Whether still to do replacing, given status.
     */
    protected boolean replace(Status status) {
        return ! onlyFirstMatch || status.replaced == 0;
    }

    public Writer transformXmlTextWords(Reader r, Writer w)  {
        Status status = newStatus();
        StringBuilder word = new StringBuilder();  // current word
        boolean translating = true;
        try {
            log.trace("Starting  replacing");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (!replace(status)) {
                    w.write(c);
                } else if (c == '<') {  // don't do it in existing tags and attributes
                    translating = false;
                    replaceWord(word, w, status);
                    word.setLength(0);
                    w.write(c);
                } else if (c == '>') {
                    translating = true;
                    w.write(word.toString());
                    w.write(c);
                    String tag = word.toString();
                    status.inA  = tag.equals("a") || tag.startsWith("a ");
                    word.setLength(0);
                } else if (! translating) {
                    word.append((char) c);
                } else {
                    if (Character.isWhitespace((char) c) || c == '\'' || c == '\"' || c == '(' || c == ')' ) {
                        replaceWord(word, w, status);
                        word.setLength(0);
                        w.write(c);
                    } else {
                        word.append((char) c);
                    }
                }
            }
            // write last word
            if (replace(status)) {
                if (translating) {
                    replaceWord(word, w, status);
                } else {
                    w.write(word.toString());
                }
                word.setLength(0);
            }
            if (log.isDebugEnabled()) {
                log.debug("Finished  replacing. Replaced " + status.replaced + " words");
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public Writer transformXmlText(Reader r, Writer w)  {
        Status status = newStatus();
        StringBuilder xmltext = new StringBuilder();  // current word
        boolean translating = true;
        try {
            log.trace("Starting replacing");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (!replace(status)) {
                    w.write(c);
                } else
                // perhaps better use SAX to decently detect XML, but then it probably won't work
                // very well on sloppy XML (like HTML).
                if (c == '<') {  // don't do it in existing tags and attributes
                    translating = false;
                    replace(xmltext.toString(), w, status);
                    xmltext.setLength(0);
                    w.write(c);
                } else if (c == '>') {
                    translating = true;
                    xmltext.setLength(0);
                    w.write(c);
                } else if (! translating) {
                    w.write(c);
                } else {
                    xmltext.append((char) c);
                }
            }
            // write last word
            if (replace(status)) {
                if (translating) replace(xmltext.toString(), w, status);
            }
            log.debug("Finished  replacing. Replaced " + status.replaced + " words");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }
    public Writer transformWords(Reader r, Writer w)  {
        Status status = newStatus();
        StringBuilder word = new StringBuilder();  // current word
        try {
            if (log.isDebugEnabled()) {
                log.trace("Starting replacing words." + Logging.stackTrace());
            }
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (replace(status) && (Character.isWhitespace((char) c) || c == '\'' || c == '\"' || c == '(' || c == ')' || c == '<' || c == '>' )) {
                    replaceWord(word, w, status);
                    word.setLength(0);
                    w.write(c);
                } else {
                    word.append((char) c);
                }
            }
            // write last word
            if (replace(status)) {
                replaceWord(word, w, status);
            } else {
                w.write(word.toString());
            }
            log.debug("Finished replacing. Replaced " + status.replaced + " words");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }



    public Writer transformLines(Reader r, Writer w) {
        BufferedReader reader = new BufferedReader(r);
        Status status = newStatus();
        try {
            String line = reader.readLine();
            while (line != null) {
                if (replace(status)) {
                    replace(line, w, status);
                } else {
                    w.write(line);
                }
                line = reader.readLine();
                if (line != null) {
                    w.write("\n");
                }
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public Writer transformEntire(Reader r, Writer w) {
        StringWriter sw = new StringWriter();
        Status status = newStatus();
        try {
            BufferedReader br = new BufferedReader(r);
            char[] buf = new char[200];
            int n = br.read(buf, 0, buf.length);
            while (n > 0) {
                sw.write(buf, 0, n);
                n = br.read(buf, 0, buf.length);
            }
            replace(sw.toString(), w, status);
        } catch (java.io.IOException e) {
            log.error(e.getMessage(), e);
        }

        return w;
    }


    public Writer transform(Reader r, Writer w) {
        switch(to) {
        case XMLTEXT_WORDS: return transformXmlTextWords(r, w);
        case XMLTEXT:       return transformXmlText(r, w);
        case WORDS:         return transformWords(r, w);
        case LINES:         return transformLines(r, w);
        case ENTIRE:        return transformEntire(r, w);
        default: throw new UnknownCodingException(getClass(), to);
        }
    }

    abstract protected String base();

    public String getEncoding() {
        switch (to) {
        case XMLTEXT_WORDS:
            return base() + "_XMLTEXT_WORDS";
        case XMLTEXT:
            return base() + "_XMLTEXT";
        case WORDS:
            return base() + "_WORDS";
        case LINES:
            return base() + "_LINES";
        case ENTIRE:
            return base() + "_ENTIRE";
        default :
            throw new UnknownCodingException(getClass(), to);
        }
    }

    public Map<String,Config> transformers() {
        Map<String,Config> h = new HashMap<String,Config>();
        h.put(base() + "_XMLTEXT_WORDS",  new Config(RegexpReplacer.class, XMLTEXT_WORDS,  "Search and replaces regexps word-by-word, only in XML text() blocks."));
        h.put(base() + "_XMLTEXT",        new Config(RegexpReplacer.class, XMLTEXT,  "Search and replaces regexps, only in XML text() blocks."));
        h.put(base() + "_WORDS",          new Config(RegexpReplacer.class, WORDS,  "Search and replaces regexps word-by-word"));
        h.put(base() + "_LINES",          new Config(RegexpReplacer.class, LINES,  "Search and replaces regexps, line-by-line"));
        h.put(base() + "_ENTIRE",         new Config(RegexpReplacer.class, ENTIRE,  "Search and replaces regexps"));

        return Collections.unmodifiableMap(h);
    }

    public static void main(String [] argv) {
        CharTransformer trans = new ChunkedTransformer(XMLTEXT) {
                protected boolean replace(String string, Writer w, Status status) throws IOException {
                    w.write(string);
                    return false;
                }
                protected String base() {
                    return "test";
                }
            };
        CharTransformer trans2 = new BufferedReaderTransformer() {
                @Override
                protected boolean transform(PrintWriter bw, String line,Status status) {
                    bw.println(line);
                    return true;
                }

                @Override
                protected Status createNewStatus() {
                    return null;
                }
            };
        long startTime = System.currentTimeMillis();
        if (argv.length > 0) {
            if("buf1".equals(argv[0])) {
                trans.transform(new BufferedReader(new InputStreamReader(System.in)), new BufferedWriter(new OutputStreamWriter(System.out)));
            } else if ("buf2".equals(argv[0])) {
                trans2.transform(new InputStreamReader(System.in), new BufferedWriter(new OutputStreamWriter(System.out)));
            } else {
                System.err.println("Don't understand '" + argv[0] + "'");
            }
        } else {
            trans.transform(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
        }
        long duration = System.currentTimeMillis() - startTime;
        System.err.println("Converstion took " + duration + " ms");


    }

}
