/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.util.*;

/**
 * Class for doing random things
 * @author Rico Jansen
 */
public class RandomThings {
private static RandomPlus rnd;

	static {
		rnd=new RandomPlus();
	}

	/**
	 * Shuffle a Vector until its a Random mess
	 */
	static public void shuffleVector(Vector v) {
		int siz;
		int src,dst;
		siz=v.size();

		for (dst=siz-1;dst>0;dst--) {
			src=Math.abs(rnd.nextInt())%(dst+1);
			swap(v,src,dst);
		}
	}

	/**
	 * return a new Shuffled vector
	 */
	static public Vector shuffleCloneVector(Vector v) {
		Vector newv;

		newv=(Vector)v.clone();
		shuffleVector(newv);
		return(newv);
	}

	/**
	 * Shuffle an integer array
	 */
	static public void shuffleArray(int arr[]) {
		int siz;
		int src,dst,t;
		siz=arr.length;

		for (dst=siz-1;dst>0;dst--) {
			src=Math.abs(rnd.nextInt())%(dst+1);
			t=arr[src];
			arr[src]=arr[dst];
			arr[dst]=t;
		}
	}

	private static void swap(Vector v,int i,int j) {
		Object ob1,ob2;
		ob1=v.elementAt(i);
		ob2=v.elementAt(j);
		v.setElementAt(ob1,j);
		v.setElementAt(ob2,i);
	}

	/**
	 * Return 'max' random elements from a Vector.
	 * No duplicates will be given.
	 */
	public static Vector giveRandomFrom(Vector v,int max) {
		Vector newv=new Vector();
		Object ob;
		int siz=v.size(),c;

		if (max>=siz) {
			newv=shuffleCloneVector(v);
		} else {
			int idx[]=new int[siz];
			int i;

			for (i=0;i<siz;i++) idx[i]=i;
			shuffleArray(idx);
			
			for (i=0;i<max;i++) {
				ob=v.elementAt(idx[i]);
				newv.addElement(ob);
			}
		}
		return(newv);
	}

	public static void main(String args[]) {
		Vector v=new Vector();
		int siz=128;
		int idx[]=new int[siz];
		int i;

		for (i=0;i<siz;i++) {
			v.addElement(new Integer(i));
			idx[i]=i;
		}
		System.out.println("shuffleCloneVector "+shuffleCloneVector(v));
		System.out.println("giveRandomFrom "+giveRandomFrom(v,64));
		shuffleArray(idx);
		System.out.println("shuffleArray "+idx);
		for (i=0;i<siz;i++) {
			if (i==0)
				System.out.print(""+idx[i]);
			else
				System.out.print(","+idx[i]);
		}
		System.out.println("");
	}
}
