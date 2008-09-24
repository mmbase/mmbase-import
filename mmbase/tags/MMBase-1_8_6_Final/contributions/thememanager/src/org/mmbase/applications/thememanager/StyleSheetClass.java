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
public class StyleSheetClass {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(StyleSheetClass.class); 
    private String id;
    private HashMap properties = new HashMap();


   public StyleSheetClass(String id) {
	this.id=id;
   }

   public String getId() {
	return id;
   }

   public Iterator getProperties() {
	return properties.values().iterator();
   }

   public StyleSheetProperty getProperty(String name) {
	return (StyleSheetProperty)properties.get(name);
   }

   public int getPropertyCount() {
	return properties.size();
   }

   public void setProperty(String name,String value) {
	// a little weird i guess i wanted/want to extend it to different
	// options per type. Needs work. fallback to just string based.
	if (name.equals("background-color")) {
		StyleSheetPropertyBackgroundColor sp = new StyleSheetPropertyBackgroundColor(name,value);
		properties.put(name,sp);
	} else {
		StyleSheetProperty sp = new StyleSheetProperty(name,value);
		properties.put(name,sp);
	}
   }	


   public void removeProperty(String name) {
	properties.remove(name);
   }	

}
