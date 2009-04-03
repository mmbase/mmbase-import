package nl.didactor.component.scorm.metastandart;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.*;

import org.jdom.Element;
import org.jdom.Namespace;

import uk.ac.reload.jdom.XMLDocument;

import nl.didactor.component.scorm.utils.Posrel;


public class MetaDataImporter
{
   private Cloud cloud = null;
   private Node nodeMetaStandartRoot;
   private Namespace namspXml =  Namespace.getNamespace("http://www.imsglobal.org/xsd/imsmd_rootv1p2p1");

   private NodeManager nmMetaStandart = null;
   private NodeManager nmMetaDefinition = null;
   private RelationManager rmRelated = null;
   private NodeManager nmMetaData = null;

   private Node nodeDestination = null;


   public MetaDataImporter(Cloud cloud, Node nodeMetaStandartRoot)
   {
      this.nodeMetaStandartRoot = nodeMetaStandartRoot;
      this.cloud = cloud;
      this.nmMetaStandart   = cloud.getNodeManager("metastandard");
      this.nmMetaDefinition = cloud.getNodeManager("metadefinition");
      this.rmRelated        = cloud.getRelationManager("related");
      this.nmMetaData       = cloud.getNodeManager("metadata");
   }

   public void process(File fileInput, Node nodeDestination) throws Exception
   {
      XMLDocument xmlDocument = new XMLDocument();
      xmlDocument.loadDocument(fileInput);
      Element elemMetaStandartRoot = xmlDocument.getRootElement();
      this.nodeDestination = nodeDestination;

      this.processOneElement(nodeMetaStandartRoot, elemMetaStandartRoot);

   }





   private void processOneElement(Node nodeMetaStandart, Element elem)
   {// It goes trought the tree of MetaStandarts recursively
      List listRelatedMetaStandarts = Posrel.getRelatedOrderedMetaStandarts(cloud, nodeMetaStandart);

      for(Iterator it = listRelatedMetaStandarts.iterator(); it.hasNext();)
      {//related metastandarts
         Node nodeRelatedMetaStandart = (Node) it.next();


         List listElemRelated = elem.getChildren((String) nodeRelatedMetaStandart.getValue("name"), namspXml);
         if(listElemRelated.size() >  0)
         {//elemRelated == null means what no such element in file
            for(Iterator it2 = listElemRelated.iterator(); it2.hasNext();)
            {
               Element elemRelated = (Element) it2.next();
               System.out.println(elemRelated.getName());
               this.processOneElement(nodeRelatedMetaStandart, elemRelated);
            }
         }
         else
         {
            Element elemTemp = elem;
            try
            {
               String[] arrstrPartialNames = ( (String) nodeRelatedMetaStandart.getValue("name")).split("-");
               for (int f = 0; f < arrstrPartialNames.length; f++)
               {
                  elemTemp = elemTemp.getChild(arrstrPartialNames[f], namspXml);
               }
               this.processOneElement(nodeRelatedMetaStandart, elemTemp);

            }
            catch (Exception ex)
            {//Not found. That's OK. Simply no such element. Give up.
            }
         }

      }

//      System.out.println(nodeMetaStandart.getValue("name"));

      List listRelatedMetaDefinitions = Posrel.getRelatedOrderedMetaDefinitions(cloud, nodeMetaStandart);
      for(Iterator it = listRelatedMetaDefinitions.iterator(); it.hasNext();)
      {
         Node nodeRelatedMetaDefinition = (Node) it.next();
         List listRelated = elem.getChildren((String) nodeRelatedMetaDefinition.getValue("name"), namspXml);
         if(listRelated.size() > 0)
         {//Direct import of content
            for(Iterator it2 = listRelated.iterator(); it2.hasNext();)
            {
               Element elemTempMetaValue = (Element) it2.next();
               this.addMetaData(nodeRelatedMetaDefinition, nodeDestination, elemTempMetaValue.getText());
            }

         }
         else
         {//We should go through name-name2-name3
            Element elemTemp = elem;
            try
            {
               String[] arrstrPartialNames = ( (String) nodeRelatedMetaDefinition.getValue("name")).split("-");
               List arliPath = new ArrayList();
               for(int f = 0; f < arrstrPartialNames.length; f++)
               {
                  arliPath.add(arrstrPartialNames[f]);
               }
               this.parseGroup(elemTemp, arliPath, nodeRelatedMetaDefinition);

            }
            catch (Exception e)
            {//Not found. That's OK. Simply there is no such element. Give up.
            }

         }

//         System.out.println(nodeMetaDefinition.getValue("name"));
      }
   }


   private void parseGroup(Element elem, List arliPath, Node nodeMetadefinition)
   {//Parser for the trees
    //name-name2-name3
    //          -name3
    //    -name2-name3
    //          -name3
    //          -name3
      for(Iterator it = arliPath.iterator(); it.hasNext();)
      {
         List listMetaValues = elem.getChildren((String) it.next(), namspXml);
         for(Iterator it2 = listMetaValues.iterator(); it2.hasNext();)
         {
            Element elemTempMetaValue = (Element) it2.next();
            if(elemTempMetaValue.getChildren().size() == 0)
            {//It is a check what the element contains only text,
             //i.e. no children elements
               this.addMetaData(nodeMetadefinition, nodeDestination, elemTempMetaValue.getText());
            }
            parseGroup(elemTempMetaValue, arliPath.subList(1, arliPath.size()), nodeMetadefinition);
         }
      }
   }




   private Node addMetaData(Node nodeMetaDefinition, Node nodeTarget, String sValue)
   {
      Node nodeNewMetaData = nmMetaData.createNode();
      nodeNewMetaData.setValue("value", sValue);
      nodeNewMetaData.commit();

      nodeNewMetaData.createRelation(nodeMetaDefinition, rmRelated).commit();
      nodeDestination.createRelation(nodeNewMetaData, rmRelated).commit();

      return nodeNewMetaData;
   }


}

