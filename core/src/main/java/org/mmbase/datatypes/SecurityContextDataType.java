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
 * A StringDataType with all security contexts strings as possible value.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class SecurityContextDataType extends StringDataType {

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public SecurityContextDataType(String name) {
        super(name);
    }

    public Iterator<Map.Entry<String, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        if (node == null && cloud == null) return null; // we don't know..
        return new Iterator() {
            StringList list = node == null ? cloud.getPossibleContexts() : node.getPossibleContexts();
            StringIterator iterator = list.stringIterator();
            public boolean hasNext() {
                return iterator.hasNext();
            }
            public Map.Entry<String, String> next() {
                String val = iterator.nextString();
                return new Entry(val, val);
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    /*
    public LocalizedEntryListFactory getEnumerationFactory() {
        throw new UnsupportedOperationException();
    }
    */

}
