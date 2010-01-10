/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface ByteToCharTransformer extends Transformer {

    public Writer transform(InputStream r); 
    public Writer transform(InputStream r, Writer w); 

    public OutputStream transformBack(Reader r);
    public OutputStream transformBack(Reader r, OutputStream o);

    public String transform(byte[] r); 
    public byte[] transformBack(String r);

}
