package nl.didactor.metadata.util;

import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.component.metadata.autofill.HandlerInterface;
import nl.didactor.component.metadata.constraints.Constraint;
import nl.didactor.component.metadata.constraints.Error;

import nl.didactor.metadata.tree.MetadataTreeModel;




public class MetaDataHelper {

   private static Logger log = Logging.getLoggerInstance(MetaDataHelper.class);

   public static final int VOCABULARY_TYPE = 1;
   public static final int DATE_TYPE = 2;
   public static final int LANGSTRING_TYPE = 3;
   public static final int DURATION_TYPE = 4;
   public static String EMPTY_VALUE = "...";
   public static String [] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   public static MetaHelper [] metaHelpers = new MetaHelper[] {null, new MetaVocabularyHelper(), new MetaDateHelper(), new MetaLangStringHelper(), new MetaDurationHelper() };


   public static NodeList getLangCodes(Cloud cloud) {

      NodeList nl = cloud.getList(null,
         "metastandard,metadefinition,metavocabulary",
         "metavocabulary.value",
         "metadefinition.handler='taal'",
         "metavocabulary.value","UP",null,true);
      return nl;
   }

   public static NodeList getRelatedMetaData(Cloud cloud, String sCurrentNode) {

      return cloud.getNode(sCurrentNode).getRelatedNodes("metadata");
   }





   /**
    * Return a HashMap of Constraint objects for all active metadefinitions
    * Pair(MetaDefinition, Constrint);
    *
    * NOTE:
    * Priorities:
    * 1. metavocabulary - constraints - metadefinition
    * 2. MetaStandart - constraints - metadefinition
    * 3. Old type
    *
    * @param cloud Cloud
    * @return HashMap
    */
   public static HashMap getConstraints(Cloud cloud){
       HashMap hashmapResult = new HashMap();

       String sActiveMetaStandarts = getActiveMetastandards(cloud, null, null);


       //Old style
       NodeList nl = cloud.getList(sActiveMetaStandarts,
                                   "metastandard,metadefinition",
                                   "metadefinition.number",
                                   "metadefinition.required='1'",
                                   null, null, null, true);

       for (int n = 0; n < nl.size(); n++) {
           String sNodeID = nl.getNode(n).getStringValue("metadefinition.number");
           Node node = cloud.getNode(sNodeID);

           if((node.getIntValue("minvalues") >= 0) && (node.getIntValue("maxvalues") >= 0)){
               Constraint constraint = new Constraint(Constraint.LIMITED, Constraint.EVENT_METADEFINITION_ITSELF);
               constraint.setMax(node.getIntValue("maxvalues"));
               constraint.setMin(node.getIntValue("minvalues"));
               hashmapResult.put(node, constraint);
               continue;
           }
           if((node.getIntValue("minvalues") == 0) && (node.getIntValue("maxvalues") == 0)){
               Constraint constraint = new Constraint(Constraint.FORBIDDEN, Constraint.EVENT_METADEFINITION_ITSELF);
               hashmapResult.put(node, constraint);
               continue;
           }
           if(node.getIntValue("minvalues") > 0){
               Constraint constraint = new Constraint(Constraint.MANDATORY, Constraint.EVENT_METADEFINITION_ITSELF);
               hashmapResult.put(node, constraint);
               continue;
           }

       }


       //metastandart - constraints - metadefinition
       nl = cloud.getList(sActiveMetaStandarts,
                          "metastandard,constraints,metadefinition",
                          "metadefinition.number,constraints.number",
                          null,
                          null, null, null, true);

       for (int n = 0; n < nl.size(); n++) {
           Node nodeMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition.number"));
           Node nodeConstraintRelation = cloud.getNode(nl.getNode(n).getStringValue("constraints.number"));

           Constraint constraint = new Constraint(nodeConstraintRelation.getIntValue("type"), Constraint.EVENT_METASTANDART_CONSTRAINT_RELATION);
           constraint.setMax(nodeConstraintRelation.getIntValue("maxvalues"));
           constraint.setMin(nodeConstraintRelation.getIntValue("minvalues"));
           constraint.setPosition(nodeConstraintRelation.getIntValue("pos"));

           hashmapResult.put(nodeMetaDefinition, constraint);
       }



       //vocabulary - constraints - metadefinition
       nl = cloud.getList(sActiveMetaStandarts,
                          "metastandard,posrel,metadefinition1,constraints,metavocabulary,posrel,metadata,metadefinition2",
                          "metadefinition1.number,constraints.number,metavocabulary.number,metadefinition2.number",
                          "metadefinition1.number != metadefinition2.number",
                          null, null, null, true);

       for (int n = 0; n < nl.size(); n++) {
           Node nodeMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition1.number"));
           Node nodeConstraintRelation = cloud.getNode(nl.getNode(n).getStringValue("constraints.number"));
           Node nodeControllerVocabulary = cloud.getNode(nl.getNode(n).getStringValue("metavocabulary.number"));
           Node nodeControllerMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition2.number"));

//           System.out.println("0000000000000=" + nodeMetaDefinition.getNumber());
//           System.out.println("1111111111111=" + nodeControllerMetaDefinition.getNumber());

           Constraint constraint = new Constraint(nodeConstraintRelation.getIntValue("type"), Constraint.EVENT_VOCABULARY_CONSTRAINT_RELATION);
           constraint.setMax(nodeConstraintRelation.getIntValue("maxvalues"));
           constraint.setMin(nodeConstraintRelation.getIntValue("minvalues"));
           constraint.setPosition(nodeConstraintRelation.getIntValue("pos"));
           Node[] value = new Node[2];
           value[0] = nodeControllerMetaDefinition;
           value[1] = nodeControllerVocabulary;
           constraint.setEventObject(value);

           hashmapResult.put(nodeMetaDefinition, constraint);
       }


       return hashmapResult;
   }











   public static void log(HttpServletRequest request, String sPage) {
      log.info("Calling " + sPage + " with the following parameters");
      log.info("number:       " + request.getParameter("number"));
      log.info("set_default:  " + request.getParameter("set_default"));
      log.info("submitted:    " + request.getParameter("submitted"));
      log.info("add:          " + request.getParameter("add"));
      log.info("remove:       " + request.getParameter("remove"));
      log.info("close:        " + request.getParameter("close"));
      log.info("query string: " + request.getQueryString());
   }

   private static String parametersToString(String[] arrstrParameters) {
      String sParameters = "[";
      for(int f = 0; f < arrstrParameters.length ; f++) {
         if(f>0) { sParameters += ";"; }
         sParameters += arrstrParameters[f];
      }
      sParameters += "]";
      return sParameters;
   }



   /**
    * Checks the type of the MetaDefinition and returns it
    * @param metadefNode Node
    * @return int
    */
   public static int getIType(Node metadefNode) {
      int iType = metadefNode.getIntValue("type");
      if( iType < VOCABULARY_TYPE || iType > DURATION_TYPE ){
        log.error("The type field of metadefinition " + metadefNode.getStringValue("number") + " is " + iType + ", it should fall in [" + VOCABULARY_TYPE + "," + DURATION_TYPE + "]");
        iType = VOCABULARY_TYPE;
      }
      return iType;
   }




   /**
    * This metod is used for checking the single object
    * @param nodeObject Node
    * @return Error
    */
   public static Error hasTheObjectValidMetadata(Node nodeObject, HttpSession session) {

       HashMap hashmapConstraints = getSessionConstraints(nodeObject.getCloud(), session);

       for(Iterator it = hashmapConstraints.keySet().iterator(); it.hasNext(); ){
           Node nodeMetaDefinition = (Node) it.next();

           Error error = hasTheObjectValidMetadata(nodeMetaDefinition, nodeObject, hashmapConstraints);
           if(error != null){
               return error;
           }
       }
       return null;
   }



   /**
    * Check the metadata for the metadefinition
    * @param nodeMetaDefinition Node
    * @param session HttpSession
    * @return Error
    */
   public static Error hasTheMetaDefinitionValidMetadata(Node nodeMetaDefinition, Node nodeObject, HttpSession session) {
       HashMap hashmapConstraints = getSessionConstraints(nodeMetaDefinition.getCloud(), session);
       return hasTheObjectValidMetadata(nodeMetaDefinition, nodeObject, hashmapConstraints);
   }

   public static Error hasTheObjectValidMetadata(Node nodeMetaDefinition, Node nodeObject, HashMap hashmapConstraints){
       Constraint constraint = (Constraint) hashmapConstraints.get(nodeMetaDefinition);
       if(constraint != null){
           //We start this checking only for MetaDefinition nodes that have got constraints
           int iType = getIType(nodeMetaDefinition);
           Node nodeMetaData = getMetadataNode(nodeObject.getCloud(), "" + nodeObject.getNumber(), "" + nodeMetaDefinition.getNumber(), false);
           Error error = metaHelpers[iType].check(nodeMetaDefinition, (Constraint) hashmapConstraints.get(nodeMetaDefinition), nodeMetaData);
           if(error != null){
               return error;
           }
       }

       return null;
   }






   /**
    * Check the metadata constrints in session
    * If there is a timeout or session is null
    * it calculates thm again(a heavy task)
    *
    * @param session HttpSession
    * @return HashMap
    */
   private static HashMap getSessionConstraints(Cloud cloud, HttpSession session){

       HashMap hashmapConstraints;

       if(session.getAttribute("metadata_timestamp") != null){
           long lTime = ((Long)  session.getAttribute("metadata_timestamp")).longValue();
           if(lTime > System.currentTimeMillis() - 5000){
               hashmapConstraints = getConstraints(cloud);
               saveConstraintsToSession(hashmapConstraints, session);
           }
           else{
               hashmapConstraints = (HashMap) session.getAttribute("metadata_constraints");
           }
       }
       else{
           hashmapConstraints = getConstraints(cloud);
           saveConstraintsToSession(hashmapConstraints, session);
       }

       return hashmapConstraints;
   }


   /**
    * Stores current constraints
    * @param hashmapConstraints HashMap
    * @param session HttpSession
    */
   private static void saveConstraintsToSession(HashMap hashmapConstraints, HttpSession session){
       session.setAttribute("metadata_timestamp", new Long(System.currentTimeMillis()));
       session.setAttribute("metadata_constraints", hashmapConstraints);
   }





/**
 * Redirects this call to the proper metaHelper
 *
 * @param nodeMetaDefinition Node
 * @param constraint Constraint
 * @param arrstrParameters String[]
 * @return ArrayList
 */

   public static ArrayList hasValidMetadata(Node nodeMetaDefinition, Constraint constraint, String[] arrstrParameters){

       int iType = getIType(nodeMetaDefinition);
       return metaHelpers[iType].check(nodeMetaDefinition, constraint, arrstrParameters);
   }







   public static Node getMetadataNode(Cloud cloud, String sCurrentNode, String sMetadefNode, boolean useDefaults) {

      Node currentNode = cloud.getNode(sCurrentNode);
      Node metadefNode = cloud.getNode(sMetadefNode);
      int iType = getIType(metadefNode);

      Node metaDataNode = null;

      NodeList nl = metaHelpers[iType].getRelatedMetaData(cloud,sCurrentNode,sMetadefNode);
      if(nl.size() == 0){
         metaDataNode = metaHelpers[iType].createMetaDataNode(cloud,currentNode,metadefNode);
      }
      else{
         metaDataNode = cloud.getNode(nl.getNode(0).getStringValue("metadata.number"));
      }


      Node defaultNode = null;

      if(useDefaults) {  // Add default values to new metadata here

         NodeList nlDefaultMetadata = cloud.getList(sMetadefNode,
            "metadefinition,metadata,metastandard",
            "metadata.number",
            null,null,null,null,true);
         defaultNode = cloud.getNode(nlDefaultMetadata.getNode(0).getStringValue("metadata.number"));

      }

      if (defaultNode != null)
      {
         metaHelpers[iType].copy(cloud, metaDataNode,currentNode);
      }
      return metaDataNode;
   }




   public static void setMetadataNode(Cloud cloud, String[] arrstrParameters, Node metadataNode, String sMetadefNode, int skipParameter) {
      Node metadefNode = cloud.getNode(sMetadefNode);
      int iType = getIType(metadefNode);
      log.debug("Using " + parametersToString(arrstrParameters) + " to set metadata " + metadataNode.getStringValue("number") + " for " +  metaHelpers[iType].toString() + " metadefinition " + sMetadefNode);
      metaHelpers[iType].set(cloud, arrstrParameters, metadataNode, metadefNode, skipParameter);
   }




   /**
    * Converts a virtual nodelist to a real one with one parameter
    * This is a quite common task.
    *
    * @param cloud Cloud
    * @param nl NodeList
    * @param sVirtualValue String
    * @return NodeList
    */
   private static NodeList convertVirtualNodeList(Cloud cloud, NodeList nl, String sVirtualValue){
       NodeList nlResult = (NodeList) new ArrayList();
       for (int n = 0; n < nl.size(); n++) {
           String sNodeID = nl.getNode(n).getStringValue(sVirtualValue);
           Node node = cloud.getNode(sNodeID);
           nlResult.add(node);
       }

       return nlResult;
   }


   /**
    *
    * Get synonym for any supported object
    *
    * @param cloud Cloud
    * @param sObjectID String
    * @param sUserID String
    * @return String
    */
   public static String getAliasForObject(Cloud cloud, String sObjectID, String sUserID){
       try{
           NodeList nlSynonyms = cloud.getList(sUserID,
              "people,workgroups,synonym,object",
              "synonym.number",
              "object.number='" + sObjectID + "'",
              null,null,null,false);
           Node nodeSynonym = cloud.getNode(nlSynonyms.getNode(0).getStringValue("synonym.number"));
           return (String) nodeSynonym.getValue("name");
       }
       catch(Exception e){
       }
       if("metavocabulary".equals(cloud.getNode(sObjectID).getNodeManager().getName())){
           return (String) cloud.getNode(sObjectID).getValue("value");
       }
       return (String) cloud.getNode(sObjectID).getValue("name");
   }


   public static String getAliasForObject(Cloud cloud, int iObjectID, int iUserID){
       return getAliasForObject(cloud, "" + iObjectID, "" + iUserID);
   }
   public static String getAliasForObject(Cloud cloud, String sObjectID, int iUserID){
       return getAliasForObject(cloud, sObjectID, "" + iUserID);
   }
   public static String getAliasForObject(Cloud cloud, int iObjectID, String sUserID){
       return getAliasForObject(cloud, "" + iObjectID, sUserID);
   }



   /**
    * Give all MetaStandars from all branches where .isused==1 at the top level.
    *
    * @param cloud Cloud
    * @param sNode String
    * @param sUserID String
    * @return String Comma separated node list
    */
   public static String getActiveMetastandards(Cloud cloud, String sNode, String sUserID){
      String sResultSet = new String();
      MetadataTreeModel metadataTreeModel = new MetadataTreeModel(cloud);
      Node nodeRootMetaStandart = (Node) metadataTreeModel.getRoot();

      NodeList nlTopLevelMetaStandarts = cloud.getList("" + nodeRootMetaStandart.getNumber(),
          "metastandard1,metastandard2",
          "metastandard2.number",
          "metastandard2.isused='1'",
          null,null,null,false);


      for(int f = 0; f < nlTopLevelMetaStandarts.size(); f++){
         Node nodeMetaStandart = cloud.getNode(nlTopLevelMetaStandarts.getNode(f).getStringValue("metastandard2.number"));
//         System.out.println("+++" + nodeMetaStandart.getNumber());

         GrowingTreeList tree = new GrowingTreeList(Queries.createNodeQuery(nodeMetaStandart), 30, nodeMetaStandart.getNodeManager(), "posrel", "destination");
         TreeIterator it = tree.treeIterator();

         while(it.hasNext()){
            Node nodeChildMetaStandart = it.nextNode();
//            System.out.println(nodeChildMetaStandart.getNumber() + " " + nodeChildMetaStandart.getStringValue("name"));
            if(sResultSet.length() > 0){
               sResultSet += ",";
            }
            sResultSet += nodeChildMetaStandart.getNumber();
         }
      }
      return sResultSet;
   }

   public static NodeList getActiveMetastandardsNodeList(Cloud cloud, String sNode, String sUserID){
      MetadataTreeModel metadataTreeModel = new MetadataTreeModel(cloud);
      Node nodeRootMetaStandart = (Node) metadataTreeModel.getRoot();

      NodeList nlTopLevelMetaStandarts = cloud.getList("" + nodeRootMetaStandart.getNumber(),
         "metastandard1,metastandard2",
         "metastandard2.number",
         "metastandard2.isused='1'",
         null,null,null,false);

      HashSet hsetResult = new HashSet();

      for(int f = 0; f < nlTopLevelMetaStandarts.size(); f++){
         Node nodeMetaStandart = cloud.getNode(nlTopLevelMetaStandarts.getNode(f).getStringValue("metastandard2.number"));

         GrowingTreeList tree = new GrowingTreeList(Queries.createNodeQuery(nodeMetaStandart), 30, nodeMetaStandart.getNodeManager(), "posrel", "destination");
         TreeIterator it = tree.treeIterator();

         while (it.hasNext()) {
             Node nodeChildMetaStandart = it.nextNode();
             hsetResult.add(nodeChildMetaStandart);
         }
      }

      return (NodeList) hsetResult;
   }



   /**
    * Fills autovalues for any supported object
    * @param nodeObject Node
    */
   public static void fillAutoValues(Node nodeObject, ServletContext servletContext){
       // <mm:field name="age()" />

      NodeList nlMetaDefinitions = nodeObject.getCloud().getList(getActiveMetastandards(nodeObject.getCloud(), null, null),
         "metastandard,metadefinition",
         "metadefinition.number",
         null,null,null,null,false);

      for(int f = 0; f < nlMetaDefinitions.size(); f++){
         Node nodeMetaDefinition = nodeObject.getCloud().getNode(nlMetaDefinitions.getNode(f).getStringValue("metadefinition.number"));

         //Start handler, all exceptions to /dev/null
         //(in case users enter wrong value we do nothing)
         String sHandler = nodeMetaDefinition.getStringValue("handler");
         if ((sHandler != null) && (!"".equals(sHandler))){
            try{
               Class classMetaDataHandler = Class.forName("nl.didactor.component.metadata.autofill.handlers." + sHandler);
               Object[] arrobjParams = {servletContext};
               HandlerInterface handler = (HandlerInterface) classMetaDataHandler.getConstructors()[0].newInstance(arrobjParams);

               if (!handler.checkMetaData(nodeMetaDefinition, nodeObject)){
                   handler.addMetaData(nodeMetaDefinition, nodeObject);
               }
            }
            catch(Exception e){
            }
            System.out.println("=" + nodeMetaDefinition.getNumber() + " " + sHandler);
         }
      }
   }
}




