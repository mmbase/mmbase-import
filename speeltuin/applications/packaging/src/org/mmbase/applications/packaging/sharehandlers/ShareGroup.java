/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.packaging.sharehandlers;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class ShareGroup {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(ShareGroup.class.getName()); 
   private String name;

   private Hashtable members=new Hashtable();

   public ShareGroup(String name) {
	this.name=name;
   }

   public void setName(String name) {
	this.name=name;
   }

   public String getName() {
	return name;
   }

   public boolean addMember(String name) {
	ShareUser su=ShareManager.getShareUser(name);
	if (su!=null) {
		members.put(name,su);
	}
	return false;
   }

   public boolean removeMember(String name) {
	members.remove(name);
	return false;
   }

   public Enumeration getMembers() {
	return members.elements();
   }
}
