/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import java.lang.*;
import org.mmbase.util.*;

/**
 */
public interface  ProcessorInterface  {

	/**
	 * Generate a list of values from a command to the processor
	 */
	abstract public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException;

	/**
	 * Execute the commands provided in the form values
	 */
	abstract public boolean process(scanpage sp, Hashtable cmds,Hashtable vars);

	/**
	*	Replace a command by a string 
	*/
	abstract public String replace (scanpage sp, String command);

	/**
	*	Replace a command by a string 
	*/
	abstract public String replace (scanpage sp, StringTagger command);

	/**
	* Do a cache check (304) for this request
	*/
	abstract public boolean cacheCheck(scanpage sp,String cmd);
}
