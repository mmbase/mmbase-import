/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import org.mmbase.util.logging.*;
/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BinaryDataType.java,v 1.4 2005-10-17 15:28:13 michiel Exp $
 * @since MMBase-1.8
 */
public class BinaryDataType extends AbstractLengthDataType {

    private static final Logger log = Logging.getLoggerInstance(BinaryDataType.class);
    /**
     * Constructor for binary field.
     * @param name the name of the data type
     */
    public BinaryDataType(String name) {
        super(name, byte[].class);
    }



    public long getLength(Object value) {
        if (! (value instanceof byte[])) {
            throw new RuntimeException("Value " + value + " of " + getName() + " is not a byte array but" + (value == null ? "null" : value.getClass().getName()));
        }
        byte[] bytes = (byte[]) value;
        if (log.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer("[");
            for (int i = 0 ; i < bytes.length; i++) {
                buf.append((char) bytes[i]);
                if (i + 1 < bytes.length) {
                    buf.append(", ");
            }
            }
            buf.append("]");
            log.debug("Getting length of " + buf);
        }
        return bytes.length;
    }


}
