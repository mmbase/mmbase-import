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

    Writer transform(InputStream r); 
    Writer transform(InputStream r, Writer w); 

    OutputStream transformBack(Reader r);
    OutputStream transformBack(Reader r, OutputStream o);

    String transform(byte[] r); 
    byte[] transformBack(String r);

}
