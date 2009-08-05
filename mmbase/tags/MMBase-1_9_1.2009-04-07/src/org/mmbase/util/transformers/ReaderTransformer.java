/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

/**
 * You need only to implement transform(Reader, Writer) you have the simplest
 * kind of tranformer (which is 'streamable'). The name becoming your class name.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */

public abstract class ReaderTransformer implements CharTransformer {

    // javadoc inherited
    public abstract Writer transform(Reader r, Writer w);

    // javadoc inherited
    public Writer transformBack(Reader r, Writer w) {
        throw new UnsupportedOperationException("transformBack is not supported for this transformer");
    }

    // javadoc inherited
    public final Writer transformBack(Reader r) {
        return transformBack(r, new StringWriter());
    }

    // javadoc inherited
    public final Writer transform(Reader r) {
        return transform(r, new StringWriter());
    }

    // javadoc inherited
    public String transform(String r) {
        if (r == null) return null;
        Writer sw = transform(new StringReader(r));
        return sw.toString();
    }

    // javadoc inherited
    public String transformBack(String r) {
        if (r == null) return null;
        Writer sw = transformBack(new StringReader(r));
        return sw.toString();
    }
}
