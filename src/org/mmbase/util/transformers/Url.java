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

import org.mmbase.util.URLEscape;
import org.mmbase.util.URLParamEscape;


/**
 * Encodings related to URL's. The implementation is still in
 * ../URL*Escape. Perhaps should be migrated to here...
 *
 * @author Michiel Meeuwissen 
 */

public class Url extends AbstractTransformer implements CharTransformer {
    
    private final static int ESCAPE       = 1;     
    private final static int PARAM_ESCAPE = 2;

    /**
     * Used when registering this class as a possible Transformer
     */

    public HashMap transformers() {
        HashMap h = new HashMap();
        h.put("escape_url".toUpperCase(), new Config(Url.class, ESCAPE));
        h.put("escape_url_param".toUpperCase(), new Config(Url.class, PARAM_ESCAPE));
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
        case ESCAPE:           return URLEscape.escapeurl(r);
        case PARAM_ESCAPE:     return URLParamEscape.escapeurl(r);
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    }
    public String transformBack(String r) {
        switch(to){
        case ESCAPE:           return URLEscape.unescapeurl(r);
        case PARAM_ESCAPE:     return URLParamEscape.unescapeurl(r);
        default: throw new UnsupportedOperationException("Cannot transform");
        }
    } 
    public String getEncoding() {
        switch(to){
        case ESCAPE:        return "ESCAPE_URL";
        case PARAM_ESCAPE:  return "ESCAPE_URL_PARAM";
        default: throw new UnsupportedOperationException("unknown encoding");
        }
    }
}
