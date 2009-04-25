/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;


/**

 * @author Michiel Meeuwissen
 */

public class BinaryStringDataType extends StringDataType {

    private static final Logger log = Logging.getLoggerInstance(BinaryStringDataType.class);

    public BinaryStringDataType(String name) {
        super(name);
    }

    @Override
    public long getLength(Object value) {
        if (value == null) return 0;
        if (value instanceof SerializableInputStream) {
            return ((SerializableInputStream) value).getSize();
        } else {
            return super.getLength(value);
        }
    }


    @Override
    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        return Casting.toSerializableInputStream(value);
    }


}
