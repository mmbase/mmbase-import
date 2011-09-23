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
 * Transforms to lowercase
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */

public class LowerCaser extends ReaderTransformer implements CharTransformer {
    private static final long serialVersionUID = 0L;
    private static Logger log = Logging.getLoggerInstance(LowerCaser.class);

    public Writer transform(Reader r, Writer w) {
        try {
            log.debug("Starting lowercasing");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                w.write(Character.toLowerCase((char) c));
            }
            log.debug("Finished lowercasing");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }


    @Override
    public String toString() {
        return "LOWERCASER";
    }
}
