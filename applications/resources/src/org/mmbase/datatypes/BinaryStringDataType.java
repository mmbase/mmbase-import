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
import java.util.*;


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

    @Override
    protected String castToPresent(Object value, Node node, Field field) {
        return Casting.toSerializableInputStream(value).getName();
    }
    @Override
    protected Collection<LocalizedString> validateRequired(Collection<LocalizedString> errors, Object castValue, Object value, Node  node, Field field) {
        String v = org.mmbase.datatypes.processors.BinaryFile.getFileName(node, field, ((SerializableInputStream) castValue).getName());
        return requiredRestriction.validate(errors, v, node, field);

    }

    @Override
    public String castForSearch(final Object value, final Node node, final Field field) {
        return Casting.toSerializableInputStream(value).getName();
    }

}
