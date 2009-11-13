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
 * Rot5 implementation. Digits 0-4 are shifted 5 positions forward, digits 5-9 are shifted 5
 * backwards and other characters are untouched, which results in scrambled - but easily decoded -
 * strings. You would want this to combine with {@link Rot13} for the letters.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class Rot5 extends ReaderTransformer implements CharTransformer {

    private static final Logger log = Logging.getLoggerInstance(Rot5.class);

    protected Writer rot5(Reader r, Writer w) {
        try {
            int c = r.read();
            while (c != -1) {
                if (c >= '0' && c <= '4') {
                    c += 5;
                } else if  (c >= '5' && c <= '9') {
                    c -= 5;
                }
                w.write(c);
                c = r.read();
            }
        } catch (java.io.IOException ioe) {
            log.error(ioe);
        }
        return w;
    }

    public Writer transform(Reader r, Writer w) {
        return rot5(r, w);
    }

    /**
     * For Rot13, transformBack does the same as {@link #transform}
     **/
    public Writer transformBack(Reader r, Writer w) {
        return rot5(r, w);
    }

    public String toString() {
        return "ROT-5";
    }
}
