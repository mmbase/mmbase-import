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

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * @author Daniel Ockeloen
 * 
 */
public class ProfileEntryDef {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(ProfileEntryDef.class); 

   private String name;
   private String guiname;
   private int guipos;
   private int size;
   private boolean edit;
   private String external;
   private String externalname;
   private String type;

   public ProfileEntryDef() {
   }
 
   public void setEdit(boolean edit) {
	this.edit = edit;
   }

   public boolean getEdit() {
	return edit;
   }

   public void setGuiPos(int guipos) {
	this.guipos = guipos;
   }

   public int getGuiPos() {
	return guipos;
   }

   public void setSize(int size) {
	this.size = size;
   }

   public int getSize() {
	return size;
   }


   public void setGuiName(String guiname) {
	this.guiname = guiname;
   }

   public String getGuiName() {
	return guiname;
   }

   public void setName(String name) {
	this.name = name;
   }

   public String getName() {
	return name;
   }


   public void setExternal(String external) {
	log.info("SET EXTERNQAL="+external+" PD="+this);
	this.external = external;
   }

   public String getExternal() {
	return external;
   }

   public String getExternalString() {
	if (external==null) return "";
	return external;
   }

   public void setExternalName(String externalname) {
	this.externalname = externalname;
   }

   public String getType() {
	return type;
   }

   public void setType(String type) {
	this.type = type;
   }

   public String getExternalName() {
	return externalname;
   }

   public String getExternalNameString() {
	if (externalname==null) return "";
	return externalname;
   }


}
