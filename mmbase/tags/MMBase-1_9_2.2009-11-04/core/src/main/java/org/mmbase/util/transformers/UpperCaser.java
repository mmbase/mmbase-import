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
 * A 'hello world' for CharTransformers.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id$
 */

public class UpperCaser extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(UpperCaser.class);

    public Writer transform(Reader r, Writer w) {
        try {
            log.debug("Starting uppercasing");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                w.write(Character.toUpperCase((char) c));
            }            
            log.debug("Finished uppercasing");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }


    public String toString() {
        return "uppercaser";
    }
}
