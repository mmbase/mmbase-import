package com.finalist.cmsc.maintenance.staging;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;
import org.mmbase.remotepublishing.CloudInfo;
import org.mmbase.remotepublishing.PublishManager;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.sql.SqlAction;

/**
 * TODO: javadoc
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
   public String getSql() {
      return "SELECT " + getTableField("remotenodes", "*") + " FROM " + getTable("remotenodes") + " LEFT JOIN "
            + getTable("object") + " ON " + getTableField("remotenodes", "sourcenumber") + " = "
            + getTableField("object", "number") + " WHERE " + getTableField("object", "number") + " IS NULL ";
   }


   /**
    * @see com.finalist.cmsc.sql.SqlAction#process(java.sql.ResultSet)
    */
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuffer result = new StringBuffer();
      if (ServerUtil.isStaging()) {
         int records = 0;
         while (rs.next()) {
            records++;
            int localNumber = rs.getInt(getFieldname("sourcenumber"));
            result.append("staging " + localNumber + " <br />");
            if ("unlink".equals(action)) {
               try {
                  CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
                  PublishManager.deletePublishedNode(localCloudInfo, localNumber);
               }
               catch (RuntimeException re) {
                  result.append("staging " + localNumber + " FAILED<br />");
               }
            }
         }
         result.append("Number of nodes unlinked = " + records);
      }
      else {
         result.append("ERROR: This server is configured as live.");
      }
      return result.toString();
   }

}
