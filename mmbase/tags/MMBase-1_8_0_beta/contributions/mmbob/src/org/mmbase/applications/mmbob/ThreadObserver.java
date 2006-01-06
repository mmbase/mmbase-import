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
public class ThreadObserver {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(ThreadObserver.class); 

   private ArrayList emailonchange = new ArrayList();
   private ArrayList bookmarked = new ArrayList();
   private ArrayList ignorelist = new ArrayList();
   private int id=-1;
   private int threadid=-1;
   private Forum parent;

   public ThreadObserver(Forum parent,int id,int threadid,String emailonchange,String bookmarked,String ignorelist) {
	this.parent = parent;
	this.id=id;
	this.threadid=threadid;
	this.emailonchange = decodeList(this.emailonchange,emailonchange,"emailonchange");
	this.bookmarked = decodeList(this.bookmarked,bookmarked,"bookmarked");
	this.ignorelist = decodeList(this.ignorelist,ignorelist,"ignorelist");
   }

   public ArrayList decodeList(ArrayList list,String names,String type) {
	StringTokenizer tok =  new StringTokenizer(names,",\n\r");
	while (tok.hasMoreTokens()) {
		String name = tok.nextToken();
		Poster p = parent.getPoster(name);
		if (p!=null) { 
			list.add(p);
			if (type.equals("bookmarked")) {
				p.addBookmarkedThread(threadid);
			}
		}
	}
	return list;
   }

   public boolean wantsEmailOnChange(Poster p) {
	if (emailonchange.contains(p)) return true;
	return false;
   }
  
   public boolean isBookmarked(Poster p) {
	if (bookmarked.contains(p)) return true;
	return false;
   }

   public void setId(int id) {
	this.id = id;
   }

   public int getId() {
	return id;
   }

   public void setThreadId(int threadid) {
	this.threadid = threadid;
   }

   public int getThreadId() {
	return threadid;
   }


  public boolean setEmailOnChange(Poster p,boolean state) {
	if (state) {
		if (!emailonchange.contains(p)) {
			emailonchange.add(p);
			return save();
		}
	} else {
		if (emailonchange.contains(p)) {
			emailonchange.remove(p);
			return save();
		}
	}
	return false;
  }


  public boolean setBookmarkedChange(Poster p,boolean state) {
	if (state) {
		if (!bookmarked.contains(p)) {
			bookmarked.add(p);
			return save();
		}
	} else {
		if (bookmarked.contains(p)) {
			bookmarked.remove(p);
			return save();
		}
	}
	return false;
  }


   public boolean save() {
	if (id!=-1) {
        	Node node = ForumManager.getCloud().getNode(id);
        	node.setValue("emailonchange",seperated(emailonchange));
        	node.setValue("bookmarked",seperated(bookmarked));
        	node.setValue("ignorelist",seperated(ignorelist));
		node.commit();
	} else {
        	NodeManager man = ForumManager.getCloud().getNodeManager("threadobservers");
        	org.mmbase.bridge.Node node = man.createNode();
        	node.setValue("emailonchange",seperated(emailonchange));
        	node.setValue("bookmarked",seperated(bookmarked));
        	node.setValue("ignorelist",seperated(ignorelist));
        	node.commit();
                RelationManager rm = ForumManager.getCloud().getRelationManager("postthreads", "threadobservers", "related");
                if (rm != null) {
                	Node rel = rm.createRelation(ForumManager.getCloud().getNode(threadid), node);
			rel.commit();
		}
		id = node.getNumber();
	}
	return true;
   }

   private String seperated(ArrayList list) {
       String result="";
       Iterator i=list.iterator();
       while (i.hasNext()) {
                Poster p=(Poster)i.next();
		if (result.equals("")) {
			result=p.getNick();
		} else {
			result+=","+p.getNick();
		}
       }
       return result;
   }

   public void signalContentAdded(PostThread t) {
       Iterator i=emailonchange.iterator();
       while (i.hasNext()) {
                Poster p=(Poster)i.next();
		p.sendEmailOnChange(t);
       }
   }

}
