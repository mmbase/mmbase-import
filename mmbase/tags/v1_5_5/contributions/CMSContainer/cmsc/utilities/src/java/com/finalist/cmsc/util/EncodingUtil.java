/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.text.Collator;
import java.util.*;


public class EncodingUtil {

    private EncodingUtil() {
        // utility class
    }
    
    private static final String PLAIN_ASCII =
        "AaEeIiOoUu"    // grave
      + "AaEeIiOoUuYy"  // acute
      + "AaEeIiOoUuYy"  // circumflex
      + "AaEeIiOoUuYy"  // tilde
      + "AaEeIiOoUuYy"  // umlaut
      + "Aa"            // ring
      + "Cc"            // cedilla
      ;

    private static final String UNICODE =
        "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"             // grave
      + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" // acute
      + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" // circumflex
      + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" // tilde
      + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" // umlaut
      + "\u00C5\u00E5"                                                             // ring
      + "\u00C7\u00E7"                                                             // cedilla
      ;

      // remove accentued from a string and replace with ascii equivalent
      public static String convertNonAscii(String s) {
         StringBuffer sb = new StringBuffer();
         int n = s.length();
         for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1){
                sb.append(PLAIN_ASCII.charAt(pos));
            }
            else {
                sb.append(c);
            }
         }
         return sb.toString();
      }

    /**
     * the byte value of a character >= 128 && <= 159 is unused in ISO-8859-1 but it is used by
     * Windows Central Europe encoding, so we can detect it and transcode some of it's
     * characters.
     */
    static Map<Character,String> w1252ToISO;
    static {
        w1252ToISO = new HashMap<Character,String>();
        w1252ToISO.put(new Character('\u0080'), "Euro");
        w1252ToISO.put(new Character('\u0082'), ",");
        w1252ToISO.put(new Character('\u0083'), "f");
        w1252ToISO.put(new Character('\u0085'), "...");
        w1252ToISO.put(new Character('\u0088'), "^");
        w1252ToISO.put(new Character('\u008B'), "<");
        w1252ToISO.put(new Character('\u008C'), "OE");
        w1252ToISO.put(new Character('\u0091'), "'");
        w1252ToISO.put(new Character('\u0092'), "'");
        w1252ToISO.put(new Character('\u0093'), "\"");
        w1252ToISO.put(new Character('\u0094'), "\"");
        w1252ToISO.put(new Character('\u0095'), ".");
        w1252ToISO.put(new Character('\u0096'), "-");
        w1252ToISO.put(new Character('\u0097'), "-");
        w1252ToISO.put(new Character('\u0098'), "~");
        w1252ToISO.put(new Character('\u009B'), ">");
        w1252ToISO.put(new Character('\u009C'), "oe");
    }

    public static String windows1252ToISO(String windows1252encoded) {
        StringBuffer transcodedValue = new StringBuffer(windows1252encoded.length());
        for (int i = 0; i < windows1252encoded.length(); i++) {
            char curChar = windows1252encoded.charAt(i);
            if ((curChar >= 128) && (curChar <= 159)) {
                // this range of characters is unused in ISO-8859-1 but
                // is used by Windows Central Europe encoding, so we can
                // detect it and transcode some of it's characters.
                Character charIndex = new Character(curChar);
                String replacementStr = w1252ToISO.get(charIndex);
                if (replacementStr != null) {
                    transcodedValue.append(replacementStr);
                }
            }
            else {
                transcodedValue.append(curChar);
            }
        }
        return transcodedValue.toString();
    }

    /** convert from UTF-8 encoded HTML-Pages -> internal Java String Format
     * @param s string to conver
     * @return converted string 
     */
    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    /** convert from internal Java String Format -> UTF-8 encoded HTML/JSP-Pages
     * @param s string to conver
     * @return converted string 
     */
    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"));
        }
        catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
    
    public int compareAccentuatedCharacters(String s1, String s2, Locale locale) {
        // (javadoc)
        // The result of String.compareTo() is a negative integer
        // if this String object lexicographically precedes the
        // argument string. The result is a positive integer if
        // this String object lexicographically follows the argument
        // string. The result is zero if the strings are equal;
        // compareTo returns 0 exactly when the equals(Object)
        // method would return true.

        // (javadoc)
        // Collator.compare() compares the source string to the target string
        // according to the collation rules for this Collator.
        // Returns an integer less than, equal to or greater than zero
        // depending on whether the source String is less than,
        // equal to or greater than the target string.
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(java.text.Collator.CANONICAL_DECOMPOSITION);
        //  or  collator.setStrength(java.text.Collator.SECONDARY); to be non case sensitive  
        return collator.compare(s1, s2);
    }

    public boolean equalAccentuatedCharacters(String s1, String s2, Locale locale) {
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(java.text.Collator.CANONICAL_DECOMPOSITION);
        collator.setStrength(java.text.Collator.SECONDARY); // to be non case sensitive  
        return collator.compare(s1, s2) == 0;
    }

}
