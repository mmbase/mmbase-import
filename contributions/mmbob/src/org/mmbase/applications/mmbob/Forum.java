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
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class Forum {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Forum.class); 


   private String name;
   private String description;
   private String administratorsline;
   private int id,totalusers,totalusersnew;
   private Node node;


   private int viewcount;
   private int postcount;
   private int postthreadcount=-1;

   private int lastposttime;
   private String lastposter;
   private String lastpostsubject;

   private Hashtable administrators=new Hashtable();

   private Hashtable postareas=new Hashtable();

   private Hashtable posters=new Hashtable();
   private Hashtable posternames=new Hashtable();
   private Vector onlineposters=new Vector();
   private Vector newposters=new Vector();

   public Forum(Node node) {
	this.node=node;
	this.name=node.getStringValue("name");
	this.description=node.getStringValue("description");
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
   	preCachePosters();
	readAreas();
	readRoles();
   }


   public void setId(int id) {
	this.id=id;
   }

   public void setNode(Node node) {
	this.node=node;
   }

   public void setName(String name) {
	this.name=name;
	node.setValue("name",name);
   }

   public void setLanguage(String language) {
	node.setValue("language",language);
   }

   public String getLanguage() {
	return node.getStringValue("language");
   }

   public void setDescription(String description) {
	this.description=description;
	node.setValue("description",description);
   }

   public String getName() {
	return name;
   }

   public String getDescription() {
	return description;
   }

   public int getId() {
	return id;
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


   public Enumeration getAdministrators() {
	return administrators.elements();
   }

   public Enumeration getPosters() {
	return posters.elements();
   }

   public Enumeration getPostersOnline() {
	return onlineposters.elements();
   }

   public boolean isAdministrator(String account) {
	return administrators.containsKey(account);
   }

   public String getAdministratorsLine(String baseurl) {
	if (administratorsline!=null) return administratorsline;
	administratorsline="";
	Enumeration e=administrators.elements();
	while (e.hasMoreElements()) {
		Poster p=(Poster)e.nextElement();
		if (!administratorsline.equals("")) administratorsline+=",";
		if (baseurl.equals("")) {
			administratorsline+=p.getAccount();
		} else {
			administratorsline+="<a href=\""+baseurl+"?forumid="+getId()+"&posterid="+p.getId()+"\">"+p.getAccount()+"</a>";
		}
	}
	return administratorsline;
   }

   public PostArea getPostArea(String id) {
	Object o=postareas.get(id);
	if (o!=null) {
		return (PostArea)o;
	}
	return null;
   }


   public boolean removePostArea(String id) {
	PostArea a=(PostArea)postareas.get(id);
	if (a!=null) {
		if (a.remove()) {
			postareas.remove(id);
			return true;
		}
	} else {
		log.error("trying to delete a unknown postarea");
	}
	return false;
   }


   public boolean removeFolder(int posterid,String foldername) {
	Poster poster=getPoster(posterid);
	if (poster!=null) {
		return poster.removeMailbox(foldername);
	}
	return false;
   }


   public void removeOnlinePoster(Poster p) {
	onlineposters.remove(p);
   }

   public int getPostAreaCount() {
	return postareas.size();
   }

   public Enumeration getPostAreas() {
	return postareas.elements();
   }


   public void newPosterOnline(Poster p) {
	if (!onlineposters.contains(p)) {
		onlineposters.add(p);
	}
   }

   public void newPoster(Poster p) {
	if (!newposters.contains(p)) {
		newposters.add(p);
	}
   }



   public int newPostArea(String name,String description) {
        NodeManager nm=ForumManager.getCloud().getNodeManager("postareas");
        if (nm!=null) {
		Node anode=nm.createNode();	
		anode.setStringValue("name",name);
		anode.setStringValue("description",description);
		anode.commit();

                RelationManager rm=ForumManager.getCloud().getRelationManager("forums","postareas","related");
                if (rm!=null) {
                        Node rel=rm.createRelation(node,anode);
                        rel.commit();
			PostArea area=new PostArea(this,anode);
			postareas.put(""+anode.getNumber(),area);
			return anode.getNumber();
		} else {
                        log.error("Forum can't load relation nodemanager forums/postareas/related");
		}
	} else {
                log.error("Forum can't load postareas nodemanager");
	}
	return -1;
   }

   private void readAreas() {
        long start=System.currentTimeMillis();
	if (node!=null) {
		NodeIterator i=node.getRelatedNodes("postareas").nodeIterator();
		while (i.hasNext()) {
			Node node2=i.nextNode();
			PostArea area=new PostArea(this,node2);
			postareas.put(""+node2.getNumber(),area);
		}
	}
        long end=System.currentTimeMillis();
   }



   public int getPostThreadCount() {
	//if (postthreadcount==-1) recalcPostThreadCount();
	return postthreadcount;
   }

   private void recalcPostCount() {
	int count=0;
	Enumeration e=postareas.elements();
	while (e.hasMoreElements()) {
		PostArea a=(PostArea)e.nextElement();
		count+=a.getPostCount();
	}
	postcount=count;
   }


   private void recalcPostThreadCount() {
	int count=0;
	Enumeration e=postareas.elements();
	while (e.hasMoreElements()) {
		PostArea a=(PostArea)e.nextElement();
		count+=a.getPostThreadCount();
	}
	postthreadcount=count;
   }

   public void leafsChanged() {
//	recalcPostCount();
//	recalcPostThreadCount();
   }


   public void signalNewReply(PostArea child) {
	postcount++;
	lastposttime=child.getLastPostTime();
	lastposter=child.getLastPoster();
	lastpostsubject=child.getLastSubject();
	syncNode(ForumManager.FASTSYNC);
   }


   public void signalNewPost(PostArea child) {
	postthreadcount++;
	syncNode(ForumManager.FASTSYNC);
   }

   public void signalViewsChanged(PostArea child) {
	viewcount++;
	syncNode(ForumManager.SLOWSYNC);
   }

   public Poster getPoster(String posterid) {
	Poster p=(Poster)posternames.get(posterid);
	if (p!=null) {
		return p;
	}
	return null;
   }

   public Poster getPoster(int posterid) {
	Poster p=(Poster)posters.get(new Integer(posterid));
	if (p!=null) {
		return p;
	} else {
		/*
		if (node!=null) {
			p=new Poster(node);	
			posters.put(new Integer(posterid),p);	
			posternames.put(p.getAccount(),p);	
			return p;
		}
		*/
	}
	return null;
   }

   public int getPostersTotalCount() {
	return totalusers; 
   }

   public int getPostersOnlineCount() {
	return onlineposters.size(); 
   }

   public int getPostersNewCount() {
	return newposters.size(); 
   }

   /**
   * this is all wrong should be replaced way to much mem to read
   * them all.
   */
   private void preCachePosters() {
	long start=System.currentTimeMillis();
	if (node!=null) {
		totalusers=0;
		totalusersnew=0;
		int onlinetime=((int)(System.currentTimeMillis()/1000))-(getPosterExpireTime());
		int newtime=((int)(System.currentTimeMillis()/1000))-(24*60*60*7);

		NodeIterator i=node.getRelatedNodes("posters","related","both").nodeIterator();
		while (i.hasNext()) {
			Node node=i.nextNode();
			Poster p=new Poster(node,this);	
			posters.put(new Integer(p.getId()),p);	
			posternames.put(p.getAccount(),p);	
			totalusers++;
			if (p.getLastSeen()>onlinetime) {
				onlineposters.add(p);
			}
			if (p.getFirstLogin()==-1 || p.getFirstLogin()>newtime) {
				newPoster(p);
			}
		}
	}
	long end=System.currentTimeMillis();
   }

   public Poster createPoster(String account,String password) {
        NodeManager nm=ForumManager.getCloud().getNodeManager("posters");
        if (nm!=null) {
		Node pnode=nm.createNode();	
		pnode.setStringValue("account",account);
		pnode.setStringValue("password",password);
		pnode.setIntValue("postcount",0);
		pnode.setIntValue("firstlogin",((int)(System.currentTimeMillis()/1000)));
		pnode.setIntValue("lastseen",((int)(System.currentTimeMillis()/1000)));
		pnode.commit();

       		 RelationManager rm=ForumManager.getCloud().getRelationManager("forums","posters","related");
		if (rm!=null) {

       			Node rel=rm.createRelation(node,pnode);
			rel.commit();

			Poster p=new Poster(pnode,this);	
			posters.put(new Integer(p.getId()),p);	
			onlineposters.add(p);	
			posternames.put(p.getAccount(),p);	
		
			totalusers++;
			totalusersnew++;
			return p;
		} else {
			log.error("Forum can't load relation nodemanager forums/posters/related");
			return null;
		}
	} else {
		log.error("Forum can't load posters nodemanager");
		return null;
	}
   }


   public boolean addAdministrator(Poster ap) {
	if (!isAdministrator(ap.getAccount())) {
                RelationManager rm=ForumManager.getCloud().getRelationManager("forums","posters","rolerel");
                if (rm!=null) {
                        Node rel=rm.createRelation(node,ap.getNode());
       			rel.setStringValue("role","administrator");
                        rel.commit();
			administrators.put(ap.getAccount(),ap);	
		} else {
                        log.error("Forum can't load relation nodemanager forums/posters/rolerel");
		}
	} 
	return false;
   }

   public void childRemoved(Poster p) {
        posters.remove(p);  
   }


   public boolean remove() {
	Enumeration e=posters.elements();
	while (e.hasMoreElements()) {
		Poster p=(Poster)e.nextElement();
		if(!p.remove()) {
			log.error("Can't remove Poster : "+p.getId());
			return false;
		}
   		posters.remove(new Integer(p.getId()));
	}


	e=postareas.elements();
	while (e.hasMoreElements()) {
		PostArea a=(PostArea)e.nextElement();
		if(!a.remove()) {
			log.error("Can't remove Area : "+a.getId());
			return false;
		}
   		postareas.remove(""+a.getId());
	}
		
	node.delete(true);
	return true;
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
			// check limited to 12 chars to counter mmbase 12
			// chars in role bug in some installs
			if (role.substring(0,12).equals("administrato")) {
				Poster po=getPoster(p.getNumber());
				administrators.put(po.getAccount(),po);	
			}
		}
	}
   }

   public int getPosterExpireTime() {
	return (5*60);
   }


   public int newPrivateMessage(String poster,String to,String subject,String body) {
	Poster toposter=getPoster(to);
	Poster fromposter=getPoster(poster);
	if (toposter!=null) {
		Mailbox mailbox=toposter.getMailbox("Inbox");
		if (mailbox==null) {
			mailbox=toposter.addMailbox("Inbox","inbox for user "+toposter.getAccount(),1,25,-1,1,1);
		}
        	NodeManager nm=ForumManager.getCloud().getNodeManager("forumprivatemessage");
      		if (nm!=null) {
			Node mnode=nm.createNode();	
			mnode.setStringValue("subject",subject);
			mnode.setStringValue("body",body);
			mnode.setStringValue("poster",fromposter.getAccount());
			mnode.setIntValue("createtime",(int)(System.currentTimeMillis()/1000));
			mnode.setIntValue("viewstate",0);
			mnode.setStringValue("fullname",fromposter.getFirstName()+" "+fromposter.getLastName());
			mnode.commit();

              		RelationManager rm=ForumManager.getCloud().getRelationManager("forummessagebox","forumprivatemessage","related");
                		if (rm!=null) {
                        		Node rel=rm.createRelation(mailbox.getNode(),mnode);
                        		rel.commit();
			} else {
                        	log.error("Forum can't load relation nodemanager forummessagebox/forumprivatemessage/related");
			}
	} else {
                log.error("Forum can't load forumprivatemessage nodemanager");
	}
	}
	return -1;
   }

   public int newFolder(int posterid,String newfolder) {
	Poster poster=getPoster(posterid);
	if (poster!=null) {
		Mailbox mailbox=poster.getMailbox(newfolder);
		if (mailbox==null) {
			mailbox=poster.addMailbox(newfolder,"mailbox "+newfolder+" for user "+poster.getAccount(),1,25,-1,1,1);
		}
	}
	return -1;
   }


    public void maintainMemoryCaches() {
        Enumeration e=postareas.elements();
        while (e.hasMoreElements()) {
                // for now all postareas nodes are loaded so
                // we just call them all for a maintain
                PostArea a=(PostArea)e.nextElement();
                a.maintainMemoryCaches();
        }
    }

}
