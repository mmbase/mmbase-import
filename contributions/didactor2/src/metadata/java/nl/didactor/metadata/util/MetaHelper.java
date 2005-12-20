package nl.didactor.metadata.util;

import java.util.*;
import java.text.SimpleDateFormat;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaHelper.class);
   private SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");
        
   private String sReason;

   public MetaHelper() {
   }

   public String getType() {
      return "NOT_SUPPORTED_TYPE";
   }

   public Date parseDate(String dateString) throws java.text.ParseException  {
      return df.parse(dateString);
   }
   
   public void createDate(Cloud cloud, Node metadataNode, String sDate, int pos) throws java.text.ParseException {
      
      Date date = parseDate(sDate);
      
      Node metadateNode = cloud.getNodeManager("metadate").createNode();
      metadateNode.setLongValue("value", date.getTime() / 1000);
      metadateNode.commit();
      
      RelationManager pm = cloud.getRelationManager("posrel");
      Relation relation = metadataNode.createRelation(metadateNode,pm);
      relation.setIntValue("pos", pos);
      relation.commit();
   }

   
   public int getMin(Node metadefNode, boolean isRequired) {
      int iMin = metadefNode.getIntValue("minvalues");
      if(iMin==-1) { iMin = 0; }
      if(isRequired) { iMin = 1; }
      return iMin;
   }

   public int getMax(Node metadefNode) {
      int iMax = metadefNode.getIntValue("maxvalues");
      if(iMax==-1) { iMax = 9999; }
      return iMax;
   }

   public void setReason(String sReason) {
      this.sReason = sReason;
   }
   
   public String getReason() {
      return sReason;   
   }
   
   public Node createMetaDataNode(Cloud cloud, Node currentNode, Node metadefNode) {
      Node metaDataNode = cloud.getNodeManager("metadata").createNode();
      metaDataNode.commit();
      RelationManager rm = cloud.getRelationManager("related");
      currentNode.createRelation(metaDataNode,rm).commit();
      metaDataNode.createRelation(metadefNode,rm).commit();
      return metaDataNode;
   }
   
   public NodeList getRelatedMetaData(Cloud cloud, String sCurrentNode, String sMetadefNode) {
      
      String sNodeManager = cloud.getNode(sCurrentNode).getNodeManager().getName();
      NodeList nl = cloud.getList(sCurrentNode,
         sNodeManager + ",metadata,metadefinition",
         "metadata.number,metadefinition.minvalues,metadefinition.maxvalues",
         "metadefinition.number='" + sMetadefNode + "'",
         null,null,null,false);
      return nl;
   }
      
   public int sizeOfRelatedMetaValue(Cloud cloud, String sCurrentNode, String sMetadefNode) {
      NodeList nl = getRelatedMetaData(cloud, sCurrentNode, sMetadefNode);
      int iCounter = 0;
      for(int m = 0; m<nl.size(); m++) {
         String sMetadataNumber = nl.getNode(m).getStringValue("metadata.number");
         iCounter += cloud.getNode(sMetadataNumber).getRelatedNodes("metavalue").size();
      }
      return iCounter;
   }

   public boolean hasRelatedMetaData(Cloud cloud, String sCurrentNode, String sMetadefNode) {
      return (sizeOfRelatedMetaValue(cloud, sCurrentNode, sMetadefNode)>0);
   }
  
   public boolean check(Cloud cloud, String sCurrentNode, String sMetadefNode, boolean isRequired) {
      boolean bValid = true;
      if(isRequired) {
         bValid = hasRelatedMetaData(cloud,sCurrentNode,sMetadefNode);
      }
      return bValid;
   }

   
   public boolean check(Cloud cloud, String[] arrstrParameters, Node metadefNode, boolean isRequired, ArrayList arliSizeErrors) {
      return true;
   }
   
   public void copy(Cloud cloud, Node metaDataNode, Node defaultNode) {
   }

   public void set(Cloud cloud, String[] arrstrParameters, Node metaDataNode, Node metadefNode, int skipParameter) {
   }

}