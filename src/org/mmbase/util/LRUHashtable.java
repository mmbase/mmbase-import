/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.lang.*;

/**
 * A hashtable which has a maximum of entries.
 * Old entries are removed when the maximum is reached.
 * This table is used mostly to implement a simple caching system.
 *
 * @author Rico Jansen
 * @version 24 Nov 1997
 */
public class LRUHashtable extends Hashtable implements Cloneable {

    /**
     * First (virtual) element of the table.
     * The element that follows root is the oldest element in the table
     * (and thus first to be removed if size maxes out).
     */
    private LRUentry root=new LRUentry("root","root");
    /**
     * Last (virtual) element of the table.
     * The element that precedes dangling is the latest element in the table
     */
    private LRUentry dangling=new LRUentry("dangling","dangling");
    /**
     * Holds working element. Possibly global for speed reasons.
     */
    private LRUentry work;

    /**
     * Maximum size (capacity) of the table
     */
    private int size=0;
    /**
     * Current size of the table.
     */
    private int currentsize=0;
    /**
     * Stores a return value. Possibly global for speed reasons.
     */
    private Object rtn;
    /**
     * Holds the entries so it's easier to find them.
     * Can't use our own superclass as storage because of
     * the recursion in there.
     */
    private Hashtable entries;
    /**
     * The number of times an element was succesfully retrieved from the table.
     */
    private int hit;
    /**
     * The number of times an element cpould not be retrieved from the table.
     */
    private int miss;
    /**
     * The number of times an element was committed to the table.
     */
    private int puts;

    /**
     * Creates the URL Hashtable.
     * @param size the maximum capacity
     * @param cap the starting capacity (used to improve performance)
     * @param lf the amount with which current capacity frows
     */
    public LRUHashtable(int size,int cap,float lf) {
        super(1);
        root.next=dangling;
        dangling.prev=root;
        this.size=size;
        entries=new Hashtable(cap,lf);
        hit=miss=puts=0;
    }

    /**
     * Creates the URL Hashtable with growing capacity 0.75.
     * @param size the maximum capacity
     * @param cap the starting capacity (used to improve performance)
     */
    public LRUHashtable(int size,int cap) {
        this(size,cap,0.75f);
    }

    /**
     * Creates the URL Hashtable with starting capacity 101 and
     * growing capacity 0.75.
     * @param size the maximum capacity
     */
    public LRUHashtable(int size) {
        this(size,101,0.75f);
    }

    /**
     * Creates the URL Hashtable with maximum capacity 100,
     * starting capacity 101, and growing capacity 0.75.
     */
    public LRUHashtable() {
        this(100,101,0.75f);
    }

    /**
     * Store an element in the table.
     * @key the key of the element
     * @value the value of the element
     * @return the original value of the element if it existed, <code>null</code> if it could not be found
     */
    public synchronized Object put(Object key,Object value) {
        work=(LRUentry)entries.get(key);
        if (work!=null) {
            rtn=work.value;
            work.value=value;
            removeEntry(work);
            appendEntry(work);
        } else {
            rtn=null;
            work=new LRUentry(key,value);
            entries.put(key,work);
            appendEntry(work);
            currentsize++;
            if (currentsize>size) {
                remove(root.next.key);
            }
        }
        puts++;
        return rtn;
    }

    /**
     * Retrieve an element from the table.
     * @key the key of the element
     * @return the value of the element, or <code>null</code> if it could not be found
     */
    public synchronized Object get(Object key) {
        work=(LRUentry)entries.get(key);
        if (work!=null) {
            hit++;
            rtn=work.value;
            removeEntry(work);
            appendEntry(work);
        } else {
            miss++;
            rtn=null;
        }
        return rtn;
    }

    /**
     * Remove an element from the table.
     * @key the key of the element
     * @return the original value of the element if it existed, <code>null</code> if it could not be found
     */
    public synchronized Object remove(Object key) {
        work=(LRUentry)entries.remove(key);
        if (work!=null) {
            rtn=work.value;
            removeEntry(work);
            currentsize--;
        } else {
            rtn=null;
        }
        return rtn;
    }

    /**
     * Return the current size of the table
     */
    public int size() {
        return currentsize;
    }

    /**
     * Change the maximum size of the table.
     * This may result in removal of entries in the table.
     * @value size the new desired size (note that specifying a negative size results in a neverending loop)
     */
    public void setSize(int size) {
        if (size<this.size) {
            while(currentsize>size) {
                remove(root.next.key);
            }
        }
        this.size=size;
    }

    /**
     * Return the maximum size of the table
     */
    public int getSize() {
        return size;
    }

    /**
     * Append an entry to the end of the list.
     */
    private void appendEntry(LRUentry wrk) {
        dangling.prev.next=wrk;
        wrk.prev=dangling.prev;
        wrk.next=dangling;
        dangling.prev=wrk;
    }

    /**
     * remove an entry from the list.
     */
    private void removeEntry(LRUentry wrk) {
        wrk.next.prev=wrk.prev;
        wrk.prev.next=wrk.next;
        wrk.next=null;
        wrk.prev=null;
    }

    /**
     * Returns a description of the table.
     * The information shown includes current size, maximum size, ratio of misses and hits,
     * and a description of the underlying hashtable
     */
    public String toString() {
        return "Size="+currentsize+", Max="+size+", Ratio="+getRatio()+" : "+entries.toString();
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
            StringBuffer b=new StringBuffer();
            b.append("Size "+currentsize+", Max "+size+" : {");
            LRUentry walk=root.next;
            while (walk!=dangling) {
                if (which) {
                    b.append(""+walk.key+"="+walk.value);
                    which=false;
                } else {
                    b.append(","+walk.key+"="+walk.value);
                }
                walk=walk.next;
            }
            b.append("}");
            return b.toString();
        } else {
            return toString();
        }
    }

    /**
     * Clears the table.
     */
    public synchronized void clear() {
        while (root.next!=dangling) removeEntry(root.next);
        entries.clear();
        currentsize=0;
    }

    /**
     * Checks if an element exists in the table, searching by key.
     * @key the key of the element
     */
    public boolean containsKey(Object key) {
        return entries.containsKey(key);
    }

    /**
     * Checks if an element exists in the table, searching by value.
     * @value the value of the element
     */
    public boolean contains(Object value) {
        return entries.contains(value);
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
        return new LRUHashtableEnumerator(entries);
    }

    /**
     * Returns <code>true</code> if no elements are in the table.
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Returns an <code>Enumeration</code> on the table's element keys.
     */
    public synchronized Enumeration keys() {
        return entries.keys();
    }

    /**
     * Returns the ratio of hits and misses.
     * The higher the ratio, the more succesfull the table retrieval is.
     * A value of 1 means every attempt to retrieve data is succesfull,
     * while a value nearing 0 means most times the object requested it is
     * not available.
     * Generally a high ratio means the table can be shrunk, while a low ratio
     * means its size needs to be increased.
     */
    public double getRatio() {
        return (1.0*hit)/(hit+miss+0.0000000001);
    }

    /**
     * Returns the number of times an element was succesfully retrieved
     * from the table.
     */
    public int getHits() {
        return hit;
    }

    /**
     * Returns the number of times an element cpould not be retrieved
     * from the table.
     */
    public int getMisses() {
        return miss;
    }

    /**
     * Returns the number of times an element was committed to the table.
     */
    public int getPuts() {
        return puts;
    }

    /**
     * Returns statistics on this table.
     * The information shown includes number of accesses, ratio of misses and hits,
     * current size, and number of puts.
     */
    public String getStats() {
        return "Access "+(hit+miss)+ " Ratio "+getRatio()+" Size "+size()+" Puts "+puts;
    }

    public Enumeration getOrderedElements() {
	return(getOrderedElements(-1));
    }

    public Enumeration getOrderedElements(int maxnumber) {
	Vector results=new Vector();
	LRUentry current=root.next;
	if (maxnumber!=-1) {
		int i=0;
        	while (current!=null && current!=dangling && i<maxnumber) {
			results.addElement(current.value);	
			current=current.next;
			i+=1;
		}
	} else {
        	while (current!=null && current!=dangling) {
			results.addElement(current.value);	
			current=current.next;
		}
	}
	return(results.elements());
    }

}

/**
 * Enumerator for the LRUHashtable.
 */
class LRUHashtableEnumerator implements Enumeration {
    private Enumeration superior;

    LRUHashtableEnumerator(Hashtable entries) {
        superior=entries.elements();
    }

    public boolean hasMoreElements() {
        return superior.hasMoreElements();
    }

    public Object nextElement() {
        LRUentry entry;

        entry=(LRUentry)superior.nextElement();
        return entry.value;
    }
}

/**
 * Element used to store information fro the LRUHashtable.
 */
class LRUentry {
    /**
     * The element value
     */
    protected Object value;
    /**
     * The next, newer, element
     */
    protected LRUentry next;
    /**
     * The previous, older, element
     */
    protected LRUentry prev;
    /**
     * The element key
     */
    protected Object key;

    LRUentry(Object key,Object val) {
        this(key,val,null,null);
    }

    LRUentry(Object key,Object value,LRUentry prev,LRUentry next) {
        this.value=value;
        this.next=next;
        this.prev=prev;
        this.key=key;
    }

    public String toString() {
        return (value!=null) ? value.toString() : null;
    }

}


