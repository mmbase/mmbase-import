package org.mmbase.applications.mmbob.util;

import java.util.*;

/**
 * this class is a wrapper for using multiple iterators
 * transparantly
 * @author Ernst Nunders
 *
 */
public class IteratorList implements Iterator {
    
    protected List iterators = new ArrayList();
    private Iterator currentIterator = null;
    private Iterator iteratorIterator;
    
    private boolean locked = false;
    
    /**
     * This method can be used until hasNext() or next() is called. After that it
     * throws an UnsupportedOperationException;
     * @param i
     * @return
     */
    public IteratorList addIterator(Iterator i) throws UnsupportedOperationException{
        if(locked) throw new UnsupportedOperationException("You can not add an iterator after you start iterating."); 
        iterators.add(i);
        return this;
    }

    public boolean hasNext() {
        if(iterators.isEmpty()){
            return false;
        }
        if(!locked){
            init();
        }
        if(currentIterator.hasNext()){
            return true;
        }else{
            // if there is a next iterator make that the cuerrent one
            // and call this method recursively.
            if(iteratorIterator.hasNext()){
                currentIterator = (Iterator) iteratorIterator.next();
                return hasNext();
            }
        }
        return false;
    }

    private void init() {
        locked = true;
        iteratorIterator = iterators.iterator();
        if(iteratorIterator.hasNext()){
            currentIterator = (Iterator) iteratorIterator.next();
        }
    }

    public Object next() {
        // TODO Auto-generated method stub
        if(hasNext()){
            return currentIterator.next();
        }else{
            throw new NoSuchElementException("iterator is finished");
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Testing the class...
     * @param args
     */
    public static void main(String[] args){
        List l1 = new ArrayList();
        List l2 = new ArrayList();
        List l3 = new ArrayList();
        
        l1.add("een - een");
        l1.add("een - twee");
        
        l3.add("drie - een");
        l3.add("drie - twee");
        
        IteratorList il = new IteratorList();
        il.addIterator(l1.iterator()).addIterator(l2.iterator()).addIterator(l3.iterator());
        
        test(il,"test with multiple iterators");
        
        il = new IteratorList();
        
        test(il,"test with empty iteratorlist");
        
        il.addIterator(l1.iterator());
        
        test(il, "test with one iterator");
        

        
    }

    private static Iterator test(IteratorList il, String testName) {
        System.out.println("**"+testName+"**");
        Iterator i;
        for(i = (Iterator)il; i.hasNext(); ){
            System.out.println("> " + (i.hasNext() ? "hasnext" : "no hasnext"));
            System.out.println("> "+i.next());
        }
        return i;
    }

}
