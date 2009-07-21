/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

/**
 * Abstract implmenetation of {@link ByteTransformer}. This only leave {@link
 * #transform(InputStream, OutputStream} to implement.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 */

public abstract class InputStreamTransformer implements ByteTransformer {


    public abstract OutputStream transform(InputStream r, OutputStream o);

    public OutputStream transformBack(InputStream r, OutputStream o) {
        throw new UnsupportedOperationException("transformBack is not supported for this transformer");
    }

    public byte[] transform(byte[] r) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transform(new ByteArrayInputStream(r), out);
        return out.toByteArray();
    }

    public byte[] transformBack(byte[]  r) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformBack(new ByteArrayInputStream(r), out);
        return out.toByteArray();
    }

}
