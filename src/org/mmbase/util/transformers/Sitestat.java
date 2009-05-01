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
 * Transforms the input to the characters which are alowed in Sitestat keys for 
 * page statistics, being: "A-Z, a-z, 0-9, - . _".
 * 
 * @author Andre van Toly
 * @since MMBase-1.7
 * @version $Id$
 */

public class Sitestat extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(Sitestat.class);
    private static String alowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-.";

    public Writer transform(Reader r, Writer w) {
        try {
            if (log.isDebugEnabled()) log.debug("Starting transforming string for Sitestat");
            int d = -1;
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if (alowedChars.indexOf((char)c) > -1) {
                    w.write((char)c);
                    d = c;
                } else if (d != '_') {
                    w.write('_');
                    d = '_';
                }
            }            
            if (log.isDebugEnabled()) log.debug("Finished transforming string for Sitestat");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }


    public String toString() {
        return "sitestat";
    }
}
