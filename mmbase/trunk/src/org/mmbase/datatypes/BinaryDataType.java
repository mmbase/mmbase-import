/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import org.mmbase.util.logging.*;
import java.util.regex.Pattern;

/**
 * The datatype associated with byte arrays ('blobs').
 *
 * @author Pierre van Rooden
 * @version $Id: BinaryDataType.java,v 1.6 2006-01-06 14:21:50 michiel Exp $
 * @since MMBase-1.8
 */
public class BinaryDataType extends AbstractLengthDataType {

    private static final Logger log = Logging.getLoggerInstance(BinaryDataType.class);

    Pattern validMimeTypes = Pattern.compile(".*");
    /**
     * Constructor for binary field.
     * @param name the name of the data type
     */
    public BinaryDataType(String name) {
        super(name, byte[].class);
    }


    // 
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
    /**
     * Returns a regular expression which describes wich mime-types are valid for blobs with this
     * DataType. This is not yet available as a Restriction, only as a property.
     */
    public Pattern getValidMimeTypes() {
        return validMimeTypes;
    }
    
    public void setValidMimeTypes(Pattern pattern) {
        validMimeTypes = pattern;
    }

}
