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
public class ShareInfo {
 
    // even if we have a share we have active boolean to allow
    // people to turn of a share without loosing all its setup(s).
    // this works the same way as windows shares.
    private boolean active;

    private Hashtable users = new Hashtable();

    private Hashtable groups =  new Hashtable();

    // logger
    static private Logger log = Logging.getLoggerInstance(ShareInfo.class.getName()); 

   public ShareInfo() {
   }

   public boolean isActive() {
	return active;
   }

   public void setActive(boolean wantedstate) {
	active=wantedstate;
   }

   public boolean addUser(String name) {
	ShareUser su=ShareManager.getShareUser(name);
	if (su!=null) {
		users.put(name,su);
		return true;
	}
	return false;
   }


   public boolean removeUser(String name) {
	users.remove(name);
	return true;
   }


   public boolean removeGroup(String name) {
	groups.remove(name);
	return true;
   }

   public boolean containsUser(String user) {
	if (users.containsKey(user)) {
		return true;
	}
	return false;
   }



   public boolean containsGroup(String group) {
	if (groups.containsKey(group)) {
		return true;
	}
	return false;
   }


   public boolean addGroup(String name) {
	ShareGroup sg=ShareManager.getShareGroup(name);
	if (sg!=null) {
		groups.put(name,sg);
		return true;
	}
	return false;
   }
 
   public Enumeration getShareUsers() {
	return users.elements();
   }

   public boolean sharedForUser(String user,String password,String method,String host) {
	ShareUser su=(ShareUser)users.get(user);
	if (su!=null && password.equals(su.getPassword())) {
		return true;
	}
	return false;
   }

   public Enumeration getShareGroups() {
	return groups.elements();
   }
}
