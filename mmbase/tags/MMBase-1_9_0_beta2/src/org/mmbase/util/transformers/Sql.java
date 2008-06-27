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
 * Encodings related to Sql. It can escape quotes, by replacing them by double quotes, as is 
 * needed in SQL statements.
 *
 * @author Michiel Meeuwissen 
 * @author Jaco de Groot
 */

public class Sql extends ConfigurableReaderTransformer implements CharTransformer {
    private final static String ENCODING     = "ESCAPE_SINGLE_QUOTE";
    public final static int ESCAPE_QUOTES    = 1;     

    public Sql() {
        super(ESCAPE_QUOTES);
    }

    public Sql(int conf) {
        super(conf);
    }

    /**
     * Escapes single quotes in a string.
     * Escaping is done by doubling any quotes encountered.
     * Strings that are rendered in such way can more easily be included
     * in a SQL query.
     * @param r the string to escape
     * @param w The escaped string goes to this writer
     * @return the writer
     * @since MMBase-1.7
     */
    public static Writer singleQuote(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if(c == '\'') w.write(c);
                w.write(c);
            }
        } catch (java.io.IOException e) {
        }
        return w;
    }

    /**
     * Unescapes single quotes in a string.
     * Unescaping is done by replacing two quotes with one quote.
     * @param r the string to unescape
     * @param w the result is written to this writer.
     * @return the writer
     * @since MMBase-1.7.2
     */
    public static Writer singleQuoteBack(Reader r, Writer w) {
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
        h.put(ENCODING, new Config(Sql.class, ESCAPE_QUOTES, "Escape single quotes for SQL statements"));
        return h;
    }

    public Writer transform(Reader r, Writer w) {
        switch(to){
        case ESCAPE_QUOTES:           return singleQuote(r, w);
        default: throw new UnsupportedOperationException("Cannot transform");
        }    
    }

    public Writer transformBack(Reader r, Writer w) {
        switch(to){
        case ESCAPE_QUOTES:           return singleQuoteBack(r, w);
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    } 

    public String getEncoding() {
        return ENCODING;
    }
}
