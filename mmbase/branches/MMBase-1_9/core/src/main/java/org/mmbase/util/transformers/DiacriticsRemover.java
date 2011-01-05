/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.text.*;
import java.util.regex.*;
import org.mmbase.util.logging.*;

/**
 * This trnasformer removes all diacritics from the characters of a string.
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */

public class DiacriticsRemover extends StringTransformer {
    private static final long serialVersionUID = 0L;
    public static final DiacriticsRemover INSTANCE = new DiacriticsRemover();

    public static final Pattern DIACRITICS
        = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+"); // I have no idea what IsLm and IsSk mean.
    // http://www.fileformat.info/info/unicode/block/combining_diacritical_marks/index.htm
    // [\u0300-\u0367]+"

    @Override
    public String transform(String r) {
        return DIACRITICS.matcher(Normalizer.normalize(r, Normalizer.Form.NFD)).replaceAll("");
    }

}
