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
import org.mmbase.bridge.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class PostArea {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(PostArea.class); 

   private int id;
   private Node node;
   private Forum parent;
   private Hashtable moderators=new Hashtable();
   private String moderatorsline;
   private Vector postthreads=null;
   private Hashtable namecache=new Hashtable();
   private boolean firstcachecall=true;

   private int viewcount;
   private int postcount;
   private int postthreadcount;
   private int lastposttime;
   private int numberofpinned=0;
   private String lastposter;
   private String lastpostsubject;

   public PostArea(Forum parent,Node node) {
	this.parent=parent;
	this.node=node;
	this.id=node.getNumber();

	this.viewcount=node.getIntValue("viewcount");
	if (viewcount==-1) viewcount=0;
	this.postcount=node.getIntValue("postcount");
	if (postcount==-1) postcount=0;
	this.postthreadcount=node.getIntValue("postthreadcount");
	if (postthreadcount==-1) postthreadcount=0;

	this.lastpostsubject=node.getStringValue("lastpostsubject");
	this.lastposter=node.getStringValue("lastposter");
	this.lastposttime=node.getIntValue("lastposttime");

	// read postareas
	// don't read all, readPostThreads();
   	readRoles();
   }

   public void setId(int id) {
	this.id=id;
   }

   public void setNode(Node node) {
	this.node=node;
   }

   public void setName(String name) {
	node.setValue("name",name);
   }

   public void setDescription(String description) {
	node.setValue("description",description);
   }

   public String getName() {
	return node.getStringValue("name");
   }

   public String getDescription() {
	return node.getStringValue("description");
   }

   public int getId() {
	return id;
   }

   public int getPostThreadCount() {
	return postthreadcount;
   }

   public int getPostThreadCountAvg() {
	if (postthreadcount==0) return 0;
	return postcount/postthreadcount;
   }

   public int getPageCount(int pagesize) {
        int pagecount=postthreadcount/pagesize;
        if ((pagecount*pagesize)!=postthreadcount) pagecount++;
        return pagecount;
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

   public Enumeration getPostThreads() {
	if (postthreads==null) readPostThreads();
	return postthreads.elements();
   }

   public Iterator getPostThreads(int page,int pagecount) {
	if (postthreads==null) readPostThreads();

        // get the range we want
        int start=(page-1)*pagecount;
        int end=page*pagecount;
        if (end>postthreadcount) {
                end=postthreads.size();
        }
	log.debug("START="+start+" "+end+" "+postthreads.size());
        List result=postthreads.subList(start,end);

	return result.iterator();
   }

   public PostThread getPostThread(String id) {
	if (postthreads==null) readPostThreads();
        Object o=namecache.get(id);
        if (o!=null) {
                return (PostThread)o;
        }
        return null;
   }

   public String getNavigationLine(String baseurl,int page, int pagesize,String cssclass) {
	int f=parent.getId();
	int a=getId();
	if (!cssclass.equals("")) {
		cssclass=" class=\""+cssclass+"\"";	
	}

	// weird way must be a better way for pagecount
	int pagecount=postthreadcount/pagesize;
	if ((pagecount*pagesize)!=postthreadcount) pagecount++;

	// if only one page no nav line is needed
	if (pagecount==1) return "";


	int c=page-1;
	if (c<1) c=1;
	int n=page+1;
	if (n>pagecount) n=pagecount;
	String result = "<a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&page="+c+"\""+cssclass+">&lt</a>";
	for (int i=1;i<=pagecount;i++) {
	  result+=" <a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&page="+i+"\""+cssclass+">";
	  if (i==page) {
		result+="["+i+"]";
	  } else {
		result+=""+i;
	  }
	  result+="</a>";
        } 
	result += " <a href=\""+baseurl+"?forumid="+f+"&postareaid="+a+"&page="+n+"\""+cssclass+">&gt</a>";
	return result;
   }

   public Enumeration getModerators() {
	return moderators.elements();
   }

   public Enumeration getNonModerators() {
	Vector result=new Vector();
	Enumeration e=parent.getPosters();
	while (e.hasMoreElements()) {
		Poster p=(Poster)e.nextElement();
		if (!isModerator(p.getAccount())) {
			result.add(p);
		}
	}	
	return result.elements();
   }

   public boolean isModerator(String account) {
	// check if he a admin then asign him
	// the moderators role too
	if (parent.isAdministrator(account)) return true;
	return moderators.containsKey(account);
   }

   public boolean removeModerator(Poster mp) {
	if (isModerator(mp.getAccount())) {
		RelationIterator i=node.getRelations("rolerel","posters").relationIterator();
		while (i.hasNext()) {
			Relation rel=i.nextRelation();
			String role=rel.getStringValue("role");
			if (role.equals("moderator")) {
				Node p=null;
				if (rel.getSource().getNumber()==node.getNumber()) {
					p=rel.getDestination();
				} else {
					p=rel.getSource();
				}
				if (p!=null && p.getNumber()==mp.getId()) {
					rel.delete();
					moderators.remove(mp.getAccount());	
					moderatorsline=null;
				}
			}
		}
	}
	return false;
   }

   public boolean addModerator(Poster mp) {
	if (isModerator(mp.getAccount())) return true;
        RelationManager rm=ForumManager.getCloud().getRelationManager("postareas","posters","rolerel");
        if (rm!=null) {
                        Node rel=rm.createRelation(node,mp.getNode());
			rel.setStringValue("role","moderator");
                        rel.commit();
			moderators.put(mp.getAccount(),mp);	
			moderatorsline=null;
	} else {
                        log.error("Forum can't load relation nodemanager postareas/posters/rolerel");
			return false;
         }
	 return true;
   }

   public String getModeratorsLine(String baseurl) {
	if (moderatorsline!=null) return moderatorsline;
	moderatorsline="";
	Enumeration e=moderators.elements();
	while (e.hasMoreElements()) {
		Poster p=(Poster)e.nextElement();
		if (!moderatorsline.equals("")) moderatorsline+=",";
		if (baseurl.equals("")) {
			moderatorsline+=p.getAccount();
		} else {
			moderatorsline+="<a href=\""+baseurl+"?forumid="+parent.getId()+"&postareaid="+getId()+"&posterid="+p.getId()+"\">"+p.getAccount()+"</a>";
		}
	}
	return moderatorsline;
   }


   private void readPostThreads() {
        long start=System.currentTimeMillis();
   	postthreads=new Vector();

	if (node!=null) {
        	NodeManager postareasmanager=ForumManager.getCloud().getNodeManager("postareas");
      	  	NodeManager postthreadsmanager=ForumManager.getCloud().getNodeManager("postthreads");
      	  	Query query=ForumManager.getCloud().createQuery();
		Step step1=query.addStep(postareasmanager);
		RelationStep step2=query.addRelationStep(postthreadsmanager);
		StepField f1=query.addField(step1,postareasmanager.getField("number"));
		StepField f2=query.addField(step2.getNext(),postthreadsmanager.getField("number"));
		StepField f3=query.addField(step2.getNext(),postthreadsmanager.getField("lastposttime"));
		query.addSortOrder(f3,SortOrder.ORDER_DESCENDING);

		//query.setConstraint(query.createConstraint(f1,node)); // werkt niet meer
		query.setConstraint(query.createConstraint(f1,new Integer(node.getNumber())));
		//f3.addNode(node.getNumber()); // dit werkt niet, snap ik de docs niet ?
	
		NodeIterator i2=ForumManager.getCloud().getList(query).nodeIterator();
		while (i2.hasNext()) {
			Node n2=i2.nextNode();
			PostThread postthread=new PostThread(this,ForumManager.getCloud().getNode(n2.getIntValue("postthreads.number")));
			if (postthread.getState().equals("pinned")) {
                        	postthreads.add(numberofpinned,postthread);
				numberofpinned++;
			} else {
       	         		postthreads.add(postthread);
			}
       	         	namecache.put(""+n2.getValue("postthreads.number"),postthread);
		}
	}

        long end=System.currentTimeMillis();
	//log.info("TIME="+start+" "+end+" "+(end-start));
	

   }

   public void incPinnedCount() {
	numberofpinned++;
   }

    public void decPinnedCount() {
	numberofpinned--;
    }


   public int newPost(String subject,Poster poster,String body) {
	return (newPost(subject,poster.getAccount(),body));
   }

   public int newPost(String subject,String poster,String body) {
	if (postthreads==null) readPostThreads();
        NodeManager nm=ForumManager.getCloud().getNodeManager("postthreads");
        if (nm!=null) {
                Node ptnode=nm.createNode();
		ptnode.setStringValue("subject",subject);
		ptnode.setStringValue("creator",poster);
		ptnode.setStringValue("state","normal");
		ptnode.setStringValue("mood","normal");
		ptnode.setStringValue("ttype","post");
		ptnode.setIntValue("createtime",(int)(System.currentTimeMillis()/1000));
                ptnode.commit();
                RelationManager rm=ForumManager.getCloud().getRelationManager("postareas","postthreads","related");
                if (rm!=null) {
                        Node rel=rm.createRelation(node,ptnode);
                        rel.commit();
			PostThread postthread=new PostThread(this,ptnode);

			// now add the first 'reply' (wrong name since its not a reply)
			postthread.postReply(subject,poster,body);
			if (postthread.getState().equals("pinned")) {
        			postthreads.add(0,postthread);
			} else {
        			postthreads.add(numberofpinned,postthread);
			}
        		namecache.put(""+ptnode.getNumber(),postthread);

			postthreadcount++;
			syncNode(ForumManager.FASTSYNC);
			parent.signalNewPost(this);
                        return ptnode.getNumber();
                } else {
                        log.error("Forum can't load relation nodemanager postareas/postthreads/related");
                }
        } else {
                log.error("Forum can't load postthreads nodemanager");
        }
	return -1;
   }

   public void signalNewReply(PostThread child) {
	postcount++;
	lastposttime=child.getLastPostTime();
	lastposter=child.getLastPoster();
	lastpostsubject=child.getLastSubject();
	syncNode(ForumManager.FASTSYNC);

	resort(child);

	parent.signalNewReply(this);
   }

   public void resort(PostThread child) {
	// move to the top of the queue
	log.info("POSTTHREADS="+postthreads+" CHILD="+child);
	if (postthreads.remove(child)) {
		if (child.getState().equals("pinned")) {
			postthreads.add(0,child);
		} else {
			postthreads.add(numberofpinned,child);
		}
	}
   }

   public Forum getParent() {
	return parent;
   }


   public void signalViewsChanged(PostThread child) {
	viewcount++;
	syncNode(ForumManager.SLOWSYNC);
	parent.signalViewsChanged(this);
   }

   public boolean save() {
        syncNode(ForumManager.FASTSYNC);
        return true;
   }

   private void syncNode(int queue) {
	node.setIntValue("postcount",postcount);
	node.setIntValue("postthreadcount",postthreadcount);
	node.setIntValue("viewcount",viewcount);
	node.setIntValue("lastposttime",lastposttime);
	node.setStringValue("lastposter",lastposter);
	node.setStringValue("lastpostsubject",lastpostsubject);
	ForumManager.syncNode(node,queue);
   }

   private void readRoles() {
	if (node!=null) {
		RelationIterator i=node.getRelations("rolerel","posters").relationIterator();
		while (i.hasNext()) {
			Relation rel=i.nextRelation();
			Node p=null;
			if (rel.getSource().getNumber()==node.getNumber()) {
				p=rel.getDestination();
			} else {
				p=rel.getSource();
			}
			String role=rel.getStringValue("role");
			if (role.equals("moderator")) {
				Poster po=parent.getPoster(p.getNumber());
				moderators.put(po.getAccount(),po);	
			}
		}
	}

   }

   public boolean remove() {
	if (postthreads==null) readPostThreads();
   	if (getPostThreadCount()!=0) {
		Enumeration e=postthreads.elements();
		while (e.hasMoreElements()) {
			PostThread t=(PostThread)e.nextElement();
			if(!t.remove()) {
				log.error("Can't remove PostThread : "+t.getId());
				return false;
			}
   			postthreads.remove(""+t.getId());
		}
	}
	node.delete(true);
	return true;
   }


   public boolean removePostThread(String postthreadid) {
   	PostThread t=getPostThread(postthreadid);
	if (t!=null) {
   		postthreads.remove(""+t.getId());
		if(!t.remove()) {
			log.error("Can't remove PostThread : "+t.getId());
			return false;
		}
	}
	return true;
   }

   public void childRemoved(PostThread t) {
   	postthreads.remove(t);
   }

    public void maintainMemoryCaches() {
	if (postthreads!=null && firstcachecall) {
		firstcachecall=false;
		int time=(int)(System.currentTimeMillis()/1000)-(24*3600*7);
		Enumeration e=postthreads.elements();
		while (e.hasMoreElements()) {
			PostThread t=(PostThread)e.nextElement();
			int time2=t.getLastPostTime();
			if (time2>time) {
				t.readPostings();
			} else {
				//log.info("ITS OLD");
			}
		}
	}
    }

}
