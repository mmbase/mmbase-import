package nl.didactor.component.scorm.metastandart.schema;

import java.io.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.mmbase.bridge.*;
//import org.mmbase.module.core.*;
//import org.mmbase.util.logging.*;

import org.jdom.Element;
import org.jdom.Namespace;

import uk.ac.reload.jdom.XMLDocument;


public class Importer
{
   private Namespace namspXsdSchema =  Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
   private Element elemSchemaRoot;
   private Element elemDataRoot;
   private Cloud cloud = null;
   private File fileSchema = null;
   private File fileXml = null;
   private HashSet hsetPassedElements = new HashSet();
   private Node nodeTopCreatedMetaStandart = null;
   private boolean bClosedLoopDurinImport = false;


   //this list stores already passed objects, which should greatly improve perfomance
   private HashMap hsetOurMetaStandarts = new HashMap();

   private final String[] sXsdVluesTypes = {"xsd:string", "xsd:int"};


   public Importer(Cloud cloud, File fileSchema) throws Exception
   {
      this.cloud = cloud;
      this.fileSchema = fileSchema;
      this.fileXml = fileXml;
   }



   public Node importScheme(String sRootMetaStandartName) throws Exception
   {
      XMLDocument xmlSchema = new XMLDocument();
      xmlSchema.loadDocument(fileSchema);
      elemSchemaRoot = xmlSchema.getRootElement();

      processElem(sRootMetaStandartName, null, 0);
      return nodeTopCreatedMetaStandart;
   }



   private void processElem(String sTargetElement, Node nodeParent, int iPos)
   {
      //Checking for a closed loop
      if(hsetPassedElements.contains(sTargetElement))
      {
//         System.out.println("-------------------Loop detected------------------");
         this.bClosedLoopDurinImport = true;
         return;
      }

      //Add current element to a list to prevent looping
      hsetPassedElements.add(sTargetElement);

      List listAllElements = elemSchemaRoot.getChildren("element" , namspXsdSchema);

      //Go through all elements in schema
      for(Iterator it = listAllElements.iterator(); it.hasNext();)
      {
         Element elem = (Element) it.next();
         if((elem.getAttributeValue("name") != null) && (elem.getAttributeValue("name").equals(sTargetElement)))
         {
            //Check is it a xsd standart type?
            boolean bXsdStandartType = checkIsItStandartType(elem.getAttributeValue("type"));

            if(bXsdStandartType)
            {//It is a standart schema type
//               System.out.println("SIMPLE: " + elem.getAttributeValue("name"));

               Node nodeMetaDefinition = addMetadefinition(elem.getAttributeValue("name"), nodeParent, iPos);
            }
            else
            {//This type is defined by user
               processCustomType(elem.getAttributeValue("type"), nodeParent, elem.getAttributeValue("name"), iPos);
            }
            break;
         }
      }

      //The element has been passed ok, without looping
      hsetPassedElements.remove(sTargetElement);
   }



   /**
    * Custom type processor
    *
    * @author Alexey Zemskov
    * @param psTargetElement - name of type of element for searching
    * @param nodeParent - parent metastandart node for creating relations
    * @param sElemName - Clean name of processing element
    * @return nothing
    * @throws exception nothing
    */
   private void processCustomType(String sTargetElement, Node nodeParent, String sElemName, int iPos)
   {//This type is comples or has custom restrictions

      List listAllElements = elemSchemaRoot.getChildren(null, namspXsdSchema);

      for(Iterator it = listAllElements.iterator(); it.hasNext();)
      {
         Element elem = (Element) it.next();

         if ((elem.getAttributeValue("name") != null) && (elem.getAttributeValue("name").equals(sTargetElement)))
         {
            //Checking the name of this type
            if (elem.getName() != null)
            {
               if (elem.getName().equals("complexType"))
               { //This elements can contain other elements
//                  System.out.println("COMPLEX: " + elem.getAttributeValue("name"));
                  processComplexType(elem, nodeParent, sElemName, iPos);
               }

               if (elem.getName().equals("simpleType"))
               {//Can't contain other elements
//                  System.out.println("SIMPLE: " + elem.getAttributeValue("name"));
                  Node nodeMetaDefinition = addMetadefinition(sElemName, nodeParent, iPos);
               }
            }

            break;
         }
      }
   }




   private Node getMetaStandartByName(String sName)
   {
      if(hsetOurMetaStandarts.containsKey(sName))
      {
         return (Node) hsetOurMetaStandarts.get(sName);
      }

      NodeManager nmMetaStandart = cloud.getNodeManager("metastandard");
      NodeList nlMetaStandart = nmMetaStandart.getList("metastandard.name='" + sName + "'", null, null);
      if (nlMetaStandart.size() > 0)
      {//Some Metastandarts with this name already exists in database
       //We have to check is it one which belongs to curent tree?
         for(Iterator it = nlMetaStandart.iterator(); it.hasNext();)
         {//Looknig for the root of tree
            Node nodeTestedMetaStandart = (Node) it.next();
            Node nodeLookingForTheParent = nodeTestedMetaStandart;
            NodeList nlParents;
            while((nlParents = nodeLookingForTheParent.getRelatedNodes(nmMetaStandart, "posrel", "source")).size() > 0)
            {//if threre are eny parent choose any othen an go to upper level
               nodeLookingForTheParent = (Node) nlParents.get(0);
            }

            //no more parents
            if(nodeTestedMetaStandart.equals(nodeTopCreatedMetaStandart))
            {//If the root of tree is equal to our root,
             //our metastandart belongs to our tree

               //mark this node as our metastandart
               hsetOurMetaStandarts.put(sName, nodeTestedMetaStandart);
               return nodeTestedMetaStandart;
            }

         }
      }

      //We haven't found any which belongs to our tree
      //In this case lets create a new one
      Node nodeMetaStandart = nmMetaStandart.createNode();
      nodeMetaStandart.setValue("name", sName);
      nodeMetaStandart.setValue("isused", "0");
      nodeMetaStandart.commit();

      //mark this node as our metastandart
      hsetOurMetaStandarts.put(sName, nodeMetaStandart);
      return nodeMetaStandart;
   }





   private void addMetastandartRelation(Node nodeParent, Node nodeMetaStandart, int iPos)
   {
      if (!nodeParent.getRelatedNodes("metastandard").contains(nodeMetaStandart))
      {
         Relation relation = nodeParent.createRelation(nodeMetaStandart, cloud.getRelationManager("posrel"));
         relation.setValue("pos", "" + iPos);
         relation.commit();
      }
   }







   private Node addMetadefinition(String sMetadefinitionName, Node nodeParent, int iPos)
   {
      Node nodeMetaDefinition = null;

      if (nodeParent != null)
      {
         NodeList nlMetadefinition = nodeParent.getRelatedNodes("metadefinition");

         for(Iterator it = nlMetadefinition.iterator(); it.hasNext();)
         {
            Node nodeRelatedMetaDefinition = (Node) it.next();
            if( ((String) nodeRelatedMetaDefinition.getValue("name")).equals(sMetadefinitionName) )
            {
               return nodeRelatedMetaDefinition;
            }
         }

         nodeMetaDefinition = cloud.getNodeManager("metadefinition").createNode();
         nodeMetaDefinition.setValue("name", sMetadefinitionName);
         nodeMetaDefinition.setValue("type", "3");
         nodeMetaDefinition.commit();

         Relation relation = nodeParent.createRelation(nodeMetaDefinition, cloud.getRelationManager("posrel"));
         relation.setValue("pos", "" + iPos);
         relation.commit();
      }

      return nodeMetaDefinition;
   }






   private void processComplexType(Element elem, Node nodeParent, String sElemName, int iPos)
   {
      boolean bElemPassed = false;
      //Is this complex type a sequence?
      Element elemSequence = null;
      try
      {
         elemSequence = elem.getChild("sequence", namspXsdSchema);
         //if elemSequence is null here will be exception,
         //So it is wrong type
         List listChildren = elemSequence.getChildren(null, namspXsdSchema);

         //If we reached this place the type of element is complex
         Node nodeMetaStandart = getMetaStandartByName(sElemName);
         if (nodeParent == null)
         {//This is a first created metastandart, i.e. it is a root of the tree
            this.nodeTopCreatedMetaStandart = nodeMetaStandart;
         }

         if(nodeParent != null)
         {
            addMetastandartRelation(nodeParent, nodeMetaStandart, iPos);
         }

         int iChildPos = 0;
         for (Iterator it2 = listChildren.iterator(); it2.hasNext(); )
         {
            Element elemChild = (Element) it2.next();
            processElem(elemChild.getAttributeValue("ref"), nodeMetaStandart, iChildPos++);
         }
         bElemPassed = true;
      }
      catch (Exception e)
      {
      }

      //Is this complex type an extension?
      if(!bElemPassed)
      {
         try
         {
            Element elemSimpleType = elem.getChild("simpleContent", namspXsdSchema);
            Element elemExtension = elemSimpleType.getChild("extension", namspXsdSchema);

            if(checkIsItStandartType(elemExtension.getAttributeValue("base")))
            {//Standart schema type has been extended
               Node nodeMetaDefinition = addMetadefinition(sElemName, nodeParent, iPos);
            }
            else
            {//Custom type

            }

            bElemPassed = true;
         }
         catch(Exception e)
         {

         }
      }
   }






   private boolean checkIsItStandartType(String sValue)
   {
      for(int f = 0; f < sXsdVluesTypes.length; f++)
      {
         if(sXsdVluesTypes[f].equals(sValue))
         {//This is a standart type
            return true;
         }
      }
      return false;
   }



   public boolean wasClosedLoopDurinImport()
   {//Reruns true if any closed loop was detected
      return this.bClosedLoopDurinImport;
   }

}
