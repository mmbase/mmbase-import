/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.*;

/**
 * Encodings related to URL's. The implementation is still in
 * ../URL*Escape. Perhaps should be migrated to here...
 *
 * @author Michiel Meeuwissen
 */

public class Url extends ConfigurableStringTransformer implements CharTransformer {

    public final static int ESCAPE       = 1;

    // maybe should be dropped, as there is no longer a difference with ESCAPE
    public final static int PARAM_ESCAPE = 2;

    public Url() {
        super(ESCAPE);
    }

    public Url(int conf) {
        super(conf);
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map<String,Config> transformers() {
        Map<String,Config> h = new HashMap<String,Config>();
        h.put("escape_url".toUpperCase(), new Config(Url.class, ESCAPE));
        h.put("escape_url_param".toUpperCase(), new Config(Url.class, PARAM_ESCAPE));
        return h;
    }

    public String transform(String r) {
        switch(to){
        case PARAM_ESCAPE:
        case ESCAPE:
            try {
                return java.net.URLEncoder.encode(r, "UTF-8");
            } catch (java.io.UnsupportedEncodingException uee) { // cannot happen
                return r;
            }
        default: throw new UnknownCodingException(getClass(), to);
        }
    }
    public String transformBack(String r) {
        switch(to){
        case ESCAPE:
        case PARAM_ESCAPE:
            try {
                return java.net.URLDecoder.decode(r, "UTF-8");
            } catch (java.io.UnsupportedEncodingException uee) { // cannot happen
                return r;
            }
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
