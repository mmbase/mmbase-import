/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Simple utility to chained several lists into a new one.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.8
 * @version $Id: ChainedList.java,v 1.1 2006-09-27 20:40:19 michiel Exp $
 * @see ChainedIterator
 */
public class ChainedList<E> extends AbstractList<E> {

    private List<List<? extends E>> lists = new ArrayList();
    private int size = 0;
    public ChainedList() {

    }

    public void addList(List<? extends E> l) {
        size += l.size();
        lists.add(l);
    }
    public int size() {
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
