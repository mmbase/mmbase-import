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
public class Signature {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(Signature.class); 

   private int id;
   private Poster parent;
   private String body;
   private String mode;
   private String encoding;

   public Signature(Poster parent,int id,String body,String mode,String encoding) {
	this.id=id;
	this.body=body;
	this.mode=mode;
	this.encoding=encoding;
	this.parent=parent;
   }
  
   public void setBody(String body) {
	if (!this.body.equals(body)) {
		this.body = body;
		save();
	}
   }

   public void setMode(String mode) {
	if (!this.mode.equals(mode)) {
		if (mode.equals("delete")) {
			delete();	
		} else {
			this.mode = mode;
			save();
		}
	}
   }

   public void setEncoding(String encoding) {
	if (!this.encoding.equals(encoding)) {
		this.encoding = encoding;
		save();
	}
   }

   public String getBody() {
	return body;
   }

   public String getMode() {
	return mode;
   }

   public int getId() {
	return id;
   }

   public String getEncoding() {
	return encoding;
   }

   public boolean delete() {
        Node node = ForumManager.getCloud().getNode(id);
	if (node!=null) {
		node.delete(true);
	}
	parent.deleteSignature(this);
	return true;
   }

   public boolean save() {
        Node node = ForumManager.getCloud().getNode(id);
        node.setValue("mode",mode);
        node.setValue("body",body);
        node.setValue("encoding",encoding);
	node.commit();
	return true;
   }
	
}
