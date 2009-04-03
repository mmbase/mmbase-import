package nl.didactor.component.scorm.metastandart.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.*;


public class Filter
{
   private Cloud cloud;
   private NodeManager nmMetaStandart = null;
   private NodeManager nmMetaDefinition = null;
   private NodeManager nmPosrel = null;
   private int iCounter;


   public Filter(Cloud cloud)
   {
      this.cloud = cloud;
      nmMetaStandart   = cloud.getNodeManager("metastandard");
      nmMetaDefinition = cloud.getNodeManager("metadefinition");
      nmPosrel         = cloud.getNodeManager("posrel");
   }




   public void process(Node nodeMetaStandart)
   {
      //The first step - empty Metastandart Chains like this
      //MetaStandart1 -> MetaStandart2 -> MetaStandart3
      this.cleanMetaStandartChains(nodeMetaStandart);


      //The second step - a MetaStandart can't have only a single MetaDefinition
      //Such Metastandarts should be removed and Metadefinitions should be
      //connected to the parent metastandart node
      while(this.cleanMetastandartsWithOnlyOneMetadefinition(nodeMetaStandart));
   }





   private void cleanMetaStandartChains(Node nodeMetaStandart)
   {
      //We don't want to run optimizer for elements of the first level
      List nlMetaStandarts = this.getRelatedOrderedMetaStandarts(nodeMetaStandart);

      for(Iterator it = nlMetaStandarts.iterator(); it.hasNext();)
      {
         Node nodeRelatedMetaStandart = (Node) it.next();
         while (this.cleanMetaStandartChainsProcessOneMetastandart(nodeRelatedMetaStandart));
      }
      for(Iterator it = nlMetaStandarts.iterator(); it.hasNext();)
      {
         Node nodeRelatedMetaStandart = (Node) it.next();
//         System.out.println(nodeRelatedMetaStandart.getValue("name"));
         cleanMetaStandartChains(nodeRelatedMetaStandart);
      }

   }

   private boolean cleanMetaStandartChainsProcessOneMetastandart(Node nodeMetaStandart)
   {
//      System.out.println(nodeMetaStandart.getValue("name"));
      List nlMetaStandarts = this.getRelatedOrderedMetaStandarts(nodeMetaStandart);
      for(Iterator it = nlMetaStandarts.iterator(); it.hasNext();)
      {
         Node nodeRelatedMetaStandart = (Node) it.next();

         //Is it possible to delete this MetaStandart?
         NodeList nlTestedChildMetaStandarts   = nodeRelatedMetaStandart.getRelatedNodes(nmMetaStandart, "posrel", "destination");
         NodeList nlTestedChildMetaDefinitions = nodeRelatedMetaStandart.getRelatedNodes(nmMetaDefinition, "posrel", "destination");


         if((nlTestedChildMetaStandarts.size() == 1) && (nlTestedChildMetaDefinitions.size() == 0))
         {//This MetaStandart has only one "child" MetaStandart and no Definitions, so it can be removed
            Node nodeThisSingleMetaStandart = (Node) nlTestedChildMetaStandarts.get(0);

            //We can't simple rename the element
            //If it has other parents they will suffer from renaming
            //So are going to perform a small check
            //If there is only one parent we can rename the element
            //If there are many parent we will make a copy of element
            NodeList nlThisSingleMetaStandartRelations = nodeThisSingleMetaStandart.getRelatedNodes(nmMetaStandart, "posrel", "source");


            if(nlThisSingleMetaStandartRelations.size() == 1)
            {//only one parent

               //Skiping this element with connection
               Relation relation = nodeMetaStandart.createRelation(nodeThisSingleMetaStandart, cloud.getRelationManager("posrel"));
               //Coping posrel.pos from parent
               relation.setValue("pos", this.getPosrelNode(nodeMetaStandart.getNumber(), nodeRelatedMetaStandart.getNumber()).getValue("pos"));
               relation.commit();

               nodeThisSingleMetaStandart.setValue("name", nodeRelatedMetaStandart.getValue("name") + "-" + nodeThisSingleMetaStandart.getValue("name"));
               nodeThisSingleMetaStandart.commit();
            }
            else
            {//at least two, so we can't simply rename it
               Node nodeCopyOfMetaStandart = nmMetaStandart.createNode();
               nodeCopyOfMetaStandart.setValue("name", nodeRelatedMetaStandart.getValue("name") + "-" + nodeThisSingleMetaStandart.getValue("name"));
               nodeCopyOfMetaStandart.setValue("isused", "0");
               nodeCopyOfMetaStandart.commit();

               //creating relation to the parent node
               Relation relation = nodeMetaStandart.createRelation(nodeCopyOfMetaStandart, cloud.getRelationManager("posrel"));
               relation.setValue("pos", this.getPosrelNode(nodeMetaStandart.getNumber(), nodeRelatedMetaStandart.getNumber()).getValue("pos"));
               relation.commit();


               //Copying metastandarts
               List nlThisSingleMetaStandartRelatedMetaStandarts = this.getRelatedOrderedMetaStandarts(nodeThisSingleMetaStandart);

               for(Iterator it2 = nlThisSingleMetaStandartRelatedMetaStandarts.iterator(); it2.hasNext();)
               {//copy relations
                  Node nodeMetaStandartToCopy = (Node) it2.next();
                  Relation relMetaDefinition = nodeCopyOfMetaStandart.createRelation(nodeMetaStandartToCopy, cloud.getRelationManager("posrel"));
                  relMetaDefinition.setValue("pos", this.getPosrelNode(nodeThisSingleMetaStandart.getNumber(), nodeMetaStandartToCopy.getNumber()).getValue("pos"));
                  relMetaDefinition.commit();
               }


               //Creating copies of metadefinitions like cat & dog into different houses
               List nlThisSingleMetaStandartRelatedMetadefinitions = this.getRelatedOrderedMetaDefinitions(nodeThisSingleMetaStandart);

               for(Iterator it2 = nlThisSingleMetaStandartRelatedMetadefinitions.iterator(); it2.hasNext();)
               {//copy relations
                  Node nodeMetaDefinitionToCopy = (Node) it2.next();
                  Node nodeCopyOfMetaDefinition = nmMetaDefinition.createNode();
                  nodeCopyOfMetaDefinition.setValue("name", nodeMetaDefinitionToCopy.getValue("name"));
                  nodeCopyOfMetaDefinition.setValue("type", "3");
                  nodeCopyOfMetaDefinition.commit();
                  Relation relMetaDefinition = nodeCopyOfMetaStandart.createRelation(nodeCopyOfMetaDefinition, cloud.getRelationManager("posrel"));
                  relMetaDefinition.setValue("pos", this.getPosrelNode(nodeThisSingleMetaStandart.getNumber(), nodeMetaDefinitionToCopy.getNumber()).getValue("pos"));
                  relMetaDefinition.commit();
               }
            }

            //now we have to delete relation
            Node nodeRelation = this.getPosrelNode(nodeMetaStandart.getNumber(), nodeRelatedMetaStandart.getNumber());
            nodeRelation.delete(true);

            //if no more relations to this MetaStandart node, we can remove it as well
            if(nodeRelatedMetaStandart.countRelatedNodes(nmMetaStandart, "posrel", "source") == 0)
            {
               nodeRelatedMetaStandart.delete(true);
            }

            //we have done changes in tree, so we have to restart our alghoritm for parent node
            return true;
         }
      }

      //no changes, this MetaStandart can't be removed
      return false;
   }








   private boolean cleanMetastandartsWithOnlyOneMetadefinition(Node nodeMetaStandart)
   {
      List nlMetaStandarts = this.getRelatedOrderedMetaStandarts(nodeMetaStandart);
      for(Iterator it = nlMetaStandarts.iterator(); it.hasNext();)
      {
         Node nodeRelatedMetaStandart = (Node) it.next();

         //Is it possible to delete this MetaStandart and move related metadefinition to parent?
         NodeList nlTestedChildMetaStandarts = nodeRelatedMetaStandart.getRelatedNodes(nmMetaStandart, "posrel", "destination");
         NodeList nlTestedChildMetaDefinitions = nodeRelatedMetaStandart.getRelatedNodes(nmMetaDefinition, "posrel", "destination");

         if((nlTestedChildMetaStandarts.size() == 0) && (nlTestedChildMetaDefinitions.size() == 1))
         {//This MetaStandart has only one "child" Metadata and no MetaStandarts, so it can be removed
            Node nodeThisSingleMetaDefinition = (Node) nlTestedChildMetaDefinitions.get(0);


            //Creating a copy of metadefition for each parent
            NodeList nlAllParents = nodeRelatedMetaStandart.getRelatedNodes(nmMetaStandart, "posrel", "source");
            for(Iterator it2 = nlAllParents.iterator(); it2.hasNext();)
            {
               Node nodeParentMetaStandart = (Node) it2.next();

               Node nodeNewMetaDefinitionCopy = nmMetaDefinition.createNode();
               nodeNewMetaDefinitionCopy.setValue("name", nodeRelatedMetaStandart.getValue("name") + "-" + nodeThisSingleMetaDefinition.getValue("name"));
               nodeNewMetaDefinitionCopy.setValue("type", "3");
               nodeNewMetaDefinitionCopy.commit();
               //Connecting it to the parent
               Relation relation = nodeParentMetaStandart.createRelation(nodeNewMetaDefinitionCopy, cloud.getRelationManager("posrel"));
               //Coping posrel.pos from parent
               relation.setValue("pos", this.getPosrelNode(nodeParentMetaStandart.getNumber(), nodeRelatedMetaStandart.getNumber()).getValue("pos"));
               relation.commit();

            }

             nodeRelatedMetaStandart.delete(true);
             nodeThisSingleMetaDefinition.delete(true);

            //we have done changes in tree, so we have to restart our alghoritm for parent node
            return true;
         }

         while(this.cleanMetastandartsWithOnlyOneMetadefinition(nodeRelatedMetaStandart));
      }

      //no changes, this MetaStandart can't be removed
      return false;
   }




   private List getRelatedOrderedMetaStandarts(Node node)
   {//Gives a list of metastandarts ordered by posrel.pos
      NodeList nlVirtualMetaStandarts = cloud.getList("" + node.getNumber(), node.getNodeManager().getName() + ",posrel,metastandard2", "metastandard2.number,posrel.pos", null, "posrel.pos", null, "destination", false);
      List listResult = new ArrayList();

      for(Iterator it = nlVirtualMetaStandarts.iterator(); it.hasNext();)
      {
         Node nodeMetaStandart = (Node) it.next();
         listResult.add(cloud.getNode(nodeMetaStandart.getStringValue("metastandard2.number")));
      }

      return listResult;
   }



   private List getRelatedOrderedMetaDefinitions(Node node)
   {//Gives a list of metadefinitons ordered by posrel.pos
      NodeList nlVirtualMetaDefinitions = cloud.getList("" + node.getNumber(), node.getNodeManager().getName() + ",posrel,metadefinition2", "metadefinition2.number,posrel.pos", null, "posrel.pos", null, "destination", false);
      List listResult = new ArrayList();

      for(Iterator it = nlVirtualMetaDefinitions.iterator(); it.hasNext();)
      {
         Node nodeMetaDefinition = (Node) it.next();
         listResult.add(cloud.getNode(nodeMetaDefinition.getStringValue("metadefinition2.number")));
      }

      return listResult;
   }




   private Node getPosrelNode(String sSource, String sDestination)
   {
      NodeList nlPosrel = nmPosrel.getList("snumber='" + sSource + "' AND dnumber='" + sDestination + "'", null, null);
      return (Node) nlPosrel.get(0);
   }
   private Node getPosrelNode(int sSource, int sDestination)
   {
      return this.getPosrelNode("" + sSource, "" + sDestination);
   }


}
