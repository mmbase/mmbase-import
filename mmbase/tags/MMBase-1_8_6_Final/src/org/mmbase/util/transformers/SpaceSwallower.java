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
 * Swallows all spaces.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */

public class SpaceSwallower extends ReaderTransformer implements CharTransformer {

    private static final Logger log = Logging.getLoggerInstance(SpaceSwallower.class);

    public Writer transform(Reader r, Writer w) {

        try {
            log.debug("Starting to remove all space.");

            int c = r.read();
            while (c != -1) {
                if (! Character.isWhitespace((char) c)) {
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
        return "SPACESWALLOWER";
    }
}
