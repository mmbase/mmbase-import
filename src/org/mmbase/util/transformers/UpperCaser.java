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
 * A 'hello world' for CharTransformers.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public class UpperCaser extends AbstractCharTransformer implements CharTransformer {


    public Writer transform(Reader r, Writer w) {
        try {
            while (true) {
                int c = r.read();
                if (c == -1) break;
                w.write(Character.toUpperCase((char) c));
            }            
        } catch (java.io.IOException e) {
            //System.out.println("u" + e.toString());
        }
        return w;
    }


    public String toString() {
        return "uppercaser";
    }
}
