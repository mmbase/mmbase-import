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

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class Posting {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Posting.class.getName()); 

   private int id;
   private PostThread parent;
   private Node node;

   public Posting(Node node,PostThread parent) {
	this.node=node;
	this.id=node.getNumber();
	this.parent=parent;
   }

   public void setId(int id) {
	this.id=id;
   }

   public void setSubject(String subject) {
	node.setValue("subject",subject);
   }

   public void setBody(String body) {
       node.setStringValue("body",body);
   }

   public void setEditTime(int time) {
	node.setIntValue("edittime",time);
   }

   public int getEditTime() {
	return node.getIntValue("edittime");
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

   public String getBody() {
	return node.getStringValue("body");
   }

   public String getPoster() {
	return node.getStringValue("poster");
   }

   public int getPostTime() {
	return node.getIntValue("createtime");
   }

   public boolean remove() {
	parent.childRemoved(this);
	return true;
   }
  
   public boolean save() {
	node.commit();
	return true;
   }
}
