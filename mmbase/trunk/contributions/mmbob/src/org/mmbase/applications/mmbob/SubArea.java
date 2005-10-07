/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class SubArea {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(SubArea.class); 

   private String subname;
   private SubArea parent;
   private int pos=0;
   private ArrayList areas = new ArrayList();
   private ArrayList subs = new ArrayList();

   public SubArea() {
	this.subname = "root";
   }

   public SubArea(String subname,int pos) {
	this.subname =  subname;
	this.pos = pos;
   }

   public synchronized void insert(PostArea area,String name) {
	int pos = name.indexOf('/');
	if (pos==-1) {
		// insert at the correct position
		PostArea oa = null;
        	for (int lp=0;lp<areas.size();lp++) {
			oa = (PostArea)areas.get(lp);
			if (oa.getPos()>area.getPos()) {
				areas.add(lp,area);
				break;
			} else {
				oa = null;
			}
		}
		if (oa==null) areas.add(area);
	} else {
		String part1=name.substring(0,pos);
		String part2=name.substring(pos+1);
		// find the sub
        	Iterator i = subs.iterator();
		SubArea sub = null;
        	while (i.hasNext()) {
			SubArea s = (SubArea)i.next();
			if (s.getName().equals(part1)) {
				sub = s;
			}
		}
		if (sub==null) {
			sub = new SubArea(part1,area.getPos());
			// insert at the correct position
			SubArea oa = null;
        		for (int lp=0;lp<subs.size();lp++) {
			oa = (SubArea)subs.get(lp);
			if (oa.getPos()>sub.getPos()) {
				subs.add(lp,sub);
				break;
			} else {
				oa = null;
			}
		}
		if (oa==null) subs.add(sub);
		} 
		sub.insert(area,part2);
	}
   }	

   public String getName() {
	return subname;
   }

   public Iterator getAreas() {
	return areas.iterator();
   }

   public Iterator getSubAreas() {
	return subs.iterator();
   }
	
   public int getAreaCount() {
	return areas.size();
   }

   public int getPostThreadCount() {
       	Iterator i = areas.iterator();
	int total = 0;
       	while (i.hasNext()) {
		PostArea a = (PostArea)i.next();
		total += a.getPostThreadCount();
	}
	return total;
   }

   public int getPostCount() {
       	Iterator i = areas.iterator();
	int total = 0;
       	while (i.hasNext()) {
		PostArea a = (PostArea)i.next();
		total += a.getPostCount();
	}
	return total;
   }

   public int getViewCount() {
       	Iterator i = areas.iterator();
	int total = 0;
       	while (i.hasNext()) {
		PostArea a = (PostArea)i.next();
		total += a.getViewCount();
	}
	return total;
   }

   public int getPos() {
	return pos;
   }

}
