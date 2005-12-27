/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;

/**
 * Analogon of {@link java.util.Collections}. Methods speak for themselves.
 *
 * @TODO not all variants of bridgelist have their counterpart here yet (but the ones used as return
 * values in bridge implementations have..)
 *
 * @author  Michiel Meeuwissen
 * @version $Id: BridgeCollections.java,v 1.1 2005-12-27 21:48:28 michiel Exp $
 * @since   MMBase-1.8
 */

public abstract class BridgeCollections {
    /**
     * Makes a BridgeList unmodifiable.
     */
    public static final BridgeList unmodifiableBridgeList(BridgeList bridgeList) {
        return new UnmodifiableBridgeList(bridgeList);
    }
    /**
     * Makes a NodeList unmodifiable.
     */
    public static final NodeList unmodifiableNodeList(NodeList nodeList) {
        return new UnmodifiableNodeList(nodeList);
    }
    /**
     * Makes a RelationList unmodifiable.
     */
    public static final RelationList unmodifiableRelationList(RelationList relationList) {
        return new UnmodifiableRelationList(relationList);
    }
    /**
     * Makes a StringList unmodifiable.
     */
    public static final StringList unmodifiableStringList(StringList stringList) {
        return new UnmodifiableStringList(stringList);
    }

    public static final BridgeList   EMPTY_BRIDGELIST     = new EmptyBridgeList();
    public static final NodeList     EMPTY_NODELIST       = new EmptyNodeList();
    public static final RelationList EMPTY_RELATIONLIST   = new EmptyRelationList();
    public static final StringList   EMPTY_STRINGLIST     = new EmptyStringList();






    // IMPLEMENTATIONS follow below.

    /* --------------------------------------------------------------------------------
     * Unmodifiable iterators
     */
    static class UnmodifiableListIterator implements ListIterator {
        final protected ListIterator i;
        UnmodifiableListIterator(ListIterator i) {
            this.i = i;
        }
        public boolean hasNext() {return i.hasNext();}
        public boolean hasPrevious() {return i.hasPrevious();}
        public Object next() {return i.next();}
        public Object previous() {return i.previous();}
        public int nextIndex() {return i.nextIndex();}
        public int previousIndex() {return i.previousIndex();}
        public void remove() { throw new UnsupportedOperationException(); }
        public void add(Object o) { throw new UnsupportedOperationException(); }
        public void set(Object o) { throw new UnsupportedOperationException(); }
    }
    static class UnmodifiableNodeIterator extends UnmodifiableListIterator implements NodeIterator {
        UnmodifiableNodeIterator(NodeIterator i) {
            super(i);
        }
        public Node nextNode() {return ((NodeIterator) i).nextNode();}
        public Node previousNode() {return ((NodeIterator) i).previousNode();}
    }
    static class UnmodifiableRelationIterator extends UnmodifiableNodeIterator implements RelationIterator {
        UnmodifiableRelationIterator(RelationIterator i) {
            super(i);
        }
        public Relation nextRelation() {return ((RelationIterator) i).nextRelation();}
        public Relation previousRelation() {return ((RelationIterator) i).previousRelation();}
    }
    static class UnmodifiableStringIterator extends UnmodifiableListIterator implements StringIterator {
        UnmodifiableStringIterator(StringIterator i) {
            super(i);
        }
        public String nextString() {return ((StringIterator) i).nextString();}
        public String previousString() {return ((StringIterator) i).previousString();}

    }

    /* --------------------------------------------------------------------------------
     * Unmodifiable Lists.
     */
    static class UnmodifiableBridgeList implements BridgeList {
	final List c;
        final BridgeList parent ; // just to expose properties to sublists.

        UnmodifiableBridgeList() {
            c = Collections.EMPTY_LIST;
            parent = null;
        }
	UnmodifiableBridgeList(BridgeList c) {
            if (c == null) { throw new NullPointerException(); }
            this.c = c;
            this.parent = null;
        }
	UnmodifiableBridgeList(List c, BridgeList parent) {
            if (c == null) { throw new NullPointerException(); }
            this.c = c;
            this.parent = parent;
        }

	public int size() 		    {return c.size();}
	public boolean isEmpty() 	    {return c.isEmpty();}
	public boolean contains(Object o)   {return c.contains(o);}
	public Object[] toArray()           {return c.toArray();}
	public Object[] toArray(Object[] a) {return c.toArray(a);}
        public String toString()            {return c.toString();}
	public ListIterator listIterator(final int s) {return new UnmodifiableListIterator(c.listIterator(s)); }
        public ListIterator listIterator() { return listIterator(0);}
        public Iterator iterator() { return listIterator();}
	public boolean add(Object o){ throw new UnsupportedOperationException(); }
        public void add(int i, Object o) { throw new UnsupportedOperationException(); }
        public Object set(int i, Object o) { throw new UnsupportedOperationException(); }
	public boolean remove(Object o) {throw new UnsupportedOperationException(); }
	public Object remove(int i) {throw new UnsupportedOperationException(); }
	public boolean containsAll(Collection coll) { return c.containsAll(coll); }
	public boolean addAll(Collection coll) { throw new UnsupportedOperationException(); }
	public boolean addAll(int i, Collection coll) { throw new UnsupportedOperationException(); }
	public boolean removeAll(Collection coll) { throw new UnsupportedOperationException(); }
	public boolean retainAll(Collection coll) { throw new UnsupportedOperationException(); }
	public void clear() { throw new UnsupportedOperationException(); }
        public Object get(int i) { return c.get(i); }

        public Object getProperty(Object key) {
            if (parent != null) return parent.getProperty(key);
            return ((BridgeList) c).getProperty(key);
        }

        public void setProperty(Object key, Object value) { throw new UnsupportedOperationException(); }
        public void sort() { throw new UnsupportedOperationException(); }
        public void sort(Comparator comparator) { throw new UnsupportedOperationException(); }
        public List subList(int fromIndex, int toIndex) {
            return new UnmodifiableBridgeList(c.subList(fromIndex, toIndex), parent != null ? parent : (BridgeList) c);
        }
        public int lastIndexOf(Object o) { return c.lastIndexOf(o);}
        public int indexOf(Object o) { return c.indexOf(o);}
        public boolean equals(Object o) { return c.equals(o);}
        public int hashCode() { return c.hashCode();}
    }

    static class UnmodifiableNodeList extends UnmodifiableBridgeList implements NodeList {
        UnmodifiableNodeList(NodeList nodeList) {
            super(nodeList);
        }
        public Node getNode(int index) {
            return ((NodeList) c).getNode(index);
        }
        public NodeIterator nodeIterator() {
	    return new UnmodifiableNodeIterator(((NodeList)c).nodeIterator());
        }
        public NodeList subNodeList(int fromIndex, int toIndex) {
            return new UnmodifiableNodeList(((NodeList) c).subNodeList(fromIndex, toIndex));
        }
    }
    static class UnmodifiableRelationList extends UnmodifiableNodeList implements RelationList {
        UnmodifiableRelationList(RelationList relationList) {
            super(relationList);
        }
        public Relation getRelation(int index) {
            return ((RelationList) c).getRelation(index);
        }
        public RelationIterator relationIterator() {
	    return new UnmodifiableRelationIterator(((RelationList)c).relationIterator());
        }
        public RelationList subRelationList(int fromIndex, int toIndex) {
            return new UnmodifiableRelationList(((RelationList) c).subRelationList(fromIndex, toIndex));
        }
    }

    static class UnmodifiableStringList extends UnmodifiableBridgeList implements StringList {
        UnmodifiableStringList(StringList stringList) {
            super(stringList);
        }
        public String getString(int index) {
            return ((StringList) c).getString(index);
        }
        public StringIterator stringIterator() {
	    return new UnmodifiableStringIterator(((StringList)c).stringIterator());
        }
    }

    /* --------------------------------------------------------------------------------
     * Empty (and unmodifiable) Lists.
     */
    static class EmptyBridgeList extends UnmodifiableBridgeList {
        EmptyBridgeList() {
        }
        public int size() { return 0;}
        public boolean isEmpty() { return true;}
        public boolean contains(Object o) {return false;}
        public boolean containsAll(Collection col) {return col.isEmpty();}
        public Object[] toArray() { return new Object[] {};}
        public String toString() {return "[]";}
        public ListIterator listIterator(int c) { return Collections.EMPTY_LIST.listIterator(c); }
    }

    static class EmptyNodeList extends EmptyBridgeList implements NodeList {
        public Node getNode(int index) {
	    throw new IndexOutOfBoundsException("Index: "+index);
        }
        public NodeIterator nodeIterator() {
	    return new UnmodifiableNodeIterator(null) {
                    public boolean hasNext() {return false;}
                    public boolean hasPrevious() {return false;}
                    public Node nextNode() {throw new NoSuchElementException();}
                    public Node previousNode() {throw new NoSuchElementException();}
                    public Object next() {throw new NoSuchElementException();}
                    public Object previous() {throw new NoSuchElementException();}

                };
        }
        public NodeList subNodeList(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == 0) return this;
            throw new IndexOutOfBoundsException();
        }
    }
    static class EmptyRelationList extends EmptyNodeList implements RelationList {
        public Relation getRelation(int index) {
	    throw new IndexOutOfBoundsException("Index: "+index);
        }
        public RelationIterator relationIterator() {
	    return new UnmodifiableRelationIterator(null) {
                    public boolean hasNext() { return false;}
                    public boolean hasPrevious() { return false;}
                    public Relation nextRelation() {throw new NoSuchElementException();}
                    public Relation previousRelation() {throw new NoSuchElementException();}
                    public Node nextNode() {throw new NoSuchElementException();}
                    public Node previousNode() {throw new NoSuchElementException();}
                    public Object next() {throw new NoSuchElementException();}
                    public Object previous() {throw new NoSuchElementException();}
                };
        }
        public RelationList subRelationList(int fromIndex, int toIndex) {
            if (fromIndex == 0 && toIndex == 0) return this;
            throw new IndexOutOfBoundsException();
        }
    }
    static class EmptyStringList extends EmptyBridgeList implements StringList {
        public String getString(int index) {
	    throw new IndexOutOfBoundsException("Index: "+index);
        }
        public StringIterator stringIterator() {
	    return new UnmodifiableStringIterator(null) {
                    public boolean hasNext() {return false;}
                    public boolean hasPrevious() {return false;}
                    public String nextString() {throw new NoSuchElementException();}
                    public String previousString() {throw new NoSuchElementException();}
                };
        }
    }



}
