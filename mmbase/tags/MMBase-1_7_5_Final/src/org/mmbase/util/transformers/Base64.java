/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Uses the sun.misc classes to do BASE64 encoding and decoding. The
 * sun.misc classes are not supported by Sun. Perhaps once we have to
 * plug in another class.
 *
 * @author Michiel Meeuwissen 
 */

public class Base64 implements ByteToCharTransformer, ConfigurableTransformer {
    private final static String ENCODING = "BASE64";
    private final static int BASE_64 = 1;

    int to = BASE_64;

    public void configure(int t) {
        to = t;
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map transformers() {
        HashMap h = new HashMap();
        h.put(ENCODING, new Config(Base64.class, BASE_64, "Base 64 encoding base on sun.misc.BASE64* classes"));
        return h;
    }

    public Writer transform(InputStream e) {
        throw new UnsupportedOperationException("transform(InputStream) is not yet supported");
    }

    public OutputStream transformBack(Reader e) {
        throw new UnsupportedOperationException("transformBack(Reader) is not yet supported");
    }

    public String transform(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }
    
    public byte[] transformBack(String r) {
        try {
            BASE64Decoder dec = new BASE64Decoder();
            return dec.decodeBuffer(r);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("the entered string to decode properly was wrong: " + e);
        }
    }

    public String getEncoding() {
        return ENCODING;
    }
}
