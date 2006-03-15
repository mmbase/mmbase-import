package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.metadata.constraints.Constraint;
import nl.didactor.component.metadata.constraints.Error;



public class MetaVocabularyHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaVocabularyHelper.class);

   public String toString() {
      return "VOCABULARY_TYPE";
   }

   public MetaVocabularyHelper() {
   }



   public Error check(Node nodeMetaDefinition, Constraint constraint, Node nodeMetaData){

       int iRelatedMetaValues = nodeMetaData.getRelatedNodes("metavalue").size();

       if(constraint.getType() == constraint.FORBIDDEN){
           if(iRelatedMetaValues > 0){
              Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
              return error;
           }
       }

       if(constraint.getType() == constraint.MANDATORY){
           if(iRelatedMetaValues == 0){
              Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
              return error;
           }
       }

       if(constraint.getType() == constraint.LIMITED){
           if((iRelatedMetaValues <  constraint.getMin()) || (iRelatedMetaValues >  constraint.getMax())) {
              Error error = new Error(nodeMetaDefinition, Error.LIMITED, constraint);
              return error;
           }
       }

       return null;
   }






   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters){
       ArrayList arliResult = new ArrayList();

       if(constraint.getType() == constraint.FORBIDDEN){
           if((arrstrParameters.length > 1) || (!arrstrParameters[0].equals(MetaDataHelper.EMPTY_VALUE))) {
              Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
              arliResult.add(error);
              return arliResult;
           }
       }

       if(constraint.getType() == constraint.MANDATORY){
           if((arrstrParameters.length == 0) || (arrstrParameters[0].equals(MetaDataHelper.EMPTY_VALUE))) {
              Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
              arliResult.add(error);
              return arliResult;
           }
       }

       if(constraint.getType() == constraint.LIMITED){
           if((arrstrParameters.length <  constraint.getMin()) || (arrstrParameters.length >  constraint.getMax())) {
              Error error = new Error(nodeMetaDefinition, Error.LIMITED, constraint);
              arliResult.add(error);
              return arliResult;
           }
       }

       return arliResult;
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
            /*
            NodeList nl = cloud.getList(metadefNode.getStringValue("number"),
               "metadefinition,related,metavocabulary",
               "metavocabulary.number",
               "metavocabulary.number='" + arrstrParameters[f] + "'",
               null,null,null,true);
            for(int n = 0; n< nl.size(); n++) {
               String sMetavocabulary = nl.getNode(n).getStringValue("metavocabulary.number");
               Node metavocabularyNode = cloud.getNode(sMetavocabulary);
               metaDataNode.createRelation(metavocabularyNode,rm).commit();
            }
            */
           String sMetavocabulary = arrstrParameters[f];
           Node metavocabularyNode = cloud.getNode(sMetavocabulary);
           metaDataNode.createRelation(metavocabularyNode,rm).commit();

         }
      }
   }
}
