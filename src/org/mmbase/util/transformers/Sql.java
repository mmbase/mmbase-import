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
     * @param str the string to escape
     * @return the escaped string
     */
    static public Writer singleQuote(Reader r, Writer w) {
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
     * Used when registering this class as a possible Transformer
     */

    public Map transformers() {
        HashMap h = new HashMap();
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
        case ESCAPE_QUOTES:           throw new UnsupportedOperationException("Not needed to revert this at anytime it tinks");
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    } 

    public String getEncoding() {
        return ENCODING;
    }
}
