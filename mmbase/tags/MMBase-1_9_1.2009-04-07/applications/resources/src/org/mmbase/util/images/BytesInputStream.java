/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.ByteArrayInputStream;

/**
 * Abstract implemntatinof a Image
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 */

public class BytesInputStream extends ByteArrayInputStream {
    BytesInputStream(byte[] b) {
        super(b);
    }
    byte[] getBuffer() {
        return buf;
    }
}
