/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.thememanager;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.mmbase.module.core.*;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class ImageSet {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(ImageSet.class); 
    private HashMap images=new HashMap();
    private String id;
    private String role;


   public ImageSet(String id) {
	this.id=id;
	this.role="default";
   }

   public ImageSet(String id,String role) {
	this.id=id;
	this.role=role;
   }

   public String getRole() {
	return role;
   }

   public boolean isRole(String role) {
	if (role.equals(this.role)) return true;
	return false;
   }

   public String getId() {
	return id;
   }
	
   public void setImage(String imageid,String imagefilename) {
	images.put(imageid,imagefilename);	
   }

   public String getImage(String imageid) {
	Object o=images.get(imageid);	
	if (o!=null) return (String)o;
	return null;
   }

   public Iterator getImageIds() {
	return images.keySet().iterator();
   }

   public int getCount() {
	return images.size();
   }



}
