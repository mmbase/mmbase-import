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
public class ProfileEntry {
 
   // logger
   static private Logger log = Logging.getLoggerInstance(ProfileEntry.class); 

   private String name;
   private String guiname;
   private int guipos;
   private boolean edit;
   private String value;
   private String external;
   private String externalname;
   private String type;
   private boolean synced;

   public ProfileEntry() {
   }
 
   public void setName(String name) {
	this.name = name;
   }

   public String getName() {
	return name;
   }

   public void setValue(String value) {
	this.value = value;
   }

   public String getValue() {
	return value;
   }

   public void setType(String type) {
	this.type = type;
   }

   public String getType() {
	return type;
   }

   public void setSynced(boolean synced) {
	this.synced = synced;
   }

   public boolean getSynced() {
	return synced;
   }


}
