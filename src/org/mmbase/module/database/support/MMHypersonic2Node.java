/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.FieldDefs;

import org.mmbase.module.database.JDBCInterface;
import org.mmbase.module.database.MultiConnection;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MMSQL92Node implements the MMJdbc2NodeInterface for
 * sql92 types of database this is the class used to abstact the query's
 * needed for mmbase for each database.
 *
 * It is now deprecated and only kept for people using the old
 * hypersonic database instead of the new version Hsqldb.
 *
 * Eduard:
 * This class is not depricated, since Orion 1.6.0 still uses this
 * database as is example database with jndi resource's
 *
 * @deprecated use {@link MMHsqldb2Node}
 * @author Daniel Ockeloen
 * @version $Id: MMHypersonic2Node.java,v 1.9 2003-03-07 08:50:17 pierre Exp $
 */
public class MMHypersonic2Node extends MMSQL92Node {
    private static Logger log = Logging.getLoggerInstance(MMHypersonic2Node.class.getName());

    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:"+jdbc.getDatabaseName(),jdbc.getUser(),jdbc.getPassword());
        return con;
    }
    /**
     * Overridden since the hypersonic has following problems:
     * <ul><li>
     * <code>rs.getString(i)</code> does not return the same as
     * <code>new String(rs.getBytes(i))</code> (was added for encoding
     * problem. Actuall we need 2 settings for MMBase. 1 for pages and 1
     * for database layer. When we keep using one will only give problems
     * </li><li>
     * fieldnames have to be in lower case
     * </li></ul>
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs, int i, String prefix) {
        try {
            fieldname=fieldname.toLowerCase();
            // is this fieldname disallowed ? ifso map it back
            if (allowed2disallowed.containsKey(fieldname)) {
                fieldname = (String)allowed2disallowed.get(fieldname);
            }
            if (node == null) {
                log.warn("Cannot decode field " + fieldname + " because given node is null");
            }
            int type=node.getDBType(prefix+fieldname);
            switch (type) {
            case FieldDefs.TYPE_XML:
            case FieldDefs.TYPE_STRING: {
                String tmp=rs.getString(i);
                if (tmp == null) {
                    node.setValue(prefix + fieldname, "");
                } else {
                    node.setValue(prefix + fieldname, tmp);
                }
                break;
            }
            case FieldDefs.TYPE_NODE:
            case FieldDefs.TYPE_INTEGER:
                node.setValue(prefix + fieldname, (Integer)rs.getObject(i));
                break;
            case FieldDefs.TYPE_LONG:
                node.setValue(prefix + fieldname, (Long)rs.getObject(i));
                break;
            case FieldDefs.TYPE_FLOAT:
                // who does this now work ????
                //node.setValue(prefix+fieldname,((Float)rs.getObject(i)));
                node.setValue(prefix + fieldname, new Float(rs.getFloat(i)));
                break;
            case FieldDefs.TYPE_DOUBLE:
                node.setValue(prefix+fieldname,(Double)rs.getObject(i));
                break;
            case FieldDefs.TYPE_BYTE:
                node.setValue(prefix+fieldname,"$SHORTED");
                break;
            }
            return node;
        } catch(SQLException e) {
            log.error(fieldname+" node="+node.getIntValue("number"));
            log.error(Logging.stackTrace(e));
        }
        return node;
    }
}
