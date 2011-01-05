/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;
import java.util.regex.*;


import org.mmbase.util.logging.*;

/**
 * Transforms strings to ascii strings. Non-ascii characters will become question marks.
 * Optionally, the question marks can be replaced by another character.
 * After some examples I found at http://stackoverflow.com/questions/2096667/convert-unicode-to-ascii-without-changing-the-string-length-in-java
 *
 * @author Andr&eacute; van Toly
 * @since MMBase-1.9.5
 * @version $Id$
 */

public class Asciifier extends StringTransformer {
    private static final long serialVersionUID = 0L;
    private static final Logger LOG = Logging.getLoggerInstance(Asciifier.class);

    private static final Pattern NOASCII = Pattern.compile("[^\\p{ASCII}]");
    private static final Pattern NOASCII_MULTIPLE = Pattern.compile("[^\\p{ASCII}]+");

    private String replacer = "?";

    private boolean removeDiacritics = true;
    private boolean collapseMultiple = false;

    /**
     * Replacement character in stead of a question mark. Note that if you use more then one
     * character in the replacement string only the first character used.
     */
    public void setReplacer(String r) {
        replacer = r;
    }

    /**
     * The replacer character in stead of a ?.
     */
    public String getReplacer() {
        return replacer;
    }

    public void setRemoveDiacritis(boolean b) {
        removeDiacritics = b;
    }
    public void setCollapseMultiple(boolean m) {
        collapseMultiple = m;
    }


    @Override
    public String transform(String str) {
        LOG.debug("Starting asciifier");

        if (removeDiacritics) {
            str = DiacriticsRemover.INSTANCE.transform(str);
        }
        if (collapseMultiple) {
            str = NOASCII_MULTIPLE.matcher(str).replaceAll(replacer);
        } else {
            str = NOASCII.matcher(str).replaceAll(replacer);
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
        Asciifier a = new Asciifier();
        if (argv.length > 1) {
            a.setReplacer(argv[1]);
        }
        System.out.println(a.transform(argv[0]));
    }

}
