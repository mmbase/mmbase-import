/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: FieldDef.java,v 1.11 2000-06-27 13:43:57 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.6  2000/04/15 21:10:34  wwwtech
	new FieldDef
	
	Revision 1.5  2000/03/30 13:11:41  wwwtech
	Rico: added license
	
	Revision 1.4  2000/03/29 10:46:33  wwwtech
	Rob: Licenses changed
	
	Revision 1.3  2000/03/07 09:20:41  wwwtech
	Rico: Changed fielddef to use a specific insert, this must be changed in future to support ORDMS, see typedef for hints how
	
	Revision 1.2  2000/02/25 12:52:15  wwwtech
	Rico: removed the insert method.
	
*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;

/**
 * FieldDef, one of the meta stucture nodes it is used to define the
 * fields (using its nodes) of object types at this point has its
 * own nodes (FieldDefs) instead of MMObjectNodes but that will change
 *
 *
 * @author Daniel Ockeloen
 * @version $Id: FieldDef.java,v 1.11 2000-06-27 13:43:57 wwwtech Exp $
 */
public class FieldDef extends MMObjectBuilder {

}
