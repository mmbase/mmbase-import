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
 * @version $Id: SecurityContextDataType.java,v 1.1 2005-09-12 17:28:43 michiel Exp $
 * @since MMBase-1.8
 */
public class SecurityContextDataType extends StringDataType {

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public SecurityContextDataType(String name) {
        super(name);
    }

    public Collection getEnumerationValues(Locale locale, Cloud cloud, Node node, Field field) {
        if (node == null && cloud == null) return null; // we don't know..
        if (cloud == null) cloud = node.getCloud();

        if (node == null) {
            Collection col = new ArrayList();
            String defaultContext = cloud.getUser().getOwnerField();
            col.add(new Entry(defaultContext, defaultContext));
            return col;
        } else {
            String value = field != null ? node.getStringValue(field.getName()) : null;

            // bit silly that  a new list..
            Collection col = new ArrayList();
            StringList possibleContexts = node.getPossibleContexts();
            if (value != null && ! possibleContexts.contains(value)) col.add(new Entry(value, value));
            StringIterator i = possibleContexts.stringIterator();
            while (i.hasNext()) {
                String val = i.nextString();
                col.add(new Entry(val, val));
            }
            return col;
        }                
    }

    public LocalizedEntryListFactory getEnumerationFactory() {
        throw new UnsupportedOperationException();
    }


}
