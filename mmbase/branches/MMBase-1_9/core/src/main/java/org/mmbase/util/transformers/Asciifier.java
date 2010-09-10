/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.lang.reflect.*;

import java.io.Reader;
import java.io.Writer;

import org.mmbase.util.logging.*;

/**
 * Transforms strings to ascii strings. Uses reflection to decide between jdk15 sun.text.Normalize
 * and jdk16 java.text.Normalize. If it can not find any of the normalizers is just returns the
 * string unharmed. Non-ascii characters will become question marks.
 * Optionally, the question marks can be replaced by another character.
 * After some examples I found at http://stackoverflow.com/questions/2096667/convert-unicode-to-ascii-without-changing-the-string-length-in-java
 *
 * @author Andr&eacute; van Toly
 * @since MMBase-1.9.5
 * @version $Id: Asciifier.java 42169 2010-05-16 17:14:51Z michiel $
 */

public class Asciifier extends StringTransformer {
    private static final long serialVersionUID = 0L;
    private static Logger log = Logging.getLoggerInstance(Asciifier.class);

    private char replacer = '?';
    
    /**
     * Replacement character in stead of a question mark. Note that if you use more then one 
     * character in the replacement string only the first character used. 
     */
    public void setReplacer(String r) {
        replacer = r.charAt(0);
    }

    /**
     * The replacer character in stead of a ?. 
     */
    public char getReplacer() {
        return replacer;
    }

    /**
     * Optionally replaces question marks (marking non-ascii characters) with something else. 
     */
     private static String replaceQuestionMark(String str, char r) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '?') {
                sb.setCharAt(i, r);
            }
        }
        return sb.toString();
    }
    
    public String transform(String s) {
        return normalize(s, replacer);
    }
    
    private String normalize(String str) {
        return normalize(str, replacer);
    }

    private static String normalize(String str, char r) {
        try {
            log.debug("Starting asciifier");
            
            try {
                Class normalizer16Class = Class.forName("java.text.Normalizer");
                Class form16Class = Class.forName("java.text.Normalizer$Form");
                
                Method m1 = normalizer16Class.getMethod("normalize", new Class[] { 
                    CharSequence.class, form16Class });
                Object form = form16Class.getField("NFD").get(null);
                
                //str = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
                str = (String) m1.invoke(null, new Object[] { (CharSequence)str, form } );
                
            } catch (Exception e) {
                log.warn("Exception invoking java.text.Normalizer : " + e);
                
                try {
                    Class normalizer15Class = Class.forName("sun.text.Normalizer");
                    Class mode15Class = Class.forName("sun.text.Normalizer$Mode");
                    
                    Method m2 = normalizer15Class.getMethod("normalize", new Class[] { 
                        String.class, mode15Class, int.class });
                    Object mode = normalizer15Class.getField("DECOMP").get(null);
                    
                    //str = sun.text.Normalizer.normalize(str, sun.text.Normalizer.DECOMP, 0);
                    str = (String) m2.invoke(null, new Object[] { str, mode, 0 });
                
                } catch (Exception ex) {
                    log.warn("Exception invoking sun.text.Normalizer : " + ex);
                }
            }
            
            //return str.replaceAll("[^\\p{ASCII}]","");
            String regex = "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+";
            str = new String(str.replaceAll(regex, "").getBytes("ascii"), "ascii");
            
            if (r != '?') {
                log.debug("replacing ? in: " + str);
                str = replaceQuestionMark(str, r);
            }
            
            log.debug("Finished identifier");
            
        } catch (Exception exc) {
            log.error(exc.toString());
        }

        return str;
    }

    @Override
    public String toString() {
        return "ASCIIFIER";
    }


    /**
     * Just to test
     */
    public static void main(String argv[]) {
        if (argv.length == 0) {
            System.out.println("Use at least one argument");
            return;
        }
        if (argv.length == 1) {
            System.out.println(normalize(argv[0], '?'));
        }
        if (argv.length == 2) {
            System.out.println(normalize(argv[0], argv[1].charAt(0) ));
        }
    }

}
