/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;


/**
 * This tranformer is the equivalent of piping to /dev/null
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id$
 */

public class Swallower extends ReaderTransformer implements CharTransformer {

    // implementation, javadoc inherited
    public Writer transform(Reader r, Writer w) {
        return w;
    } 

    // implementation, javadoc inherited
    public Writer transformBack(Reader r, Writer w) {
        throw new UnsupportedOperationException("This is utterly impossible :-)");
    }

    // overridden for performance.
    public String transform(String s) {
        return "";
    }
    public String toString() {
        return "SWALLOW";
    }

}
