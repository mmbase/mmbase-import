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

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class PostThread {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(PostThread.class.getName()); 

   private String subject;
   private String creator;
   private int id;
   private int viewcount;
   private int postcount;

   private int lastposttime;
   private String lastposter;
   private String lastpostsubject;
 
   private Node node;
   private PostArea parent;
   private Vector postings=null;
   private Vector writers=new Vector();

   public PostThread(PostArea parent,Node node) {
	this.parent=parent;
	this.node=node;
	this.subject=node.getStringValue("subject");
	this.creator=node.getStringValue("creator");
	this.id=node.getNumber();
	this.viewcount=node.getIntValue("viewcount");
	if (viewcount==-1) viewcount=0;
	this.postcount=node.getIntValue("postcount");
	if (postcount==-1) postcount=0;

	this.lastpostsubject=node.getStringValue("lastpostsubject");
	this.lastposter=node.getStringValue("lastposter");
	this.lastposttime=node.getIntValue("lastposttime");

	// read postings
	// readPostings();
   }

   public void setId(int id) {
	this.id=id;
   }

   public int getId() {
	return id;
   }

   public void setNode(Node node) {
	this.node=node;
   }

   public String getSubject() {
	return node.getStringValue("subject");
   }

   public String getState(Poster ap) {
	boolean isnew=true;
	int lastsessionend=ap.getLastSessionEnd();

	// was the post older than my last session ?
	if (lastposttime<lastsessionend) {
		isnew=false;
	} 

	// of did i read it in this session ?
	if (ap.viewedThread(id,new Integer(lastposttime))) {
		isnew=false;
	}

	
	String state=getState();
	if (state.equals("normal")) {
		if (isnew) state="normalnew";
	} else if (state.equals("hot")) {
		if (isnew) state="hotnew";
	}

	// even extra lets see if im in this thread;
	if (isWriter(ap.getAccount())) {
		state+="me";
	}

	return state;
   }

   public String getState() {
	String state=node.getStringValue("state");
	if (state==null || state.equals("")) {
		state="normal";
	}
	
	// figure out if its hot
	boolean hot=false;
	if (postcount>parent.getPostThreadCountAvg()) {
		hot=true;
	}

	if (state.equals("normal") && hot) state="hot";
	
	return state;
   }

   public void setState(String state) {
	String oldstate=node.getStringValue("state");
	if (oldstate.equals("pinned") && !state.equals("pinned")) parent.decPinnedCount();
	if (!oldstate.equals("pinned") && state.equals("pinned")) parent.incPinnedCount();
	node.setStringValue("state",state);
   }

   public void setMood(String mood) {
	node.setStringValue("mood",mood);
   }

   public void setType(String type) {
	node.setStringValue("ttype",type);
   }

   public String getMood() {
	String mood=node.getStringValue("mood");
	if (mood==null || mood.equals("")) {
		return "normal";
	}
	return mood;
   }

   public String getType() {
	String type=node.getStringValue("ttype");
	if (type==null || type.equals("")) {
		return "normal";
	}
	return type;
   }

   public String getCreator() {
	return creator;
   }

   public int getPostCount() {
	return postcount;
   }

   public int getViewCount() {
	return viewcount;
   }

   public String getLastPoster() {
	return lastposter;
   }


   public int getLastPostTime() {
	return lastposttime;
   }


   public String getLastSubject() {
	return lastpostsubject;
   }

   public Iterator getPostings(int page,int pagecount) {
	if (postings==null) readPostings();

	// get the range we want
	int start=(page-1)*pagecount;
	int end=page*pagecount;
	if (end>postcount) {
		end=postings.size();
	}
	List result=postings.subList(start,end);

	viewcount++;
	syncNode(ForumManager.SLOWSYNC);
	parent.signalViewsChanged(this);

	return result.iterator();
   }

   public boolean save() {
        node.commit();
	parent.resort(this);
        return true;
   }

   public boolean postReply(String subject,Poster poster,String body) {
	return(postReply(subject,poster.getAccount(),body));
   }

   public boolean postReply(String subject,String poster,String body) {
	if (postings==null) readPostings();
	
        NodeManager nm=ForumManager.getCloud().getNodeManager("postings");
        if (nm!=null) {
                Node pnode=nm.createNode();
		if (subject!=null && !subject.equals("")) {
			pnode.setStringValue("subject",subject);
		} else {
			pnode.setStringValue("subject",node.getStringValue("subject"));
		}
		pnode.setStringValue("poster",poster);
	       //start and end tag must be added, this is not the correct place to do this
               //this must be done in somewhere in the bridge (fieldtypes?)
		pnode.setStringValue("body",body);
		pnode.setIntValue("createtime",(int)(System.currentTimeMillis()/1000));
                pnode.commit();
                RelationManager rm=ForumManager.getCloud().getRelationManager("postthreads","postings","related");
                if (rm!=null) {
                        Node rel=rm.createRelation(node,pnode);
                        rel.commit();
        		Posting posting=new Posting(pnode,this);
		        postings.add(posting);

			// update stats and signal parent of change
			Poster p=parent.getParent().getPoster(poster);
			if (p!=null) p.addPostCount();
			addWriter(posting);

			// update the counters
			postcount++;

			lastposttime=pnode.getIntValue("createtime");
			lastposter=pnode.getStringValue("poster");
			lastpostsubject=pnode.getStringValue("subject");

			syncNode(ForumManager.FASTSYNC);
			parent.signalNewReply(this);

                } else {
                        log.error("Forum can't load relation nodemanager postthreads/postings/related");
                }
        } else {
                log.error("Forum can't load postings nodemanager");
        }
	return true;
   }

   private void syncNode(int queue) {
	node.setIntValue("postcount",postcount);
	node.setIntValue("viewcount",viewcount);
	node.setIntValue("lastposttime",lastposttime);
	node.setStringValue("lastposter",lastposter);
	node.setStringValue("lastpostsubject",lastpostsubject);
	ForumManager.syncNode(node,queue);
   }

   public void readPostings() {
	if (postings!=null) return;
        long start=System.currentTimeMillis();
	postings=new Vector();
	if (node!=null) {
		NodeIterator i=node.getRelatedNodes("postings").nodeIterator();
		while (i.hasNext()) {
			Node node=i.nextNode();
                        Posting posting=new Posting(node,this);
			addWriter(posting);
                      	postings.add(posting);
		}
	}
        long end=System.currentTimeMillis();
        //log.info("READTIME POSTINGS="+(end-start)+" postingsize="+postings.size());
   }

   public boolean isLastPage(int page,int pagesize) {

	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;
	if (page==pagecount) {
		return true;
	}
	return false;
   }

   public int getPageCount(int pagesize) {
	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;
	return pagecount;
   }

   /**
   * I hate how this is done but don't see a way to get this fast enough
   * any other way.
   */
   public String getNavigationLine(String baseurl, int page,int pagesize,String cssclass) {
	int f=parent.getParent().getId();
	int a=parent.getId();
	int p=getId();

	if (!cssclass.equals("")) {
		cssclass=" class=\""+cssclass+"\"";	
	}

	// weird way must be a better way for pagecount
	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;
	

	int c=page-1;
	if (c<1) c=1;
	int n=page+1;
	if (n>pagecount) n=pagecount;
	//String result = "("+pagecount+") <a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+c+"\""+cssclass+">&lt</a>";
	String result = "<a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+c+"\""+cssclass+">&lt</a>";
	for (int i=1;i<=pagecount;i++) {
	  result+=" <a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+i+"\""+cssclass+">";
	  if (i==page) {
		result+="["+i+"]";
	  } else {
		result+=""+i;
	  }
	  result+="</a>";
        } 
	result += " <a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+n+"\""+cssclass+">&gt</a>";
	return result;
   }


   public String getNavigationLine(String baseurl, int pagesize,String cssclass) {
	int f=parent.getParent().getId();
	int a=parent.getId();
	int p=getId();

	if (!cssclass.equals("")) {
		cssclass=" class=\""+cssclass+"\"";	
	}

	// weird way must be a better way for pagecount
	int pagecount=postcount/pagesize;
	if ((pagecount*pagesize)!=postcount) pagecount++;

	// if only one page no nav line is needed
	if (pagecount==1) return "";

	String result = "(";
	for (int i=1;i<=pagecount;i++) {
	  if (i!=1) result+=" ";
	  result+="<a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&postthreadid="+p+"&page="+i+"\""+cssclass+">"+i+"</a>";
        } 
	result += ")";
	return result;
   }

   public Posting getPosting(int postingid) {
	if (postings==null) readPostings();

	Enumeration e=postings.elements();
	while (e.hasMoreElements()) {
		Posting p=(Posting)e.nextElement();
		if(p.getId()==postingid) {
			return p;
		}
	}
	return null;
   }

   public boolean remove() {
	if (postings==null) readPostings();

	Enumeration e=postings.elements();
	while (e.hasMoreElements()) {
		Posting p=(Posting)e.nextElement();
		if(!p.remove()) {
			log.error("Can't remove Posting : "+p.getId());
			return false;
		}
		postings.remove(p);
	}
	ForumManager.nodeDeleted(node);
	return true;
   }

   public void addWriter(Posting p) {
	if (!writers.contains(p.getPoster())) writers.add(p.getPoster());
   }

   public boolean isWriter(String asker) {
	if (writers.contains(asker)) return true;
	return false;
   }

   public void childRemoved(Posting p) {
	if (postings==null) readPostings();
	postings.remove(p);
	postcount--;
	syncNode(ForumManager.FASTSYNC);
	if (postings.size()==0) {
		node.delete(true);
		ForumManager.nodeDeleted(node);
		parent.childRemoved(this);
	} 
   }

}
