/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.maintenance.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.*;

import com.finalist.cmsc.sql.SqlAction;


public class SingleRelationDuplication extends SqlAction {

    private String role;
    private String searchDir;
    private String action;
    
    public SingleRelationDuplication(String role, String searchDir, String action) {
        this.role = role;
        this.searchDir = searchDir;
        this.action = action;
    }
    
    @Override
    public String getSql() {
        return "select " + getFieldname("snumber") + ", " + getFieldname("dnumber") + ", " + getFieldname("rnumber")
                + ", count(" + getFieldname("number") + ")" + 
        		" from "+ getTable(role) + 
        		" group by " + getFieldname("snumber") + ", " + getFieldname("dnumber") + ", " + getFieldname("rnumber") + 
        		" having count(*) > 1;";
    }

    @Override
    public String process(ResultSet rs) throws BridgeException, SQLException {
        StringBuffer result = new StringBuffer();
        int records = 0;
        int relations = 0;

        while (rs.next()) {
            records++;
           int nodeNumber = rs.getInt(getFieldname("snumber"));
           result.append("node " + nodeNumber + "<br />");
           if ("remove".equals(action)) {
              Node content = getCloud().getNode(nodeNumber);
              
              RelationList list = content.getRelations(role, null, searchDir);
              if (list.size() >= 2) {
                  for (int i = 1; i < list.size(); i++) {
                     Relation creationrel = list.getRelation(i);
                     result.append("  relation " + creationrel.getNumber() + " deleted <br />");
                     creationrel.delete();
                     relations++;
                  }
              }
           }
        }
        result.append("Number of nodes deleted = " + records + " total relations " + relations);
        return result.toString();
    }

}
