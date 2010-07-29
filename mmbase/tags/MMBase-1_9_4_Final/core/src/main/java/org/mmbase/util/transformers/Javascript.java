/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


/**
 * Encodings related Javascript It can escape single quotes, by replacing them by \\', as is needed in document.write actions.

 * @author Michiel Meeuwissen
 * @since MMBase-1.7.4
 */

public class Javascript extends ConfigurableReaderTransformer implements CharTransformer {
    private static final long serialVersionUID = 0L;
    private final static String SINGLE_QUOTES     = "JAVASCRIPT_ESCAPE_SINGLE_QUOTES";
    private final static String DOUBLE_QUOTES     = "JAVASCRIPT_ESCAPE_DOUBLE_QUOTES";
    private final static String BOTH_QUOTES     = "JAVASCRIPT_ESCAPE_BOTH_QUOTES";
    public final static int ESCAPE_SINGLE_QUOTES    = 1;
    public final static int ESCAPE_DOUBLE_QUOTES    = 2;
    public final static int ESCAPE_BOTH_QUOTES      = 3;

    public Javascript() {
        super(ESCAPE_SINGLE_QUOTES);
    }

    public Javascript(int conf) {
        super(conf);
    }

    /**
     * Escapes a quote
     * @param escapeChar The quote character to be escaped.
     * @return the writer
     */
    public static Writer escapeChar(Reader r, Writer w, char escapeChar) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if(c == escapeChar) w.write('\\');
                if(c == '\\') w.write('\\');
                if (c == '\n') {
                    w.write("\\n");
                } else if (c == '\r') {
                } else {
                    w.write(c);
                }
            }
        } catch (java.io.IOException e) {
        }
        return w;
    }

    public static Writer escapeChar(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if(c == '\'' || c == '"') w.write('\\');
                if(c == '\\') w.write('\\');
                if (c == '\n') {
                    w.write("\\n");
                } else if (c == '\r') {
                } else {
                    w.write(c);
                }
            }
        } catch (java.io.IOException e) {
        }
        return w;
    }

    /**
     * Unescapes \-escapes in a string.
     */
    public static Writer escapeCharBack(Reader r, Writer w) {
        try {
            boolean skipNext = false;
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if(c == '\'') {
                    if (skipNext) {
                        skipNext = false;
                    } else {
                        w.write(c);
                        skipNext = true;
                    }
                } else {
                      w.write(c);
                      skipNext = false;
                }
            }
        } catch (java.io.IOException e) {
        }
        return w;
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map<String,Config> transformers() {
        Map<String,Config> h = new HashMap<String,Config>();
        h.put(SINGLE_QUOTES, new Config(Sql.class, ESCAPE_SINGLE_QUOTES, "Escape single quotes for Javascript statements"));
        h.put(DOUBLE_QUOTES, new Config(Sql.class, ESCAPE_DOUBLE_QUOTES, "Escape single quotes for Javascript statements"));
        h.put(BOTH_QUOTES, new Config(Sql.class, ESCAPE_BOTH_QUOTES, "Escape single and double quotes for Javascript statements"));
        return h;
    }

    public Writer transform(Reader r, Writer w) {
        switch(to){
        case ESCAPE_SINGLE_QUOTES:           return escapeChar(r, w, '\'');
        case ESCAPE_DOUBLE_QUOTES:           return escapeChar(r, w, '\"');
        case ESCAPE_BOTH_QUOTES:           return escapeChar(r, w);

        default: throw new UnsupportedOperationException("Cannot transform");
        }
    }

    @Override
    public Writer transformBack(Reader r, Writer w) {
        switch(to){
        case ESCAPE_SINGLE_QUOTES:
        case ESCAPE_DOUBLE_QUOTES:
        case ESCAPE_BOTH_QUOTES:
            return escapeCharBack(r, w);
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    }

    public String getEncoding() {
        switch(to){
        case ESCAPE_SINGLE_QUOTES: return SINGLE_QUOTES;
        case ESCAPE_DOUBLE_QUOTES: return DOUBLE_QUOTES;
        case ESCAPE_BOTH_QUOTES: return BOTH_QUOTES;
        default: return "UNKNOWN";
        }
    }
}
