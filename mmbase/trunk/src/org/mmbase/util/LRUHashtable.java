package org.mmbase.util;

import java.util.*;
import java.lang.*;

class LRUentry {
	protected Object value;
	protected LRUentry next;
	protected LRUentry prev;
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
		if (value!=null) return(value.toString()); else return(null);
	}

}


/**
 * A hashtable which has a maximum of entries,
 * old entries are removed when the maximum is reached
 *
 * @author Rico Jansen
 * @version 24 Nov 1997
 */
public class LRUHashtable extends Hashtable implements Cloneable {
private LRUentry root=new LRUentry("root","root");
private LRUentry dangling=new LRUentry("dangling","dangling");
private LRUentry work;
private int size,currentsize=0;
private Object rtn;
// Can't use the superclass as storage because of the recursion in there;
private Hashtable entries;
private int hit,miss,puts;

	public LRUHashtable(int size,int cap,float lf) {
		super(1);
		root.next=dangling;
		dangling.prev=root;
		this.size=size;
		entries=new Hashtable(cap,lf);
		hit=miss=puts=0;
	}

	public LRUHashtable(int size,int cap) {
		this(size,cap,0.75f);
	}

	public LRUHashtable(int size) {
		this(size,101,0.75f);
	}

	public LRUHashtable() {
		this(100,101,0.75f);
	}

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
		return(rtn);
	}

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
		return(rtn);
	}

	public synchronized Object remove(Object key) {
		work=(LRUentry)entries.remove(key);
		if (work!=null) {
			rtn=work.value;
			removeEntry(work);
			currentsize--;
		} else {
			rtn=null;
		}
		return(rtn);
	}

	public int size() {
		return(currentsize);
	}

	public void setSize(int size) {
		if (size<this.size) {
			while(currentsize>size) {
				remove(root.next.key);
			}
		}
		this.size=size;
	}

	public int getSize() {
		return(size);
	}

	private void appendEntry(LRUentry wrk) {
		dangling.prev.next=wrk;
		wrk.prev=dangling.prev;
		wrk.next=dangling;
		dangling.prev=wrk;
	}

	private void removeEntry(LRUentry wrk) {
		wrk.next.prev=wrk.prev;
		wrk.prev.next=wrk.next;
		wrk.next=null;
		wrk.prev=null;
	}

	public String toString() {
		return("Size="+currentsize+", Max="+size+", Ratio="+getRatio()+" : "+entries.toString());
	}

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
			return(b.toString());
		} else {
			return(toString());
		}
	}

	public synchronized void clear() {
		while (root.next!=dangling) removeEntry(root.next);
		entries.clear();
		currentsize=0;
	}

	public boolean containsKey(Object key) {
		return(entries.containsKey(key));
	}
	
	public boolean contains(Object value) {
		return(entries.contains(value));
	}

	/**
	 * NOT IMPLEMENTED
	 */
	public synchronized Object clone() {
		throw new InternalError();
	}

	public synchronized Enumeration elements() {
		return(new LRUHashtableEnumerator(entries));
	}

	public boolean isEmpty() {
		return(entries.isEmpty());
	}

	public synchronized Enumeration keys() {
		return(entries.keys());
	}

	public double getRatio() {
		return((1.0*hit)/(hit+miss+0.0000000001));
	}

	public int getHits() {
		return(hit);
	}

	public int getMisses() {
		return(miss);
	}

	public int getPuts() {
		return(puts);
	}

	public String getStats() {
		return("Access "+(hit+miss)+ " Ratio "+getRatio()+" Size "+size()+" Puts "+puts);
	}

}

class LRUHashtableEnumerator implements Enumeration {
private Enumeration superior;

	LRUHashtableEnumerator(Hashtable entries) {
		superior=entries.elements();
	}

	public boolean hasMoreElements() {
		return(superior.hasMoreElements());
	}

	public Object nextElement() {
		LRUentry entry;

		entry=(LRUentry)superior.nextElement();
		return(entry.value);
	}
}
