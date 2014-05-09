/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.HashMap;
import java.util.Map;

/**
 * Encode a bytearray to a string by converting every byte to a hexadecimal (00 - FF)
 * representation, and decode the other way around.
 *
 * @author Johannes Verelst
 * @since MMBase-1.8.1
 * @version $Id$
 */

public class Hex extends ByteArrayToCharTransformer implements ByteToCharTransformer, ConfigurableTransformer {
    private final static String ENCODING = "HEX";
    private final static int HEX = 1;

    int to = HEX;

    public void configure(int t) {
        to = t;
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map transformers() {
        HashMap h = new HashMap();
        h.put(ENCODING, new Config(Hex.class, HEX, "Encoding bytearrays to and from a hexidecimal string"));
        return h;
    }


    /**
     * Transform a bytearray to a string of hexadecimal digits.
     */
    public String transform(byte[] bytes) {
        StringBuffer strbuf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i<bytes.length; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) bytes[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    /**
     * Transform a string of hexadecimal digits to a bytearray.
     * @param r The string to transform
     * @return an array of bytes
     * @throws IllegalArgumentException whenever the input string is not correctly formatted.
     */
    public byte[] transformBack(String r) {
        try {
            int strlen = r.length();
            byte[] retval = new byte[strlen/2];
            for (int i=0; i<strlen; i+=2) {
                char c1 = r.charAt(i);
                char c2 = r.charAt(i+1);
                int b = 0;
                if (c1 >= '0' && c1 <= '9') {
                    b += 16 * (c1 - '0');
                } else {
                    b += 16 * (10 + c1 - 'a');
                }
                if (c2 >= '0' && c2 <= '9') {
                    b += (c2 - '0');
                } else {
                    b += (10 + c2 - 'a');
                }
                retval[i/2] = (byte)b;
            }
            return retval;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("the entered string to decode properly was wrong: " + e);
        }
    }

    public String getEncoding() {
        return ENCODING;
    }
}
