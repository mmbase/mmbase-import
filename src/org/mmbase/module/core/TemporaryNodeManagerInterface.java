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
	$Id: TemporaryNodeManagerInterface.java,v 1.4 2000-11-08 13:24:19 vpro Exp $

	$Log: not supported by cvs2svn $
	Revision 1.3  2000/10/25 14:49:36  install
	John Balder changed getObject method
	
	Revision 1.2  2000/10/13 09:39:54  vpro
	Rico: added a method
	
	Revision 1.1  2000/08/14 19:19:06  rico
	Rico: added the temporary node and transaction support.
	      note that this is rather untested but based on previously
	      working code.
	
*/

/**
 * @author Rico Jansen
 * @version $Id: TemporaryNodeManagerInterface.java,v 1.4 2000-11-08 13:24:19 vpro Exp $
 */
public interface TemporaryNodeManagerInterface {
	public String createTmpNode(String type,String owner,String key);
	public String deleteTmpNode(String owner,String key);
	public MMObjectNode getNode(String owner,String key);
	public String getObject(String owner,String key);
	public String setObjectField(String owner,String key,String field,Object value);
	public String getObjectFieldAsString(String owner,String key,String field);
	public Object getObjectField(String owner,String key,String field);
}
