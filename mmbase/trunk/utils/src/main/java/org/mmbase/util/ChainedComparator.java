/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Straight forward utility to chain several comparators into a new one. This 'chained comparator falls back to the next comparator if two object compare equally large ({@link Comparator#compare} returns <code>0</code>).
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.9.2
 * @version $Id$
 */
public class ChainedComparator<E> implements Comparator<E> {

    private final List<Comparator<E>> comparators = new ArrayList<Comparator<E>>();


    public ChainedComparator(Comparator<E>... cmps) {
        for (Comparator<E> c : cmps) {
            addComparator(c);
        }
    }

    public final ChainedComparator<E> addComparator(Comparator<E> c) {
        comparators.add(c);
        return this;
    }
    public int compare(E e1, E e2) {
        int result = 0;
        for (Comparator<E> c : comparators) {
            result = c.compare(e1, e2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof ChainedComparator) {
            ChainedComparator c = (ChainedComparator) o;
            return comparators.equals(c.comparators);
        } else {
            return false;
        }
    }


}
