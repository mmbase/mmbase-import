package com.finalist.cmsc.maintenance.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;

import com.finalist.cmsc.sql.SqlAction;

/**
 * TODO: javadoc
 * 
 * @author Nico Klasens
 */
public class OrphanNodes extends SqlAction {

   private String managerName;
   private String action;


   public OrphanNodes(String managerName, String action) {
      this.managerName = managerName;
      this.action = action;
   }


   @Override
   public String getSql() {
      return "SELECT * FROM " + getTable("object") + " WHERE " + getFieldname("number") + " in (" + " SELECT "
            + getTableField(managerName, "number") + " FROM " + getTable(managerName) + " LEFT JOIN "
            + getTable("insrel") + " ON " + getTableField("insrel", "dnumber") + " = "
            + getTableField(managerName, "number") + " WHERE " + getTableField("insrel", "dnumber") + " IS NULL"
            + " ) ";
   }


   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuffer result = new StringBuffer();
      int records = 0;
      while (rs.next()) {
         records++;
         int nodeNumber = rs.getInt(getFieldname("number"));
         result.append(" " + managerName + " node " + nodeNumber + "<br />");
         if ("remove".equals(action)) {
            getCloud().getNode(nodeNumber).delete(false);
         }
      }
      result.append("Number of nodes deleted = " + records);
      return result.toString();
   }

}
