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



   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, Node nodeMetaData){

       ArrayList arliResult = new ArrayList();

       int iRelatedMetaValues = nodeMetaData.getRelatedNodes("metavocabulary").size();

       if(constraint.getEvent() == constraint.EVENT_VOCABULARY_TO_VOCABULARY_RELATION){
           arliResult.addAll(vocabularyToVocabulary_ConstraintProcessor(nodeMetaDefinition, nodeMetaData, constraint));
       }
       else{

           if(constraint.getType() == constraint.FORBIDDEN){
               if(iRelatedMetaValues > 0){
                   Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
                   arliResult.add(error);
               }
           }

           if(constraint.getType() == constraint.MANDATORY){
               if(iRelatedMetaValues == 0){
                   Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
                   arliResult.add(error);
               }
           }

           if(constraint.getType() == constraint.LIMITED){
               if((iRelatedMetaValues < constraint.getMin()) || (iRelatedMetaValues > constraint.getMax())){
                   Error error = new Error(nodeMetaDefinition, Error.LIMITED, constraint);
                   arliResult.add(error);
               }
           }
       }


       if(constraint.getConstraintsChain() != null){
           ArrayList arliConstraintsChain = constraint.getConstraintsChain();
           for(Iterator it = arliConstraintsChain.iterator(); it.hasNext(); ){
               Constraint chainConstraint = (Constraint) it.next();
               arliResult.addAll(check(nodeMetaDefinition, chainConstraint, nodeMetaData));
           }
       }

       return arliResult;
   }






   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters){
       ArrayList arliResult = new ArrayList();

       if(constraint.getEvent() == constraint.EVENT_VOCABULARY_TO_VOCABULARY_RELATION){
           arliResult.addAll(vocabularyToVocabulary_ConstraintProcessor(nodeMetaDefinition, constraint, arrstrParameters));
       }
       else{
           if(constraint.getType() == constraint.FORBIDDEN){
               if((arrstrParameters.length != 0) && (!arrstrParameters[0].equals(MetaDataHelper.EMPTY_VALUE))){
                   Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
                   arliResult.add(error);
               }
           }

           if(constraint.getType() == constraint.MANDATORY){
               if((arrstrParameters.length == 0) || (arrstrParameters[0].equals(MetaDataHelper.EMPTY_VALUE))){
                   Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
                   arliResult.add(error);
               }
           }

           if(constraint.getType() == constraint.LIMITED){
               if((arrstrParameters.length < constraint.getMin()) || (arrstrParameters.length > constraint.getMax())){
                   Error error = new Error(nodeMetaDefinition, Error.LIMITED, constraint);
                   arliResult.add(error);
               }
           }
       }

       if(constraint.getConstraintsChain() != null){
           ArrayList arliConstraintsChain = constraint.getConstraintsChain();
           for(Iterator it = arliConstraintsChain.iterator(); it.hasNext(); ){
               Constraint chainConstraint = (Constraint) it.next();
               arliResult.addAll(check(nodeMetaDefinition, chainConstraint, arrstrParameters));
           }
       }

       return arliResult;
   }





   /**
    * Do all job about vocabulary - vocabulary constraint
    * @param nodeMetaDefinition Node
    * @param constraint Constraint
    * @param arrstrParameters String[]
    * @return ArrayList
    */
   private ArrayList vocabularyToVocabulary_ConstraintProcessor(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters){
       ArrayList arliResult = new ArrayList();

       Node[] value = (Node[]) constraint.getEventObject();

       if(constraint.getType() == Constraint.FORBIDDEN){
           if(isParametersContainNode(arrstrParameters, (Node) value[0])){
               Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
               arliResult.add(error);
           }
       }
       if((constraint.getType() == Constraint.MANDATORY) || (constraint.getType() == Constraint.LIMITED)){
           if(!isParametersContainNode(arrstrParameters, (Node) value[0])){
               Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
               arliResult.add(error);
           }
       }

       return arliResult;
   }


   private ArrayList vocabularyToVocabulary_ConstraintProcessor(Node nodeMetaDefinition, Node nodeMetaData, Constraint constraint){
       ArrayList arliResult = new ArrayList();

       Node[] value = (Node[]) constraint.getEventObject();

       if(constraint.getType() == Constraint.FORBIDDEN){
           if(isVocabularyLinked(nodeMetaData, (Node) value[0])){
               Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
               arliResult.add(error);
           }
       }
       if((constraint.getType() == Constraint.MANDATORY) || (constraint.getType() == Constraint.LIMITED)){
           if(!isVocabularyLinked(nodeMetaData, (Node) value[0])){
               Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
               arliResult.add(error);
           }
       }

       return arliResult;
   }




   /**
    * Looks for the specific node in the parameters list
    * @param arrstrParameters String[]
    * @param node Node
    * @return boolean
    */
   private boolean isParametersContainNode(String[] arrstrParameters, Node node){
       for(int f = 0; f < arrstrParameters.length; f++){
           if (arrstrParameters[f].equals("" + node.getNumber())){
               return true;
           }
       }
       return false;
   }

   private boolean isVocabularyLinked(Node nodeMetaData, Node nodeMetaVocabulary){
      return nodeMetaData.getRelatedNodes("metavocabulary").contains(nodeMetaVocabulary);
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




   /**
    * Return false if there are any selected metavocabularies
    *
    * Don't pay attention to any other constraints
    * @param nodeMetaDefinition Node
    * @param nodeObject Node
    * @return boolean
    */

   public boolean isEmpty(Node nodeMetaDefinition, Node nodeObject){
       log.debug("isEmpty() for MetaDefinition=" + nodeMetaDefinition.getNumber() + ", Object=" + nodeObject.getNumber());
       Cloud cloud = nodeMetaDefinition.getCloud();

       NodeList nl = cloud.getList("" + nodeMetaDefinition.getNumber(),
                          "metadefinition,metavocabulary,metadata,object",
                          "metadefinition.number",
                          "object.number=" + nodeObject.getNumber(),
                          null, null, null, true);


       return !(nl.size() > 0);
   }
}
