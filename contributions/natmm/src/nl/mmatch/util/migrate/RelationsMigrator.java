package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import nl.mmatch.NMIntraConfig;
import org.mmbase.util.logging.*;

/*

Converts the NMIntra XML to XML that fits the NatMM objectmodel.
Can be called from jsp by: <% (new nl.mmatch.util.migrate.RelationsMigrator()).run(); %>

This is a script that will only be used once, so it does not conform to any coding standard.

*/


public class RelationsMigrator {

   private static final Logger log = Logging.getLoggerInstance(RelationsMigrator.class);

   public static String sFolder = NMIntraConfig.rootDir + "NMIntraXML/";
   // public static String sFolder = "E:/nmm/tmp/";

   public static void run() throws Exception{

      log.info("RelationsMigrator.run()");
      log.info("Importing files from " + sFolder);

      NMIntraToNatMMigrator mmm = new NMIntraToNatMMigrator();
      mmm.run();

      TreeMap tmAllRelations = new TreeMap();

      log.info("treating phaserel.xml");
      String sPhaserelContent = mmm.readingFile(sFolder + "phaserel.xml");
      sPhaserelContent = sPhaserelContent.replaceAll("&","&amp;");

      log.info("treating discountrel.xml");
      String sDiscountrelContent = mmm.readingFile(sFolder + "discountrel.xml");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<dnumber>"," dnumber=\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("</dnumber>","\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<rnumber>"," rnumber=\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("</rnumber>","\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<enddate>"," enddate=\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("</enddate>","\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<snumber>"," snumber=\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("</snumber>","\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<dir>"," dir=\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("</dir>","\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<startdate>"," startdate=\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("</startdate>","\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<type></type>","");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<body></body>","");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<title></title>","");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<threshold>-1</threshold>","");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t\t<amount>-1</amount>","");
      sDiscountrelContent = sDiscountrelContent.replaceAll("admin\">","admin\"");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\t</node>","/>");
      sDiscountrelContent = sDiscountrelContent.replaceAll("\n\n","\n");

      log.info("deleting authrel relation");
      String sInsrelContent = mmm.readingFile(sFolder + "insrel.xml");
      sInsrelContent = mmm.deletingRelation(sInsrelContent, "authrel");

      log.info("Changing relation page-dreadmore-article to pagina-readmore-artikel");
      log.info("Changing relation page-dreadmore-page to pagina-readmore-pagina");
      String sReadmoreContent = mmm.readingFile(sFolder + "readmore.xml");
      sReadmoreContent = sReadmoreContent.replaceAll("dreadmore", "readmore");
      sReadmoreContent = sReadmoreContent.replaceAll("unidirectional",
         "bidirectional");

      log.info("changing relation page-related-template to pagina-gebruikt-paginatemplate");
      ArrayList alPagina = getNodes(sFolder + "pagina.xml");
      ArrayList alPaginaTemplate = getNodes(sFolder + "paginatemplate.xml");
      sInsrelContent = mmm.movingRelations(alPagina, alPaginaTemplate, sInsrelContent,
                                       "gebruikt");

      log.info("deleting editwizards of deleted objects");
      ArrayList alDelEd = new ArrayList();
      alDelEd.add("channel");
      alDelEd.add("poll");
      alDelEd.add("people");
      alDelEd.add("media");
      alDelEd.add("site");

      ArrayList alDelRel = new ArrayList();

      String sEditwizardsContent = mmm.readingFile(sFolder + "editwizards.xml");
      Iterator it = alDelEd.iterator();
      while (it.hasNext()){
         String sBuilderName = (String)it.next();
         int index = sEditwizardsContent.indexOf("/" + sBuilderName + "/");
         int iBegNodeIndex = sEditwizardsContent.indexOf("<node number=\"",index - 110);
         int iBegRelNumberIndex = iBegNodeIndex + 14;
         int iEndRelNumberIndex = sEditwizardsContent.indexOf("\"",iBegRelNumberIndex + 1);
         String sNodeNumber = sEditwizardsContent.substring(iBegRelNumberIndex,iEndRelNumberIndex);
         alDelRel.add(sNodeNumber);
         int iEndNodeIndex = sEditwizardsContent.indexOf("</node>",iBegNodeIndex) + 9;
         sEditwizardsContent = sEditwizardsContent.substring(0,iBegNodeIndex-1) +
         sEditwizardsContent.substring(iEndNodeIndex);
      }

      File file = new File (sFolder + "editwizards.xml");
      mmm.writingFile(file,sFolder + "editwizards.xml",sEditwizardsContent);



      log.info("deleting relation of deleted editwizards from posrel.xml");

      it = alDelRel.iterator();
      String sPosrelContent = mmm.readingFile(sFolder + "posrel.xml");
      while (it.hasNext()) {
         String sNodeNumber = (String) it.next();
         int iDNIndex = sPosrelContent.indexOf("dnumber=\"" + sNodeNumber + "\"");
         int iBegNodeIndex = sPosrelContent.indexOf("<node number=", iDNIndex - 70);
         int iEndNodeIndex = sPosrelContent.indexOf("</node>", iBegNodeIndex) +
            9;
         sPosrelContent = sPosrelContent.substring(0, iBegNodeIndex-1) +
            sPosrelContent.substring(iEndNodeIndex);
      }

      log.info("changing relation users-posrel-menu to users-gebruikt-menu");

      ArrayList alUsers = getNodes(sFolder + "users.xml");
      ArrayList alMenu = getNodes(sFolder + "menu.xml");
      String [] sResultRel = mmm.movingRelations(alUsers, alMenu, sPosrelContent,
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
      sInsrelContent = mmm.addingContent(sInsrelContent, "insrel", sRelatedAdd);

      log.info("changing relation page-posrel-article to pagina-contentrel-artikel");
      ArrayList alArtikel = getNodes(sFolder + "artikel.xml");
      sResultRel = mmm.movingRelations(alPagina, alArtikel, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      String sContentrelContent = sResultRel[1];

      log.info("changing relation rubriek-posrel-images to rubriek-contentrel-images");
      ArrayList alRubriek = getNodes(sFolder + "rubriek.xml");
      ArrayList alImages = getNodes(sFolder + "images.xml");
      sResultRel = mmm.movingRelations(alRubriek, alImages, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      sContentrelContent += sResultRel[1];

      log.info("changing relation employees-posrel-page to medewerekers-contentrel-pagina");
      ArrayList alMedewerkers = getNodes(sFolder + "medewerkers.xml");
      sResultRel = mmm.movingRelations(alMedewerkers, alPagina, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      sContentrelContent += sResultRel[1];

      log.info("changing relation page-posrel-items to pagina-rolerel-shorty");
      ArrayList alShorty = getNodes(sFolder + "shorty.xml");
      sResultRel = mmm.movingRelations(alPagina, alShorty, sPosrelContent,
                                   "posrel", "rolerel");
      sPosrelContent = sResultRel[0];
      String sRolerelAdd = sResultRel[1];

      log.info("changing relation page-posrel-teasers to pagina-rolerel-teaser");
      ArrayList alTeaser = getNodes(sFolder + "teaser.xml");
      sResultRel = mmm.movingRelations(alPagina, alTeaser, sPosrelContent,
                                   "posrel", "rolerel");
      sPosrelContent = sResultRel[0];
      sRolerelAdd += sResultRel[1];

      log.info("changing relation page-posrel-vacature to pagina-contentrel-vacature");
      ArrayList alVacature = getNodes(sFolder + "vacature.xml");
      sResultRel = mmm.movingRelations(alPagina, alVacature, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultRel[0];
      sContentrelContent += sResultRel[1];

      log.info("changing relation rubriek-posrel-rubriek to rubriek-parent-rubriek");
      String [] sResultParRel = mmm.movingRelations(alRubriek, alRubriek, sPosrelContent,
                                   "posrel", "parent");
      sPosrelContent = sResultParRel[0];
      String sParentContent = sResultParRel[1];

      log.info("changing relation pagina-posrel-ads to pagina-contentrel-ads");
      ArrayList alAds = getNodes(sFolder + "ads.xml");
      String [] sResultPosRel = mmm.movingRelations(alPagina, alAds, sPosrelContent,
                                   "posrel", "contentrel");
      sPosrelContent = sResultPosRel[0];
      sContentrelContent += sResultPosRel[1];

      log.info("changing relation page-posrel-employees-related-mmbaseuser to pagina-rolerel-user");
      sResultRel = mmm.movingRelations(alPagina, alMedewerkers, sPosrelContent,
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
            int iSNBegIndex = sInsrelContent.indexOf("snumber=\"",
               iDNIndex - 25) + 9;
            int iSNEndIndex = sInsrelContent.indexOf("\"", iSNBegIndex + 1);
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
      sResultRel = mmm.movingRelations(alRubriek, alMedewerkers, sPosrelContent,
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

      String sRolerelContent = mmm.readingFile(sFolder + "rolerel.xml");

      sRolerelContent = mmm.addingContent(sRolerelContent, "rolerel", sRolerelAdd);

      tmAllRelations.put("childrel", sParentContent);
      tmAllRelations.put("contentrel", sContentrelContent);
      tmAllRelations.put("discountrel", sDiscountrelContent);
      tmAllRelations.put("insrel", sInsrelContent);
      tmAllRelations.put("posrel", sPosrelContent);
      tmAllRelations.put("phaserel", sPhaserelContent);
      tmAllRelations.put("readmore", sReadmoreContent);
      tmAllRelations.put("rolerel", sRolerelContent);

      log.info("writing relations files");
      set = tmAllRelations.entrySet();
      it = set.iterator();
      while (it.hasNext()) {
         Map.Entry me = (Map.Entry) it.next();
         String sContent = (String) me.getValue();
         String sBuilderName = (String) me.getKey();
         if ( (sBuilderName.equals("childrel")) ||
             (sBuilderName.equals("contentrel")) ) {
            mmm.creatingNewXML(sBuilderName, sContent);
         }
         else {
            file = new File(sFolder + sBuilderName + ".xml");
            mmm.writingFile(file, sFolder + sBuilderName + ".xml", sContent);
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
