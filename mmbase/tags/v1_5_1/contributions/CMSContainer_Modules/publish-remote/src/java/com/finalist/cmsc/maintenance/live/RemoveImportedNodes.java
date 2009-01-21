/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.maintenance.live;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.mmbase.bridge.*;
import org.mmbase.remotepublishing.CloudInfo;
import org.mmbase.remotepublishing.PublishManager;

import com.finalist.cmsc.mmbase.TypeUtil;
import com.finalist.cmsc.navigation.ServerUtil;
import com.finalist.cmsc.sql.SqlAction;

/**
 * Removes suspected imported nodes which are not in the remotenodes table
 * anymore
 */
public class RemoveImportedNodes extends SqlAction {

   private String table;
   private String action;


   public RemoveImportedNodes(String table, String action) {
      if (TypeUtil.isSystemType(table)) {
         throw new IllegalArgumentException("Nodemanager is a system type and never imported to this cloud");
      }

      this.table = table;
      this.action = action;
   }


   @Override
   public String getSql() {
      return "SELECT " + getTableField(table, "*") + " FROM " + getTable(table) + " LEFT JOIN "
            + getTable("remotenodes") + " ON " + getTableField(table, "number") + " = "
            + getTableField("remotenodes", "destinationnumber") + " WHERE "
            + getTableField("remotenodes", "destinationnumber") + " IS NULL ";
   }


   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      StringBuffer result = new StringBuffer();
      if (ServerUtil.isLive()) {
         Cloud localCloud = getCloud();
         CloudInfo localCloudInfo = CloudInfo.getCloudInfo(getCloud());
         // Cloud remoteCloud = CloudManager.getCloud(localCloud,
         // stagingServer);

         int records = 0;
         while (rs.next()) {
            int nodeNumber = rs.getInt(getFieldname("number"));
            Node node = localCloud.getNode(nodeNumber);

            if (node.isRelation()) {
               Relation rel = node.toRelation();
               Node source = rel.getSource();
               Node dest = rel.getDestination();
               if (!PublishManager.isImported(source) || !PublishManager.isImported(dest)) {
                  continue;
               }
            }

            records++;

            RelationList relations = node.getRelations();
            for (Iterator<Relation> iter = relations.iterator(); iter.hasNext();) {
               Relation rel = iter.next();
               if (PublishManager.isImported(localCloudInfo, rel)) {
                  if ("remove".equals(action)) {
                     PublishManager.unLinkImportedNode(rel.getNumber());
                  }
                  result.append("Unlink relation " + rel.getNumber() + " with source " + rel.getIntValue("snumber")
                        + " and destination " + rel.getIntValue("dnumber") + " <br />");
               }
               if ("remove".equals(action)) {
                  rel.delete(false);
               }
               result.append("Removed relation " + rel.getNumber() + "(" + rel.getNodeManager().getName() + ")");
            }
            if ("remove".equals(action)) {
               node.delete(false);
            }
            result.append("Removed relation " + node.getNumber() + "(" + node.getNodeManager().getName() + ")");
         }
         result.append("Number of nodes removed = " + records);
      }
      else {
         result.append("ERROR: This server is configured as staging.");
      }
      return result.toString();
   }

}
