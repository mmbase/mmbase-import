package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import nl.leocms.applications.NMIntraConfig;
import org.mmbase.util.logging.*;

/*

Converts the NMIntra XML to XML that fits the NatMM objectmodel.
Can be called from jsp by: <% (new nl.mmatch.util.migrate.RelationsMigrator()).run(); %>

This is a script that will only be used once, so it does not conform to any coding standard.

*/


public class RelationsMigrator {

   private static final Logger log = Logging.getLoggerInstance(RelationsMigrator.class);

   public static void run() throws Exception{

      String sFolder = NMIntraConfig.getIncomingDir() + "NMIntraXML/";
     
      log.info("RelationsMigrator.run()");
      log.info("Importing files from " + sFolder);

      MigrateUtil mu = new MigrateUtil();
      
      TreeMap tmAllRelations = new TreeMap();

      log.info("treating phaserel.xml");
      String sPhaserelContent = mu.readingFile(sFolder + "phaserel.xml");
      sPhaserelContent = sPhaserelContent.replaceAll("&","&amp;");

      log.info("Changing relation page-discountrel-article to pagina-rolerel-artikel");
      String sDiscountrelContent = mu.readingFile(sFolder + "discountrel.xml");
      int iBegNodeIndex = sDiscountrelContent.indexOf("<node");
      int iInterimIndex = sDiscountrelContent.indexOf("owner=\"admin\"") + 13;
      int iSNBegIndex = sDiscountrelContent.indexOf("snumber>");
      int iSNEndIndex = sDiscountrelContent.indexOf("<",iSNBegIndex + 10);
      int iDNBegIndex = sDiscountrelContent.indexOf("dnumber>");
      int iDNEndIndex = sDiscountrelContent.indexOf("<",iDNBegIndex + 10);

      String sRolerelAdd = sDiscountrelContent.substring(iBegNodeIndex,iInterimIndex) + " "
        + sDiscountrelContent.substring(iSNBegIndex,iSNEndIndex) + "\" "
        + sDiscountrelContent.substring(iDNBegIndex,iDNEndIndex) + "\" " +
        "rtype=\"rolerel\" dir=\"bidirectional\"/>";
      sRolerelAdd = sRolerelAdd.replaceAll("number>","number=\"");
      File file = new File(sFolder + "discountrel.xml");
      file.delete();


      log.info("deleting authrel relation");
      String sInsrelContent = mu.readingFile(sFolder + "insrel.xml");
      sInsrelContent = mu.deletingRelation(sInsrelContent, "authrel");

      log.info("Changing relation page-dreadmore-article to pagina-readmore-artikel");
      log.info("Changing relation page-dreadmore-page to pagina-readmore-pagina");
      String sReadmoreContent = mu.readingFile(sFolder + "readmore.xml");
      sReadmoreContent = sReadmoreContent.replaceAll("dreadmore", "readmore");
      sReadmoreContent = sReadmoreContent.replaceAll("unidirectional",
         "bidirectional");

      log.info("changing relation page-related-template to pagina-gebruikt-paginatemplate");
      ArrayList alPagina =  mu.getNodesFromFile(sFolder + "pagina.xml");
      ArrayList alPaginaTemplate =  mu.getNodesFromFile(sFolder + "paginatemplate.xml");
      sInsrelContent = mu.movingRelations(alPagina, alPaginaTemplate, sInsrelContent,
                                       "gebruikt");

      log.info("deleting editwizards of deleted objects");
      ArrayList alDelEd = new ArrayList();
      alDelEd.add("channel");
      alDelEd.add("poll");
      alDelEd.add("people");
      alDelEd.add("media");
      alDelEd.add("site");

      ArrayList alDelRel = new ArrayList();

      String sEditwizardsContent = mu.readingFile(sFolder + "editwizards.xml");
      Iterator it = alDelEd.iterator();
      while (it.hasNext()){
         String sBuilderName = (String)it.next();
         int index = sEditwizardsContent.indexOf("/" + sBuilderName + "/");
         iBegNodeIndex = sEditwizardsContent.indexOf("<node number=\"",index - 110);
         int iBegRelNumberIndex = iBegNodeIndex + 14;
         int iEndRelNumberIndex = sEditwizardsContent.indexOf("\"",iBegRelNumberIndex + 1);
         String sNodeNumber = sEditwizardsContent.substring(iBegRelNumberIndex,iEndRelNumberIndex);
         alDelRel.add(sNodeNumber);
         int iEndNodeIndex = sEditwizardsContent.indexOf("</node>",iBegNodeIndex) + 9;
         sEditwizardsContent = sEditwizardsContent.substring(0,iBegNodeIndex-1) +
         sEditwizardsContent.substring(iEndNodeIndex);
      }

      file = new File (sFolder + "editwizards.xml");
      mu.writingFile(file,sFolder + "editwizards.xml",sEditwizardsContent);



      log.info("deleting relation of deleted editwizards from posrel.xml");

      it = alDelRel.iterator();
      String sPosrelContent = mu.readingFile(sFolder + "posrel.xml");
      while (it.hasNext()) {
         String sNodeNumber = (String) it.next();
         int iDNIndex = sPosrelContent.indexOf("dnumber=\"" + sNodeNumber + "\"");
         iBegNodeIndex = sPosrelContent.indexOf("<node number=", iDNIndex - 70);
         int iEndNodeIndex = sPosrelContent.indexOf("</node>", iBegNodeIndex) +
            9;
         sPosrelContent = sPosrelContent.substring(0, iBegNodeIndex-1) +
            sPosrelContent.substring(iEndNodeIndex);
      }

      log.info("changing relation users-posrel-menu to users-gebruikt-menu");

      ArrayList alUsers =  mu.getNodesFromFile(sFolder + "users.xml");
      ArrayList alMenu =  mu.getNodesFromFile(sFolder + "menu.xml");
      String [] sResultRel = mu.movingRelations(alUsers, alMenu, sPosrelContent,
                                   "posrel", "gebruikt");
      sPosrelContent = sResultRel[0];
      String sRelatedAdd = sResultRel[1];

      int iBegPosIndex = sRelatedAdd.indexOf("<pos>");
      while (iBegPosIndex>-1){
         int iEndPosIndex = sRelatedAdd.indexOf("</pos>",iBegPosIndex) + 8;
         sRelatedAdd = sRelatedAdd.substring(0,iBegPosIndex) +
            sRelatedAdd.substring(iEndPosIndex);
         iBegPosIndex = sRelatedAdd.indexOf("<pos>");
      }
      sInsrelContent = mu.addingContent(sInsrelContent, "insrel", sRelatedAdd);

      log.info("changing relation page-posrel-article to pagina-contentrel-artikel");
      ArrayList alArtikel =  mu.getNodesFromFile(sFolder + "artikel.xml");
      sResultRel = mu.movingRelations(alPagina, alArtikel, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      String sContentrelContent = sResultRel[1];

      log.info("changing relation rubriek-posrel-images to rubriek-contentrel-images");
      ArrayList alRubriek =  mu.getNodesFromFile(sFolder + "rubriek.xml");
      ArrayList alImages =  mu.getNodesFromFile(sFolder + "images.xml");
      sResultRel = mu.movingRelations(alRubriek, alImages, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      sContentrelContent += sResultRel[1];

      log.info("changing relation employees-posrel-page to medewerekers-contentrel-pagina");
      ArrayList alMedewerkers =  mu.getNodesFromFile(sFolder + "medewerkers.xml");
      sResultRel = mu.movingRelations(alMedewerkers, alPagina, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      sContentrelContent += sResultRel[1];

      log.info("changing relation page-posrel-items to pagina-lijstcontentrel-linklijst");
      ArrayList alLinklijst =  mu.getNodesFromFile(sFolder + "linklijst.xml");
      sResultRel = mu.movingRelations(alPagina, alLinklijst, sPosrelContent,
                                   "posrel", "lijstcontentrel");
      sPosrelContent = sResultRel[0];
      String sLijstcontentrelContent = sResultRel[1];

      log.info("changing relation items-posrel-exturls to linklijst-posrel-link");
      ArrayList alLink =  mu.getNodesFromFile(sFolder + "link.xml");
      sResultRel = mu.movingRelations(alLinklijst, alLink, sPosrelContent,
                                   "posrel", "lijstcontentrel");
      sPosrelContent = sResultRel[0];
      sLijstcontentrelContent += sResultRel[1];

      log.info("deleting relation items-related-style");
      ArrayList alStyle =  mu.getNodesFromFile(sFolder + "style.xml");
      sInsrelContent = mu.deletingRelation(alLinklijst, alStyle, sInsrelContent);

      log.info("changing relation page-posrel-teasers to pagina-rolerel-teaser");
      ArrayList alTeaser =  mu.getNodesFromFile(sFolder + "teaser.xml");
      sResultRel = mu.movingRelations(alPagina, alTeaser, sPosrelContent,
                                   "posrel", "rolerel");
      sPosrelContent = sResultRel[0];
      sRolerelAdd += sResultRel[1];

      log.info("changing relation teaser-posrel-link to teaser-readmore-link");
      sResultRel = mu.movingRelations(alTeaser, alLink, sPosrelContent,
                             "posrel", "readmore");
      sPosrelContent = sResultRel[0];
      String sReadmoreAdd = sResultRel[1];

      log.info("changing relation page-posrel-vacature to pagina-contentrel-vacature");
      ArrayList alVacature =  mu.getNodesFromFile(sFolder + "vacature.xml");
      sResultRel = mu.movingRelations(alPagina, alVacature, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      sContentrelContent += sResultRel[1];

      log.info("changing relation rubriek-posrel-rubriek to rubriek-parent-rubriek");
      String [] sResultParRel = mu.movingRelations(alRubriek, alRubriek, sPosrelContent,
                                   "posrel", "parent");
      sPosrelContent = sResultParRel[0];
      String sParentAdd = sResultParRel[1];

      log.info("changing relation pagina-posrel-ads to pagina-contentrel-ads");
      ArrayList alAds =  mu.getNodesFromFile(sFolder + "ads.xml");
      String [] sResultPosRel = mu.movingRelations(alPagina, alAds, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultPosRel[0];
      sContentrelContent += sResultPosRel[1];

      log.info("changing relation page-posrel-employees-related-mmbaseuser to pagina-rolerel-user");
      sResultRel = mu.movingRelations(alPagina, alMedewerkers, sPosrelContent,
                                   "posrel", "rolerel");
      sPosrelContent = sResultRel[0];
      sRolerelAdd += sResultRel[1];

      iBegPosIndex = sRolerelAdd.indexOf("<pos>");
      while (iBegPosIndex>-1){
         int iEndPosIndex = sRolerelAdd.indexOf("</pos>",iBegPosIndex) + 6;
         sRolerelAdd = sRolerelAdd.substring(0,iBegPosIndex) +
            "<rol>1</rol>" + sRolerelAdd.substring(iEndPosIndex);
         iBegPosIndex = sRolerelAdd.indexOf("<pos>");
      }


      TreeMap tmEmployeesMMBaseUsers = new TreeMap();
      it = alMedewerkers.iterator();
      while (it.hasNext()) {
         String sFromNode = (String) it.next();
         int iDNIndex = sInsrelContent.indexOf("dnumber=\"" + sFromNode + "\"");
         while (iDNIndex>-1){
            iSNBegIndex = sInsrelContent.indexOf("snumber=\"",
               iDNIndex - 25) + 9;
            iSNEndIndex = sInsrelContent.indexOf("\"", iSNBegIndex + 1);
            String sToNode = sInsrelContent.substring(iSNBegIndex, iSNEndIndex);
            if (alUsers.contains(sToNode)) {
               tmEmployeesMMBaseUsers.put(sFromNode, sToNode);
            }
            iDNIndex = sInsrelContent.indexOf("dnumber=\"" + sFromNode + "\"",iDNIndex + 1);
         }
      }
      Set set = tmEmployeesMMBaseUsers.entrySet();
      it = set.iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String sOldNode = (String) me.getKey();
         String sNewNode = (String) me.getValue();
         sRolerelAdd = sRolerelAdd.replaceAll("dnumber=\"" + sOldNode +
            "\"",
            "dnumber=\"" + sNewNode + "\"");
      }

      log.info("changing relation pijler(site)-posrel-employees-related-mmbaseuser to rubriek-rolerel-user");
      sResultRel = mu.movingRelations(alRubriek, alMedewerkers, sPosrelContent,
                                   "posrel", "rolerel");
      sPosrelContent = sResultRel[0];
      sRolerelAdd += sResultRel[1];
      set = tmEmployeesMMBaseUsers.entrySet();
      it = set.iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String sOldNode = (String) me.getKey();
         String sNewNode = (String) me.getValue();
         sRolerelAdd = sRolerelAdd.replaceAll("dnumber=\"" + sOldNode +
            "\"",
            "dnumber=\"" + sNewNode + "\"");
      }

      String sRolerelContent = mu.readingFile(sFolder + "rolerel.xml");

      sRolerelContent = mu.addingContent(sRolerelContent, "rolerel", sRolerelAdd);
      sReadmoreContent = mu.addingContent(sReadmoreContent, "readmore", sReadmoreAdd);
      String sParentContent = mu.readingFile(sFolder + "childrel.xml");
      sParentContent = mu.addingContent(sParentContent,"childrel",sParentAdd);

      tmAllRelations.put("childrel", sParentContent);
      tmAllRelations.put("contentrel", sContentrelContent);
      tmAllRelations.put("insrel", sInsrelContent);
      tmAllRelations.put("lijstcontentrel", sLijstcontentrelContent);
      tmAllRelations.put("phaserel", sPhaserelContent);
      tmAllRelations.put("posrel", sPosrelContent);
      tmAllRelations.put("readmore", sReadmoreContent);
      tmAllRelations.put("rolerel", sRolerelContent);

      log.info("writing relations files");
      set = tmAllRelations.entrySet();
      it = set.iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String sContent = (String) me.getValue();
         String sBuilderName = (String) me.getKey();
         if ( (sBuilderName.equals("contentrel")) ||
             (sBuilderName.equals("lijstcontentrel")) ) {
            mu.creatingNewXML(sFolder, sBuilderName,"intranet", sContent);
         }
         else {
            file = new File(sFolder + sBuilderName + ".xml");
            mu.writingFile(file, sFolder + sBuilderName + ".xml", sContent);
         }

      }
   }

   public static ArrayList getNodes (String sFileName) throws Exception{

      ArrayList al = new ArrayList();

      FileReader fr = new FileReader(sFileName);
      BufferedReader br = new BufferedReader(fr);
      String sOneString;
      while ( (sOneString = br.readLine()) != null) {
         if (sOneString.indexOf("<node number=")>-1) {
            al.add(sOneString.substring(sOneString.indexOf("<node number=\"") + 14,
                   sOneString.indexOf("\" ")));
            }
      }
      fr.close();

      return al;
   }

   public static void main(String args[]) throws Exception{

    run();
  }

}
