/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Vector;
import java.util.Enumeration;
import org.mmbase.util.logging.*;

/**
 * Class to store Objects in, in a Sorted manner
 *
 * @see org.mmbase.util.Sortable
 * @see org.mmbase.util.CompareInterface
 *
 *
 * Todo: Remove duplicate code for the binary search
 * @deprecated You can use java.util.SortedSet (implementations of that), or Collections.sort(), if duplicate entries are essential (but how should they be sorted then?)
 * @author Rico Jansen
 * @version $Id$
 */
public class SortedVector extends java.util.Vector {

    // logger
    private static Logger log = Logging.getLoggerInstance(SortedVector.class.getName());

    CompareInterface cmp=null;
    CompareInterface sortcmp=new SortableCompare();
    CompareInterface stringcmp=new StringCompare();

    /**
     * Create a SortedVector
     */
    public SortedVector() {
        super();
    }

    /**
     * Create a SortedVector, specifying which compare function to use.
     * @see org.mmbase.util.CompareInterface
     */
    public SortedVector(CompareInterface cmp) {
        super();
        this.cmp=cmp;
    }

    /**
     * Store an object in its right location.
     * This will do a linear insert until 16 elements and
     * than switch over to binary insert.
     */
    public void addSorted(Object newObject) {
        // No compare set at creation so it must be strings
        if (cmp==null) cmp=stringcmp;
        if (size()>16) { // Turnaround in code is around 16 elements
            addBinSorted(newObject);
        } else {
            addLinSorted(newObject);
        }
    }

    /**
     * Store an object in its right location.
     * This will do a linear insert until 16 elements and
     * than switch over to binary insert.
     */
    public void addSorted(Sortable newObject) {
        if (cmp==null) cmp=sortcmp;
        if (size()>16) { // Turnaround in code is around 16 elements
            addBinSorted(newObject);
        } else {
            addLinSorted(newObject);
        }
    }

    /**
     * Store an object in its right location, search for the location
     * the binary (smart) way and only insert it if it's not in the Vector yet.
     */
    public void addUniqueSorted(Object newObject) {
        int low,med,high,which=0;
        boolean done;

        med=0;
        done=false;
        Object other;

        for (low=0,high=size()-1;low<=high && !done; ) {
            med=(low+high)/2;
            other=elementAt(med);
            which=cmp.compare(newObject,other);
            if (which<0) {
                high=med-1;
            } else if (which>0) {
                low=med+1;
                med=low;
            } else {
                done=true;
            }
        }
        if (!done)
            insertElementAt(newObject,med);
    }

    /**
     * Store an object in its right location, search for the location
     * the linear (stupid) way.
     */
    public void addLinSorted(Object newObject) {
        Enumeration e = elements();
        boolean done = false;
        int i = 0;

        Object other;

        while (e.hasMoreElements() && !done) { // While more elements and not done
            other = e.nextElement();
            if (cmp.compare(newObject,other)<0) {    //insert newObject before other
                insertElementAt(newObject,i);
                done = true;
            }
            i++;
        }
        if (!done) {
            addElement(newObject);
        }
    }

    /**
     * Store an object in its right location, search for the location
     * the binary (smart) way.
     */
    public void addBinSorted(Object newObject) {
        int low,med,high,which=0;
        boolean done;

        med=0;
        done=false;
        Object other;

        for (low=0,high=size()-1;low<=high && !done; ) {
            med=(low+high)/2;
            other=elementAt(med);
            which=cmp.compare(newObject,other);
            if (which<0) {
                high=med-1;
            } else if (which>0) {
                low=med+1;
                med=low;
            } else {
                done=true;
            }
        }
        insertElementAt(newObject,med);
    }

    /**
     * See if an object is in the SortedVector
     * This should be contains(Object) but that one is final.
     * @see java.util.Vector
     */
    public boolean has(Object newObject) {
        return find(newObject)>=0;
    }

    /**
     * Find an object in the SortedVector.
     * This uses binary search. As usual this should be a overide
     * of an existing method but that one is final.
     * In this case indexOf(Object,int) in Vector.
     * @see java.util.Vector
     */
    public int find(Object newObject) {
        int low,med,high,which=0;
        boolean done;

        med=0;
        done=false;
        Object other;

        for (low=0,high=size()-1;low<=high && !done; ) {
            med=(low+high)/2;
            other=elementAt(med);
            which=cmp.compare(newObject,other);
            if (which<0) {
                high=med-1;
            } else if (which>0) {
                low=med+1;
                med=low;
            } else {
                done=true;
            }
        }
        if (done) return med;
        else return -1;
    }

    /**
     * Change the compare function the SortedVector should use.
     * This forces a (re)sort of the data.
     */
    public void setCompare(CompareInterface cmp) {
        this.cmp=cmp;
        Sort();
    }

    /**
     * Sort the SortedVector
     */
    public void Sort() {
        if (size()>1) {
            if (cmp==null) {
                if (firstElement() instanceof String) {
                    cmp=stringcmp;
                } else {
                    cmp=sortcmp;
                }
            }
            qsortCompare(0,size()-1);
        }
    }

    /**
     * The actual sort routine, this is just plain old qsort.
     */
    private void qsortCompare(int first,int last ) {
        Object x;
        int left=first+1;
        int right=last;


        if (last-first+1<2) return;
        loop:
        while(left<right) {
            while(cmp.compare(elementAt(left),elementAt(first))<=0) {
                left++;
                if (left>=right) break loop;
            }
            while(cmp.compare(elementAt(right),elementAt(first))>0) {
                right--;
                if (left>=right) break loop;
            }
            /* Swap */
            x=elementAt(left);
            setElementAt(elementAt(right),left);
            setElementAt(x,right);
            left++;
        }
        if (cmp.compare(elementAt(first),elementAt(left))<0) {
            left--;
        }
        x=elementAt(left);
        setElementAt(elementAt(first),left);
        setElementAt(x,first);
        qsortCompare(first,left-1);
        qsortCompare(left+1,last);
    }

    /**
     * Sort a Vector and return a SortedVector.
     * Note: You should make sure it are Strings or Sortables,
     * otherwise use SortVector(Vector,CompareInterface) to specify
     * the interface
     */
    public static SortedVector SortVector(Vector vec) {
        SortedVector newvec=new SortedVector();

        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            newvec.addElement(e.nextElement());
        }
        newvec.Sort();
        return newvec;
    }

    /**
     * Sort a Vector and return a SortedVector using the specified compare
     * function.
     */
    public static SortedVector SortVector(Vector vec,CompareInterface cmpI) {
        SortedVector newvec=new SortedVector(cmpI);

        for (Enumeration e=vec.elements();e.hasMoreElements();) {
            newvec.addElement(e.nextElement());
        }
        newvec.Sort();
        return newvec;
    }

    /**
     * Test the class
     */
    public static void main(String args[]) {
        SortedVector v;
        Vector vec=new Vector();
        StringCompare strc=new StringCompare();

        /* Binary insert test */
        v=new SortedVector(strc);
        for (int i=0;i<args.length;i++) {
            v.addBinSorted(args[i]);
            log.info("V "+v);
        }
        /* See if find works */
        log.info("Element "+args[0]+" at "+v.find(args[0])+" : "+v.has(args[0]));

        /* Normal String Qsort test */
        for (int i=0;i<args.length;i++) {
            vec.addElement(args[i]);
        }
        log.info("V1 "+vec);
        log.info("V2 "+SortVector(vec));


        /* Qsort test through compare function */
        v=new SortedVector(strc);
        for (int i=0;i<args.length;i++) {
            v.addElement(args[i]);
        }
        log.info("V1 "+vec);
        log.info("V2 "+SortVector(vec));
    }

}
