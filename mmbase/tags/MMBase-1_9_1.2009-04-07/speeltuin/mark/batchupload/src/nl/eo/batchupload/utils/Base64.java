package nl.eo.batchupload.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Class with static methods to encode bytearrays into Base64 encoded Strings
 * Loosely based on code from the MMBase project, but largely rewritten.
 * 
 * @author Johannes Verelst
 */
public class Base64 {
    private static char base64chars[] = {
        'A','B','C','D','E','F','G','H','I','J','K','L','M',
        'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
        'a','b','c','d','e','f','g','h','i','j','k','l','m',
        'n','o','p','q','r','s','t','u','v','w','x','y','z',
        '0','1','2','3','4','5','6','7','8','9','+','/'
    };


    /**
     * Encode the base64 string.
     * Based on MMBase 1.5 source
     * 
     * @param bytes The bytearray that will be encoded
     */
    public static String encode(byte[] bytes) {

		int triplets = (int)(bytes.length/3);
		int res = bytes.length % 3;
			
		StringBuffer result = new StringBuffer();
		int written = 0;
		
		for (int i=0; i < triplets; i++) {
			byte b0 = bytes[3*i];
			byte b1 = bytes[3*i + 1];
			byte b2 = bytes[3*i + 2];
			
			result.append(base64chars[((b0 & 253) >> 2)]);
			result.append(base64chars[((b0 & 3) << 4) | ((b1 & 240) >> 4)]);
			result.append(base64chars[((b1 & 15) << 2) | ((b2 & 253) >> 6)]);
            result.append(base64chars[b2 & 63 ]);
			written += 4;
			if (written >= 72) {
				written = 0;

                // GRRRR, I cannot append a '\n' because MMBase Base64 decoder does
                // not recognise it.
                //
				// result.append("\n");
			}
        }   

		byte b0, b1, b2;
		b0 = (res > 0)?bytes[3*triplets]:0;
		b1 = (res > 1)?bytes[3*triplets + 1]:0;
		
		if (res > 0) {
			result.append(base64chars[((b0 & 253) >> 2)]);
			result.append(base64chars[((b0 & 3) << 4) | ((b1 & 240) >> 4)]);
			written += 2;
		}
		
		if (res == 1) {
			result.append("==");
			written +=2;
		} else if (res == 2) {
			result.append(base64chars[((b1 & 15) << 2)]);
			result.append("=");
			written += 2;
		}
        return result.toString();
    } 
    
    /**
     * Main method callable to use this class as Base64 encoder from commandline
     * (usefull for debugging).
     */
    public static void main(String[] arg) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int b;
		try {
			while ((b = System.in.read()) != -1 ) {
				bo.write(b);
			}
			byte[] bor = bo.toByteArray();
			System.out.print(encode(bor));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }    
}   
