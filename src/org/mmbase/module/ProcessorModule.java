/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;

/**
 * The Processor Module extends the baisc module to the Processor
 * interface so it can perform for servscan (pagelets).
 *
 * @author Daniel Ockeloen
 */
public class ProcessorModule extends Module implements ProcessorInterface {

	private String  classname   = getClass().getName();
	private boolean debug       = true;
	private void    debug( String msg ) { System.out.println( classname +":"+ msg ); } 

	/**
	 * Generate a list of values from a command to the processor
	 */
	public Vector  getList(scanpage sp,StringTagger tagger, String value) {
		Vector result = new Vector();
		result.addElement("This module doesn't implement this processor call");
		return(result);
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		return(false);
	}

	/**
	*	Replace a command by a string 
	*/
	public String replace (scanpage sp, String command) {
		return("This module doesn't implement this processor call");
	}

	/**
	*	Replace a command by a string 
	* who the hell uses this (daniel)
	*/
	public String replace (scanpage sp, StringTagger command) {
		return("This module doesn't implement this processor call");
	}

	/**
	* Do a cache check (304) for this request
	*/
	public boolean cacheCheck(scanpage sp,String cmd) {
		return(false);
	}


	public void init() {
	}

	public void reload() {
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}

}
