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
 * Optionally, white space can be replaced by something else (e.g. a hyphen).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id$
 */

public class Identifier extends ReaderTransformer implements CharTransformer {
    private static final long serialVersionUID = 0L;
    private static Logger log = Logging.getLoggerInstance(Identifier.class);

    private String whitespace = "_";
    /**
     * @since MMBase-1.9.4
     */
    public void setWhitespaceReplacer(String ws) {
        whitespace = ws;
    }

    /**
     * @since MMBase-1.9.4
     */
    public String getWhitespaceReplacer() {
        return whitespace;
    }

    public Writer transform(Reader r, Writer w) {
        try {
            log.debug("Starting identifier");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (Character.isLetterOrDigit((char)c)) {
                    w.write((char)c);
                } if (Character.isWhitespace((char) c))  {
                    w.write(whitespace);
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

    @Override
    public String toString() {
        return "IDENTIFIER";
    }
}
