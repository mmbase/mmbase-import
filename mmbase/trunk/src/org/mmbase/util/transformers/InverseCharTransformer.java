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
 * Turns a Transformer around. This only works if this Tranformer
 * implements transform(Reader, Writer) and/or transformBack(Reader,
 * Writer);
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public class InverseCharTransformer extends AbstractCharTransformer implements CharTransformer {

    private CharTransformer ct;

    /**
     * The constructor
     * @param c The CharTransformed to be turned around.
     */

    public InverseCharTransformer(CharTransformer c) {
        super();
        ct = c;
    }

    public Writer transformBack(Reader r, Writer w) {
        return ct.transform(r, w);
    }

    public Writer transform(Reader r, Writer w) {
        return ct.transformBack(r, w);
    }

    public String toString() {
        return "INVERSE "  + ct;
    }
}
