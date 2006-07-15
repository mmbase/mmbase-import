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
import org.mmbase.util.logging.*;

/**
 * A StringDataType with all security contexts strings as possible value.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SecurityContextDataType.java,v 1.4 2006-01-06 17:19:21 michiel Exp $
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

    public Iterator getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        if (node == null && cloud == null) return null; // we don't know..

        if (node == null) {
            return new Iterator() {
                    private boolean next = true;
                    public boolean hasNext() {
                        return next;
                    }
                    public Object next() {
                        String defaultContext = cloud.getUser().getOwnerField();
                        next = false;
                        return new Entry(defaultContext, defaultContext);
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
        } else {
            String value = field != null ? node.getStringValue(field.getName()) : null;
            return new Iterator() {
                    StringIterator i = node.getPossibleContexts().stringIterator();
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    public Object next() {
                        String val = i.nextString();
                        return new Entry(val, val);
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
        }
    }
    /*
    public LocalizedEntryListFactory getEnumerationFactory() {
        throw new UnsupportedOperationException();
    }
    */

}
