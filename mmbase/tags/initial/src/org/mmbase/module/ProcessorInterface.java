/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
	abstract public Vector  getList(scanpage sp,StringTagger tagger, String value);

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
