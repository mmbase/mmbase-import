package com.finalist.cmsc.maintenance.remotepublishing;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;
import org.mmbase.module.core.MMBase;
import org.mmbase.remotepublishing.CloudInfo;
import org.mmbase.remotepublishing.PublishManager;

import com.finalist.cmsc.sql.SqlAction;

/**
 * Remove administrative nodes (remotenodes) for published nodes which are deleted
 * 
 * @author Nico Klasens
 */
public class UnpublishRemotenodes extends SqlAction {

   private String action;

   public UnpublishRemotenodes(String action) {
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
            + getTableField("remotenodes", "sourcenumber") + " = "
            + getTableField("object", "number") + " WHERE " + getTableField("object", "number")
            + " IS NULL " + " AND " + getTableField("remotenodes", "sourcecloud") + " = "
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
         int localNumber = rs.getInt(getFieldname("sourcenumber"));
         result.append("sourcenumber " + localNumber + " <br />");
         if ("unpublish".equals(action)) {
            try {
               CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
               PublishManager.deletePublishedNode(localCloudInfo, localNumber);
            }
            catch (RuntimeException re) {
               result.append("sourcenumber " + localNumber + " FAILED<br />");
            }
         }
      }
      result.append("Number of nodes unpublished = " + records);
      return result.toString();
   }

}
