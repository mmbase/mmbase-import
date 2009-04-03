package nl.didactor.metadata.util;

import java.util.*;
import java.text.SimpleDateFormat;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.metadata.constraints.Constraint;
import nl.didactor.component.metadata.constraints.Error;



public abstract class MetaHelper {

   private static Logger log = Logging.getLoggerInstance(MetaHelper.class);
   private SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy|hh:mm");




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




   public static Node createMetaDataNode(Cloud cloud, Node currentNode, Node metadefNode) {
      Node metaDataNode = cloud.getNodeManager("metadata").createNode();
      metaDataNode.commit();
      RelationManager rm = cloud.getRelationManager("related");
      currentNode.createRelation(metaDataNode,rm).commit();
      metaDataNode.createRelation(metadefNode,rm).commit();
      return metaDataNode;
   }



   public static NodeList getRelatedMetaData(Cloud cloud, String sCurrentNode, String sMetadefNode) {
      NodeList nl = cloud.getList(sCurrentNode,
         "object,metadata,metadefinition",
         "metadata.number",
         "metadefinition.number='" + sMetadefNode + "'",
         null,null,null,false);
      return nl;
   }



   /**
    * Template for different helpers.
    * They extends this class.
    *
    * @param nodeMetaDefinition Node
    * @param constraint Constraint
    * @param arrstrParameters String[]
    * @return ArrayList
    */
   public abstract ArrayList check(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters);



   /**
    * Checks only one concrete metadata
    * @param nodeMetaDefinition Node
    * @param constraint Constraint
    * @param nodeMetaData Node
    * @return Error
    */
   public abstract ArrayList check(Node nodeMetaDefinition, Constraint constraint, Node nodeMetaData);



   /**
    * Are there any filled-in values?
    *
    * It doesn't pay attention to any constraints
    * @param nodeMetaDefinition Node
    * @param nodeObject Node
    * @return boolean
    */

   public abstract boolean isEmpty(Node nodeMetaDefinition, Node nodeObject);



   public abstract void copy(Cloud cloud, Node metaDataNode, Node defaultNode);

   public abstract void set(Cloud cloud, String[] arrstrParameters, Node metaDataNode, Node metadefNode, int skipParameter);






   /**
    * Get constraint relation for any two object in base
    * if there is no constraint relation it returns null
    *
    * @param nodeObject1 Node
    * @param nodeObject2 Node
    * @return Node
    */
   public Node getConstraintRelation(Node nodeParent, Node nodeChild){
       Node nodeResult = null;

       NodeList nlNodes = nodeParent.getCloud().getList("" + nodeParent.getNumber(),
          "object1,constraints,object2",
          "constraints.number",
          "object2.number='" + nodeChild.getNumber() + "'",
          null,null,null,false);

       try{
           nodeResult = nodeParent.getCloud().getNode(nlNodes.getNode(0).getStringValue("constraints.number"));
       }
       catch(Exception e){
       }
       return nodeResult;
   }




   /**
    * Gets parent metastnadart for metadefinition
    * Add a message to log if the metadefinition has got no parents
    * @param nodeMetaDefinition Node
    * @return Node
    */
   private Node getMetaStandart(Node nodeMetaDefinition){
       NodeList nlMetaStandarts = nodeMetaDefinition.getCloud().getList("" + nodeMetaDefinition.getNumber(),
          "metadefinition,metastandard",
          "metastandard.number",
          null,
          null,null,"source",false);


      Node nodeMetaStandart = null;
      try{
          nodeMetaStandart = nodeMetaDefinition.getCloud().getNode(nlMetaStandarts.getNode(0).getStringValue("metastandard.number"));
      }
      catch(Exception e){
          log.error("Metadefinition with id=" + nodeMetaDefinition.getNumber() + " has got no parents(no metastandart node is related)");
      }

      return nodeMetaStandart;
   }

}
