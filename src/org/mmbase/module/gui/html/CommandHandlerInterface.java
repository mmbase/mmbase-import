package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;


/**
 * Used in the editors, the CommandHandlerInterface allows multiple
 * command handlers to be defined (hitlisted).
 *
 * @author Hans Speijer
 */
public interface CommandHandlerInterface {

	/**
	 * List commands
	 */
	public Vector getList(scanpage sp, StringTagger args, StringTokenizer command);	

	/**
	 * Replace/Trigger commands
	 */
	public String replace(scanpage sp, StringTokenizer command);
	
	/**
	 * The hook that passes all form related pages to the correct handler
	 */
	public boolean process(scanpage sp, StringTokenizer command, Hashtable cmds, Hashtable vars);		

}
