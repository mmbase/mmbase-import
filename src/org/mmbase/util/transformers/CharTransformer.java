/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

/**
 * Interface for transformations.
 *
 * @author Michiel Meeuwissen
 */

public interface CharTransformer extends Transformer {

    public Writer transform(Reader r, Writer w); 
    public Writer transformBack(Reader r, Writer w);

    public Writer transform(Reader r); 
    public Writer transformBack(Reader r);

    public String transform(String r); 
    public String transformBack(String r);

}
