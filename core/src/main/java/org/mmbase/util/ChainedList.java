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
 * @since	MMBase-1.9
 * @version $Id$
 * @see ChainedIterator
 */
public class ChainedList<E> extends AbstractList<E> {

    private final List<List<? extends E>> lists = new ArrayList<List<? extends E>>();
    
    public ChainedList() {
        // nothing to do yet
    }
    
    public ChainedList(List<? extends E>... ls) {
        for (List<? extends E> l : ls) {
            addList(l);
        }
    }

    public ChainedList<E> addList(List<? extends E> l) {
        lists.add(l);
        return this;
    }
    public int size() {
        int size = 0;
        for (List<? extends E> l : lists) {
            size += l.size();
        }
        return size;
    }
    public E get(int i) {
        for (List<? extends E> l : lists) {
            if (l.size() > i) {
                return l.get(i);
            }
            i -= l.size();
        }
        throw new IndexOutOfBoundsException();
    }


}
