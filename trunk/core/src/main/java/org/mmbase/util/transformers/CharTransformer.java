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

public interface CharTransformer extends Transformer, java.io.Serializable {


    Writer transform(Reader r, Writer w);
    Writer transformBack(Reader r, Writer w);

    Writer transform(Reader r);
    Writer transformBack(Reader r);

    String transform(String r);
    String transformBack(String r);

}
