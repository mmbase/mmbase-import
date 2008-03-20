package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import nl.leocms.applications.NatMMConfig;
import org.mmbase.util.logging.*;

public class WebShopToNatMMigrator {

private static final Logger log = Logging.getLoggerInstance(WebShopToNatMMigrator.class);

public void run() throws Exception{

      MigrateUtil mu = new MigrateUtil();
        
      TreeMap tmRenamingFields = new TreeMap();
      ArrayList alDeletingFields = new ArrayList();
      String sFolder =  NatMMConfig.getIncomingDir() + "Shop/";
  
      tmRenamingFields.put("title","titel");
      tmRenamingFields.put("body","omschrijving");
      tmRenamingFields.put("displaydate","embargo");
      tmRenamingFields.put("expiredate","verloopdatum");
      tmRenamingFields.put("subtitle","titel_fra");
      tmRenamingFields.put("quotetitle","titel_eng");
      tmRenamingFields.put("id","metatags");
      tmRenamingFields.put("related","gebruikt");

      alDeletingFields.add("copyright");
      alDeletingFields.add("lastmodified");
      alDeletingFields.add("source");
      alDeletingFields.add("quote");
      alDeletingFields.add("create");
      alDeletingFields.add("ttl");
      alDeletingFields.add("email");

      log.info("treating files in which we want to change fields names");

      ArrayList alChangingFiles = new ArrayList();
      alChangingFiles.add("discounts.xml");
      alChangingFiles.add("insrel.xml");
      alChangingFiles.add("products.xml");

      Iterator itChangingFiles = alChangingFiles.iterator();

      while (itChangingFiles.hasNext()){
         String sFileName = sFolder + itChangingFiles.next();
         File file = new File(sFileName);
         if (file.exists()){
            String sAllContent = mu.readingFile(sFileName);
            sAllContent = mu.renamingFields(sAllContent,tmRenamingFields);
            sAllContent = mu.deleteFields(sAllContent, alDeletingFields);
            mu.writingFile(file,sFileName,sAllContent);
         }
      }

      log.info("treating files which we want to rename and change fields");
      TreeMap tmRenamingFiles = new TreeMap();
      tmRenamingFiles.put("articles","artikel");
      tmRenamingFiles.put("products","items");
      tmRenamingFiles.put("keys","keywords");
      tmRenamingFiles.put("pages","pagina");
      tmRenamingFiles.put("paragraphs","paragraaf");
      tmRenamingFiles.put("templates","paginatemplate");
      tmRenamingFiles.put("websites","rubriek");

      Set set = tmRenamingFiles.entrySet();
      Iterator itRenamingFiles = set.iterator();

      while (itRenamingFiles.hasNext()){
         Map.Entry me = (Map.Entry)itRenamingFiles.next();
         String sFileName = sFolder + me.getKey() + ".xml";
         File file = new File(sFileName);
         if (file.exists()){
            String sAllContent = mu.readingFile(sFileName);
            sAllContent = sAllContent.replaceAll("<" + me.getKey(), "<" + me.getValue());
            sAllContent = sAllContent.replaceAll("</" + me.getKey() + ">", "</" + me.getValue() + ">");
            if (me.getKey().equals("websites")){
               sAllContent = sAllContent.replaceAll("<name>","<naam>");
               sAllContent = sAllContent.replaceAll("</name>","</naam>");
               int iIndex = sAllContent.indexOf("<expiredate>");
               if (iIndex>-1){
                  String sInterim = sAllContent.substring(0,iIndex) +
                     sAllContent.substring(sAllContent.indexOf("</expiredate>")+14);
                  sAllContent = sInterim;
               }
               iIndex = sAllContent.indexOf("<menuname>");
               if (iIndex>-1){
                  String sInterim = sAllContent.substring(0,iIndex) +
                     sAllContent.substring(sAllContent.indexOf("</menuname>")+13);
                  sAllContent = sInterim;
               }

            }
            else {
               sAllContent = mu.renamingFields(sAllContent,tmRenamingFields);
            }
            mu.writingFile(file,sFolder + me.getValue() + ".xml",sAllContent);
         }
      }

      log.info("getting pools numbers");
      String sFileName = sFolder + "pools.xml";
      File file = new File(sFileName);

      ArrayList alPools = new ArrayList();
      if (file.exists()){
         alPools = mu.getNodesFromFile(sFileName);
      }

      log.info("deleting data that we do not want to migrate");

      ArrayList alDeletingFiles = new ArrayList();
      alDeletingFiles.add("editwizards.xml");
      alDeletingFiles.add("mmbaseusers.xml");
      alDeletingFiles.add("mmevents.xml");
      alDeletingFiles.add("portals.xml");
      alDeletingFiles.add("groups.xml"); //new
      alDeletingFiles.add("jumpers.xml"); //new

      Iterator itDeletingFiles = alDeletingFiles.iterator();

      ArrayList alDeletedNodes = new ArrayList(); //new

      while (itDeletingFiles.hasNext()){
         sFileName = sFolder + itDeletingFiles.next();
         file = new File(sFileName);
         log.info("building list of nodes to be deleted");
         if (file.exists()){
            alDeletedNodes.addAll(mu.getNodesFromFile(sFileName));
         }
         file.delete();
      }
      alDeletedNodes.addAll(alPools);

      log.info("getting rubriek number");
      sFileName = sFolder + "rubriek.xml";
      file = new File(sFileName);
      String sRubriekNumber = "";
      if (file.exists()){
         sRubriekNumber = (String) mu.getNodesFromFile(sFileName).toArray()[0];
      }


      sFileName = sFolder + "posrel.xml";
      file = new File(sFileName);
      String sAllContent = mu.readingFile(sFileName);
      Iterator itPools = alPools.iterator();

      log.info("building correspondense list between pools and pages");
      TreeMap tmPoolsPagina = new TreeMap();
      ArrayList alPaginas = new ArrayList();
      while (itPools.hasNext()){
         String sPoolNumber = (String)itPools.next();
         int iDNumberIndex = sAllContent.indexOf("dnumber=\"" + sPoolNumber);
         if (iDNumberIndex>-1){
            int iSNIndex = sAllContent.indexOf("snumber",iDNumberIndex-20);
            String sPaginaNumber = sAllContent.substring(iSNIndex+9,iDNumberIndex-2);
            tmPoolsPagina.put(sPoolNumber,sPaginaNumber);
            alPaginas.add(sPaginaNumber);
         }
      }

      log.info("replacing relations pools->products to pagina->products");
      set = tmPoolsPagina.entrySet();
      Iterator it = set.iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry)it.next();
         String sPoolNumber = (String)me.getKey();
         String sPaginaNumber = (String)me.getValue();
         sAllContent = sAllContent.replaceAll("snumber=\"" + sPoolNumber,
                                              "snumber=\"" + sPaginaNumber);
      }
      mu.writingFile(file,sFileName,sAllContent);

      log.info("deleting unnecessary relations");
      ArrayList alRelations = new ArrayList();
      //alRelations.add("discountrel.xml");
      alRelations.add("insrel.xml");
      alRelations.add("posrel.xml");
      alRelations.add("readmore.xml");

      Iterator itRelations = alRelations.iterator();
      while (itRelations.hasNext()){
         sFileName = sFolder + itRelations.next();
         file = new File(sFileName);
         sAllContent = mu.readingFile(sFileName,alDeletedNodes);
         mu.writingFile(file,sFileName,sAllContent);
      }


      log.info("replacing relations rubriek->pagina->pagina to rubriek->pagina");
      sFileName = sFolder + "posrel.xml";
      file = new File(sFileName);
      sAllContent = mu.readingFile(sFileName);

      Iterator itPaginas = alPaginas.iterator();
      while (itPaginas.hasNext()){
         String sPaginaNumber = (String)itPaginas.next();
         int iDNumberIndex = sAllContent.indexOf("dnumber=\"" + sPaginaNumber);
         if (iDNumberIndex>-1){
            int iPosIndex = sAllContent.indexOf("<pos>",iDNumberIndex);
            int iPosCloseIndex = sAllContent.indexOf("</pos>",iDNumberIndex);
            String sPosrelPos = sAllContent.substring(iPosIndex+5,iPosCloseIndex);
            String sPosrelPosNew = Integer.toString(Integer.parseInt(sPosrelPos) + 6);
            int iSNIndex = sAllContent.indexOf("snumber",iDNumberIndex-20);
            if ( (!sRubriekNumber.equals("")) && (!sPosrelPos.equals("-1")) ){
               sAllContent = sAllContent.substring(0, iSNIndex + 9) + sRubriekNumber +
                  sAllContent.substring(iDNumberIndex-2);
               sAllContent = sAllContent.substring(0,iPosIndex+5) + sPosrelPosNew +
                  sAllContent.substring(iPosCloseIndex);

            }
         }
      }

      mu.writingFile(file,sFileName,sAllContent);

      /* We need to create contentrel.xml file and add there information about
         relations between pages and articles*/
         
      String sContentrelContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
         "\n" + "<contentrel exportsource=\"mmbase://127.0.0.1/natmmww/install\"" +
         " timestamp=\"" + mu.timeStamp() + "\"" +
         " >" + "\n";

      ArrayList alPages =  mu.getNodesFromFile(sFolder + "pagina.xml");
      ArrayList alArticles =  mu.getNodesFromFile(sFolder + "artikel.xml");
      TreeMap tmDeletedNodesFromPosrelXMLPositions = new TreeMap();

      String sPosrelContent = mu.readingFile(sFolder + "posrel.xml");
      String sPosrelContentAll = sPosrelContent;

      Iterator itPages = alPages.iterator();

      while (itPages.hasNext()){
         String sPageNumber = (String)itPages.next();
         int iPNPos = sPosrelContent.indexOf("snumber=\"" + sPageNumber + "\"");
         while (iPNPos!=-1){
            int iRelatedNodeEndIndex = sPosrelContent.indexOf("\" rtype=",iPNPos);
            String sRelatedNodeNumber = sPosrelContent.substring(iPNPos + 20 +
               sPageNumber.length(),iRelatedNodeEndIndex);
            if (alArticles.contains(sRelatedNodeNumber)) {
               int iNewRecordBeginIndex = sPosrelContent.indexOf("<node number",iPNPos-40);
               int iNewRecordEndIndex =  sPosrelContent.indexOf("</node>",iPNPos)+7;

               int iDelRelationNumberBeginIndex = iNewRecordBeginIndex + 14;
               int iDelRelationNumberEndIndex = sPosrelContent.indexOf("owner=",iDelRelationNumberBeginIndex)-2;
               String sRelationNumber = sPosrelContent.substring(iDelRelationNumberBeginIndex,iDelRelationNumberEndIndex);
               int iRecordLength = iNewRecordEndIndex - iNewRecordBeginIndex;
               tmDeletedNodesFromPosrelXMLPositions.put(sRelationNumber,new Integer(iRecordLength));

               String sNewRecord = sPosrelContent.substring(iNewRecordBeginIndex,iNewRecordEndIndex);
               sContentrelContent += sNewRecord + "\n";
            }
            int iNewBeingIndex = sPosrelContent.indexOf("<node",iPNPos);
            sPosrelContent = sPosrelContent.substring(iNewBeingIndex);
            iPNPos = sPosrelContent.indexOf("snumber=\"" + sPageNumber + "\"");
         }
         sPosrelContent = sPosrelContentAll;
      }

      sContentrelContent = sContentrelContent.replaceAll("posrel","contentrel");
      sContentrelContent += "</contentrel>";

      sFileName = sFolder + "contentrel.xml";
      file = new File(sFileName);

      mu.writingFile(file,sFileName,sContentrelContent);

      log.info("we need to delete from posrel.xml records migrated to contentrel.xml");

      Set setDeletedNodesFromPosrelXMLPositions = tmDeletedNodesFromPosrelXMLPositions.entrySet();
      Iterator ItDeletedNodesFromPosrelXMLPositions = setDeletedNodesFromPosrelXMLPositions.iterator();
      while (ItDeletedNodesFromPosrelXMLPositions.hasNext()){
         Map.Entry me = (Map.Entry)ItDeletedNodesFromPosrelXMLPositions.next();
         int iBeginDeletingPos = sPosrelContentAll.indexOf("<node number=\"" + me.getKey() +"\"");
         int iEndDeletingPos = iBeginDeletingPos + ((Integer)me.getValue()).intValue();
         sPosrelContentAll = sPosrelContentAll.substring(0,iBeginDeletingPos-1) +
            sPosrelContentAll.substring(iEndDeletingPos+1);
      }

      sFileName = sFolder + "posrel.xml";
      file = new File(sFileName);
      mu.writingFile(file,sFileName,sPosrelContentAll);

   }

}
