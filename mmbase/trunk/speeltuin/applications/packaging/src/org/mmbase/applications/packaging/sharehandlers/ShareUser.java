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
public class ShareUser {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(ShareUser.class); 

   private String name;
   private String password;
   private String method;
   private String host;

   public ShareUser(String name) {
	this.name=name;
   }

   public void setName(String name) {
	this.name=name;
   }

   public void setPassword(String password) {
	this.password=password;
   }

   public void setMethod(String method) {
	this.method=method;
   }

   public void setHost(String host) {
	this.host=host;
   }

   public String getName() {
	return name;
   }

   public String getPassword() {
	return password;
   }

   public String getMethod() {
	return method;
   }

   public String getHost() {
	return host;
   }

}
