/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.logging.*;

/**
 * Escapes and Unescapes undesirable characters using % (URLEncoding)
 *
 * @deprecated use Encode
 * @author vpro
 * @version $Id$
 */
public class URLEscape {

    // logger
    private static Logger log = Logging.getLoggerInstance(URLEscape.class.getName());

    /**
     * List for all ASCII characters whether it can be part of an
     * URL line.
     */
    static boolean isacceptable[] = {
        false, false, false, false, false, false, false, false,    //  !"#$%&'
        false, false, true, true, true, true, true, false,         // ()*+,-./
        true, true, true, true, true, true, true, true,            // 01234567
        true, true, true, false, false, false, false, false,       // 89:;<=>?
        true, true, true, true, true, true, true, true,            // @ABCDEFG
        true, true, true, true, true, true, true, true,            // HIJKLMNO
        true, true, true, true, true, true, true, true,            // PQRSTUVW
        true, true, true, false, false, false, false, true,        // XYZ[\]^_
        false, true, true, true, true, true, true, true,           // `abcdefg
        true, true, true, true, true, true, true, true,            // hijklmno
        true, true, true, true, true, true, true, true,            // pqrstuvw
        true, true, true, false, false, false, false, false        // xyz{|}~
    };

    /**
     * Hex characters
     */
    static char hex[] = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };

    /**
     * Character to use for escaping invalid characters
     */
    static char HEX_ESCAPE='%';

    /**
     * Escape a url.
     * Replaces 'invalid characters' with their Escaped code, i.e.
     * the questionmark (?) is escaped with %3F.
     * @param str the urls to escape
     * @return the escaped url.
     */
    public static String escapeurl(String str) {
        StringBuffer esc = new StringBuffer();
        byte buf[];
        try {
            buf = str.getBytes("UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            return str; // should not happen
        }

        for (byte element : buf) {
            int a = element & 0xff;
            if (a>=32 && a<128 && isacceptable[a-32]) {
                esc.append((char)a);
            } else {
                esc.append(HEX_ESCAPE);
                esc.append(hex[a >> 4]);
                esc.append(hex[a & 15]);
            }
        }
        return esc.toString();
    }

    /**
     * converts a HEX-character to its approprtiate byte value.
     * i.e. 'A' is returned as '/011'
     * @param c the Hex character
     * @return the byte value as a <code>char</code>
     */
    private static char from_hex(char c) {
        return (char)(c >= '0' && c <= '9' ? c - '0'
            : c >= 'A' && c <= 'F' ? c - 'A' + 10
            : c - 'a' + 10);            /* accept small letters just in case */
    }

    /**
     * Unescape a url.
     * Replaces escapesequenced with the actual character.
     * i.e %3F is replaced with the the questionmark (?).
     * @param str the urls to unescape
     * @return the unescaped url.
     */
    public static String unescapeurl(String str) {
        int i;
        char j,t;
        StringBuffer esc=new StringBuffer();

        if (str!=null) {
            for (i=0;i<str.length();i++) {
                t=str.charAt(i);
                if (t==HEX_ESCAPE) {
                    t=str.charAt(++i);
                    j=(char)(from_hex(t)*16);
                    t=str.charAt(++i);
                    j+=from_hex(t);
                    esc.append(j);
                } else {
                    esc.append(t);
                }
            }
        } else {
            log.warn("Unescapeurl -> Bogus parameter");
        }
        return esc.toString();
    }

    /**
     * Method for testing this class from the command line
     */
    public static void main(String args[]) {
        for (String element : args) {
            log.info("Original : '"+element+"'");
            log.info("Escaped : '"+escapeurl(element)+"'");
            log.info("Unescaped again : '"+unescapeurl(escapeurl(element))+"'");
        }

    }
}
