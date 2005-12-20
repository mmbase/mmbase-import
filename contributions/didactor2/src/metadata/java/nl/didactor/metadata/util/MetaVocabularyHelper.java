package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MetaVocabularyHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaVocabularyHelper.class);

   public String toString() {
      return "VOCABULARY_TYPE";
   }
   
   public MetaVocabularyHelper() {
      setReason("number_of_vocabularies_should_match_min_max");
   }

   public boolean check(Cloud cloud, String sCurrentNode, String sMetadefNode, boolean isRequired) {
      Node metadefNode =  cloud.getNode(sMetadefNode);
      int iMin = getMin(metadefNode, isRequired);
      int iMax = getMax(metadefNode);
      int iCounter = sizeOfRelatedMetaValue(cloud,sCurrentNode,sMetadefNode);
      boolean bValid = (iCounter >= iMin) && (iCounter <= iMax);
      if(!bValid) {
         log.debug(sCurrentNode + " has range ["  + iMin + "," + iMax + "] but " + iCounter + " metavocabularies for metadefinition " + sMetadefNode);
      }
      return bValid;
   }
   
   public boolean check(Cloud cloud, String[] arrstrParameters, Node metadefNode, boolean isRequired, ArrayList arliSizeErrors) {
      boolean bValid = true;
      int iMin = getMin(metadefNode,isRequired);
      int iMax = getMax(metadefNode);
      if ((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(MetaDataHelper.EMPTY_VALUE))) {
         
         if( (iMax < arrstrParameters.length) || (iMin > arrstrParameters.length)) {
            
            bValid = false;
         }
      } else if(iMin>0) {
         
         bValid = false;
      }
      if(!bValid) {
         arliSizeErrors.add(metadefNode.getStringValue("number"));
         log.debug("For " + metadefNode.getStringValue("name") + " at least " 
            + iMin + " and at most " + iMax + " values have to be selected.");
      }
      return bValid;
   }
   
   public void copy(Cloud cloud, Node metaDataNode, Node defaultNode) {
      
      RelationManager rm = cloud.getRelationManager("posrel");
      NodeList nl = defaultNode.getRelatedNodes("metavocabulary");
      for(int m = 0; m< nl.size(); m++) {
         Node metavocabularyNode = nl.getNode(m);
         metaDataNode.createRelation(metavocabularyNode,rm).commit();
      }
   }

   public void set(Cloud cloud, String[] arrstrParameters, Node metaDataNode, Node metadefNode, int skipParameter) {
   
      RelationManager rm = cloud.getRelationManager("posrel");
      RelationList rl = metaDataNode.getRelations("posrel","metavocabulary");
      for(int r = 0; r< rl.size(); r++) {
         Relation relation = rl.getRelation(r);
         relation.delete();
      }
      if ((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(MetaDataHelper.EMPTY_VALUE)))
      {
         for(int f = 0; f < arrstrParameters.length; f++)
         {
            NodeList nl = cloud.getList(metadefNode.getStringValue("number"),
               "metadefinition,related,metavocabulary",
               "metavocabulary.number",
               "metavocabulary.value='" + arrstrParameters[f] + "'",
               null,null,null,true);
            for(int n = 0; n< nl.size(); n++) {
               String sMetavocabulary = nl.getNode(n).getStringValue("metavocabulary.number");
               Node metavocabularyNode = cloud.getNode(sMetavocabulary);
               metaDataNode.createRelation(metavocabularyNode,rm).commit();
            }
         }
      }
   }

}