/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.util.*;
/*
	$Id: TemporaryNodeManagerInterface.java,v 1.1 2000-08-14 19:19:06 rico Exp $

	$Log: not supported by cvs2svn $
*/

/**
 * @author Rico Jansen
 * @version $Id: TemporaryNodeManagerInterface.java,v 1.1 2000-08-14 19:19:06 rico Exp $
 */
public interface TemporaryNodeManagerInterface {
	public String createTmpNode(String type,String owner,String key);
	public String deleteTmpNode(String key);
	public MMObjectNode getNode(String key);
	public String setObjectField(String key,String field,Object value);
	public String getObjectFieldAsString(String key,String field);
	public Object getObjectField(String key,String field);
}
