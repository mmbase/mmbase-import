/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;

import org.mmbase.util.logging.*;

/**
 * Surrogates the Windows CP1252 characters which are not valid
 * ISO-8859-1. Java has the tricky feature that is does not translate
 * CP1252 bytes correctly to ISO-8859-1 (?-ing the incorrect
 * characters).
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.2
 * @version $Id: CP1252Surrogator.java,v 1.1 2004-11-12 15:46:12 michiel Exp $
 */

public class CP1252Surrogator extends ReaderTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(CP1252Surrogator.class);

    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                // used: http://www.kostis.net/charsets/cp1252.htm
                switch (c) {
                case 128: w.write("EURO"); break; // EURO SIGN
                case 130: w.write(',');    break; // SINGLE LOW-9 QUOTATION MARK
                case 131: w.write('f');    break; // LATIN SMALL LETTER F WITH HOOK
                case 132: w.write(",,");   break; // DOUBLE LOW-9 QUOTATION MARK
                case 133: w.write("...");  break; // HORIZONTAL ELLIPSIS
                case 134: w.write('?');    break; // DAGGER
                case 135: w.write('?');    break; // DOUBLE DAGGER
                case 136: w.write('^');    break; // MODIFIER LETTER CIRCUMFLEX ACCENT
                case 137: w.write("0/00"); break; // PER MILLE SIGN
                case 138: w.write('S');    break; // LATIN CAPITAL LETTER S WITH CARON
                case 139: w.write('<');    break; // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
                case 140: w.write("OE");   break; // LATIN CAPITAL LIGATURE OE
                case 141: w.write('Z');    break; // LATIN CAPITAL LETTER Z WITH CARON
                case 145: w.write('`');    break; // LEFT SINGLE QUOTATION MARK
                case 146: w.write('\'');   break; // RIGHT SINGLE QUOTATION MARK
                case 147: w.write('\"');   break; // LEFT DOUBLE QUOTATION MARK
                case 148: w.write('\"');   break; // RIGHT DOUBLE QUOTATION MARK
                case 149: w.write('-');    break; // BULLET
                case 150: w.write('-');    break; // EN DASH
                case 151: w.write('-');    break; // EM DASH
                case 152: w.write('~');    break; // SMALL TILDE
                case 153: w.write("TM");   break; // TRADE MARK SIGN
                case 154: w.write('s');    break; // LATIN SMALL LETTER S WITH CARON
                case 155: w.write('>');    break; // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
                case 156: w.write("oe");   break; // LATIN SMALL LIGATURE OE
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


    public String toString() {
        return "CP1252 surrogator";
    }

    public static void main(String[] args) {
        byte[] testString = new byte[32];
        for (int i = 0; i < 32; i++) {
            testString[i] = (byte) i;            
        }
        try {            
            System.out.println("Test-string: " + new String(testString, "CP1252"));        
        } catch (Exception e) {
            System.err.println("" + e);            
        }
        
         
    }
    
        
}
