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
import org.mmbase.util.logging.*;


/**
* MMHsqldb2Node implements the MMJdbc2NodeInterface for
* the hsqldb-database (previously called Hypersonic). 
*
* @since MMBase-1.5
* @author Gerard van Enk
* @version $Id: MMHsqldb2Node.java,v 1.3 2002-05-28 21:45:14 gerard Exp $
*  
*/
public class MMHsqldb2Node extends MMSQL92Node {


   /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(MMSQL92Node.class.getName());

	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
		fieldname=fieldname.toLowerCase();
		return(super.decodeDBnodeField(node,fieldname,rs,i,prefix));
	}


	public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
		MultiConnection con=jdbc.getConnection("jdbc:hsqldb:"+jdbc.getDatabaseName(),jdbc.getUser(),jdbc.getPassword());

		return(con);
	}

    public String getDBText(ResultSet rs,int idx) {
        String result = null;
        try {
            result = rs.getString(idx);
       } catch (Exception e) {
            log.error("MMObjectBuilder -> MMHsqldb2Node text exception "+e);
            log.error(Logging.stackTrace(e));
            return "";
        }
        return result;
    }

    public void setDBText(int i, PreparedStatement stmt,String body) {
        try {
            stmt.setString(i,body);
        } catch (Exception e) {
            log.error("MMObjectBuilder : Can't set ascii stream");
            log.error(Logging.stackTrace(e));
        }
    }

}



