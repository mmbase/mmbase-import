/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;
import org.mmbase.util.URLParamEscape;


/**
 * Encodings related to URL's. The implementation is still in
 * ../URL*Escape. Perhaps should be migrated to here...
 *
 * @author Michiel Meeuwissen 
 */

public class Url extends ConfigurableStringTransformer implements CharTransformer {
    
    public final static int ESCAPE       = 1;     
    public final static int PARAM_ESCAPE = 2;

    public Url() {
        super();
    }

    public Url(int conf) {
        super(conf);
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map transformers() {
        HashMap h = new HashMap();
        h.put("escape_url".toUpperCase(), new Config(Url.class, ESCAPE));
        h.put("escape_url_param".toUpperCase(), new Config(Url.class, PARAM_ESCAPE));
        return h;
    }

    public String transform(String r) {
        switch(to){
        case ESCAPE:           return java.net.URLEncoder.encode(r);
        case PARAM_ESCAPE:     return URLParamEscape.escapeurl(r);
        default: throw new UnknownCodingException(getClass(), to);
        }
    }
    public String transformBack(String r) {
        switch(to){
        case ESCAPE:           return java.net.URLDecoder.decode(r);
        case PARAM_ESCAPE:     return URLParamEscape.unescapeurl(r);
        default: throw new UnknownCodingException(getClass(), to);
        }
    } 
    public String getEncoding() {
        switch(to){
        case ESCAPE:        return "ESCAPE_URL";
        case PARAM_ESCAPE:  return "ESCAPE_URL_PARAM";
        default: throw new UnknownCodingException(getClass(), to);
        }
    }
}
