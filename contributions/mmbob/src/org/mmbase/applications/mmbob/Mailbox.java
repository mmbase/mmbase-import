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

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class Mailbox {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Mailbox.class); 

   private Node node;
   private Poster parent;
   private int id;

   public Mailbox(Node node,Poster parent) {
	this.parent=parent;
	this.node=node;
	this.id=node.getNumber();
   }

   public String getName() {
	return node.getStringValue("name");
   }

   public int getId() {
	return node.getNumber();
   }

   public boolean remove() {
	try {
		node.delete();
		return true;
	} catch (Exception e) {
		return false;
	}
   }

   public Node getNode() {
	return node;
   }
}
