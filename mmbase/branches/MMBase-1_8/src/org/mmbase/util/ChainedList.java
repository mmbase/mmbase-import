/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Simple utility to chain several lists into a new one.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.8.5
 * @version $Id$
 * @see ChainedIterator
 */
public class ChainedList extends AbstractList {

    private final List lists = new ArrayList();
    public ChainedList() {
    }

    public ChainedList addList(List l) {
        lists.add(l);
        return this;
    }
    public int size() {
        int size = 0;
        Iterator i =  lists.iterator();
        while (i.hasNext()) {
            List l = (List) i.next();
            size += l.size();
        }
        return size;
    }
    public Object get(int j) {
        Iterator i =  lists.iterator();
        while (i.hasNext()) {
            List l = (List) i.next();
            if (l.size() > j) {
                return l.get(j);
            }
            j -= l.size();
        }
        throw new IndexOutOfBoundsException();
    }


}
