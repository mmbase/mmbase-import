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
public class Poster {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Poster.class); 

   private int id,postcount,sessionstart,lastsessionend;
   private int avatar=0;
   private String firstname,lastname,email,level,location,gender;
   private Node node;
   private Forum parent;
   private Hashtable mailboxes;
   private HashMap seenthreads=new HashMap();

   public Poster(Node node,Forum parent) {
	this.parent=parent;
	this.node=node;
	this.id=node.getNumber();
	this.firstname=node.getStringValue("firstname");
	this.lastname=node.getStringValue("lastname");
	this.email=node.getStringValue("email");
	this.postcount=node.getIntValue("postcount");
	this.level=node.getStringValue("level");
	this.location=node.getStringValue("location");
	this.gender=node.getStringValue("gender");
	int fl=node.getIntValue("firstlogin");
	if (fl==-1) {
        	node.setIntValue("firstlogin",((int)(System.currentTimeMillis()/1000)));
		syncNode(ForumManager.FASTSYNC);
	} 
	lastsessionend=node.getIntValue("lastseen");
      	sessionstart=(int)(System.currentTimeMillis()/1000);
   }

   public void addPostCount() {
	postcount++;
	syncNode(ForumManager.FASTSYNC);
   }

   public void setId(int id) {
	this.id=id;
   }

   public void setFirstName(String firstname) {
	this.firstname=firstname;
   }

   public void setLastName(String lastname) {
	this.lastname=lastname;
   }

   public void setEmail(String email) {
	this.email=email;
   }

   public void setGender(String gender) {
	this.gender=gender;
   }

   public void setLocation(String location) {
	this.location=location;
   }

   public void setPostCount(int postcount) {
	this.postcount=postcount;
   }

   public String getAccount() {
	return node.getStringValue("account");
   }

   public int getAvatar() {
	if (avatar==0) readImages();
	return avatar;
   }

   public String getEmail() {
	return email;
   }

   public boolean viewedThread(int id,Integer lastposttime) {
	Integer time=(Integer)seenthreads.get(new Integer(id));
	if (time!=null) {
		if (lastposttime.equals(time)) {
			return true;
		}
	}
	return false;
   }

   public void seenThread(PostThread t) {
	Integer tid=new Integer(t.getId());
	seenthreads.put(tid,new Integer(t.getLastPostTime()));
	log.info("seenthreads="+seenthreads);
   }

   public String getFirstName() {
	return node.getStringValue("firstname");
   }

   public String getLastName() {
	return node.getStringValue("lastname");
   }

   public int getPostCount() {
	return postcount;
   }

   public String getLevel() {
	return level;
   }

   public String getGender() {
	return gender;
   }

   public String getLocation() {
	return location;
   }

   public int getFirstLogin() {
	return node.getIntValue("firstlogin");
   }

   public int getLastSeen() {
	return node.getIntValue("lastseen");
   }

   public int getSessionStart() {
	return sessionstart;
   }

   public int getLastSessionEnd() {
	return lastsessionend;
   }

   public int getId() {
	return id;
   }

   public void setNode(Node node) {
	this.node=node;
   }

   public Node getNode() {
	return node;
   }

   private void syncNode(int queue) {
	node.setIntValue("postcount",postcount);
	node.setStringValue("level",level);
	node.setStringValue("gender",gender);
	node.setStringValue("location",location);
	ForumManager.syncNode(node,queue);
   }

   public void signalSeen() {
	int oldtime=node.getIntValue("lastseen");
        int onlinetime=((int)(System.currentTimeMillis()/1000))-(parent.getPosterExpireTime());

	if (oldtime<onlinetime) {
		parent.newPosterOnline(this);
	}

	node.setIntValue("lastseen",(int)((System.currentTimeMillis()/1000)));
	ForumManager.syncNode(node,ForumManager.SLOWSYNC);
   }


   public void savePoster() {
	node.setValue("firstname",firstname);
	node.setValue("lastname",lastname);
	node.setValue("email",email);
	node.setValue("gender",gender);
	node.setValue("location",location);
	readImages();
	ForumManager.syncNode(node,ForumManager.FASTSYNC);
   }


   private void readImages() {
	if (avatar==0) avatar=-1;
	if (node!=null) {
		/*
                NodeIterator i=node.getRelatedNodes("images").nodeIterator();
                while (i.hasNext()) {
                        Node node=i.nextNode();
                        avatar=node.getNumber();
		}
		*/
		int oldrelnumber=-1;
        	RelationIterator i=node.getRelations("rolerel","images").relationIterator();
                while (i.hasNext()) {
                        Relation rel=i.nextRelation();
                        Node p=null;
                        if (rel.getSource().getNumber()==node.getNumber()) {
                                p=rel.getDestination();
                        } else {
                                p=rel.getSource();
                        }
			if (rel.getNumber()>oldrelnumber) {
				oldrelnumber=rel.getNumber();
				avatar=p.getNumber();
			}
		}
	}
   }

   public boolean profileUpdated() {
	log.info("POSTER UPDATED !!!");
	readImages();
	return true;
   }

   public boolean remove() {
	node.delete(true);
	parent.childRemoved(this);
	return true;
   }

   public Mailbox getMailbox(String name) {
	if (mailboxes==null) mailboxes=readMailboxes();
	Object o=mailboxes.get(name);
	return (Mailbox)o;
   }

   public boolean removeMailbox(String name) {
	Mailbox m=getMailbox(name);
	if (!m.remove()) return false;
	mailboxes.remove(name);
	return true;
   }


   private Hashtable readMailboxes() {
	Hashtable result=new Hashtable();
        if (node!=null) {
        	RelationIterator i=node.getRelations("posrel","forummessagebox").relationIterator();
                while (i.hasNext()) {
                        Relation rel=i.nextRelation();
                        Node p=null;
                        if (rel.getSource().getNumber()==node.getNumber()) {
                                p=rel.getDestination();
                        } else {
                                p=rel.getSource();
                        }
			Mailbox mailbox=new Mailbox(p,this);
			result.put(mailbox.getName(),mailbox);
		}
	}
	return result;
   }
 
   public Mailbox addMailbox(String name,String description,int editstate,int maxmessages,int maxsize,int carboncopymode,int pos) {
        NodeManager nm=ForumManager.getCloud().getNodeManager("forummessagebox");
        if (nm!=null) {
		Node mnode=nm.createNode();	
		mnode.setStringValue("name",name);
		mnode.setStringValue("description",description);
		mnode.setIntValue("editstate",editstate);
		mnode.setIntValue("maxmessages",maxmessages);
		mnode.setIntValue("maxsize",maxsize);
		mnode.setIntValue("carboncopymode",carboncopymode);
		mnode.commit();

                RelationManager rm=ForumManager.getCloud().getRelationManager("posters","forummessagebox","posrel");
                if (rm!=null) {
                        Node rel=rm.createRelation(node,mnode);
        		rel.setIntValue("pos",1); // little weird, daniel
                        rel.commit();
			Mailbox newbox=new Mailbox(mnode,this);
			mailboxes.put(name,newbox);
			return newbox;
		} else {
                        log.error("Forum can't load relation nodemanager posters/forummessagebox/posrel");
		}
	} else {
                log.error("Forum can't load forummessagebox nodemanager");
	}
	return null;
   }


}
