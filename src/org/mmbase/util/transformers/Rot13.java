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
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class Rot13 extends ReaderTransformer implements CharTransformer {

    private static Logger log = Logging.getLoggerInstance(Rot13.class);

    protected Writer rot13(Reader r, Writer w) {
        try {
            int c = r.read();
            while (c != -1) {
                if (c >= 'a' && c <= 'm') {
                    c += 13;
                } else if  (c >= 'n' && c <= 'z') {
                    c -= 13;
                } else if  (c >= 'A' && c <= 'M') {
                    c += 13;
                } else if  (c >= 'A' && c <= 'Z') {
                    c -= 13;
                }
                w.write(c);
            }
        } catch (java.io.IOException ioe) {
            log.error(ioe);
        }
        return w;
    }

    public Writer transform(Reader r, Writer w) {
        return rot13(r, w);
    }

    public Writer transformBack(Reader r, Writer w) {
        return rot13(r, w);
    }

    public String toString() {
        return "ROT-13";
    }
}
