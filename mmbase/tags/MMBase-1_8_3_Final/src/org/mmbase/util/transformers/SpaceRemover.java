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
 * Replace 1 or white space by 1 space, and all spaces on the begin and end.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.4
 */

public class SpaceRemover extends ReaderTransformer implements CharTransformer {

    private static final Logger log = Logging.getLoggerInstance(SpaceRemover.class);

    public Writer transform(Reader r, Writer w) {

        try {
            log.debug("Starting to remove all space.");

            int c = r.read();

            // remove all leading space;
            while (c != -1 && Character.isWhitespace((char) c)) {
                c = r.read();
            }
            // consider the rest;
            boolean writing = true;
            while (c != -1) {
                if (Character.isWhitespace((char) c)) {
                    writing = false;
                } else {
                    if (! writing) {
                        w.write(' ');
                        writing = true;
                    }
                    w.write(c);
                }
                c = r.read();
            }
            log.debug("Finished");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public String toString() {
        return "SPACEREMOVER";
    }
}
