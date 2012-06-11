/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * The comparator which sorts Comparable on the inverse natural order.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.5
 */
public class ReverseComparator implements Comparator<Comparable> {


    public int compare(Comparable o1, Comparable o2) {
        return o1 != null ? -1 * o1.compareTo(o2) : (o2 == null ? 0 : 1);
    }

}
