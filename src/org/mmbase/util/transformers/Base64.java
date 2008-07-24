/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

// still present in java 1.6, but give horrible warnings during compilation
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

/**
 * Code taken from {@link "http://www.source-code.biz/snippets/java/2.htm"}
 *
 * @author Michiel Meeuwissen
 */

public class Base64 implements ByteToCharTransformer, ConfigurableTransformer {
    private final static String ENCODING = "BASE64";
    private final static int BASE_64 = 1;

    // Mapping table from 6-bit nibbles to Base64 characters.
    private static char[]    map1 = new char[64];
    static {
        int i=0;
        for (char c='A'; c<='Z'; c++) map1[i++] = c;
        for (char c='a'; c<='z'; c++) map1[i++] = c;
        for (char c='0'; c<='9'; c++) map1[i++] = c;
        map1[i++] = '+'; map1[i++] = '/';
    }

    // Mapping table from Base64 characters to 6-bit nibbles.
    private static byte[]    map2 = new byte[128];
    static {
        for (int i=0; i<map2.length; i++) map2[i] = -1;
        for (int i=0; i<64; i++) map2[map1[i]] = (byte)i;
    }


    int to = BASE_64;

    public void configure(int t) {
        to = t;
    }

    /**
     * Used when registering this class as a possible Transformer
     */

    public Map<String,Config> transformers() {
        Map<String,Config> h = new HashMap<String,Config>();
        h.put(ENCODING, new Config(Base64.class, BASE_64, "Base 64 encoding base on sun.misc.BASE64* classes"));
        return h;
    }


    public String transform(byte[] in) {
        int iLen = in.length;
        int oDataLen = (iLen*4+2)/3;       // output length without padding
        int oLen = ((iLen+2)/3)*4;         // output length including padding
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iLen ? in[ip++] & 0xff : 0;
            int i2 = ip < iLen ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '=';
            op++;
            out[op] = op < oDataLen ? map1[o3] : '=';
            op++;
        }
        return new String(out);
    }
    public Writer transform(InputStream in, Writer w)  {
        int iLen = 0;
        int op = 0;
        while (true) {
            try {
                int ch = in.read();
                if (ch != -1 ) iLen++;
                int i0 = ch & 0xff;
                ch = in.read();
                if (ch != -1 ) iLen++;
                int i1 = ch != -1 ? ch & 0xff : 0;
                ch = in.read();
                if (ch != -1 ) iLen++;
                int i2 = ch != -1 ? ch & 0xff : 0;
                int o0 = i0 >>> 2;
                int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
                int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
                int o3 = i2 & 0x3F;
                int oDataLen = (iLen * 4 + 2) / 3;       // output length without padding
                w.write(map1[o0]); ++op;
                w.write(map1[o1]); ++op;
                w.write(op < oDataLen ? map1[o2] : '='); ++op;
                w.write(op < oDataLen ? map1[o3] : '='); ++op;
                if (ch == -1) break;
            } catch (IOException ioe) {
            }
        }
        return w;
    }


    public byte[] transformBack(String r) {
        char[] in = r.toCharArray();
        int iLen = in.length;
        if (iLen%4 != 0) throw new IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4.");
        while (iLen > 0 && in[iLen-1] == '=') iLen--;
        int oLen = (iLen*3) / 4;
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iLen ? in[ip++] : 'A';
            int i3 = ip < iLen ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");
            }
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
                throw new IllegalArgumentException ("Illegal character in Base64 encoded data.");
            }
            int o0 = ( b0       <<2) | (b1>>>4);
            int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
            int o2 = ((b2 &   3)<<6) |  b3;
            out[op++] = (byte)o0;
            if (op<oLen) out[op++] = (byte)o1;
            if (op<oLen) out[op++] = (byte)o2; }
        return out;
    }
    public OutputStream transformBack(Reader in, OutputStream out)  {
        try {
            StringWriter sw = new StringWriter();
            while (true) {
                int c = in.read();
                if (c == -1) break;
                sw.write(c);
            }
            out.write(transformBack(sw.toString()));
        } catch (java.io.IOException e) {

        }
        return out;
    }
    // javadoc inherited
    public final OutputStream transformBack(Reader r) {
        return transformBack(r, new ByteArrayOutputStream());
    }

    // javadoc inherited
    public final Writer transform(InputStream in) {
        return transform(in, new StringWriter());
    }

    public String getEncoding() {
        return ENCODING;
    }

    public static void  main(String[] argv) throws Exception {
        Base64 enc = new Base64();
        enc.transform(System.in, new OutputStreamWriter(System.out, "UTF-8")).flush();

    }
}
