package nl.leocms.util.tools.documents;

import java.io.*;
import java.util.*;
import org.mmbase.bridge.*;
import java.io.PrintWriter;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.applications.NMIntraConfig;

public class DirReader implements Runnable
{
   
   private static final Logger log = Logging.getLoggerInstance(DirReader.class);
       
   public DirReader()
   {
   }

   /***
   * utility function for merging a subtree to one page
   */   
   public static void mergeSubtree(Cloud cloud, Node thisPage, Node subTreeDoc) {
		
		log.info("Merge subree " + subTreeDoc.getNumber() + " for page " + thisPage.getNumber());
      Stack st = new Stack();
      st.push(subTreeDoc.getStringValue("number"));
      while(!st.isEmpty()){
         String sElem = (String) st.pop();
         NodeList nodeList = cloud.getList(sElem, "documents1,posrel,documents2", "documents2.number", null, null, null, "DESTINATION", true);
         for(int d=0; d<nodeList.size(); d++) {
            String childNodeNumber = nodeList.getNode(d).getStringValue("documents2.number");
            Node childNode = cloud.getNode(childNodeNumber);
            if(childNode.getStringValue("type")!=null&&childNode.getStringValue("type").equals("dir")) {
               st.push(childNodeNumber);
            } else {
               Node newNode = cloud.getNodeManager("documents").createNode();
               newNode.setStringValue("url", childNode.getStringValue("url"));
               newNode.setStringValue("filename", childNode.getStringValue("filename"));
               newNode.setStringValue("description", childNode.getStringValue("description"));
               newNode.setStringValue("type", childNode.getStringValue("type"));
               newNode.commit();
               Relation relation = thisPage.createRelation(newNode,cloud.getRelationManager("posrel"));
               relation.commit();
            }
         }
      }
      // ** merge duplicates with same filename, but different extensions         
      Node lastDocument = null;
      String lastFileName = "";
      NodeList nodeList = cloud.getList(thisPage.getStringValue("number"), "pagina,posrel,documents", "documents.number,documents.filename", null, "documents.filename", "UP", "DESTINATION", true);
      for(int d=0; d<nodeList.size(); d++) {
         Node thisNode = nodeList.getNode(d);
         Node thisDocument = cloud.getNode(thisNode.getStringValue("documents.number"));      
         String thisFileName = thisNode.getStringValue("documents.filename");
         int dPos = thisFileName.lastIndexOf(".");
         if(dPos>-1 && dPos < thisFileName.length()) {
            String fileExtension = thisFileName.substring(dPos+1);
            thisFileName = thisFileName.substring(0,dPos);
            if(thisFileName.equals(lastFileName)) {
               lastDocument.setStringValue("filename", lastDocument.getStringValue("filename") + "." + fileExtension);
               lastDocument.commit();
               thisDocument.delete(true);
            } else {
               lastDocument = thisDocument;
               lastFileName = thisFileName;
            }
         }
      }
      // ** base documents position on documents filename
      nodeList = cloud.getList(thisPage.getStringValue("number"), "pagina,posrel,documents", "posrel.number", null, "documents.filename", "UP", "DESTINATION", true);
      for(int d=0; d<nodeList.size(); d++) {
         String posrelNumber = nodeList.getNode(d).getStringValue("posrel.number");
         Node posrelNode = cloud.getNode(posrelNumber);
         posrelNode.setIntValue("pos",d+1);
         posrelNode.commit();
      }
		log.info("Done.");
   }

   public void createNode(Cloud cloud, String sDocumentUrl, String filename, String descr,
                          String type, String alias){
      Node newNode;
      newNode = cloud.getNodeManager("documents").createNode();
      newNode.setStringValue("url", sDocumentUrl);
      newNode.setStringValue("filename", filename);
      newNode.setStringValue("description", descr);
      newNode.setStringValue("type", type);
      newNode.commit();
      if (!alias.equals("")){
         newNode.createAlias(alias);
      }
      Node parentNode=null;
      if (alias.equals("documents_root")){ // add the document_root to the page with objectalias "documents"
         parentNode = cloud.getNode("documents");
      } else {
         String sParentUrl = sDocumentUrl.substring(0,sDocumentUrl.lastIndexOf("/"));
         NodeList list = cloud.getNodeManager("documents").getList("url='" + sParentUrl + "'",null,null);
         if (list.size()==1) {
            parentNode = list.getNode(0);
         }
      }
      if(parentNode!=null) {
         Relation relation = parentNode.createRelation(newNode,cloud.getRelationManager("posrel"));
         relation.commit();
      } else {
         log.error("Could not find parent for document " + newNode.getStringValue("number"));
      }
   }

   public void run()
   {
      Cloud cloud = CloudFactory.getCloud();
      
      // ** start with deleting all documents nodes
      NodeList documentsList = cloud.getNodeManager("documents").getList(null,null,null);
      for(int i = 0; i< documentsList.size(); i++) {
         documentsList.getNode(i).delete(true);
      }
      
      String sDocumentRoot = NMIntraConfig.getSDocumentsRoot(); // root directory where the documents can be found
      String sDocumentUrl = NMIntraConfig.getSDocumentsUrl(); // the relative url of the root directory
      int iDocuments_rootLength = sDocumentRoot.length();
      File fDir = new File(sDocumentRoot);
      if (fDir.isDirectory()) {
         String sRootDirName = sDocumentRoot.substring(sDocumentRoot.lastIndexOf("/")+1);
         createNode(cloud, sDocumentUrl, sRootDirName, "The root of the file tree", "dir","documents_root");
         Stack st = new Stack();
         String[] sArContent = fDir.list();
         for(int i=0;i<sArContent.length;i++){
            st.push(sDocumentRoot + "/" + sArContent[i]);
         }
         while(!st.isEmpty()){
            String sElem = (String) st.pop();
            String sObjectName = sElem.substring(sElem.lastIndexOf("/")+1);
            File fObject = new File(sElem);
            String sEnd = sElem.substring(iDocuments_rootLength+1);
            String sRealUrl = sDocumentUrl + "/" + sEnd;
            if(fObject.isDirectory()){
               createNode(cloud, sRealUrl, sObjectName, "", "dir","");
               sArContent = fObject.list();
               for(int i=0;i<sArContent.length;i++){
                  st.push(sElem + "/" + sArContent[i]);
               }
            } else {
               createNode(cloud, sRealUrl, sObjectName, "", "file","");
            }
         }
      }
      else {
         createNode(cloud, sDocumentUrl, sDocumentRoot, "", "file","documents_root");
         log.error("The " + sDocumentRoot + " directory does not exist");
      }
   }
}
