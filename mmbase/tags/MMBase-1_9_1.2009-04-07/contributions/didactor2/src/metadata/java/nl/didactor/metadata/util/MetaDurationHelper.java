package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.metadata.constraints.Constraint;
import nl.didactor.component.metadata.constraints.Error;


public class MetaDurationHelper extends MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaDurationHelper.class);

   public MetaDurationHelper() {
   }

   public String toString() {
      return "DURATION_TYPE";
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
    * Check the corectness of the duration
    * If the date is empty we suppose it is wrong
    * @param arrstrParameters String[]
    * @return boolean
    */
   private boolean isTheDateCorrect(String[] arrstrParameters){
       try{
           String sDateBegin = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
           String sDateEnd   = arrstrParameters[5] + "-" + arrstrParameters[6] + "-" + arrstrParameters[7] + "|" + arrstrParameters[8] + ":" + arrstrParameters[9];
           parseDate(sDateBegin);
           parseDate(sDateEnd);
           return true;
       }
       catch(Exception e){
           return false;
       }
   }
   private boolean isTheDateCorrect(Node nodeMetaData){
       return nodeMetaData.countRelatedNodes("metadate") > 1;
   }






   public void copy(Cloud cloud, Node metaDataNode, Node defaultNode) {

      RelationManager rm = cloud.getRelationManager("posrel");
      NodeList nl = cloud.getList(defaultNode.getStringValue("number"),
         "metadata,posrel,metadate",
         "metalangstring.language,metalangstring.value",
         null,"posrel.pos","UP",null,true);
      if(nl.size()!=2) {
         log.error(defaultNode.getStringValue("number") + " is not related to two metadates");
      } else {
         for(int m=0; m< nl.size(); m++) {
            Node dateNode = cloud.getNodeManager("metadate").createNode();
            dateNode.setStringValue("value",nl.getNode(m).getStringValue("value"));
            dateNode.commit();
            Relation rel = metaDataNode.createRelation(dateNode,rm);
            rel.setIntValue("pos",m);
            rel.commit();
         }
      }
   }

   public void set(Cloud cloud, String[] arrstrParameters, Node metaDataNode, Node metadefNode, int skipParameter) {
      boolean bNotEmpty = false;
      NodeList nl = metaDataNode.getRelatedNodes("metadate");
      for(int n = 0; n < nl.size(); n++) {
         nl.getNode(n).delete(true);
      }
      String sDateBegin = "";
      String sDateEnd = "";
      try
      {
         sDateBegin = arrstrParameters[0] + "-" + arrstrParameters[1] + "-" + arrstrParameters[2] + "|" + arrstrParameters[3] + ":" + arrstrParameters[4];
         sDateEnd   = arrstrParameters[5] + "-" + arrstrParameters[6] + "-" + arrstrParameters[7] + "|" + arrstrParameters[8] + ":" + arrstrParameters[9];

         createDate(cloud, metaDataNode, sDateBegin, 1);
         createDate(cloud, metaDataNode, sDateEnd, 2);
      }
      catch(Exception e)
      {
         log.error("'" + sDateBegin + "' '"+ sDateEnd + "' can not be used to set duration for metadata "
            + metaDataNode.getStringValue("number")
            + " and metadefinition "
            + metadefNode.getStringValue("number"));
      }
   }



   /**
    * Return false if the duration is filled in
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

       return !(nl.size() == 2);
   }

}
