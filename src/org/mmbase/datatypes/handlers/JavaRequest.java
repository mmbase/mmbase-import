/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers;

import org.mmbase.bridge.*;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: JavaRequest.java,v 1.1 2008-07-28 16:47:31 michiel Exp $
 * @since MMBase-1.9.1
 */

public class JavaRequest extends  AbstractRequest {

    private Map<String, Object> values = new HashMap<String, Object>();


    public JavaRequest() {
    }

    public Object put(String f, Object v) {
        return values.put(f, v);
    }

    /**
     *
     */
    public String getName(Field field) {
        return field.getName();
    }

    /**
     * Gets the user specified value for a field
     */
    public Object getValue(Field field) {
        return values.get(field.getName());
    }

    public Object getValue(Field field, String part) {
        return values.get(field.getName() + "_" + part);
    }

    


}
