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
public class FieldAlias {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(FieldAlias.class); 

   private String id;
   private String object;
   private String extern;
   private String field;
   private String externfield;
   private String key;
   private String externkey;

   public FieldAlias (String id) {
	this.id=id;
   }
  
   public void setObject(String object) {
	this.object = object;
   }

   public void setExtern(String extern) {
	this.extern = extern;
   }

   public void setField(String field) {
	this.field = field;
   }

   public void setExternField(String externfield) {
	this.externfield = externfield;
   }


   public void setKey(String key) {
	this.key = key;
   }

   public void setExternKey(String externkey) {
	this.externkey = externkey;
   }

   public String getValue(org.mmbase.bridge.Node node) {
	// is it a key mapping ?
	if (key!=null) {
		String keyvalue=node.getStringValue(key);
		//log.info("key="+key+" keyvalue="+keyvalue+" object="+object);
		Cloud cloud = ForumManager.getCloud();
                NodeManager manager=cloud.getNodeManager(extern);
        	NodeQuery query = manager.createQuery();
                StepField f1=query.getStepField(manager.getField(externkey));
		query.setConstraint(new BasicFieldValueConstraint(f1,keyvalue));		
		org.mmbase.bridge.NodeList result=manager.getList(query);
		NodeIterator i=result.nodeIterator();
		if (i.hasNext()) {
			org.mmbase.bridge.Node n=(org.mmbase.bridge.Node)i.nextNode();
			return n.getStringValue(externfield);
		}
	}
	return null;
   }

}
