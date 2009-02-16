package com.finalist.cmsc.maintenance.live;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mmbase.bridge.BridgeException;
import org.mmbase.remotepublishing.CloudInfo;
import org.mmbase.remotepublishing.CloudManager;
import org.mmbase.remotepublishing.PublishManager;

import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.sql.SqlAction;

/**
 * TODO: javadoc
 * 
 * @author Nico Klasens
 */
public class UnlinkRemotenodes extends SqlAction {

   private String stagingServer;
   private String liveServer;
   private String action;


   public UnlinkRemotenodes(String stagingServer, String liveServer, String action) {
      this.stagingServer = stagingServer;
      this.liveServer = liveServer;
      this.action = action;

   }


   /**
    * @see com.finalist.cmsc.sql.SqlAction#getSql()
    */
   @Override
   public String getSql() {
      int liveNumber = CloudManager.getCloudNumber(getCloud(), liveServer);

      return "SELECT " + getTableField("remotenodes", "*") + " FROM " + getTable("remotenodes") + " LEFT JOIN "
            + getTable("object") + " ON " + getTableField("remotenodes", "destinationnumber") + " = "
            + getTableField("object", "number") + " WHERE " + getTableField("object", "number") + " IS NULL " + "and "
            + getTableField("remotenodes", "destinationcloud") + " = " + liveNumber;
   }


   /**
    * @see com.finalist.cmsc.sql.SqlAction#process(java.sql.ResultSet)
    */
   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuffer result = new StringBuffer();
      if (ServerUtil.isLive()) {
         CloudInfo remoteCloudInfo = CloudInfo.getCloudInfoByName(stagingServer);
         // Cloud remoteCloud = CloudManager.getCloud(getCloud(),
         // stagingServer);

         int records = 0;
         while (rs.next()) {
            records++;
            int remoteNodeNumber = rs.getInt(getFieldname("number"));
            int stagingNodeNumber = rs.getInt(getFieldname("sourcenumber"));
            int liveNodeNumber = rs.getInt(getFieldname("destinationnumber"));
            result.append("Remote node " + remoteNodeNumber + " with staging " + stagingNodeNumber + " and live "
                  + liveNodeNumber + " <br />");
            if ("unlink".equals(action)) {
               PublishManager.unLinkImportedNode(liveNodeNumber);
            }
         }
         result.append("Number of nodes unlinked = " + records);
      }
      else {
         result.append("ERROR: This server is configured as staging.");
      }
      return result.toString();
   }

}
