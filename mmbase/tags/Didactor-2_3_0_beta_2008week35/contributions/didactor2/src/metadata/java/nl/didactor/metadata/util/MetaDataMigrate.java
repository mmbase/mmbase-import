package nl.didactor.metadata.util;

import java.util.*;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class MetaDataMigrate {

    private static Logger log = Logging.getLoggerInstance(MetaDataMigrate.class);


    public static void convert(Cloud cloud){
      convert(cloud, false);
    }
   
    public static void convert(Cloud cloud, boolean upgradeTo22){

        RelationManager rmPosrel = cloud.getRelationManager("posrel");
        NodeManager nmMetavocabulary = cloud.getNodeManager("metavocabulary");
        NodeManager nmMetastandard = cloud.getNodeManager("metastandard");

        //First cleanup the double metavocabularyies, created by prior versions of the metadata editor

        //All metavocabularies in base
        HashSet hsetNodesAll = new HashSet();
        NodeList nlNodes = cloud.getList(null,
                                "metavocabulary,posrel,metadata,related,metadefinition",
                                "metavocabulary.number",
                                null,
                                null, null, null, true);

        for(int f = 0; f < nlNodes.size(); f++){
            hsetNodesAll.add(cloud.getNode(nlNodes.getNode(f).getStringValue("metavocabulary.number")));
        }

        //All good metavocabularies
        HashSet hsetGoodNodes = new HashSet();
        nlNodes = cloud.getList(null,
                                "metadefinition,related,metavocabulary",
                                "metavocabulary.number",
                                null,
                                null, null, null, true);
        for(int f = 0; f < nlNodes.size(); f++){
            hsetGoodNodes.add(cloud.getNode(nlNodes.getNode(f).getStringValue("metavocabulary.number")));
        }

        //Only bad nodes are in the list now
        hsetNodesAll.removeAll(hsetGoodNodes);
        int iNotCorrectable = 0;
        //Correct bad nodes
        for(Iterator it = hsetNodesAll.iterator(); it.hasNext();){
            Node nodeVocabularyToCorrect = (Node) it.next();
            // find related metadata
            NodeList nlNodesToCorrect = cloud.getList("" + nodeVocabularyToCorrect.getNumber(),
                                    "metavocabulary,posrel,metadata,related,metadefinition",
                                    "metavocabulary.number,metadefinition.number,metadata.number,metavocabulary.value",
                                    null,
                                    null, null, null, false);
            for(int g = 0; g < nlNodesToCorrect.size(); g++){
                String value = nlNodesToCorrect.getNode(g).getStringValue("metavocabulary.value");
                String sMetadef = nlNodesToCorrect.getNode(g).getStringValue("metadefinition.number");
                value = value.replaceAll("'","%");
                NodeList correctMetavocNodes = cloud.getList(sMetadef,
                              "metadefinition,related,metavocabulary",
                              "metavocabulary.number",
                              "metavocabulary.value = '" + value + "'",
                              null, null, null, true);
                if(correctMetavocNodes.size()==1) {
                  // relate metadata to this metavoc, delete the bad metavoc
                  Node goodMetaVocabulary = cloud.getNode(correctMetavocNodes.getNode(0).getStringValue("metavocabulary.number"));
                  Node nodeMetaData = cloud.getNode(nlNodesToCorrect.getNode(g).getStringValue("metadata.number"));                 
                  nodeMetaData.createRelation(goodMetaVocabulary, rmPosrel).commit();
                  Node badMetaVocabulary = cloud.getNode(nlNodesToCorrect.getNode(g).getStringValue("metavocabulary.number"));
                  log.info("Corrected: " + nodeMetaData.getNumber() + " was related to " + badMetaVocabulary.getNumber() + ", now related to " + goodMetaVocabulary.getNumber());
                  badMetaVocabulary.delete(true);
                } else if(correctMetavocNodes.size()==0) {
                  iNotCorrectable++;
                  log.info("" + iNotCorrectable + ". metavocabulary " + nodeVocabularyToCorrect.getNumber() + " is a bad node, but no good metavoc with the same value '" + value 
                     + "' could be found in its metadefinition " + sMetadef + " (" + cloud.getNode(sMetadef).getStringValue("name") + ")");
                  
                } else {
                  log.error("Metavocabulary " + nodeVocabularyToCorrect.getNumber() + " is a bad node, and had more than one related metadata.");
                } 
            }
        }
        
        if(upgradeTo22) {
           //-------------- metavocabulary1 - related - metavocabulary2 ------------
           nlNodes = cloud.getList(null,
                                            "metavocabulary1,related,metavocabulary2",
                                            "metavocabulary1.number,related.number,metavocabulary2.number",
                                            null,
                                            null, null, null, true);
   
           for(int f = 0; f < nlNodes.size(); f++){
               try{
                   Node nodeSource = cloud.getNode(nlNodes.getNode(f).getStringValue("metavocabulary1.number"));
                   Node nodeDestination = cloud.getNode(nlNodes.getNode(f).getStringValue("metavocabulary2.number"));
                   Node nodeRelation = cloud.getNode(nlNodes.getNode(f).getStringValue("related.number"));
   
                   nodeSource.createRelation(nodeDestination, rmPosrel).commit();
                   nodeRelation.delete(true);
               }
               catch(NotFoundException e){
                  log.error("NotFoundException" + e);
               }
           }
   
   
           //metadefinition-related-metavocabulary =>  metadefinition-posrel-metavocabulary
           nlNodes = cloud.getList(null,
                                   "metadefinition,related,metavocabulary",
                                   "metadefinition.number,related.number,metavocabulary.number",
                                   null,
                                   null, null, null, true);
   
           for(int f = 0; f < nlNodes.size(); f++){
               try{
                   Node nodeSource = cloud.getNode(nlNodes.getNode(f).getStringValue("metadefinition.number"));
                   Node nodeDestination = cloud.getNode(nlNodes.getNode(f).getStringValue("metavocabulary.number"));
                   Node nodeRelation = cloud.getNode(nlNodes.getNode(f).getStringValue("related.number"));
   
                   nodeSource.createRelation(nodeDestination, rmPosrel).commit();
                   nodeRelation.delete(true);
               }
               catch(NotFoundException e){
                  log.error("NotFoundException" + e);
               }
           }
       }
    }
}
