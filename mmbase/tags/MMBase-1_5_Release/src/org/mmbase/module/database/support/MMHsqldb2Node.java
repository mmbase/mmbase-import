/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;


/**
* MMHsqldb2Node implements the MMJdbc2NodeInterface for
* the hsqldb-database (previously called Hypersonic). 
*
* @since MMBase-1.5
* @author Gerard van Enk
* @version $Id: MMHsqldb2Node.java,v 1.1.2.1 2002-03-26 22:47:22 gerard Exp $
*  
*/
public class MMHsqldb2Node extends MMSQL92Node {


	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
		fieldname=fieldname.toLowerCase();
		return(super.decodeDBnodeField(node,fieldname,rs,i,prefix));
	}


	public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
		MultiConnection con=jdbc.getConnection("jdbc:hsqldb:"+jdbc.getDatabaseName(),jdbc.getUser(),jdbc.getPassword());

		return(con);
	}

}



