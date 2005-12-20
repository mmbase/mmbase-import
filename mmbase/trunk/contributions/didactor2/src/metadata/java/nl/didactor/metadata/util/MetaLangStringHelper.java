package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MetaLangStringHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaLangStringHelper.class);

   public String toString() {
      return "LANGSTRING_TYPE";
   }

   public MetaLangStringHelper() {
      setReason("langstring_is_required");
   }
   
   public boolean check(Cloud cloud, String sCurrentNode, String sMetadefNode, boolean isRequired) {
      return super.check(cloud, sCurrentNode, sMetadefNode, isRequired);
   }
   
   public boolean check(Cloud cloud, String[] arrstrParameters, Node metadefNode, boolean isRequired, ArrayList arliSizeErrors) {
      boolean bValid = true;
      return bValid;
   }
      
   public void copy(Cloud cloud, Node metaDataNode, Node defaultNode) {
   
      RelationManager rm = cloud.getRelationManager("posrel");
      NodeList nl = cloud.getList(defaultNode.getStringValue("number"),
         "metadata,posrel,metalangstring",
         "metalangstring.language,metalangstring.value",
         null,"posrel.pos","UP",null,true);
      for(int m=0; m< nl.size(); m++) {
         Node langStringNode = cloud.getNodeManager("metalangstring").createNode();
         langStringNode.setStringValue("language",nl.getNode(m).getStringValue("language"));
         langStringNode.setStringValue("value",nl.getNode(m).getStringValue("value"));
         langStringNode.commit();
         Relation rel = metaDataNode.createRelation(langStringNode,rm);
         rel.setIntValue("pos",m);
         rel.commit();
      }
   }

   public void set(Cloud cloud, String[] arrstrParameters, Node metaDataNode, Node metadefNode, int skipParameter) {
      boolean bNoData = true;
      NodeList nl = metaDataNode.getRelatedNodes("metalangstring");
      for(int n = 0; n < nl.size(); n++) {
         nl.getNode(n).delete(true);
         bNoData = false;
      }

      for(int f = 0; f < arrstrParameters.length ; f += 2) {
         
         if (skipParameter==f/2) {
             log.debug("MetaLangString skipping parameter " + skipParameter);
             continue;
         }
         String sLang = arrstrParameters[f];
         String sCode = arrstrParameters[f + 1];
         if ((sCode.equals("")) && (arrstrParameters.length == 2) && (bNoData)) {
            // if we have got only one parameter and it is empty, 
            // and there are no existing nodes in db then we shouldn't store this lang string
            break;
         }
         Node metaLangStringNode = cloud.getNodeManager("metalangstring").createNode();
         metaLangStringNode.setStringValue("language", sLang);
         metaLangStringNode.setStringValue("value", sCode);
         metaLangStringNode.commit();
         
         RelationManager pm = cloud.getRelationManager("posrel");
         Relation relation = metaDataNode.createRelation(metaLangStringNode,pm);
         relation.setIntValue("pos", f + 1);
         relation.commit();
      }
   }

}