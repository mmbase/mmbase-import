/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.module.database.MultiConnection;
import org.mmbase.module.database.JDBCInterface;
import org.mmbase.module.core.MMObjectNode;

/**
 * MMHypersonic2Node implements the MMJdbc2NodeInterface for
 * Hypsersonic.
 *
 * @author Daniel Ockeloen
 * @version $Id: MMHypersonic2Node.java,v 1.5 2002-03-22 13:44:48 pierre Exp $
 */
public class MMHypersonic2Node extends MMSQL92Node {

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
        fieldname=fieldname.toLowerCase();
        return(super.decodeDBnodeField(node,fieldname,rs,i,prefix));
    }

    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:"+jdbc.getDatabaseName(),jdbc.getUser(),jdbc.getPassword());

        return(con);
    }

}
