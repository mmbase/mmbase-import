/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Any non-ASCII character will be replaced by an XML-entity.
 * 
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id$
 */
public class XmlEntities extends ReaderTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(XmlEntities.class);
      
    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if ( // c >= '\u0020' && Control character
                    c <= '\u007f') {
                    w.write(c); // ASCII character, simply write
                } else {
                    w.write("&#" + c + ";"); // construct XML style unicode escaping
                }
            }
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
    }
    public String toString() {
        return "XMLENTITIES";
    }
}
