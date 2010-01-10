/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.mmbase.cache.CacheImplementationInterface;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * A hashtable which has a maximum of entries.  Old entries are
 * removed when the maximum is reached.  This table is used mostly to
 * implement a simple caching system.
 *
 * @move consider moving to org.mmbase.cache
 * @author  Rico Jansen
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @see    org.mmbase.cache.Cache
 */
public class LRUHashtable extends Hashtable implements Cloneable, CacheImplementationInterface, SizeMeasurable {

    private static final Logger log = Logging.getLoggerInstance(LRUHashtable.class);

    private static final String ROOT     = "root";
    private static final String DANGLING = "dangling";
    /**
     * First (virtual) element of the table.
     * The element that follows root is the oldest element in the table
     * (and thus first to be removed if size maxes out).
     */
    private final LRUEntry root     = new LRUEntry(ROOT, ROOT);
    /**
     * Last (virtual) element of the table.
     * The element that precedes dangling is the latest element in the table
     */
    private final LRUEntry dangling = new LRUEntry(DANGLING, DANGLING);

    /**
     * Maximum size (capacity) of the table
     */
    private int size = 0;
    /**
     * Current size of the table.
     */
    private int currentSize = 0;

    /**
     * Creates the URL Hashtable.
     * @param size the maximum capacity
     * @param cap the starting capacity (used to improve performance)
     * @param lf the amount with which current capacity frows
     */
    public LRUHashtable(int size, int cap, float lf) {
        super(cap, lf);
        root.next = dangling;
        dangling.prev = root;
        this.size = size;
    }

    /**
     * Creates the URL Hashtable with growing capacity 0.75.
     * @param size the maximum capacity
     * @param cap the starting capacity (used to improve performance)
     */
    public LRUHashtable(int size, int cap) {
        this(size, cap, 0.75f);
    }

    /**
     * Creates the URL Hashtable with starting capacity 101 and
     * growing capacity 0.75.
     * @param size the maximum capacity
     */
    public LRUHashtable(int size) {
        this(size, 101, 0.75f);
    }

    /**
     * Creates the URL Hashtable with maximum capacity 100,
     * starting capacity 101, and growing capacity 0.75.
     */
    public LRUHashtable() {
        this(100, 101, 0.75f);
    }

    public Object getLock() {
        return this;
    }

    /**
     * Store an element in the table.
     * @param key the key of the element
     * @param value the value of the element
     * @return the original value of the element if it existed, <code>null</code> if it could not be found
     */
    public synchronized Object put(Object key, Object value) {
        LRUEntry work = (LRUEntry) super.get(key);
        Object rtn;
        if (work != null) {
            rtn = work.value;
            work.value = value;
            removeEntry(work);
            appendEntry(work);
        } else {
            rtn = null;
            work = new LRUEntry(key, value);
            super.put(key, work);
            appendEntry(work);
            currentSize++;
            if (currentSize > size) {
                Object remove = root.next.key;
                Object was =  remove(remove);
                if (was == null) {
                    log.warn("Nothing was removed, while that was expected " + remove + " should have been removed");
                }
            }
        }
        return rtn;
    }

    /**
     * Retrieves the count of the object with a certain key.
     * @param key the key of the element
     * @return the times the key has been requested
     */
    public int getCount(Object key) {
        LRUEntry work = (LRUEntry) super.get(key);
        if (work != null) {
            return work.requestCount;
        } else {
            return -1;
        }
    }

    /**
     * Retrieves an element from the table.
     * @param key the key of the element
     * @return the value of the element, or <code>null</code> if it could not be found
     */
    public synchronized Object get(Object key) {
        LRUEntry work = (LRUEntry) super.get(key);
        if (work != null) {
            work.requestCount++;
            Object rtn = work.value;
            removeEntry(work);
            appendEntry(work);
            return rtn;
        } else {
            return null;
        }
    }

    /**
     * Remove an element from the table.
     * @param key the key of the element
     * @return the original value of the element if it existed, <code>null</code> if it could not be found
     */
    public synchronized Object remove(Object key) {
        LRUEntry work = (LRUEntry) super.remove(key);
        if (work != null) {
            Object rtn = work.value;
            removeEntry(work);
            currentSize--;
            return rtn;
        } else {
            return null;
        }
    }


    /**
     * You should only remove entries from LRUHashtable using the 'remove' function, or using the
     * iterator of entrySet() otherwise the linked list gets messed up.  The keySet of LRUHashtable
     * therefore returns an unmodifiable set.
     * @since MMBase-1.6.3
     */
    public Set keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    /**
     * Returns the entries of this Map. Modification are reflected.
     *
     * @since MMBase-1.6.3
     */
    public Set entrySet() {
        return new LRUEntrySet();
    }

    /**
     * @see   #keySet
     * @since MMBase-1.6.3
     */
    public Collection values() {
        return Collections.unmodifiableCollection(super.values());
    }

    /**
     * Return the current size of the table
     */
    public int size() {
        return currentSize;
    }

    /**
     * Change the maximum size of the table.
     * This may result in removal of entries in the table.
     * @param size the new desired size
     */
    public void setMaxSize(int size) {
        if (size < 0 ) throw new IllegalArgumentException("Cannot set size of LRUHashtable to negative value");
        if (size < this.size) {
            while(currentSize > size) {
                remove(root.next.key);
            }
        }
        this.size = size;
    }

    /**
     * Return the maximum size of the table
     */
    public int maxSize() {
        return size;
    }

    /**
     * Append an entry to the end of the list.
     */
    private void appendEntry(LRUEntry wrk) {
        dangling.prev.next = wrk;
        wrk.prev = dangling.prev;
        wrk.next = dangling;
        dangling.prev = wrk;
    }

    /**
     * remove an entry from the list.
     */
    private void removeEntry(LRUEntry wrk) {
        wrk.next.prev = wrk.prev;
        wrk.prev.next = wrk.next;
        wrk.next = null;
        wrk.prev = null;
    }

    /**
     * Returns a description of the table.
     * The information shown includes current size, maximum size, ratio of misses and hits,
     * and a description of the underlying hashtable
     */
    public String toString() {
        return "Size=" + currentSize + ", Max=" + size;
    }

    /**
     * Returns a description of the table.
     * The information shown includes current size, maximum size, ratio of misses and hits,
     * and either a description of the underlying hashtable, or a list of all stored values.
     * @param which if <code>true</code>, the stored values are described.
     * @return a description of the table.
     */
    public String toString(boolean which) {
        if (which) {
            StringBuffer b = new StringBuffer();
            b.append("Size " + currentSize + ", Max " + size + " : ");
            b.append(super.toString());
            return b.toString();
        } else {
            return toString();
        }
    }

    /**
     * Clears the table.
     */
    public synchronized void clear() {
        while (root.next != dangling) removeEntry(root.next);
        super.clear();
        currentSize = 0;
    }

    /**
     * NOT IMPLEMENTED
     */
    public synchronized Object clone() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an <code>Enumeration</code> on the table's element values.
     */
    public synchronized Enumeration elements() {
        return new LRUHashtableEnumeration();
    }



    /**
     * @deprecated use getOrderedEntries
     */
    public Enumeration getOrderedElements() {
        return getOrderedElements(-1);
    }

    /**
     * @deprecated use getOrderedEntries
     */
    public Enumeration getOrderedElements(int maxnumber) {
        List results = new ArrayList();
        LRUEntry current = root.next;
        if (maxnumber != -1) {
            int i = 0;
            while (current!=null && current!=dangling && i<maxnumber) {
                results.add(0, current.value);
                current = current.next;
                i++;
            }
        } else {
            while (current!=null && current!=dangling) {
                results.add(0, current.value);
                current = current.next;
            }
        }
        return Collections.enumeration(results);
    }

    /**
     * Returns an ordered list of Map.Entry's.
     *
     * @since MMBase-1.6
     */

    public List getOrderedEntries() {
        return getOrderedEntries(-1);
    }

    /**
     * Returns an ordered list of Map.Entry's. This can be used to
     * present the contents of the LRU Map.
     *
     * @since MMBase-1.6
     */

    public List getOrderedEntries(int maxNumber) {
        List results = new ArrayList();
        LRUEntry current = root.next;
        int i = 0;
        while (current != null && current != dangling && (maxNumber < 0 || i < maxNumber)) {
            results.add(0, current);
            current = current.next;
            i++;
        }
        return Collections.unmodifiableList(results);
    }


    public void config(Map map) {
        // lru needs no configuration.
    }

    public int getByteSize() {
        return getByteSize(new SizeOf());
    }
    public int getByteSize(SizeOf sizeof) {
        int len = 4 * SizeOf.SZ_REF + (30 + 5 * SizeOf.SZ_REF) * currentSize;  // 30:overhead of Hashtable, 5*SZ_REF: overhead of LRUEntry
        LRUEntry current = root.next;
        while (current != null && current != dangling) {
            current = current.next;
            len += sizeof.sizeof(current.key);
            len += sizeof.sizeof(current.value);
        }
        return len;
    }

    /**
     * Enumerator for the LRUHashtable.
     */
    private class LRUHashtableEnumeration implements Enumeration {
        private Enumeration superior;

        LRUHashtableEnumeration() {
            superior = LRUHashtable.this.elements();
        }

        public boolean hasMoreElements() {
            return superior.hasMoreElements();
        }

        public Object nextElement() {
            LRUEntry entry = (LRUEntry) superior.nextElement();
            return entry.value;
        }
    }


    /**
     * Element used to store information from the LRUHashtable.
     */
    public class LRUEntry implements Map.Entry, SizeMeasurable {
        /**
         * The element value
         */
        protected Object value;
        /**
         * The next, newer, element
         */
        protected LRUEntry next;
        /**
         * The previous, older, element
         */
        protected LRUEntry prev;
        /**
         * The element key
         */
        protected Object key;
        /**
         * the number of times this
         * entry has been requested
         */
        protected int requestCount = 0;

        LRUEntry(Object key, Object val) {
            this(key, val, null, null);
        }

        LRUEntry(Object key, Object value, LRUEntry prev, LRUEntry next) {
            this.value = value;
            this.next  = next;
            this.prev  = prev;
            this.key   = key;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object o) {
            throw new UnsupportedOperationException("Cannot change values in LRU Hashtable");
        }

        public int getByteSize() {
            return new SizeOf().sizeof(value);
        }
        public int getByteSize(SizeOf sizeof) {
            return 20 + // 5 references
                sizeof.sizeof(value);
        }
        public String toString() {
            return  value == LRUHashtable.this ? "[this lru]" : String.valueOf(value);
        }

    }
    /**
     * Used by 'entrySet' implementation, to make the Map modifiable.
     * @since MMBase-1.7.2
     */
    protected class LRUEntrySet extends AbstractSet {
        Set set;
        LRUEntrySet() {
            set = LRUHashtable.super.entrySet();
        }
        public int size() {
            return set.size();
        }
        public Iterator iterator() {
            return new LRUEntrySetIterator(set.iterator());
        }
    }

    /**
     * Used by 'entrySet' implementation, to make the Map modifiable.
     * @since MMBase-1.7.2
     */
    protected class LRUEntrySetIterator implements Iterator {
        Iterator it;
        LRUEntry work;
        LRUEntrySetIterator(Iterator i ) {
            it = i;
        }
        public boolean 	hasNext() {
            return it.hasNext();
        }
        public Object next() {
            Map.Entry entry = (Map.Entry) it.next();
            work = (LRUEntry) entry.getValue();
            return work;
        }
        public void remove() {
            it.remove();
            if (work != null) {
                LRUHashtable.this.removeEntry(work);
                LRUHashtable.this.currentSize--;
            }
        }
    }

    public static void main(String argv[]) {
        int treesiz = 1024;
        int opers = 1000000;
        int thrds  = 1;

        try {
            if (argv.length > 0) {
                treesiz = Integer.parseInt(argv[0]);
            }
            if (argv.length > 1) {
                opers = Integer.parseInt(argv[1]);
            }
            if (argv.length > 2) {
                thrds = Integer.parseInt(argv[2]);
            }
        } catch (Exception e) {
            System.out.println("Usage: java org.mmbase.util.LRUHashtable <size of table> <number of operation to do> <threads>");
            return;
        }

        final int TREESIZ = treesiz;
        final int OPERS = opers;
        final int THREADS = thrds;

        final LRUHashtable treap = new LRUHashtable(TREESIZ);
        long ll1 = System.currentTimeMillis();

        // fill the map
        for (int i = 0; i < TREESIZ; i++) {
            treap.put(""+i,""+i);
        }
        long ll2=System.currentTimeMillis();
        System.out.println("Size "+treap.size());

        if (TREESIZ <= 1024) {
            System.out.println("LRUHashtable initaly " + treap.toString(true));
        }

        final  int score[][] = new int[TREESIZ][THREADS];
        long ll3 = System.currentTimeMillis();

        Thread[] threads = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            final int  threadnr = t;
            Runnable runnable = new Runnable() {
                    public void run() {
                        if (THREADS > 1) {
                            System.out.println("Thread " + threadnr + " started");
                        }
                        Random rnd = new Random();
                        for (int i = 0;i<OPERS;i++) {
                            // Put and get mixed
                            int j = Math.abs(rnd.nextInt())%(TREESIZ/2)+(TREESIZ/4);
                            int k = Math.abs(rnd.nextInt())%2;
                            Object rtn;
                            switch (k) {
                            case 0:
                                rtn=treap.put(""+j,""+j);
                                score[j][threadnr]++;
                                break;
                            case 1:
                                rtn=treap.get(""+j);
                                score[j][threadnr]++;
                                break;
                            }
                            // Only a get
                            j = Math.abs(rnd.nextInt())%(TREESIZ);
                            rtn = treap.get(""+j);
                            score[j][threadnr]++;
                        }
                        if (THREADS > 1) {
                            System.out.println("Thread " + threadnr + " ready");
                        }
                    }
            };
            threads[t] = new Thread(runnable);
            threads[t].start();
        }
        long ll4 = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ie) {
                System.err.println("Interrupted");
            }
        }

        long ll5 = System.currentTimeMillis();
        if (TREESIZ <= 1024) {
            System.out.println("LRUHashtable afterwards " + treap.toString(true));

            for (int i = 0; i <TREESIZ; i++) {
                int totscore = 0;
                for (int j = 0; j < THREADS; j++) {
                    totscore += score[i][j];
                }
                System.out.println("" + i + " score " + totscore);
            }
        }
        System.out.println("Creation " + (ll2-ll1) + " ms");
        System.out.println("Thread starting " + (ll4-ll3) + " ms");
        if (TREESIZ <= 1024) {
            System.out.println("Print    " + (ll3-ll2) + " ms");
        } else {
            System.out.println("Not printed (too huge)");
        }
        long timePerKop = (ll5 - ll3) * 1000 / (OPERS);
        System.out.println("Run      " + (ll5-ll3) + " ms (" + timePerKop + " ms/koperation,  " + (timePerKop / THREADS) + " ms/koperation total from " + THREADS + " threads)");
    }
}



