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
 * Transforms strings to identifiers, replacing punctuation and whitespace with
 * underscores.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: Identifier.java,v 1.1 2003-09-30 08:01:22 pierre Exp $
 */

public class Identifier extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(LowerCaser.class);

    public Writer transform(Reader r, Writer w) {
        try {
            log.debug("Starting identifier");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (Character.isLetterOrDigit((char)c)) {
                    w.write((char)c);
                } else {
                    w.write('_');
                }
            }
            log.debug("Finished identifier");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }

    public String toString() {
        return "IDENTIFIER";
    }
}
