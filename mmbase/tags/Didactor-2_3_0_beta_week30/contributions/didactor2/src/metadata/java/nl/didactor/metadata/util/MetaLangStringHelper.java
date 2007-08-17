package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.metadata.constraints.Constraint;
import nl.didactor.component.metadata.constraints.Error;

public class MetaLangStringHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaLangStringHelper.class);

   public String toString() {
      return "LANGSTRING_TYPE";
   }

   public MetaLangStringHelper() {
   }


   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, Node nodeMetaData){
       return new ArrayList();
   }

   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters){
       return new ArrayList();
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



   /**
    * Adds a new LangString node to the metadata object
    *
    * @param nodeObject Node
    * @param sValue String
    * @param sLanguage String
    * @return Node
    */

   public static Node addNewLangString(Node nodeMetaData, String sValue, String sLanguage, int iPos){
      Node nodeResult = nodeMetaData.getCloud().getNodeManager("metalangstring").createNode();
      nodeResult.setStringValue("value", sValue);
      nodeResult.setStringValue("language", sLanguage);
      nodeResult.commit();

      RelationManager rmPosrel = nodeMetaData.getCloud().getRelationManager("posrel");
      Relation relation = nodeMetaData.createRelation(nodeResult, rmPosrel);
      relation.setIntValue("pos", iPos);
      relation.commit();

      return nodeResult;
   }

   /**
    * Restriction by exactly one langstring
    * If there are more than one only first will be kept
    * If there is no langsrings only one will be crated
    *
    * @param nodeMetaData Node Metadata node
    * @return Node The only LangString for this metadata node
    */
   public static Node doOneLangString(Node nodeMetaData){
       Node nodeResultLangString = null;
       if (nodeMetaData.countRelatedNodes("metalangstring") > 1) {
           //Too many langstring, let's delete all but one
           NodeList nlLangStrings = nodeMetaData.getRelatedNodes("metalangstring");

           Iterator it = nlLangStrings.iterator();
           nodeResultLangString = (Node) it.next();
           for (; it.hasNext(); ) {
               Node nodeLangString = (Node) it.next();
               nodeLangString.delete(true);
           }
       }

       if (nodeMetaData.countRelatedNodes("metalangstring") == 0) {
           //there is no lang strings, let's add one
           nodeResultLangString = MetaLangStringHelper.addNewLangString(nodeMetaData, "", "", -1);
           nodeResultLangString.commit();
       }

       if (nodeMetaData.countRelatedNodes("metalangstring") == 1) {
           //That's fine. There is one exactly.
           nodeResultLangString = nodeMetaData.getRelatedNodes("metalangstring").getNode(0);
       }

       return nodeResultLangString;
   }


   public boolean isEmpty(Node nodeMetaDefinition, Node nodeOBject){
       return false;
   }

}
