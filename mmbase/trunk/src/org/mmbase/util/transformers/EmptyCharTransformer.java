/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;


/**
 * This is the transformer which does not transform.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public class EmptyCharTransformer extends AbstractCharTransformer implements CharTransformer {

    public Writer transform(Reader r) {
        StringWriter sw = new StringWriter();
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                sw.write(c);
            }
        } catch (java.io.IOException e) {
        }
        return sw;        
    } 
    public Writer transformBack(Reader r) {
        return transform(r);
    }

    public String transform(String s) {
        return s;
    }

    public String transformBack(String s) {
        return s;
    }

    public String toString() {
        return "NOTHING";
    }

}
