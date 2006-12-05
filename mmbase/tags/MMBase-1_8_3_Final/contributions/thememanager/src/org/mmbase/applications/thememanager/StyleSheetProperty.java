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
public class StyleSheetProperty {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(StyleSheetProperty.class); 
    private String name;
    private String value;


   public StyleSheetProperty(String name,String value) {
	this.name=name;
	this.value=value;
   }

   public String getName() {
	return name;
   }

   public String getValue() {
	return value;
   }

   public void setValue(String value) {
	this.value = value;
	log.info("WEEEOOO VALUESET="+value);
   }
}
