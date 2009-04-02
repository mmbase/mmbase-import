package com.finalist.cmsc.maintenance.remotepublishing;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;
import org.mmbase.module.core.MMBase;
import org.mmbase.remotepublishing.PublishManager;

import com.finalist.cmsc.sql.SqlAction;

/**
 * Remove administrative nodes (remotenodes) for imported nodes which are deleted
 * 
 * @author Nico Klasens
 */
public class UnlinkRemotenodes extends SqlAction {

   private String action;

   public UnlinkRemotenodes(String action) {
      this.action = action;
   }

   /**
    * @see com.finalist.cmsc.sql.SqlAction#getSql()
    */
   @Override
   public String getSql() {
      int localCloudNumber = getLocalCloudNumber();

      return "SELECT " + getTableField("remotenodes", "*") + " FROM " + getTable("remotenodes")
            + " LEFT JOIN " + getTable("object") + " ON "
            + getTableField("remotenodes", "destinationnumber") + " = "
            + getTableField("object", "number") + " WHERE " + getTableField("object", "number")
            + " IS NULL " + " AND " + getTableField("remotenodes", "destinationcloud") + " = "
            + localCloudNumber;
   }

   private int getLocalCloudNumber() {
      final String CLOUD = "cloud";
      final String CLOUD_DEFAULT = "cloud.default";
      int localCloudNumber = MMBase.getMMBase().getBuilder(CLOUD).getNode(CLOUD_DEFAULT)
            .getNumber();
      return localCloudNumber;
   }

   /**
    * @see com.finalist.cmsc.sql.SqlAction#process(java.sql.ResultSet)
    */
   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuffer result = new StringBuffer();
      int records = 0;
      while (rs.next()) {
         records++;
         int remoteNodeNumber = rs.getInt(getFieldname("number"));
         int sourceNodeNumber = rs.getInt(getFieldname("sourcenumber"));
         int destinationNodeNumber = rs.getInt(getFieldname("destinationnumber"));
         result.append("Remote node " + remoteNodeNumber + " with source " + sourceNodeNumber
               + " and destination " + destinationNodeNumber + " <br />");
         if ("unlink".equals(action)) {
            PublishManager.unLinkImportedNode(destinationNodeNumber);
         }
      }
      result.append("Number of nodes unlinked = " + records);
      return result.toString();
   }

}
