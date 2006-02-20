package nl.didactor.metadata.util;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.metadata.tree.MetadataTreeModel;

public class MetaDataHelper {

   private static Logger log = Logging.getLoggerInstance(MetaDataHelper.class);

   public static int VOCABULARY_TYPE = 1;
   public static int DATE_TYPE = 2;
   public static int LANGSTRING_TYPE = 3;
   public static int DURATION_TYPE = 4;
   public static String EMPTY_VALUE = "...";
   public static String [] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   MetaHelper [] metaHelpers = null;

   public MetaDataHelper() {
      metaHelpers = new MetaHelper[] { new MetaHelper(), new MetaVocabularyHelper(), new MetaDateHelper(), new MetaLangStringHelper(), new MetaDurationHelper() };
   }

   public NodeList getLangCodes(Cloud cloud) {

      NodeList nl = cloud.getList(null,
         "metastandard,metadefinition,metavocabulary",
         "metavocabulary.value",
         "metadefinition.handler='taal'",
         "metavocabulary.value","UP",null,true);
      return nl;
   }

   public NodeList getRelatedMetaData(Cloud cloud, String sCurrentNode) {

      return cloud.getNode(sCurrentNode).getRelatedNodes("metadata");
   }

   public NodeList getRequiredMetadefs(Cloud cloud) {

     NodeList nl = cloud.getList(null,
         "metastandard,metadefinition",
         "metadefinition.name,metadefinition.number",
         "metastandard.isused='1' AND metadefinition.required='1'",
         null,null,null,true);
     NodeList nlMetadef = cloud.getNodeManager("metadefinition").getList("number='-1'",null,null);
     for(int n = 0; n< nl.size(); n++) {
         String sMetadefNode = nl.getNode(n).getStringValue("metadefinition.number");
         Node metadefNode = cloud.getNode(sMetadefNode);
         nlMetadef.add(metadefNode);
     }
     return nlMetadef;
   }

   public void log(HttpServletRequest request, String sPage) {
      log.info("Calling " + sPage + " with the following parameters");
      log.info("number:       " + request.getParameter("number"));
      log.info("set_default:  " + request.getParameter("set_default"));
      log.info("submitted:    " + request.getParameter("submitted"));
      log.info("add:          " + request.getParameter("add"));
      log.info("remove:       " + request.getParameter("remove"));
      log.info("close:        " + request.getParameter("close"));
      log.info("query string: " + request.getQueryString());
   }

   private String parametersToString(String[] arrstrParameters) {
      String sParameters = "[";
      for(int f = 0; f < arrstrParameters.length ; f++) {
         if(f>0) { sParameters += ";"; }
         sParameters += arrstrParameters[f];
      }
      sParameters += "]";
      return sParameters;
   }

   public int getIType(Node metadefNode) {
      int iType = metadefNode.getIntValue("type");
      if( iType<VOCABULARY_TYPE || iType>DURATION_TYPE ){
        log.error("The type field of metadefinition " + metadefNode.getStringValue("number") + " is " + iType + ", it should fall in [" + VOCABULARY_TYPE + "," + DURATION_TYPE + "]");
        iType = VOCABULARY_TYPE;
      }
      return iType;
   }

   public String getReason(Cloud cloud, String sMetadefNode) {
      Node metadefNode = cloud.getNode(sMetadefNode);
      int iType = getIType(metadefNode);
      return  metaHelpers[iType].getReason();
   }

   public boolean hasValidMetadata(Cloud cloud, String sCurrentNode, String sMetadefNode) {
      Node metadefNode = cloud.getNode(sMetadefNode);
      int iType = getIType(metadefNode);
      boolean isRequired = metadefNode.getStringValue("required").equals("1");
      boolean bValid = metaHelpers[iType].check(cloud, sCurrentNode, sMetadefNode, isRequired);
      if(!bValid) {
         log.debug(sCurrentNode + " has invalid metadata, because "  + metaHelpers[iType].getReason() + " for metadefinition " + sMetadefNode);
      }
      return bValid;
   }

   public boolean hasValidMetadata(Cloud cloud, String sCurrentNode) {
      boolean bValid = true;
      NodeList nl = cloud.getList(null,
         "metastandard,posrel,metadefinition",
         "metadefinition.number,metadefinition.required",
         "metastandard.isused='1'",
         null,null,null,false);
      for(int m =0; m < nl.size(); m++) {
         if(bValid) { // Metadata is still valid
            String sMetadefNode = nl.getNode(m).getStringValue("metadefinition.number");
            bValid = hasValidMetadata(cloud, sCurrentNode ,sMetadefNode);
         }
      }
      return bValid;
   }

   public boolean hasValidMetadata(Cloud cloud, String[] arrstrParameters, String sMetadefNode, ArrayList arliSizeErrors) {

      Node metadefNode = cloud.getNode(sMetadefNode);
      boolean isRequired = metadefNode.getStringValue("required").equals("1");
      int iType = getIType(metadefNode);
      boolean bValid = metaHelpers[iType].check(cloud, arrstrParameters, metadefNode, isRequired, arliSizeErrors);
      if(!bValid) {
         log.debug(parametersToString(arrstrParameters) + " has invalid metadata, because "  + metaHelpers[iType].getReason() + " for metadefinition " + sMetadefNode);
      }
      return bValid;
   }

   public Node getMetadataNode(Cloud cloud, String sCurrentNode, String sMetadefNode, boolean useDefaults) {

      Node currentNode = cloud.getNode(sCurrentNode);
      Node metadefNode = cloud.getNode(sMetadefNode);
      int iType = getIType(metadefNode);

      Node metaDataNode = null;

      NodeList nl = metaHelpers[iType].getRelatedMetaData(cloud,sCurrentNode,sMetadefNode);
      if (nl.size()==0) {

         metaDataNode = metaHelpers[iType].createMetaDataNode(cloud,currentNode,metadefNode);

      } else {

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

   public void setMetadataNode(Cloud cloud, String[] arrstrParameters, Node metadataNode, String sMetadefNode, int skipParameter) {
      Node metadefNode = cloud.getNode(sMetadefNode);
      int iType = getIType(metadefNode);
      log.debug("Using " + parametersToString(arrstrParameters) + " to set metadata " + metadataNode.getStringValue("number") + " for " +  metaHelpers[iType].toString() + " metadefinition " + sMetadefNode);
      metaHelpers[iType].set(cloud, arrstrParameters, metadataNode, metadefNode, skipParameter);
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
   public String getAliasForObject(Cloud cloud, String sObjectID, String sUserID){
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


   public String getAliasForObject(Cloud cloud, int iObjectID, int iUserID){
       return getAliasForObject(cloud, "" + iObjectID, "" + iUserID);
   }
   public String getAliasForObject(Cloud cloud, String sObjectID, int iUserID){
       return getAliasForObject(cloud, sObjectID, "" + iUserID);
   }
   public String getAliasForObject(Cloud cloud, int iObjectID, String sUserID){
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
   public String getActiveMetastandards(Cloud cloud, String sNode, String sUserID){
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
}




