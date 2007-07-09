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
 * This is the character transformer which does not actually transform
 * anything, it just copies the reader to the writer.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 * @version $Id: CopyCharTransformer.java,v 1.6 2005-05-08 13:22:05 michiel Exp $
 */

public class CopyCharTransformer extends ReaderTransformer implements CharTransformer {

    public static final CopyCharTransformer INSTANCE = new CopyCharTransformer();

    private CopyCharTransformer() {
        super();
    }

    // implementation, javadoc inherited
    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                w.write(c);
            }
        } catch (java.io.IOException e) {
            System.out.println("c " + e.toString());
        }
        return w;        
    } 

    // implementation, javadoc inherited
    public Writer transformBack(Reader r, Writer w) {
        return transform(r, w);
    }

    // overridden for performance.
    public String transform(String s) {
        return s;
    }

    // overridden for performance.
    public String transformBack(String s) {
        return s;
    }

    public String toString() {
        return "COPY";
    }

}
