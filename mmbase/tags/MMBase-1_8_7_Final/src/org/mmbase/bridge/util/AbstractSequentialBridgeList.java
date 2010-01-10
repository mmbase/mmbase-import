/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import java.util.*;

/**
 * As AbstractSequentialList, but implements some extra methods required by BridgeList
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.7
 */

abstract public  class AbstractSequentialBridgeList extends AbstractSequentialList implements BridgeList {

    private Map properties = new HashMap();

    // javadoc inherited
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    // javadoc inherited
    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }

    // javadoc inherited
    public void sort() {
        Collections.sort(this);
    }

    // javadoc inherited
    public void sort(Comparator comparator) {
        Collections.sort(this, comparator);
    }


}
