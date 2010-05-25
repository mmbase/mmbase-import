/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.module.*;
import org.mmbase.util.*;


/**
 * Used in the editors, the CommandHandlerInterface allows multiple
 * command handlers to be defined (hitlisted).
 *
 * @application SCAN
 * @author Hans Speijer
 * @version $Id$
 */
public interface CommandHandlerInterface {

    /**
     * List commands
     */
    public List<String> getList(PageInfo sp, StringTagger args, StringTokenizer command) throws ParseException;

    /**
     * Replace/Trigger commands
     */
    public String replace(PageInfo sp, StringTokenizer command);

    /**
     * The hook that passes all form related pages to the correct handler
     */
    public boolean process(PageInfo sp, StringTokenizer command, Hashtable cmds, Hashtable vars);

}
