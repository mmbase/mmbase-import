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

import org.mmbase.util.Escape;

/**
 * Encodings related to Sql. It can escape quotes, by replacing them by double quotes, as is 
 * needed in SQL statements.
 *
 * @author Michiel Meeuwissen 
 */

public class Sql extends AbstractTransformer implements CharTransformer {
    
    private final static int ESCAPE_QUOTES    = 1;     

    /**
     * Used when registering this class as a possible Transformer
     */

    public HashMap transformers() {
        HashMap h = new HashMap();
        h.put("escape_single_quote".toUpperCase(), new Config(Sql.class, ESCAPE_QUOTES, "Escape single quotes for SQL statements"));
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
        case ESCAPE_QUOTES:           return Escape.singlequote(r);
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    }
    public String transformBack(String r) {
        switch(to){
        case ESCAPE_QUOTES:           throw new UnsupportedOperationException("Not needed to revert this at anytime it tinks");
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    } 

}
