package com.finalist.cmsc.maintenance.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;

import com.finalist.cmsc.sql.SqlAction;

/**
 * TODO: javadoc
 * 
 * @author Jayjay
 */
public class DuplicateICaches extends SqlAction {

   private String action;

   public DuplicateICaches(String action) {
      this.action = action;
   }

   @Override
   public String getSql() {
      return "SELECT number FROM " + getTable("icaches") + " group by " + getFieldname("ckey") +
      " having count(*) > 1 order by " + getFieldname("number"); 
   }

   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuilder result = new StringBuilder();
      int records = 0;
      while (rs.next()) {
         records++;
         int nodeNumber = rs.getInt(getFieldname("number"));
         if ("remove".equals(action)) {
            result.append(nodeNumber + ":deleted, ");
            getCloud().getNode(nodeNumber).delete(false);
         } else {
            result.append(nodeNumber + ", ");   
         }
      }
      
      result.append("<br/>Number of duplicated nodes ");
      if ("remove".equals(action)) {
         result.append("<b>DELETED: ");
      } else {
         result.append("found: <b>");
      }
      result.append( + records + "</b>");
      
      return result.toString();
   }

}
