package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.metadata.constraints.Constraint;
import nl.didactor.component.metadata.constraints.Error;



public class MetaDateHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaDateHelper.class);

   public MetaDateHelper() {
   }

   public String toString() {
      return "DATE_TYPE";
   }





   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, Node nodeMetaData){
       ArrayList arliResult = new ArrayList();
       if(constraint.getType() == constraint.FORBIDDEN){
           if(isTheDateCorrect(nodeMetaData)){
               //The Date is ok, but it is forbidden
               arliResult.add(new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint));
           }
       }
       if((constraint.getType() == constraint.LIMITED) || (constraint.getType() == constraint.MANDATORY)){
           if(!isTheDateCorrect(nodeMetaData)){
               //The Date is required, but it is not ok
               arliResult.add(new Error(nodeMetaDefinition, Error.MANDATORY, constraint));
           }
       }
       return arliResult;
   }

   public ArrayList check(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters){
       ArrayList arliResult = new ArrayList();

       if(constraint.getType() == constraint.FORBIDDEN){
           if(isTheDateCorrect(arrstrParameters)){
               //The Date is ok, but it is forbidden
               Error error = new Error(nodeMetaDefinition, Error.FORBIDDEN, constraint);
               arliResult.add(error);
           }
       }
       if((constraint.getType() == constraint.LIMITED) || (constraint.getType() == constraint.MANDATORY)){
           if(!isTheDateCorrect(arrstrParameters)){
               //The Date is required, but it is not ok
               Error error = new Error(nodeMetaDefinition, Error.MANDATORY, constraint);
               arliResult.add(error);
           }
       }
       return arliResult;
   }



   /**
    * Check the corectness of the date
    * If the date is empty we suppose it is wrong
    * @param arrstrParameters String[]
    * @return boolean
    */
   private boolean isTheDateCorrect(String[] arrstrParameters){
       try{
           String sDate = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
           Date date = parseDate(sDate);
           return true;
       }
       catch(Exception e){
           return false;
       }
   }
   private boolean isTheDateCorrect(Node nodeMetaData){
       return nodeMetaData.countRelatedNodes("metadate") > 0;
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


   /**
    * Return false if the date is filled in
    *
    * Don't pay attention to any other constraints
    * @param nodeMetaDefinition Node
    * @param nodeObject Node
    * @return boolean
    */

   public boolean isEmpty(Node nodeMetaDefinition, Node nodeObject){
       Cloud cloud = nodeMetaDefinition.getCloud();

       NodeList nl = cloud.getList("" + nodeMetaDefinition.getNumber(),
                          "metadefinition,metadata,metadate,metadata,object",
                          "metadefinition.number",
                          "object.number=" + nodeObject.getNumber(),
                          null, null, null, true);

       return !(nl.size() > 0);
   }

}
