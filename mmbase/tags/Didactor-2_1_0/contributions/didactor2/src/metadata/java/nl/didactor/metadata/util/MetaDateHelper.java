package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MetaDateHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaDateHelper.class);

   public MetaDateHelper() {
      setReason("date_is_required");
   }

   public String toString() {
      return "DATE_TYPE";
   }

   public boolean check(Cloud cloud, String sCurrentNode, String sMetadefNode, boolean isRequired) {
      return super.check(cloud, sCurrentNode, sMetadefNode, isRequired);
   }
   
   public boolean check(Cloud cloud, String[] arrstrParameters, Node metadefNode,  boolean isRequired, ArrayList arliSizeErrors) {
      boolean bValid = true;
      try
      {
         String sDate = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
         Date date = parseDate(sDate);
      }
      catch(Exception e)
      {
         if(isRequired) {
            bValid = false;
         }
      } 
      return bValid;
   }
     
   public void copy(Cloud cloud, Node metaDataNode, Node defaultNode) {
      
      RelationManager rm = cloud.getRelationManager("posrel");
      NodeList nl = defaultNode.getRelatedNodes("metadate");
      for(int m = 0; m< nl.size(); m++) {
         Node dateNode = cloud.getNodeManager("metadate").createNode();
         dateNode.setStringValue("value",nl.getNode(m).getStringValue("value"));
         dateNode.commit();
         metaDataNode.createRelation(dateNode,rm).commit();
      }
   }

   public void set(Cloud cloud, String[] arrstrParameters, Node metaDataNode, Node metadefNode, int skipParameter) {
   
      NodeList nl = metaDataNode.getRelatedNodes("metadate");
      for(int n = 0; n < nl.size(); n++) {
         nl.getNode(n).delete(true);
      }
      String sDate = "";
      try
      {
         sDate = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
         createDate(cloud, metaDataNode, sDate, 1);
      }
      catch(Exception e)
      {
         log.error("'" + sDate + "' can not be used to set date for metadata " 
            + metaDataNode.getStringValue("number") 
            + " and metadefinition " 
            + metadefNode.getStringValue("number"));
      }
   }

}