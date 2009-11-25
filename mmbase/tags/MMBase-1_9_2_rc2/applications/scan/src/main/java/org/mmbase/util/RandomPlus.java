/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Random;

/**
 * Better random function (see Knuth)
 * @application SCAN
 * @author Rico Jansen
 * @version $Id$
 */
public class RandomPlus extends Random {
    private int table[];
    private int tablesize;

    public RandomPlus() {
        this(System.currentTimeMillis(),97);
    }

    public RandomPlus(long seed) {
        this(seed,97);
    }

    public RandomPlus(long seed,int size) {
        super(seed);
        tablesize=size;
        table=new int[tablesize];
        for (int i=0;i<tablesize;i++) {
            table[i]=super.nextInt();
        }
    }

    public void setSeed(long seed) {
        super.setSeed(seed);
        for (int i=0;i<tablesize;i++) {
            table[i]=super.nextInt();
        }
    }

    private synchronized int mynext(int bits) {
        int idx;
        int rtn;

        idx=((Math.abs(super.nextInt()))%tablesize);
        rtn=table[idx];
        table[idx]=super.nextInt();
        return rtn>>>(32-bits);
    }

    public int nextInt() {
        return mynext(32);
    }

    public long nextLong() {
        return mynext(32)<<32L + mynext(32);
    }

    public float nextFloat() {
        int i=mynext(30);
        return i /((float)(1<<30));
    }

    public double nextDouble() {
        long l = ((long)(mynext(27))<<27) + mynext(27);
        return l / (double)(1L<<54);
    }
}
