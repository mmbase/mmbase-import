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
public class RemoteHost {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(RemoteHost.class); 

   private String host;
   private int lastupdatetime;
   private int updatecount;
   private int id=-1;
   private Poster parent;

   public RemoteHost(Poster parent,String host,int lastupdatetime,int updatecount) {
	this.parent = parent;
	this.host=host;
	this.lastupdatetime=lastupdatetime;
	this.updatecount=updatecount;
   }
  
   public void setHost(String host) {
	this.host = host;
   }

   public void setLastUpdateTime(int lastupdatetime) {
	this.lastupdatetime = lastupdatetime;
   }

   public void setUpdateCount(int updatecount) {
	this.updatecount = updatecount;
   }

   public void setId(int id) {
	this.id = id;
   }

   public int getId() {
	return id;
   }

   public String getHost() {
	return host;
   }

   public int getLastUpdateTime() {
	return lastupdatetime;
   }

   public int getUpdateCount() {
	return updatecount;
   }

   public boolean delete() {
        Node node = ForumManager.getCloud().getNode(id);
	if (node!=null) {
		node.delete(true);
	}
	return true;
   }

   public boolean save() {
	if (id!=-1) {
        	Node node = ForumManager.getCloud().getNode(id);
        	node.setValue("host",host);
        	node.setIntValue("lastupdatetime",lastupdatetime);
        	node.setIntValue("updatecount",updatecount);
		node.commit();
	} else {
        	NodeManager man = ForumManager.getCloud().getNodeManager("remotehosts");
        	org.mmbase.bridge.Node node = man.createNode();
        	node.setValue("host",host);
        	node.setIntValue("lastupdatetime",lastupdatetime);
        	node.setIntValue("updatecount",updatecount);
        	node.commit();
                RelationManager rm = ForumManager.getCloud().getRelationManager("posters", "remotehosts", "related");
                if (rm != null) {
                	Node rel = rm.createRelation(parent.getNode(), node);
			rel.commit();
		}
		id = node.getNumber();
	}
	return true;
   }
	
}
