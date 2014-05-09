/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.util.*;

import org.mmbase.util.logging.*;

/**
 * Surrogates the Windows CP1252 characters which are not valid ISO-8859-1.  It can also repair
 * wrongly encoded Strings (byte arrays which were actually CP1252, but were considered ISO-8859-1
 * when they were made to a Java String).
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.2
 * @version $Id$
 */

public class CP1252Surrogator extends ConfigurableReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(CP1252Surrogator.class);


    public static final int WELL_ENCODED = 0;
    public static final int WRONG_ENCODED = 1;


    public CP1252Surrogator() {
        this(WELL_ENCODED);
    }
    public CP1252Surrogator(int conf) {
        super(conf);
    }


    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                int cp;
                if (to == WELL_ENCODED) { // CP1252 chars appear all over the place in the unicode set, this makes a nice an clear int of it, with the ISO-8859-1 values (0-255)
                    cp = ("" + (char) c).getBytes("CP1252")[0] & 0xff; // should this really be done by a String?
                } else {
                    cp = c;
                    
                }                
                switch (cp) {
                case 128: w.write("EURO"); break; // EURO SIGN
                case 129: w.write('?');    break; // 
                case 130: w.write(',');    break; // SINGLE LOW-9 QUOTATION MARK
                case 131: w.write('f');    break; // LATIN SMALL LETTER F WITH HOOK
                case 132: w.write(",,");   break; // DOUBLE LOW-9 QUOTATION MARK
                case 133: w.write("...");  break; // HORIZONTAL ELLIPSIS
                case 134: w.write('+');    break; // DAGGER
                case 135: w.write("++");   break; // DOUBLE DAGGER
                case 136: w.write('^');    break; // MODIFIER LETTER CIRCUMFLEX ACCENT
                case 137: w.write("0/00"); break; // PER MILLE SIGN
                case 138: w.write('S');    break; // LATIN CAPITAL LETTER S WITH CARON
                case 139: w.write('<');    break; // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
                case 140: w.write("OE");   break; // LATIN CAPITAL LIGATURE OE
                case 141: w.write('?');    break; // 
                case 142: w.write('Z');    break; // LATIN CAPITAL LETTER Z WITH CARON
                case 143: w.write('?');    break; // 
                case 144: w.write('?');    break; // 
                case 145: w.write('\'');    break; // LEFT SINGLE QUOTATION MARK
                case 146: w.write('\'');   break; // RIGHT SINGLE QUOTATION MARK
                case 147: w.write('\"');   break; // LEFT DOUBLE QUOTATION MARK
                case 148: w.write('\"');   break; // RIGHT DOUBLE QUOTATION MARK
                case 149: w.write('-');    break; // BULLET
                case 150: w.write('-');    break; // EN DASH
                case 151: w.write('-');    break; // EM DASH
                case 152: w.write('~');    break; // SMALL TILDE
                case 153: w.write("(TM)"); break; // TRADE MARK SIGN
                case 154: w.write('s');    break; // LATIN SMALL LETTER S WITH CARON
                case 155: w.write('>');    break; // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
                case 156: w.write("oe");   break; // LATIN SMALL LIGATURE OE
                case 157: w.write('?');    break; // 
                case 158: w.write('z');    break; // LATIN SMALL LETTER Z WITH CARON
                case 159: w.write('Y');    break; // LATIN CAPITAL LETTER Y WITH DIAERESIS
                default:  w.write(c);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return w;
    }


    public Map transformers() {
        Map h = new HashMap();
        h.put("CP1252_SURROGATOR",  new Config(CP1252Surrogator.class, WELL_ENCODED,  "Takes the java String, and surrogates the 32 characters of it which are in CP1252 but not in ISO-8859-1"));
        h.put("CP1252_WRONG_SURROGATOR",  new Config(CP1252Surrogator.class, WRONG_ENCODED,  "Also surrogates the characters specific to CP1252, but supposed the String originally wrong encoded (it was suppoed to be ISO-8859-1, but actually was CP1252)"));
        return h;
    }


    public String getEncoding() {
        switch (to) {
        case WELL_ENCODED:
            return "CP1252_SURROGATOR";
        case WRONG_ENCODED:
            return "CP1252_WRONG_SURROGATOR";
        default :
            throw new UnknownCodingException(getClass(), to);
        }
    }


    public static byte[] getTestBytes() {
        byte[] testBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            testBytes[i] = (byte) (-128 + i);
        }
        return testBytes;
    }

    public static String getTestString() {    
        try {
            return new String(getTestBytes(), "CP1252");
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * For testing only.
     *
     * Use on a UTF-8 terminal:
     *  java -Dfile.encoding=UTF-8 org.mmbase.util.transformers.CP1252Surrogator
     * Or, on a ISO-8859-1 terminal: (you will see question marks, for the CP1252 chars)
     *  java -Dfile.encoding=ISO-8859-1 org.mmbase.util.transformers.CP1252Surrogator 
     * Or, if - may God forbid - you have a CP1252 terminal:
     *  java -Dfile.encoding=CP1252 org.mmbase.util.transformers.CP1252Surrogator 
     * 
     * This last thing you may simulate with something like this:
     *  java -Dfile.encoding=CP1252 org.mmbase.util.transformers.CP1252Surrogator  | konwert cp1252-utf8
     *
     */
    public static void main(String[] args) {

        // construct a String with all specific CP1252 charachters.       
        String testStringCP1252 = "bla bla " + getTestString();
        String testStringISO1 = "";
        try {
            testStringISO1   = "bla bla " + new String(getTestBytes(), "ISO-8859-1"); /// it's a lie, but try it anyway.
        } catch (Exception e) {
            log.error("", e);
        }

        CharTransformer transOk  = new CP1252Surrogator();
        CharTransformer transNok = new CP1252Surrogator(WRONG_ENCODED);
        CharTransformer unicode  = new UnicodeEscaper();
        
        System.out.println("Test-string (CP1252): " + testStringCP1252);        
        // System.out.println("Test-string (ISO-1) : " + testStringISO1); _DOES NOT MAKE SENSE_.

        System.out.println("Java-escaped (CP1252): " + unicode.transform(testStringCP1252));        
        System.out.println("Java-escaped (ISO-1) : "  + unicode.transform(testStringISO1));        
        System.out.println("Surrogated test-string (CP1252): " + transOk.transform(testStringCP1252));
        System.out.println("Surrogated test-string (ISO-1) : " + transNok.transform(testStringISO1)); // fixe the non-sensical string.
                           
         
    }
    
        
}
