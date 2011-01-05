/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;
import java.text.*;
import org.mmbase.util.logging.*;

/**
 * This trnasformer removes all diacritics from the characters of a string.
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */

public class DiacriticsRemover extends StringTransformer {

    @Override
    public String transform(String r) {
        // http://www.fileformat.info/info/unicode/block/combining_diacritical_marks/index.htm
        return Normalizer.normalize(r, Normalizer.Form.NFD).replaceAll("[\u0300-\u0367]", "");
    }

}
