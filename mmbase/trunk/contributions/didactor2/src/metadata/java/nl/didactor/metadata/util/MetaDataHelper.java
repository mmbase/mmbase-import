package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MetaDataHelper {

   private static Logger log = Logging.getLoggerInstance(MetaDataHelper.class);

   public static String sVocabularyType = "1";
   public static String sDateType = "2";
   public static String sLangStringType = "3";
   public static String sDurationType = "4";

   public MetaDataHelper() {
   }

   public NodeList getRelatedMetaData(Cloud cloud, String sCurrentNode) {
      
      return cloud.getNode(sCurrentNode).getRelatedNodes("metadata");
   }


   public NodeList getRelatedMetaData(Cloud cloud, String sCurrentNode, String sMetadefNumber) {
      
      String sNodeManager = cloud.getNode(sCurrentNode).getNodeManager().getName();
      NodeList nl = cloud.getList(sCurrentNode,
         sNodeManager + ",metadata,metadefinition",
         "metadata.number,metadefinition.minvalues,metadefinition.maxvalues",
         "metadefinition.number='" + sMetadefNumber + "'",
         null,null,null,false);
      return nl;
   }
      
   public boolean hasRelatedMetaData(Cloud cloud, String sCurrentNode, String sMetadefNumber) {
      return (getRelatedMetaData(cloud, sCurrentNode, sMetadefNumber).size()>0);
   }

   public int sizeOfRelatedMetaValue(Cloud cloud, String sCurrentNode, String sMetadefNumber) {
      NodeList nl = getRelatedMetaData(cloud, sCurrentNode, sMetadefNumber);
      int iCounter = 0;
      for(int m = 0; m<nl.size(); m++) {
         String sMetadataNumber = nl.getNode(m).getStringValue("metadata.number");
         iCounter += cloud.getNode(sMetadataNumber).getRelatedNodes("metavalue").size();
      }
      return iCounter;
   }

   public boolean fitsMinMax(Cloud cloud, String sCurrentNode, String sMetadefNumber, boolean isRequired) {
      int iMin = cloud.getNode(sMetadefNumber).getIntValue("metadefinition.minvalues");
      if(iMin==-1) { iMin = 0; }
      if(isRequired) { iMin = 1; }
      int iMax = cloud.getNode(sMetadefNumber).getIntValue("metadefinition.maxvalues");
      if(iMax==-1) { iMax = 9999; }
      int iCounter = sizeOfRelatedMetaValue(cloud,sCurrentNode,sMetadefNumber);
      boolean bValid = (iCounter >= iMin) && (iCounter <= iMax);
      if(!bValid) {
         log.info(sCurrentNode + " has range ["  + iMin + "," + iMax + "] but " + iCounter + " metavocabularies for metadefinition " + sMetadefNumber);
      }
      return bValid;
   }
 
   public boolean hasRelatedLangString(Cloud cloud, String sCurrentNode, String sMetadefNumber) {
      return sizeOfRelatedMetaValue(cloud,sCurrentNode,sMetadefNumber)>0;
   }

   public boolean hasValidMetadata(Cloud cloud, String sCurrentNode) {
      boolean bValid = true;
   
      NodeList nl = cloud.getList(null,
         "metastandard,posrel,metadefinition",
         "metadefinition.number,metadefinition.type,metadefinition.required",
         "metastandard.isused='1'",
         null,null,null,false);
         
      for(int m =0; m < nl.size(); m++) {
         
         if(bValid) { // Metadata is still valid
      
            String sType = nl.getNode(m).getStringValue("metadefinition.type");
            boolean isRequired = nl.getNode(m).getStringValue("metadefinition.required").equals("1");
            String sMetadefNumber = nl.getNode(m).getStringValue("metadefinition.number");
            String reason = "";
            
            if(sType.equals(sVocabularyType)) { // *** Vocabulary
               
               bValid = fitsMinMax(cloud,sCurrentNode,sMetadefNumber,isRequired);
               reason = "number of vocabularies does not fit [min,max]";
             
            } else if(sType.equals(sDateType)) { // *** Date
               
               if(isRequired) {
                  bValid = hasRelatedMetaData(cloud,sCurrentNode,sMetadefNumber);
               }
               reason = "no related date";
                           
            } else if(sType.equals("3")) { // *** Langstrings
               
               if(isRequired) { // *** Required
                  bValid = hasRelatedLangString(cloud,sCurrentNode,sMetadefNumber);
               }
               reason = "no related langstring";
               
            } else if(sType.equals("4")) { // *** Duration
            
               if(isRequired) { // *** Required
                  bValid = hasRelatedMetaData(cloud,sCurrentNode,sMetadefNumber);
               }
               reason = "no related duration";
            }
            if(!bValid) {
               log.info(sCurrentNode + " has invalid metadata, because there is "  + reason + " for metadefinition " + sMetadefNumber); 
            }
         }
      }
      return bValid;
   }
   
   public NodeList getLangCodes(Cloud cloud) {
   
      NodeList nl = cloud.getList(null,
         "metastandard,metadefinition,metavocabulary",
         "metavocabulary.value",
         "metadefinition.handler='taal'",
         "metavocabulary.value","UP",null,true);
      return nl;
   }
 
}