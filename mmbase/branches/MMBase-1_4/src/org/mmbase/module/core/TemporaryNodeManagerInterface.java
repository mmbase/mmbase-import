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
	$Id: TemporaryNodeManagerInterface.java,v 1.11 2001-07-24 07:30:06 pierre Exp $

	$Log: not supported by cvs2svn $
	Revision 1.10  2001/01/08 12:32:46  install
	Rob added createRelation() throws exception if relationname is incorrect

	Revision 1.9  2000/11/13 15:33:47  vpro
	Rico: added relation support, note that this must be changed when the whole relation mess changes

	Revision 1.8  2000/11/13 10:44:53  install
	Rob: added temporary Relation in interface

	Revision 1.7  2000/11/08 16:24:13  vpro
	Rico: fixed key bussiness

	Revision 1.6  2000/11/08 16:11:52  vpro
	Rico: added temporary key method

	Revision 1.5  2000/11/08 14:24:46  vpro
	Rico: fixed getObject

	Revision 1.4  2000/11/08 13:24:19  vpro
	Rico: included owner in operations

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
 * @version $Id: TemporaryNodeManagerInterface.java,v 1.11 2001-07-24 07:30:06 pierre Exp $
 */
public interface TemporaryNodeManagerInterface {
	public String createTmpNode(String type,String owner,String key);
	public String createTmpRelationNode(String type,String owner,String key, String source,String destination) throws Exception;
	public String createTmpAlias(String name,String owner,String key, String destination);
	public String deleteTmpNode(String owner,String key);
	public MMObjectNode getNode(String owner,String key);
	public String getObject(String owner,String key,String dbkey);
	public String setObjectField(String owner,String key,String field,Object value);
	public String getObjectFieldAsString(String owner,String key,String field);
	public Object getObjectField(String owner,String key,String field);
}
