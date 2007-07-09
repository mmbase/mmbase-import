/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Like org.apache.commons.collections.iterators.IteratorChain, to avoid the dependency....
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.8
 * @version $Id: ChainedIterator.java,v 1.3 2006-09-08 12:12:40 michiel Exp $
 */
public class ChainedIterator<E> implements Iterator<E> {

    List<Iterator<E>> iterators = new ArrayList<Iterator<E>>();
    Iterator<Iterator<E>> iteratorIterator = null;
    Iterator<E> iterator = null;
    public ChainedIterator() {
    }

    public void addIterator(Iterator<E> i) {
        if (iteratorIterator != null) throw new IllegalStateException();
        iterators.add(i);
    }


    private void setIterator() {
       while(iteratorIterator.hasNext() && iterator == null) {
           iterator = iteratorIterator.next();
           if (! iterator.hasNext()) iterator = null;
       }
    }
    private void start() {
        if (iteratorIterator == null) {
            iteratorIterator = iterators.iterator();
            setIterator();
        }
    }

    public boolean hasNext() {
        start();
        return  (iterator != null && iterator.hasNext());

    }

    public E next() {
        start();
        if (iterator == null) throw new NoSuchElementException();
        E res = iterator.next();
        if (! iterator.hasNext()) {
            iterator = null;
            setIterator();
        }
        return res;

    }
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Just testing
     */
    public static void main(String argv[]) {
        ChainedIterator it = new ChainedIterator();
        List o = new ArrayList();
        List a = new ArrayList();
        a.add("a");
        a.add("b");
        List b = new ArrayList();
        List c = new ArrayList();
        c.add("c");
        c.add("d");
        List d = new ArrayList();
        it.addIterator(o.iterator());
        it.addIterator(a.iterator());
        it.addIterator(b.iterator());
        it.addIterator(c.iterator());
        it.addIterator(d.iterator());
        while (it.hasNext()) {
            System.out.println("" + it.next());
        }
    }


}
