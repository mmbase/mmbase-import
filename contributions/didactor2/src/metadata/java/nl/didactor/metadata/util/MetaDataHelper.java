package nl.didactor.metadata.util;

import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

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

   public static final String APPLICATION_USER_TO_METASTANDARTS_KEY = "metaedit_user_to_metastandarts_key";
   public static final String APPLICATION_METASTANDARTS_TO_CONSTRAINTS_KEY = "metaedit_metastandarts_to_constraints_key";

   public static final int VOCABULARY_TYPE = 1;
   public static final int DATE_TYPE = 2;
   public static final int LANGSTRING_TYPE = 3;
   public static final int DURATION_TYPE = 4;
   public static String EMPTY_VALUE = "...";
   public static String [] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   public static MetaHelper [] metaHelpers = new MetaHelper[] {null, new MetaVocabularyHelper(), new MetaDateHelper(), new MetaLangStringHelper(), new MetaDurationHelper() };


   public static NodeList getLangCodes(Cloud cloud) {

      NodeList nl = cloud.getList(null,
         "metadefinition,metavocabulary",
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
    * 2. metaStandard - constraints - metadefinition
    * 3. metadefinition fields
    *
    *
    * Vocabulary-constraint-vocabulary is an extra constraint that can work together with any of above
    *
    * NOTE2:
    * At this moment only "vocabulary-constraint-vocabulary" uses ArrayList "ConstraintChain"
    * (So some checks in MetaVocabularyHelper are ommited)
    * But in the future it can be extended.
    *
    * @param cloud Cloud
    * @return HashMap
    */
   public static HashMap getConstraints(Cloud cloud, String sActiveMetaStandarts) throws Exception{

       HashMap hashmapResult = new HashMap();


       //Fields from MetaDefinition node
       NodeList nl = cloud.getList(sActiveMetaStandarts,
                                   "metastandard,metadefinition",
                                   "metadefinition.number",
                                   "metadefinition.required='1'",
                                   null, null, null, true);

       for (int n = 0; n < nl.size(); n++) {
           String sNodeID = nl.getNode(n).getStringValue("metadefinition.number");
           Node node = cloud.getNode(sNodeID);

           switch (node.getIntValue("type")){
               case DATE_TYPE:
               case DURATION_TYPE:{
                   Constraint constraint = new Constraint(Constraint.MANDATORY, Constraint.EVENT_METADEFINITION_ITSELF);
                   hashmapResult.put(node, constraint);
                   log.debug("Constraints: Metadefinition(" + node.getNumber() + ") is mandatory by its fields");
                   break;
               }

               case VOCABULARY_TYPE:
               case LANGSTRING_TYPE:{
                   if((node.getIntValue("minvalues") == 0) && (node.getIntValue("maxvalues") == 0)){
                       Constraint constraint = new Constraint(Constraint.FORBIDDEN, Constraint.EVENT_METADEFINITION_ITSELF);
                       hashmapResult.put(node, constraint);
                       log.debug("Constraints: Metadefinition(" + node.getNumber() + ") is forbidden by its fields");
                   }
                   else if((node.getIntValue("minvalues") > 0) && (node.getIntValue("maxvalues") > 0)){
                       Constraint constraint = new Constraint(Constraint.LIMITED, Constraint.EVENT_METADEFINITION_ITSELF);
                       constraint.setMax(node.getIntValue("maxvalues"));
                       constraint.setMin(node.getIntValue("minvalues"));
                       hashmapResult.put(node, constraint);
                       log.debug("Constraints: Metadefinition(" + node.getNumber() + ") is limited by its fields");
                   }
                   else if(node.getIntValue("minvalues") > 0){
                       Constraint constraint = new Constraint(Constraint.MANDATORY, Constraint.EVENT_METADEFINITION_ITSELF);
                       hashmapResult.put(node, constraint);
                       log.debug("Constraints: Metadefinition(" + node.getNumber() + ") is mandatory by its fields");
                   }
                   break;
               }
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
           int iConstraintType = nodeConstraintRelation.getIntValue("type");

           if(iConstraintType != Constraint.NOT_USED){
               //NOT_USED means that metastandart-constraint-metadefinition constraint is turned off
               Constraint constraint = new Constraint(iConstraintType, Constraint.EVENT_METASTANDART_CONSTRAINT_RELATION);
               constraint.setMax(nodeConstraintRelation.getIntValue("maxvalues"));
               constraint.setMin(nodeConstraintRelation.getIntValue("minvalues"));
               constraint.setPosition(nodeConstraintRelation.getIntValue("pos"));

               log.debug("Metadefinition(" + nodeMetaDefinition.getNumber() + ") is controlled by MetaStandart-Constraint relation(" + nodeConstraintRelation.getNumber() + ")");

               hashmapResult.put(nodeMetaDefinition, constraint);
           }
       }


       //vocabulary - constraints - metadefinition
       nl = cloud.getList(sActiveMetaStandarts,
                          "metastandard,posrel,metadefinition1,constraints,metavocabulary,metadefinition2",
                          "metadefinition1.number,constraints.number,metavocabulary.number,metadefinition2.number",
                          "metadefinition1.number != metadefinition2.number",
                          null, null, null, true);

       for (int n = 0; n < nl.size(); n++) {
           Node nodeControllerVocabulary = cloud.getNode(nl.getNode(n).getStringValue("metavocabulary.number"));
           NodeList nl2 = cloud.getList(null,
                             "object,metadata,metavocabulary",
                             "metavocabulary.number",
                             "metavocabulary.number=" + nodeControllerVocabulary.getNumber(),
                             null, null, null, true);

           if(nl2.size() > 0){

               Node nodeMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition1.number"));
               Node nodeConstraintRelation = cloud.getNode(nl.getNode(n).getStringValue("constraints.number"));
               Node nodeControllerMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition2.number"));

               log.debug("Metadefinition(" + nodeMetaDefinition.getNumber() + ") is controlled by MetaVocabulary(" + nodeControllerVocabulary.getNumber() + ")");

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
       }


       //vocabulary - constraints - vocabulary
       log.debug("Trying to find vocabulary - constraints - vocabulary");
       nl = cloud.getList(sActiveMetaStandarts,
                          "metastandard,posrel,metadefinition,posrel,metavocabulary1,constraints,metavocabulary2",
                          "metadefinition.number,constraints.number,metavocabulary1.number,metavocabulary2.number",
                          null,
                          null, null, "destination", true);
       log.debug("found " + nl.size() + " constraints for the active metastandards " + sActiveMetaStandarts);
       for (int n = 0; n < nl.size(); n++) {
           Node nodeControllerMetaVocabulary = cloud.getNode(nl.getNode(n).getStringValue("metavocabulary1.number"));

           NodeList nl2 = cloud.getList(null,
                                        "object,metadata,metavocabulary",
                                        "metavocabulary.number",
                                        "metavocabulary.number=" + nodeControllerMetaVocabulary.getNumber(),
                                        null, null, null, true);

//           log.debug("found " + nl2.size() + " metadata object that relate this object with metavocabulary  "
//               + nodeControllerMetaVocabulary.getStringValue("value"));
           if(nl2.size() > 0){
               Node nodeControllerMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition.number"));
               Node nodeConstraintRelation = cloud.getNode(nl.getNode(n).getStringValue("constraints.number"));
               Node nodeMetaVocabulary = cloud.getNode(nl.getNode(n).getStringValue("metavocabulary2.number"));

               Node nodeMetaDefinition = null;
               try{
                   nodeMetaDefinition = nodeMetaVocabulary.getRelatedNodes("metadefinition").getNode(0);
               }
               catch(Exception e){
                   throw new Exception("Metavocabulary node(" + nodeMetaVocabulary.getNumber() + ") has got NO METADEFINITION");
               }
/*
               log.debug("Found metavocabulary-constraint-metavocabulary");
               log.debug("metadefinition=" + nodeMetaDefinition.getNumber());
               log.debug("metavocabulary=" + nodeMetaVocabulary.getNumber());
               log.debug("constraints=" + nodeConstraintRelation.getNumber());
               log.debug("metavocabulary_cont=" + nodeControllerMetaVocabulary.getNumber());
               log.debug("metadefinition_cont=" + nodeControllerMetaDefinition.getNumber());
*/

               log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") is controlled by MetaVocabulary(" + nodeControllerMetaVocabulary.getNumber() + ")");


               Constraint constraint = new Constraint(nodeConstraintRelation.getIntValue("type"), Constraint.EVENT_VOCABULARY_TO_VOCABULARY_RELATION);
               if(hashmapResult.containsKey(nodeMetaDefinition)){
                   //There is an additional constraint for this metadefinition
                   Constraint mainConstraint = ((Constraint) hashmapResult.get(nodeMetaDefinition));
                   ArrayList arliConstraintsChain = mainConstraint.getConstraintsChain();
                   if(arliConstraintsChain == null){
                       arliConstraintsChain = new ArrayList();
                   }
                   arliConstraintsChain.add(constraint);
                   mainConstraint.setConstraintsChain(arliConstraintsChain);
               }
               else{
                   //This is a first constraint for this MetaDefinition
                   hashmapResult.put(nodeMetaDefinition, constraint);
               }

               Node[] value = new Node[3];
               value[0] = nodeMetaVocabulary;
               value[1] = nodeControllerMetaDefinition;
               value[2] = nodeControllerMetaVocabulary;
               constraint.setEventObject(value);
           }

       }


       return hashmapResult;
   }

   public static void log(HttpServletRequest request, String sPage) {
      log.debug("Calling " + sPage + " with the following parameters");
      log.debug("number:       " + request.getParameter("number"));
      log.debug("set_default:  " + request.getParameter("set_default"));
      log.debug("submitted:    " + request.getParameter("submitted"));
      log.debug("add:          " + request.getParameter("add"));
      log.debug("remove:       " + request.getParameter("remove"));
      log.debug("close:        " + request.getParameter("close"));
      log.debug("query string: " + request.getQueryString());
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
   public static ArrayList hasTheObjectValidMetadata(Node nodeObject, ServletContext application, Node nodeUser) throws Exception{
       log.debug("---");
       log.debug("hasTheObjectValidMetadata(" + nodeObject.getNumber() + ") for user(" + nodeUser.getNumber() + ")");




       HashMap hashmapConstraints = getCachedConstraints(nodeUser, application);


       ArrayList arliResult = new ArrayList();

       for(Iterator it = hashmapConstraints.keySet().iterator(); it.hasNext(); ){
           Node nodeMetaDefinition = (Node) it.next();

           arliResult.addAll(hasTheObjectValidMetadata(nodeMetaDefinition, nodeObject, hashmapConstraints));
       }
       return arliResult;
   }



   /**
    * Check the metadata for the metadefinition
    * @param nodeMetaDefinition Node
    * @param session HttpSession
    * @return Error
    */
   public static ArrayList hasTheMetaDefinitionValidMetadata(Node nodeMetaDefinition, Node nodeObject, ServletContext application, Node nodeUser) throws Exception{
       log.debug("---");
       log.debug("hasTheMetaDefinitionValidMetadata(" + nodeMetaDefinition.getNumber() + ") for user(" + nodeUser.getNumber() + ")");
       HashMap hashmapConstraints = getCachedConstraints(nodeUser, application);
       return hasTheObjectValidMetadata(nodeMetaDefinition, nodeObject, hashmapConstraints);
   }

   public static ArrayList hasTheObjectValidMetadata(Node nodeMetaDefinition, Node nodeObject, HashMap hashmapConstraints){
       Constraint constraint = (Constraint) hashmapConstraints.get(nodeMetaDefinition);

       if(constraint != null){
           //We start this checking only for MetaDefinition nodes that have got constraints
           int iType = getIType(nodeMetaDefinition);
           Node nodeMetaData = getMetadataNode(nodeObject.getCloud(), "" + nodeObject.getNumber(), "" + nodeMetaDefinition.getNumber(), false);
           return metaHelpers[iType].check(nodeMetaDefinition, (Constraint) hashmapConstraints.get(nodeMetaDefinition), nodeMetaData);
       }

       return new ArrayList();
   }



   /**
    * We keep all constraints in application
    * Therefore any user can invalidate the structure
    *
    * We store constraints by key "activemetastandarts"
    * The reason: 10 people in a workgroup have got the same active metastandarts
    * and we have to store only one "constraint array" for them
    *
    * @param session HttpSession
    * @return HashMap
    */
   public static HashMap getCachedConstraints(Node nodeUser, ServletContext application) throws Exception{
       HashMap hashmapConstraints;
       Cloud cloud = nodeUser.getCloud();

       String sActiveMetaStandartsForThisUser = getCachedActiveMetastandards(nodeUser.getCloud(), application, null, nodeUser);


       if(application.getAttribute(APPLICATION_METASTANDARTS_TO_CONSTRAINTS_KEY) != null){

           HashMap hmapMetaStandartstoConstraints = (HashMap) application.getAttribute(APPLICATION_METASTANDARTS_TO_CONSTRAINTS_KEY);

           if(hmapMetaStandartstoConstraints.get(sActiveMetaStandartsForThisUser) != null){
               //this user has got already calculated constraints
               hashmapConstraints = (HashMap) hmapMetaStandartstoConstraints.get(sActiveMetaStandartsForThisUser);
           }
           else{
               //New user, let's calculate
               hashmapConstraints = MetaDataHelper.getConstraints(cloud, sActiveMetaStandartsForThisUser);
               hmapMetaStandartstoConstraints.put(sActiveMetaStandartsForThisUser, hashmapConstraints);
           }


       }
       else{

          HashMap hmapMetaStandartstoConstraints = new HashMap();
          hashmapConstraints = MetaDataHelper.getConstraints(cloud, sActiveMetaStandartsForThisUser);

          //We have assigned these constraints to this uniqe metastandart number
          hmapMetaStandartstoConstraints.put(sActiveMetaStandartsForThisUser, hashmapConstraints);

          application.setAttribute(APPLICATION_METASTANDARTS_TO_CONSTRAINTS_KEY, hmapMetaStandartstoConstraints);
       }
       return hashmapConstraints;
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
       log.debug("getActiveMetastandards() for user(" + sUserID + ")");
       String sResultSet = new String();

       try{
           NodeList nlWorkgroupMetaStandards = cloud.getList(sUserID,
               "people,workgroups,metastandard",
               "workgroups.number,metastandard.number",
               null,
               null, null, null, false);

           if(nlWorkgroupMetaStandards.size() > 0){
               //We have to get user-workgroup-metastandart list instead of "old algorithm"
               log.debug("getActiveMetastandards(): User(" + sUserID + ") has got group related metastandarts:");

               for(int f = 0; f < nlWorkgroupMetaStandards.size(); f++){
                   Node nodeGroup = cloud.getNode(nlWorkgroupMetaStandards.getNode(f).getStringValue("workgroups.number"));
                   Node nodeMetaStandart = cloud.getNode(nlWorkgroupMetaStandards.getNode(f).getStringValue("metastandard.number"));

                   log.debug("group(" + nodeGroup.getNumber() + ") -> " + nodeMetaStandart.getNumber());

                   if(sResultSet.length() > 0){
                       sResultSet += ",";
                   }
                   sResultSet += nodeMetaStandart.getNumber();
               }

           }
           else{
               log.debug("getActiveMetastandards(): workgroups -> metastandard for user(" + sUserID + ") are not found. Old algorithm will be used.");
           }
       }
       catch(NotFoundException e){
           log.debug("getActiveMetastandards():ERROR! workgroups -> metastandard for user(" + sUserID + ") is:" + e.toString());
       }

       //The User isn't a member of any workgroups
       //or workroups have got no related MetaStandards.
       if(sResultSet.length() == 0) {
         MetadataTreeModel metadataTreeModel = new MetadataTreeModel(cloud);
         Node nodeRootMetaStandart = (Node) metadataTreeModel.getRoot();
         sResultSet = "" + nodeRootMetaStandart.getNumber();
       }

       NodeList nlTopLevelMetaStandarts = cloud.getList(sResultSet,
           "metastandard1,metastandard2",
           "metastandard2.number",
           "metastandard2.isused='1'",
           null, null, null, false);

       for(int f = 0; f < nlTopLevelMetaStandarts.size(); f++){
           Node nodeMetaStandart = cloud.getNode(nlTopLevelMetaStandarts.getNode(f).getStringValue("metastandard2.number"));

           GrowingTreeList tree = new GrowingTreeList(Queries.createNodeQuery(nodeMetaStandart), 30, nodeMetaStandart.getNodeManager(), "posrel", "destination");
           TreeIterator it = tree.treeIterator();

           while(it.hasNext()){
               Node nodeChildMetaStandart = it.nextNode();
               if(sResultSet.length() > 0){
                   sResultSet += ",";
               }
               sResultSet += nodeChildMetaStandart.getNumber();
           }
       }
       return sResultSet;
   }




   /**
    * Caches active metastandarts in apllication
    * @param cloud Cloud
    * @param application ServletContext
    * @param unusedNode Node
    * @param nodeUser Node
    * @return String
    */

   public static String getCachedActiveMetastandards(Cloud cloud, ServletContext application, Node unusedNode, Node nodeUser){
       String sActiveMetaStandartsForThisUser;


       if(application.getAttribute(APPLICATION_USER_TO_METASTANDARTS_KEY) != null){

           HashMap hmapUserToMetaStandarts = (HashMap) application.getAttribute(APPLICATION_USER_TO_METASTANDARTS_KEY);

           if(hmapUserToMetaStandarts.get(nodeUser) != null){
               //this user has got already calculated active metastandarts
               sActiveMetaStandartsForThisUser = (String) hmapUserToMetaStandarts.get(nodeUser);
           }
           else{
               //New user, let's register
               sActiveMetaStandartsForThisUser = getActiveMetastandards(cloud, null, "" + nodeUser.getNumber());
               hmapUserToMetaStandarts.put(nodeUser, sActiveMetaStandartsForThisUser);
           }
       }
       else{
           HashMap hmapUserToMetaStandarts = new HashMap();
           sActiveMetaStandartsForThisUser = getActiveMetastandards(cloud, null, "" + nodeUser.getNumber());

           //We have assigned these MetaStandarts to User
           hmapUserToMetaStandarts.put(nodeUser, sActiveMetaStandartsForThisUser);
           application.setAttribute(APPLICATION_USER_TO_METASTANDARTS_KEY, hmapUserToMetaStandarts);
       }

       return sActiveMetaStandartsForThisUser;
   }




   /**
    * Checks exlude /include relations
    * @param nodeMetaDefinition Node
    * @return boolean
    */
   public static boolean isTheMetaVocabularyActive(Node nodeMetaVocabulary, String sActiveMetaStandarts){
       Cloud cloud = nodeMetaVocabulary.getCloud();

       //----------------------- Include -------------------------
       if(nodeMetaVocabulary.countRelations("include") > 0){
           NodeList nlIncludes = cloud.getList(sActiveMetaStandarts,
               "metastandard,include,metavocabulary",
               "include.number",
               null,
               null, null, null, false);

           if(nlIncludes.size() > 0){
               Node nodeInclude = cloud.getNode(nlIncludes.getNode(0).getStringValue("include.number"));
               log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") has got \"include\" relations. At least one of them(" + nodeInclude.getNumber() + ") is connected to active MetaStandards.");
           }
           else{
               log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") has got \"include\" relations but nan of them is connected to active metastandards. The MetaVocabulary will be disabled.");
               return false;
           }
       }
       else{
           log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") has got no \"include\" relations.");
       }

       //----------------------- Exclude -------------------------
       if(nodeMetaVocabulary.countRelations("exclude") > 0){
           NodeList nlExcludes = cloud.getList(sActiveMetaStandarts,
               "metastandard,exclude,metavocabulary",
               "exclude.number",
               null,
               null, null, null, false);

           if(nlExcludes.size() > 0){
               Node nodeInclude = cloud.getNode(nlExcludes.getNode(0).getStringValue("exclude.number"));
               log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") has got \"exclude\" relations. At least one of them(" + nodeInclude.getNumber() + ") is connected to active MetaStandards. The MetaVocabulary will be disabled.");
               return false;
           }
           else{
               log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") has got \"exclude\" relations but nan of them is connected to active metastandards.");
           }
       }
       else{
           log.debug("MetaVocabulary(" + nodeMetaVocabulary.getNumber() + ") has got no \"exclude\" relations.");
       }


       return true;
   }




   /**
    * Fills autovalues for any supported object
    * @param nodeObject Node
    */
   public static void fillAutoValues(Node nodeObject, ServletContext servletContext, Node nodeUser){
       log.debug("--------------fillAutoValues() for user(" + nodeUser.getNumber() + ") for object(" + nodeObject.getNumber() + "--------------");
       // <mm:field name="age()" />

       NodeList nlMetaDefinitions = nodeObject.getCloud().getList(getActiveMetastandards(nodeObject.getCloud(), null, "" + nodeUser.getNumber()),
           "metastandard,metadefinition",
           "metadefinition.number",
           null, null, null, null, false);

       for(int f = 0; f < nlMetaDefinitions.size(); f++){
           Node nodeMetaDefinition = nodeObject.getCloud().getNode(nlMetaDefinitions.getNode(f).getStringValue("metadefinition.number"));

           //Start handler, all exceptions to /dev/null
           //(in case users enter wrong value we do nothing)
           String sHandler = nodeMetaDefinition.getStringValue("handler");
           if((sHandler != null) && (!"".equals(sHandler))){
               log.debug("Autofiller(" + sHandler + "), object=" + nodeObject.getNumber() + "  metadefinition=" + nodeMetaDefinition.getNumber() + " trying to execute...");
               try{
                   Class classMetaDataHandler = Class.forName("nl.didactor.component.metadata.autofill.handlers." + sHandler);
                   Object[] arrobjParams = {servletContext};
                   HandlerInterface handler = (HandlerInterface) classMetaDataHandler.getConstructors()[0].newInstance(arrobjParams);

                   if(!handler.checkMetaData(nodeMetaDefinition, nodeObject)){
                       handler.addMetaData(nodeMetaDefinition, nodeObject);
                   }
                   log.debug("Autofiller(" + sHandler + ") PASSED, object=" + nodeObject.getNumber() + "  metadefinition=" + nodeMetaDefinition.getNumber());
               }
               catch(Exception e){
                   log.debug("Autofiller(" + sHandler + ") ERROR, object=" + nodeObject.getNumber() + "  metadefinition=" + nodeMetaDefinition.getNumber() + " REASON=" + e.toString());
               }
           }
       }
       log.debug("fillAutoValues() ------------END-----------");
   }

}




