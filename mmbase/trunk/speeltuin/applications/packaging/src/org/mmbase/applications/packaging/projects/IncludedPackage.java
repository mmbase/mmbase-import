/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.packaging.projects;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


/**
 * @author Daniel Ockeloen
 * 
 */
public class IncludedPackage {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(IncludedPackage.class); 

   String name;
   String type;
   String maintainer;
   String version;
   boolean included;

   public String getName() {
	return name;
   }

   public String getType() {
	return type;
   }

   public String getMaintainer() {
	return maintainer;
   }

   public String getVersion() {
	return version;
   }

   public boolean getIncluded() {
	return included;
   }

   public void setIncluded(boolean included) {
	this.included=included;
   }

   public void setName(String name) {
	this.name=name;
   }

   public void setVersion(String version) {
	this.version=version;
   }

   public void setType(String type) {
	this.type=type;
   }

   public String getId() {
        String id=name+"@"+maintainer+"_"+type;
        id=id.replace(' ','_');
        id=id.replace('/','_');
	return id;
  }

   public void setMaintainer(String maintainer) {
	this.maintainer=maintainer;
   }

}
