/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Base64 is an implementation of the Base64 algorithm.
 *
 * @author Daniel
 * @author Rob Vermeulen
 */
public class Base64 {

    private final static char pem2_array[] = {
        'A','B','C','D','E','F','G','H','I','J','K','L','M',
        'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
        'a','b','c','d','e','f','g','h','i','j','k','l','m',
        'n','o','p','q','r','s','t','u','v','w','x','y','z',
        '0','1','2','3','4','5','6','7','8','9','+','/'
    };

    private final static byte pem_array[] = {
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,62,64,64,64,63,
        52,53,54,55,56,57,58,59,60,61,64,64,64,64,64,64,64,0,1,2,3,4,5,6,7,8,9,
        10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,64,64,64,64,64,64,26,27,
        28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,
        64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,64,
        64,64,64,64,64,64,64,64,64,64,64,64,64
    };

    /**
     * decodes the base64 string.
     *
     * @param mimel the string to decode
     */
    public static String decode(String mimel) {
        int i,a,b,c,d;
        String decodeline=mimel;
        String decodedline="";
        String temp,name;

        while (decodeline.length()>3) {
            a=pem_array[decodeline.charAt(0)];
            b=pem_array[decodeline.charAt(1)];
            c=pem_array[decodeline.charAt(2)];
            d=pem_array[decodeline.charAt(3)];
            if (c!=64 && d!=64) {
                decodedline+=(char)((a<<2)+((b&48)>>4));
                decodedline+=(char)(((b&15)<<4)+((c&60)>>2));
                decodedline+=(char)(((c&3)<<6)+(d&63));
            } else if (c==64) {
                decodedline+=(char)((a<<2)+((b&48)>>4));
            } else {
                decodedline+=(char)((a<<2)+((b&48)>>4));
                decodedline+=(char)(((b&15)<<4)+((c&60)>>2));
            }
            decodeline=decodeline.substring(4);
        }
        return decodedline;
    }

    /**
     * encode the base64 string.
     *
     * @param mimel the string to encode
     */
    public static String encode(String mimel) {
        int i,a,b,c,d;
        String encodeline;
        String encodedline;
        String temp,name;

        encodedline="";
        encodeline=mimel;

        if (encodeline.indexOf(0)!=-1) {
            encodeline=encodeline.substring(0,encodeline.indexOf(0));
        }
        while (encodeline.length()>2) {
            encodedline+=(char)pem2_array[((encodeline.charAt(0))&253)>>2];
            encodedline+=(char)pem2_array[(((encodeline.charAt(0))&3)<<4)|(((encodeline.charAt(1))&240)>>4)];
            encodedline+=(char)pem2_array[(((encodeline.charAt(1))&15)<<2)|(((encodeline.charAt(2))&253)>>6)];
            encodedline+=(char)pem2_array[((encodeline.charAt(2))&63)];
            encodeline=encodeline.substring(3);
        }
        if (encodeline.length()==2) {
            encodedline+=(char)pem2_array[((encodeline.charAt(0))&253)>>2];
            encodedline+=(char)pem2_array[(((encodeline.charAt(0))&3)<<4)|(((encodeline.charAt(1))&240)>>4)];
            encodedline+=(char)pem2_array[(((encodeline.charAt(1))&15)<<2)];
            encodedline+="=";
        }
        if (encodeline.length()==1) {
            encodedline+=(char)pem2_array[((encodeline.charAt(0))&253)>>2];
            encodedline+=(char)pem2_array[(((encodeline.charAt(0))&3)<<4)];
            encodedline+="==";
        }
        return encodedline;
    }
}




