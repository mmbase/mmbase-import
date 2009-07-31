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
 * @version $Id$
 */
public class ChainedIterator<E> implements Iterator<E> {

    private final List<Iterator<E>> iterators = new ArrayList<Iterator<E>>();
    private Iterator<Iterator<E>> iteratorIterator = null;
    private Iterator<E> iterator = null;
    public ChainedIterator() {
    }
    public ChainedIterator(Iterator<E>... is) {
        for (Iterator<E> i : is) {
            iterators.add(i);
        }
    }

    public ChainedIterator<E>  addIterator(Iterator<E> i) {
        if (iteratorIterator != null) throw new IllegalStateException();
        iterators.add(i);
        return this;
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
        ChainedIterator<String> it = new ChainedIterator<String>();
        List<String> o = new ArrayList<String>();
        List<String> a = new ArrayList<String>();
        a.add("a");
        a.add("b");
        List<String> b = new ArrayList<String>();
        List<String> c = new ArrayList<String>();
        c.add("c");
        c.add("d");
        List<String> d = new ArrayList<String>();
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
