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

    public OutputStream transform(InputStream r, OutputStream o); 
    public OutputStream transformBack(InputStream r, OutputStream o);

    public byte[] transform(byte[] r); 
    public byte[] transformBack(byte[]  r);
}
