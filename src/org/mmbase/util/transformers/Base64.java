package org.mmbase.util.transformers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Uses the sun.misc classes to do BASE64 encoding and decoding. The
 * sun.misc classes are not supported by Sun. Perhaps once we have to
 * plug in another class.
 *
 * @author Michiel Meeuwissen 
 */

public class Base64 extends AbstractTransformer implements ByteToCharTransformer {
    
    private final static int BASE_64         = 1;     

    int to = BASE_64;

    /**
     * Used when registering this class as a possible Transformer
     */

    public HashMap transformers() {
        HashMap h = new HashMap();
        h.put("base_64".toUpperCase(), new Config(Base64.class, BASE_64, "Base 64 encoding base on sun.misc.BASE64* classes"));
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
        } catch(Exception e) {
            e.printStackTrace();
	    throw new IllegalArgumentException("the entered string to decode properly was wrong: " + e);
	}
    }


}
