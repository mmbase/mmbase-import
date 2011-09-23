/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.lang.reflect.*;
import java.io.*;
import java.text.*;
import java.util.regex.*;
import org.mmbase.util.logging.*;

/**
 * This transformer removes all diacritics from the characters of a string.
 * Depends on Java 1.6 or higher to use java.text.Normalizer but falls back for lower versions.
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */

public class DiacriticsRemover extends StringTransformer {
    private static final long serialVersionUID = 0L;
    private static final Logger LOG = Logging.getLoggerInstance(DiacriticsRemover.class);
    public static final DiacriticsRemover INSTANCE = new DiacriticsRemover();

    public static final Pattern DIACRITICS
        = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+"); // I have no idea what IsLm and IsSk mean.
    // http://www.fileformat.info/info/unicode/block/combining_diacritical_marks/index.htm
    // [\u0300-\u0367]+"

    @Override
    public String transform(String str) {

        try {
            Class normalizer16Class = Class.forName("java.text.Normalizer");
            Class form16Class = Class.forName("java.text.Normalizer$Form");
            
            Method m1 = normalizer16Class.getMethod("normalize", new Class[] { 
                CharSequence.class, form16Class });
            Object form = form16Class.getField("NFD").get(null);
            
            //str = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
            str = (String) m1.invoke(null, new Object[] { (CharSequence)str, form } );
            
        } catch (Exception e) {
            LOG.warn("Exception invoking java.text.Normalizer (please install a jvm > 1.6) : " + e);
            
            try {
                Class normalizer15Class = Class.forName("sun.text.Normalizer");
                Class mode15Class = Class.forName("sun.text.Normalizer$Mode");
                
                Method m2 = normalizer15Class.getMethod("normalize", new Class[] { 
                    String.class, mode15Class, int.class });
                Object mode = normalizer15Class.getField("DECOMP").get(null);
                
                //str = sun.text.Normalizer.normalize(str, sun.text.Normalizer.DECOMP, 0);
                str = (String) m2.invoke(null, new Object[] { str, mode, 0 });
            
            } catch (Exception ex) {
                LOG.warn("Exception invoking sun.text.Normalizer : " + ex);
            }
        }
        
        return DIACRITICS.matcher(str).replaceAll("");

        //return DIACRITICS.matcher(Normalizer.normalize(r, Normalizer.Form.NFD)).replaceAll("");
    }

}
