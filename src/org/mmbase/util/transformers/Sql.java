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


/**
 * Encodings related to Sql. It can escape quotes, by replacing them by double quotes, as is 
 * needed in SQL statements.
 *
 * @author Michiel Meeuwissen 
 */

public class Sql extends AbstractTransformer implements CharTransformer {
    private final static String ENCODING     = "ESCAPE_SINGLE_QUOTE";
    private final static int ESCAPE_QUOTES    = 1;     

    /**
     * Escapes single quotes in a string.
     * Escaping is done by doubling any quotes encountered.
     * Strings that are rendered in such way can more easily be included
     * in a SQL query.
     * @param str the string to escape
     * @return the escaped string
     */
    static public String singlequote(String str) {
        String line=null,obj;
        int idx;
        if (str!=null) {
            /* Single ' protection */
            line=new String("");
            obj=new String(str);
            while((idx=obj.indexOf('\''))!=-1) {
                line+=obj.substring(0,idx)+"''";
                obj=obj.substring(idx+1);
            }
            line=line+obj;
        }
        return line;
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public HashMap transformers() {
        HashMap h = new HashMap();
        h.put(ENCODING, new Config(Sql.class, ESCAPE_QUOTES, "Escape single quotes for SQL statements"));
        return h;
    }

    public Writer transform(Reader r) {
        throw new UnsupportedOperationException("transform(Reader) is not yet supported");
    }
    public Writer transformBack(Reader r) {
        throw new UnsupportedOperationException("transformBack(Reader) is not yet supported");
    } 

    public String transform(String r) {
        switch(to){
        case ESCAPE_QUOTES:           return singlequote(r);
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    }
    public String transformBack(String r) {
        switch(to){
        case ESCAPE_QUOTES:           throw new UnsupportedOperationException("Not needed to revert this at anytime it tinks");
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    } 

    public String getEncoding() {
        return ENCODING;
    }
}
