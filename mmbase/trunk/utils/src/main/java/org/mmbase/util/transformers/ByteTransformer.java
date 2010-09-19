/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface ByteTransformer extends Transformer {

    OutputStream transform(InputStream r, OutputStream o);
    OutputStream transformBack(InputStream r, OutputStream o);

    byte[] transform(byte[] r);
    byte[] transformBack(byte[]  r);
}
