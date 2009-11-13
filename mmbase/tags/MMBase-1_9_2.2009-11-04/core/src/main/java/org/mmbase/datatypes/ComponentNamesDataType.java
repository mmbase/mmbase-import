/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;

/**
 * A StringDataType with the names of all installed components.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class ComponentNamesDataType extends StringDataType {

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public ComponentNamesDataType(String name) {
        super(name);
    }

    @Override
    public String getDefaultValue(Locale locale, Cloud cloud, Field field) {
        return "core";
    }

    @Override
    public Iterator<Map.Entry<String, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        return new Iterator<Map.Entry<String, String>>() {
            Iterator<String> iterator = org.mmbase.framework.ComponentRepository.getInstance().toMap().keySet().iterator();
            public boolean hasNext() {
                return iterator.hasNext();
            }
            public Map.Entry<String, String> next() {
                String val = iterator.next();
                return new Entry<String, String>(val, val);
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


}
