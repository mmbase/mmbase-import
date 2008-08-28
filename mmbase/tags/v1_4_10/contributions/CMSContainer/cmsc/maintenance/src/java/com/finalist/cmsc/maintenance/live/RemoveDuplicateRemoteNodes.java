package com.finalist.cmsc.maintenance.live;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.Cloud;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.mmbase.bridge.Node;
import org.mmbase.remotepublishing.CloudManager;
import org.mmbase.remotepublishing.PublishManager;

import com.finalist.cmsc.sql.SqlAction;

/**
 * TODO: javadoc
 * 
 * @author Nico Klasens
 * @author Hillebrand Gelderblom
 */
public class RemoveDuplicateRemoteNodes extends SqlAction {

   private String cloudname;


   public RemoveDuplicateRemoteNodes(String cloudname) {
      this.cloudname = cloudname;
   }


   @Override
   public String getSql() {
      return "    SELECT MIN(a." + getFieldname("destinationnumber") + ") AS number" + "      FROM "
            + getTable("remotenodes") + " a, " + getTable("remotenodes") + " b" + "     WHERE a."
            + getFieldname("sourcenumber") + " = b." + getFieldname("sourcenumber") + "       AND a."
            + getFieldname("number") + " <> b." + getFieldname("number") + "  GROUP BY a."
            + getFieldname("sourcenumber");
   }


   @Override
   public String process(ResultSet rs) throws BridgeException, SQLException {
      Cloud liveCloud = CloudManager.getCloud(getCloud(), cloudname);

      StringBuffer result = new StringBuffer();
      int records = 0;

      while (rs.next()) {
         records++;

         int nodeNumber = rs.getInt(getFieldname("number"));
         Node duplicateNode = liveCloud.getNode(nodeNumber);
         result.append("Node " + nodeNumber + " removed from live cloud.<br />");
         PublishManager.unLinkImportedNode(duplicateNode.getNumber());
         duplicateNode.delete();
      }

      result.append("Number of nodes deleted = " + records);

      return result.toString();
   }
}
