package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import nl.mmatch.NMIntraConfig;
import org.mmbase.util.logging.*;

/*

Converts the NMIntra XML to XML that fits the NatMM objectmodel.
This script is called from RelationMigrator.run()

This is a script that will only be used once, so it does not conform to any coding standard.

*/

public class NMIntraToNatMMigrator {

  private static final Logger log = Logging.getLoggerInstance(NMIntraToNatMMigrator.class);

  public static String sFolder = NMIntraConfig.rootDir + "NMIntraXML/";
  // public static String sFolder = "E:/nmm/tmp/";

  public static void run() throws Exception{

      log.info("NMIntraToNatMMigrator.run()");
      TreeMap tmAllData = new TreeMap();

      log.info("deleting data that we do not want to migrate");

      ArrayList alDeletingFiles = new ArrayList();
      alDeletingFiles.add("articles.xml");
      alDeletingFiles.add("channel.xml");
      alDeletingFiles.add("chatter.xml");
      alDeletingFiles.add("makers.xml");
      alDeletingFiles.add("media.xml");
      alDeletingFiles.add("message.xml");
      alDeletingFiles.add("people.xml");
      alDeletingFiles.add("poll.xml");
      alDeletingFiles.add("typedef.xml"); //is system data
      alDeletingFiles.add("urls.xml"); // doesn't contain any info

      Iterator itDeletingFiles = alDeletingFiles.iterator();

      while (itDeletingFiles.hasNext()) {
         String sFileName = sFolder + itDeletingFiles.next();
         File file = new File(sFileName);
         file.delete();
      }

      log.info("find articles that should become formulier");
      String sContent = readingFile(sFolder + "templates.xml");
      tmAllData.put("templates",sContent);
      int index = sContent.indexOf("<linktext>formulier</linktext>");
      int iBegNodeIndex = sContent.indexOf("<node number=\"",index - 150);
      int iBegNodeNumberIndex = iBegNodeIndex + 14;
      int iEndNodeNumberIndex = sContent.indexOf("\"",iBegNodeNumberIndex + 1);
      String sTemplateNumber = sContent.substring(iBegNodeNumberIndex,iEndNodeNumberIndex);
      log.info("number of formulier template is " + sTemplateNumber);

      sContent = readingFile(sFolder + "page.xml");
      tmAllData.put("page",sContent);
      ArrayList alPages = getNodes(sContent);
      ArrayList alPageRelatedTemplate = new ArrayList();
      String sInsrelContent = readingFile(sFolder + "insrel.xml");
      int iDNIndex = sInsrelContent.indexOf("dnumber=\"" + sTemplateNumber + "\"");
      while (iDNIndex>-1){
        iBegNodeNumberIndex = sInsrelContent.indexOf("snumber=\"",iDNIndex - 25) + 9;
        iEndNodeNumberIndex = sInsrelContent.indexOf("\"",iBegNodeNumberIndex + 1);
        String sNodeNumber = sInsrelContent.substring(iBegNodeNumberIndex,iEndNodeNumberIndex);
        if (alPages.contains(sNodeNumber)){
          alPageRelatedTemplate.add(sNodeNumber);
          log.info("found a formulier page " + sNodeNumber);
        }
        iDNIndex = sInsrelContent.indexOf("dnumber=\"" + sTemplateNumber + "\"",iDNIndex + 1);
      }

      String sArticleContent = readingFile(sFolder + "article.xml");
      ArrayList alArticle = getNodes(sArticleContent);
      String sPosrelContent = readingFile(sFolder + "posrel.xml");
      ArrayList alFormulier = new ArrayList();
      Iterator it = alPageRelatedTemplate.iterator();
      while (it.hasNext()){
        String sPageNumber = (String)it.next();
        int iSNIndex = sPosrelContent.indexOf("snumber=\"" + sPageNumber + "\"");
        while (iSNIndex>-1){
          iBegNodeNumberIndex = sPosrelContent.indexOf("dnumber=\"", iSNIndex) + 9;
          iEndNodeNumberIndex = sPosrelContent.indexOf("\"",iBegNodeNumberIndex + 1);
          String sNodeNumber = sPosrelContent.substring(iBegNodeNumberIndex,iEndNodeNumberIndex);
          if (alArticle.contains(sNodeNumber)) {
            log.info("found a formulier article " + sNodeNumber);
            alFormulier.add(sNodeNumber);
            alArticle.remove(sNodeNumber);
          }
        iSNIndex = sPosrelContent.indexOf("snumber=\"" + sPageNumber + "\"",iSNIndex + 1);
        }
      }

      String sFormulierContent = "";
      it = alFormulier.iterator();
      while (it.hasNext()){
        String sFormulierNode = (String)it.next();
        iBegNodeIndex = sArticleContent.indexOf("<node number=\"" + sFormulierNode + "\"");
        int iEndNodeIndex = sArticleContent.indexOf("</node>",iBegNodeIndex) + 9;
        sFormulierContent += sArticleContent.substring(iBegNodeIndex,iEndNodeIndex);
        sArticleContent = sArticleContent.substring(0,iBegNodeIndex) + sArticleContent.substring(iEndNodeIndex);
      }

      tmAllData.put("article",sArticleContent);
      tmAllData.put("formulier",sFormulierContent);

      log.info("deleting not necessary fields in files");
      TreeMap tmDeletingFields = new TreeMap();
      tmDeletingFields.put("answer","layout;total_answers");
      tmDeletingFields.put("formulier","source;quote;creditline;quote_title;" +
      "transmissiondate;expiredate");
      tmDeletingFields.put("article","creditline;quote;quote_title;copyright");
      tmDeletingFields.put("employees","position;intro;deptdescr;progdescr;showinfo");
      tmDeletingFields.put("locations","city2;country2;mobile");
      tmDeletingFields.put("pijler","description");
      tmDeletingFields.put("readmore","readmore1");
      tmDeletingFields.put("site","description");

      Set set = tmDeletingFields.entrySet();
      it = set.iterator();
      while (it.hasNext()){
        Map.Entry me = (Map.Entry)it.next();
        String sBuilderName = (String)me.getKey();
        ArrayList alThisDeletedFields = new ArrayList();
        String fields = (String)me.getValue();
        int iSemicolonIndex = fields.indexOf(";");
        while (iSemicolonIndex>-1){
          alThisDeletedFields.add(fields.substring(0,iSemicolonIndex));
          fields = fields.substring(iSemicolonIndex+1);
          iSemicolonIndex = fields.indexOf(";");
        }
        alThisDeletedFields.add(fields);

        if (!tmAllData.containsKey(sBuilderName)){
           sContent = readingFile(sFolder + sBuilderName + ".xml");
        } else {
          sContent = (String)tmAllData.get(sBuilderName);
        }

        Iterator it1 = alThisDeletedFields.iterator();
        while (it1.hasNext()){
          String sField = (String)it1.next();
          int iBegIndex = sContent.indexOf("<" + sField + ">");
          while (iBegIndex>-1){
            int iEndEndex = sContent.indexOf("</" + sField + ">",iBegIndex);
            iEndEndex = sContent.indexOf("<",iEndEndex + sField.length() + 3);
            sContent = sContent.substring(0,iBegIndex) + sContent.substring(iEndEndex);
            iBegIndex = sContent.indexOf("<" + sField + ">");
          }
        }

        if (sBuilderName.equals("readmore")){
           log.info("in readmore.xml replacing & to &amp;");
           sContent = sContent.replaceAll("&","&amp;");
           log.info("in readmore.xml replacing \"inactive\" value readmore2 field to empty");
           sContent = sContent.replaceAll("<readmore2>inactive</readmore2>","<readmore2></readmore2>");
        }
        tmAllData.put(sBuilderName, sContent);
      }

      String sParagraphContent = readingFile(sFolder + "paragraph.xml");
      tmAllData.put("paragraph",sParagraphContent);
      ArrayList alParagraphs = getNodes(sParagraphContent);
      sPosrelContent = deletingRelation(alFormulier,alParagraphs,sPosrelContent);

      log.info("finding page nodes that should become rubriek nodes");

      ArrayList alPaginaToRubriek = new ArrayList();
      int iDelRelIndex = sPosrelContent.indexOf("dposrel");
      while (iDelRelIndex>-1){
        int iBegPageIndex = sPosrelContent.indexOf("snumber=\"",iDelRelIndex - 75) + 9;
        int iEndPageIndex = sPosrelContent.indexOf("\"",iBegPageIndex + 1) ;
        String sPaginaToRubriek = sPosrelContent.substring(iBegPageIndex,iEndPageIndex);
        if (!alPaginaToRubriek.contains(sPaginaToRubriek)){
          alPaginaToRubriek.add(sPaginaToRubriek);
        }
        iDelRelIndex = sPosrelContent.indexOf("dposrel",iDelRelIndex + 1);
      }
      sPosrelContent = sPosrelContent.replaceAll("dposrel","posrel");
      sPosrelContent = sPosrelContent.replaceAll("unidirectional","bidirectional");

      tmAllData.put("posrel",sPosrelContent );

      String sPageContent = readingFile(sFolder + "page.xml");
      String sRubriekContent = "";
      it = alPaginaToRubriek.iterator();
      while (it.hasNext()){
        String sRubriek = (String)it.next();
        iBegNodeIndex = sPageContent.indexOf("<node number=\"" + sRubriek);
        int iEndNodeIndex = sPageContent.indexOf("</node>",iBegNodeIndex) + 9;
        sRubriekContent += sPageContent.substring(iBegNodeIndex,iEndNodeIndex);
        sPageContent = sPageContent.substring(0,iBegNodeIndex) + sPageContent.substring(iEndNodeIndex);
      }

      tmAllData.put("page",sPageContent);
      tmAllData.put("rubriek",sRubriekContent);

      log.info("renaming fields");
      TreeMap tmRenamingFields = new TreeMap();
      tmRenamingFields.put("answer","answer:waarde;description:tekst");
      tmRenamingFields.put("article","editors_note:metatags;expiredate:verloopdatum;" +
      "introduction:intro;source:bron;subtitle:titel_fra;title:titel;transmissiondate:embargo");
      tmRenamingFields.put("attachments","title:titel;description:omschrijving");
      tmRenamingFields.put("companies","name:naam;description:omschrijving");
      tmRenamingFields.put("departments","name:naam;description:omschrijving");
      tmRenamingFields.put("editwizardgroups","name:naam;description:omschrijving");
      tmRenamingFields.put("editwizards","title:naam");
      tmRenamingFields.put("employees","location:account;birthday:dayofbirth;description:omschrijving");
      tmRenamingFields.put("formulier","copyright:titel_de;subtitle:titel_fra;title:titel;" +
      "editors_note:emailadressen;introduction:omschrijving");
      tmRenamingFields.put("images","title:titel;description:omschrijving");
      tmRenamingFields.put("items","name:titel;description:omschrijving");
      tmRenamingFields.put("locations","name:naam;address:bezoekadres;postalcode:bezoekadres_postcode;" +
      "city:plaatsnaam;country:land;address2:postbus;postalcode2:postbus_postcode;" +
      "phone:telefoonnummer;fax:faxnummer;description:omschrijving");
      tmRenamingFields.put("mmbaseusers","username:account");
      tmRenamingFields.put("page","title:titel;subtitle:titel_fra");
      tmRenamingFields.put("paragraph","title:titel;body:tekst");
      tmRenamingFields.put("pijler","title:naam;subtitle:naam_eng");
      tmRenamingFields.put("projects","name:titel;description:omschrijving");
      tmRenamingFields.put("providers","name:naam;address:bezoekadres;postalcode:bezoekadres_postcode;" +
      "city:plaatsnaam;country:land;phone:telefoonnummer;fax:faxnummer;description:omschrijving");
      tmRenamingFields.put("rubriek","title:naam;subtitle:naam_eng");
      tmRenamingFields.put("questions","title:label;body:omschrijving_fra;required:verplicht");
      tmRenamingFields.put("site","title:naam");
      tmRenamingFields.put("shop_items","title:titel;displaydate:embargo;expiredate:verloopdatum");
      tmRenamingFields.put("style","title:titel;description:omschrijving");
      tmRenamingFields.put("teasers","title:titel;body:omschrijving;expiredate:verloopdatum;" +
      "transmissiondate:embargo");
      tmRenamingFields.put("templates","linktext:naam;description:omschrijving");
      tmRenamingFields.put("users","firstname:voornaam;lastname:achternaam;email:emailadres");

      String sEditwizardsContent = readingFile(sFolder + "editwizards.xml");

      log.info("treating formulier editwizard");

      sEditwizardsContent = sEditwizardsContent.replaceAll("wizards/article/article_form&amp;" +
      "startnodes=formulieren_template&amp;" +
      "nodepath=templates,page,article&amp;" +
      "fields=article.title&amp;" +
      "orderby=article.title&amp;",
      "wizards/formulier/formulier&amp;" +
      "nodepath=formulier&amp;" +
      "fields=titel&amp;" +
      "orderby=titel&amp;");

      log.info("treating subrubriek editwizard");

      sEditwizardsContent = sEditwizardsContent.replaceAll("wizards/page/page&amp;" +
      "nodepath=pijler,posrel,page,dposrel,page&amp;" +
      "fields=page.title&amp;" +
      "distinct=true&amp;" +
      "orderby=page.title&amp;",
      "wizards/rubriek/rubriek&amp;" +
      "nodepath=rubriek1,parent,rubriek2&amp;" +
      "fields=rubriek2.naam&amp;" +
      "distinct=true&amp;" +
      "orderby=rubriek2.naam&amp;");

     set = tmRenamingFields.entrySet();
     it = set.iterator();
     while (it.hasNext()){
        sContent = "";
        Map.Entry me = (Map.Entry)it.next();
        String sBuilderName = (String)me.getKey();
        if (sBuilderName.equals("editwizards")){
          sContent = sEditwizardsContent;
        } else {
          if (tmAllData.containsKey(sBuilderName)) {
            sContent = (String) tmAllData.get(sBuilderName);
          }
          else {
            sContent = readingFile(sFolder + sBuilderName + ".xml");
          }
        }
        String fields = (String)me.getValue();
        TreeMap tmThisRenamingFields = new TreeMap();
        int iSemicolonIndex = fields.indexOf(";");
        while (iSemicolonIndex>-1){
          String sThisPair = fields.substring(0,iSemicolonIndex);
          int iColonIndex = sThisPair.indexOf(":");
          if (iColonIndex>-1){
            tmThisRenamingFields.put(sThisPair.substring(0,iColonIndex),sThisPair.substring(iColonIndex+1));
          }
          fields = fields.substring(iSemicolonIndex+1);
          iSemicolonIndex = fields.indexOf(";");
        }
        int iColonIndex = fields.indexOf(":");
        if (iColonIndex>-1){
          tmThisRenamingFields.put(fields.substring(0,iColonIndex),fields.substring(iColonIndex+1));
        }
        sContent = renamingFields(sContent, tmThisRenamingFields);


        if (!sBuilderName.equals("editwizards")){
          int iBegIndex = sEditwizardsContent.indexOf("nodepath=" + sBuilderName + "&amp;");
          while (iBegIndex>-1){
            int iEndIndex = sEditwizardsContent.indexOf("</url>",iBegIndex) + 6;
            String sWork =  sEditwizardsContent.substring(iBegIndex,iEndIndex);
            Set set1 = tmThisRenamingFields.entrySet();
            Iterator it1 = set1.iterator();
            while (it1.hasNext()) {
              me = (Map.Entry)it1.next();
              String sOld = (String)me.getKey();
              if (sWork.indexOf(sOld)>-1){
                String sNew = (String)me.getValue();
                sWork = sWork.replaceAll(sOld, sNew);
              }
            }
            sEditwizardsContent = sEditwizardsContent.substring(0,iBegIndex) +
                sWork + sEditwizardsContent.substring(iEndIndex);
            iBegIndex = sEditwizardsContent.indexOf("nodepath=" + sBuilderName + "&amp;",iEndIndex);
          }

          tmAllData.put(sBuilderName, sContent);

        }
      }

      log.info("#NZ# analyz of artikel and paragraaf titels will be added after migration");

      log.info("analyzing editwizards.url");

      ArrayList alEdwFields = new ArrayList();

      alEdwFields.add("wizard");
      alEdwFields.add("startnodes");
      alEdwFields.add("nodepath");
      alEdwFields.add("objectnumber");
      alEdwFields.add("fields");
      alEdwFields.add("orderby");
      alEdwFields.add("directions");
      alEdwFields.add("search");
      alEdwFields.add("searchfields");
      alEdwFields.add("pagelength");
      alEdwFields.add("maxpagecount");
      alEdwFields.add("distinct");

      int iBegUrlIndex = sEditwizardsContent.indexOf("<url>");
      while (iBegUrlIndex>-1){
        int iEndUrlIndex = sEditwizardsContent.indexOf("</url>",iBegUrlIndex) + 6;
        String sUrlValue = sEditwizardsContent.substring(iBegUrlIndex,iEndUrlIndex);
        String sAddInfo = "";
        if (sUrlValue.indexOf("mmbase")>-1){
          int iDotIndex = sUrlValue.indexOf(".");
          String sType = sUrlValue.substring(29,iDotIndex);
          if (sType.equals("wizard")){
            sAddInfo = "<type>wizard</type>\n\t\t";
          } else {
            sAddInfo = "<type>list</type>\n\t\t";
          }
          it = alEdwFields.iterator();
          while (it.hasNext()){
            String sField = (String)it.next();
            String sFieldValue = "";
            int iBegFieldIndex = sUrlValue.indexOf(sField + "=");
            if (iBegFieldIndex>-1){
              iBegFieldIndex +=  sField.length() + 1;
              int iEndFieldIndex = sUrlValue.indexOf("&amp;",iBegFieldIndex);
              if (iEndFieldIndex==-1){
                iEndFieldIndex = sUrlValue.indexOf("</url>");
              }
              sFieldValue = sUrlValue.substring(iBegFieldIndex,iEndFieldIndex);
              if (sField.equals("distinct")) {
                if (sFieldValue.equals("true")) {
                  sAddInfo += "<m_distinct>1</m_distinct>\n\t\t";
                } else {
                  sAddInfo += "<m_distinct>0</m_distinct>\n\t\t";
                }
              } else {
                sAddInfo += "<" + sField + ">" + sFieldValue + "</" + sField + ">\n\t\t";
              }
            }
          }
        } else {
          sAddInfo = "<type>jsp</type>\n\t\t<wizard>" + sUrlValue.substring(5,sUrlValue.length()-6) + "</wizard>";
        }
        sEditwizardsContent = sEditwizardsContent.substring(0,iBegUrlIndex) +
            sAddInfo + sEditwizardsContent.substring(iEndUrlIndex);
        sEditwizardsContent = sEditwizardsContent.replaceFirst("\n\t\t\n\t\t<description>","\n\t\t<description>");
        iBegUrlIndex = sEditwizardsContent.indexOf("<url>");
      }

      sEditwizardsContent = sEditwizardsContent.replaceAll("article.title","article.titel");
      sEditwizardsContent = sEditwizardsContent.replaceAll("article.transmissiondate","article.embargo");
      sEditwizardsContent = sEditwizardsContent.replaceAll("article.expiredate","article.verloopdatum");
      sEditwizardsContent = sEditwizardsContent.replaceAll("locations.name","locations.naam");
      sEditwizardsContent = sEditwizardsContent.replaceAll("page.title","page.titel");
      sEditwizardsContent = sEditwizardsContent.replaceAll("page.subtitle","page.titel_fra");

      TreeMap tmRenamingFiles = new TreeMap();
      tmRenamingFiles.put("answer","formulierveldantwoord");
      tmRenamingFiles.put("article","artikel");
      tmRenamingFiles.put("departments","afdelingen");
      tmRenamingFiles.put("editwizardgroups","menu");
      tmRenamingFiles.put("employees","medewerkers");
      tmRenamingFiles.put("exturls","link");
      tmRenamingFiles.put("items","shorty");
      tmRenamingFiles.put("mmbaseusers","users");
      tmRenamingFiles.put("page","pagina");
      tmRenamingFiles.put("paragraph","paragraaf");
      tmRenamingFiles.put("questions","formulierveld");
      tmRenamingFiles.put("shop_items","items");
      tmRenamingFiles.put("teasers","teaser");
      tmRenamingFiles.put("templates","paginatemplate");

      set = tmRenamingFiles.entrySet();
      it = set.iterator();
      while (it.hasNext()){
        Map.Entry me = (Map.Entry)it.next();
        String sOld = (String)me.getKey();
        String sNew = (String)me.getValue();
        sEditwizardsContent = sEditwizardsContent.replaceAll(sOld,sNew);
      }

      sEditwizardsContent = sEditwizardsContent.replaceAll("cleanartikels","cleanarticles");
      sEditwizardsContent = sEditwizardsContent.replaceAll("medewerkers.jsp","employees.jsp");
      sEditwizardsContent = sEditwizardsContent.replaceAll("shopshorty","items");
      sEditwizardsContent = sEditwizardsContent.replaceAll("shop_shorty","shop_items");
      sEditwizardsContent = sEditwizardsContent.replaceAll("paginalength","pagelength");
      sEditwizardsContent = sEditwizardsContent.replaceAll("maxpaginacount","maxpagecount");
      sEditwizardsContent = sEditwizardsContent.replaceAll("homepagina","homepage");

      sEditwizardsContent = sEditwizardsContent.replaceAll("wizards/","config/");

      tmAllData.put("editwizards",sEditwizardsContent);

      log.info("treating educations.xml");
      sContent = readingFile(sFolder + "educations.xml");
      sContent = buildingUrlsTitels(sContent);
      tmAllData.put("educations",sContent);

      log.info("treating exturls.xml");
      sContent = readingFile(sFolder + "exturls.xml");
      sContent = buildingUrlsTitels(sContent);

      int iBegPosttextIndex = sContent.indexOf("<posttext>");
      while (iBegPosttextIndex>-1){
         int iBegPretextIndex = sContent.indexOf("<pretext>");
         int iEndPretextIndex = sContent.indexOf("</pretext>");
         String sPretext = sContent.substring(iBegPretextIndex + 9, iEndPretextIndex);
         int iEndPosttextIndex = sContent.indexOf("</posttext>");
         String sPosttext = sContent.substring(iBegPosttextIndex + 10, iEndPosttextIndex);
         String sAdd = "";
         if (!sPretext.equals("")){
           sAdd = sPretext;
           if (!sPosttext.equals("")){
             sAdd += "; " + sPosttext;
           }
         } else {
           if (!sPosttext.equals("")){
             sAdd = sPosttext;
           }
         }

         sContent = sContent.substring(0,iBegPosttextIndex-1) +
         "\t<alt_tekst>" + sAdd +
         "</alt_tekst>" + sContent.substring(iEndPretextIndex+10);
         iBegPosttextIndex = sContent.indexOf("<posttext>");
      }

      tmAllData.put("exturls",sContent);

      log.info("analyzing enrolldate");

      sContent = (String)tmAllData.get("employees");
      int iBegEnrolldateIndex = sContent.indexOf("<enrolldate>");
      int iEndEnrolldateIndex = sContent.indexOf("</enrolldate>");
      while ((iBegEnrolldateIndex>-1)&&(iEndEnrolldateIndex>-1)){
        String sEnrolldateValue = sContent.substring(iBegEnrolldateIndex + 12,iEndEnrolldateIndex);
        if (sEnrolldateValue.equals("2114377200")) {
          sContent = sContent.substring(0,iBegEnrolldateIndex) + "<importstatus>inactive</importstatus>" +
              sContent.substring(iEndEnrolldateIndex + 13);
        } else if (sEnrolldateValue.equals("0")) {
          sContent = sContent.substring(0,iBegEnrolldateIndex) + "<importstatus>active</importstatus>" +
              sContent.substring(iEndEnrolldateIndex + 13);
        } else {
          sContent = sContent.substring(0,iBegEnrolldateIndex) +
              sContent.substring(iEndEnrolldateIndex + 13);
        }
        iBegEnrolldateIndex = sContent.indexOf("<enrolldate>");
        iEndEnrolldateIndex = sContent.indexOf("</enrolldate>");
      }

      tmAllData.put("employees",sContent);

      log.info("joining rubriek, pijler and site into rubriek");

      String sPijlerContent = (String)tmAllData.get("pijler");
      tmAllData.remove("pijler");
      int iBegInfoIndex = sPijlerContent.indexOf("<node number");
      int iEndInfoIndex = sPijlerContent.indexOf("</pijler>");
      sPijlerContent = sPijlerContent.substring(iBegInfoIndex,iEndInfoIndex);


      String sSiteContent = (String)tmAllData.get("site");
      tmAllData.remove("site");
      iBegInfoIndex = sSiteContent.indexOf("<node number");
      iEndInfoIndex = sSiteContent.indexOf("</site>");
      sSiteContent = sSiteContent.substring(iBegInfoIndex,iEndInfoIndex);

      sRubriekContent = (String)tmAllData.get("rubriek");
      sRubriekContent += sPijlerContent + sSiteContent;

      tmAllData.put("rubriek",sRubriekContent);

      File file = new File(sFolder + "pijler.xml");
      file.delete();

      file = new File(sFolder + "site.xml");
      file.delete();

      log.info("joining mmbaseusers and users into users");
      String sUsersContent = (String)tmAllData.get("users");
      String sMMBaseUsersContent = (String)tmAllData.get("mmbaseusers");
      tmAllData.remove("mmbaseusers");
      iBegInfoIndex = sMMBaseUsersContent.indexOf("<node number");
      iEndInfoIndex = sMMBaseUsersContent.indexOf("</mmbaseusers>");
      sMMBaseUsersContent = sMMBaseUsersContent.substring(iBegInfoIndex,iEndInfoIndex);

      sUsersContent = addingContent(sUsersContent,"users",sMMBaseUsersContent);

      tmAllData.put("users",sUsersContent);

      log.info("making changes in renaming files and writing them");

      set = tmRenamingFiles.entrySet();
      it = set.iterator();
      while (it.hasNext()){
        Map.Entry me = (Map.Entry)it.next();
        String sOldBuilderName = (String)me.getKey();
        String sNewBuilderName = (String)me.getValue();
        if (tmAllData.containsKey(sOldBuilderName)) {
          sContent = (String)tmAllData.get(sOldBuilderName);
          tmAllData.remove(sOldBuilderName);
        } else {
          sContent = readingFile(sFolder + sOldBuilderName + ".xml");
        }
        if (sOldBuilderName.equals("answer")){
          index = sContent.lastIndexOf("</waarde>");
          sContent = sContent.substring(0,index) + "</answer>";
        }
        sContent = sContent.replaceAll("<" + sOldBuilderName,"<" + sNewBuilderName);
        sContent = sContent.replaceAll("</" + sOldBuilderName,"</" + sNewBuilderName);
        file = new File(sFolder + sOldBuilderName + ".xml");
        writingFile(file,sFolder + sNewBuilderName + ".xml",sContent);
      }

      log.info("writing the rest files");
      set = tmAllData.entrySet();
      it = set.iterator();
      while (it.hasNext()){
        Map.Entry me = (Map.Entry)it.next();
        String sBuilderName = (String)me.getKey();
        file = new File(sFolder + sBuilderName + ".xml");
        if (sBuilderName.equals("formulier")){
          creatingNewXML("formulier", (String) me.getValue());
        } else if (sBuilderName.equals("rubriek")){
          creatingNewXML("rubriek", (String) me.getValue());
        } else {
          writingFile(file, sFolder + sBuilderName + ".xml", (String) me.getValue());
        }
      }
  }

  public static ArrayList getNodes (String sContent) throws Exception{

      ArrayList al = new ArrayList();
      int iBegNodeIndex = sContent.indexOf("<node number=\"");
      while (iBegNodeIndex>-1){
        int iBegNodeNumberIndex = iBegNodeIndex + 14;
        int iEndNodeNumberIndex = sContent.indexOf("\"",iBegNodeNumberIndex);
        String sNodeNumber = sContent.substring(iBegNodeNumberIndex,iEndNodeNumberIndex);
        al.add(sNodeNumber);
        iBegNodeIndex = sContent.indexOf("<node number=\"",iEndNodeNumberIndex);
      }

      return al;
   }

   public static String readingFile(String sFileName) throws Exception{

      FileInputStream file = new FileInputStream (sFileName);
      DataInputStream in = new DataInputStream (file);
      byte[] b = new byte[in.available ()];
      in.readFully (b);
      in.close ();
      String sResult = new String (b, 0, b.length, "UTF-8");

      return sResult;
   }

   public static String deletingRelation(String sContent, String sDelRel){

     int iDelRelIndex = sContent.indexOf(sDelRel);
     while (iDelRelIndex>-1){
       int iBegNodeIndex = sContent.indexOf("<node number=",iDelRelIndex-75) - 1;
       int iEndNodeIndex = sContent.indexOf("</node>",iDelRelIndex) + 9;
       sContent = sContent.substring(0,iBegNodeIndex) + sContent.substring(iEndNodeIndex);
       iDelRelIndex = sContent.indexOf(sDelRel);
     }
     return sContent;
   }

   public static void writingFile(File file, String sNewFile, String sAllContent) throws Exception{

   file.delete();
   file = new File(sNewFile);
   file.createNewFile();
   FileOutputStream fos = new FileOutputStream(sNewFile);
   OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
   BufferedWriter bw = new BufferedWriter(osw);
   bw.write(sAllContent);
   bw.close();

   }

   public static String renamingFields(String sContent,TreeMap tmRenamingFields){

      Set set = tmRenamingFields.entrySet();
      Iterator it = set.iterator();

      while (it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         if (sContent.indexOf("<" + me.getKey() + ">")>-1){
            sContent = sContent.replaceAll("<" + me.getKey() + ">","<" + me.getValue() + ">");
            sContent = sContent.replaceAll("</" + me.getKey() + ">","</" + me.getValue() + ">");
         }
      }

      return sContent;
   }

   public static String buildingUrlsTitels(String sContent){

     int iBegNameIndex = sContent.indexOf("<name>") ;
     int iEndNameIndex = sContent.indexOf("</name>");
     while ((iBegNameIndex>-1)&&(iEndNameIndex>-1)){
       String sNameValue = sContent.substring(iBegNameIndex + 6,iEndNameIndex);
       if (!sNameValue.equals("")){
         sContent = sContent.substring(0,iBegNameIndex) + "<titel>" + sNameValue +
             "</titel>" + sContent.substring(iEndNameIndex + 7);
       } else {
         sContent = sContent.substring(0,iBegNameIndex) + sContent.substring(iEndNameIndex + 7);
         int iBegDescrIndex = sContent.indexOf("<description>",iEndNameIndex);
         int iEndDescrIndex = sContent.indexOf("</description>",iBegDescrIndex);
         if ((iBegDescrIndex>-1)&&(iEndDescrIndex>-1)){
           String sDecrValue = sContent.substring(iBegDescrIndex + 13,iEndDescrIndex);
           sContent = sContent.substring(0,iBegDescrIndex) + "<titel>" + sDecrValue +
             "</titel>" + sContent.substring(iEndDescrIndex + 14);
         }
       }
       iBegNameIndex = sContent.indexOf("<name>") ;
       iEndNameIndex = sContent.indexOf("</name>");
     }
     sContent = sContent.replaceAll("<description></description>","");
     sContent = sContent.replaceAll("\n\t\t\n\t\t","\n\t\t");

     return sContent;
   }

   public static String deletingRelation(ArrayList alFrom, ArrayList alTo, String sContent) {

     Iterator it = alFrom.iterator();
     while (it.hasNext()){
       String sNextNode = (String)it.next();
       int iSNumberIndex = sContent.indexOf("snumber=\"" + sNextNode + "\"");
       while (iSNumberIndex != -1) {
         int iDNIndex = sContent.indexOf("dnumber", iSNumberIndex);
         int iQuotIndex = sContent.indexOf("\"", iDNIndex + 9);
         String sRelNode = sContent.substring(iDNIndex + 9, iQuotIndex);
         if (alTo.contains(sRelNode)){
           int iBegNodeIndex = sContent.indexOf("<node number=",iSNumberIndex - 60) - 1;
           int iEndNodeIndex = sContent.indexOf("</node>",iSNumberIndex) + 9;
           sContent = sContent.substring(0,iBegNodeIndex) + sContent.substring(iEndNodeIndex);
           iSNumberIndex = sContent.indexOf("snumber=\"" + sNextNode + "\"", iBegNodeIndex);
         } else {
            iSNumberIndex = sContent.indexOf("snumber=\"" + sNextNode + "\"",
                                             iSNumberIndex + 1);
         }
       }
     }

     return sContent;
   }

   public static void creatingNewXML(String sBuilderName,String sContent) throws Exception{
      Calendar cal = Calendar.getInstance();
      String sToday = "" + cal.get(Calendar.YEAR);
      if ((cal.get(Calendar.MONTH) + 1)<10){
         sToday += "0";
      }
      sToday += (cal.get(Calendar.MONTH) + 1);
      if (cal.get(Calendar.DAY_OF_MONTH)<10){
         sToday += "0";
      }
      sToday += cal.get(Calendar.DAY_OF_MONTH);
      if (cal.get(Calendar.HOUR_OF_DAY)<10){
          sToday += "0";
      }
      sToday += cal.get(Calendar.HOUR_OF_DAY);
      if (cal.get(Calendar.MINUTE)<10){
          sToday += "0";
      }
      sToday += cal.get(Calendar.MINUTE);
      if (cal.get(Calendar.SECOND)<10){
          sToday += "0";
      }
      sToday += cal.get(Calendar.SECOND);
      String sRealContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
         "\n" + "<" + sBuilderName + " exportsource=\"mmbase://127.0.0.1/intranet/install\"" +
         " timestamp=\"" + sToday + "\"" + ">" + "\n";
      sRealContent += sContent + "</" + sBuilderName + ">";

      String sFileName = sFolder + sBuilderName + ".xml";
      File file = new File(sFileName);

      writingFile(file,sFileName,sRealContent);

   }

   public static String[] movingRelations(ArrayList alFrom, ArrayList alTo,
     String sOldBuilderContent, String sOldBuilderName, String sNewBuilderName){

     String sNewContent = "";
     Iterator it = alFrom.iterator();
     while (it.hasNext()){
       String sFromNode = (String)it.next();
       int iSNIndex = sOldBuilderContent.indexOf("snumber=\"" + sFromNode + "\"");
       while (iSNIndex>-1){
         int iDNBegIndex = sOldBuilderContent.indexOf("dnumber=\"", iSNIndex) + 9;
         int iSNEndIndex = sOldBuilderContent.indexOf("\"", iDNBegIndex + 1);
         String sDNNodeNumber = sOldBuilderContent.substring(iDNBegIndex, iSNEndIndex);
         if (alTo.contains(sDNNodeNumber)) {
           int iBegNodeIndex = sOldBuilderContent.indexOf("<node number",iSNIndex - 60) - 1;
           int iEndNodeIndex = sOldBuilderContent.indexOf("</node>",iBegNodeIndex) + 9;
           sNewContent += sOldBuilderContent.substring(iBegNodeIndex, iEndNodeIndex);
           sOldBuilderContent = sOldBuilderContent.substring(0, iBegNodeIndex) +
               sOldBuilderContent.substring(iEndNodeIndex);
           iSNIndex = sOldBuilderContent.indexOf("snumber=\"" + sFromNode + "\"", iBegNodeIndex);
         } else {
            iSNIndex = sOldBuilderContent.indexOf("snumber=\"" + sFromNode +
               "\"", iSNIndex + 1);
         }
       }
      }
      sNewContent = sNewContent.replaceAll(sOldBuilderName,sNewBuilderName);
      String [] sRel = new String[2];
      sRel[0] = sOldBuilderContent;
      sRel[1] = sNewContent;
     return sRel;
   }


   public static String movingRelations(ArrayList alFrom, ArrayList alTo,
     String sContent, String sNewBuilderName){

     Iterator it = alFrom.iterator();
     while (it.hasNext()){
       String sFromNode = (String)it.next();
       int iSNIndex = sContent.indexOf("snumber=\"" + sFromNode + "\"");
       while (iSNIndex>-1){
         int iDNBegIndex = sContent.indexOf("dnumber=\"", iSNIndex) + 9;
         int iSNEndIndex = sContent.indexOf("\"", iDNBegIndex + 1);
         String sDNNodeNumber = sContent.substring(iDNBegIndex, iSNEndIndex);
         if (alTo.contains(sDNNodeNumber)) {
           int iRTypeBegIndex = sContent.indexOf("rtype=\"", iSNEndIndex) + 7;
           int iRTypeEndIndex = sContent.indexOf("\"", iRTypeBegIndex + 1);
           sContent = sContent.substring(0, iRTypeBegIndex) + sNewBuilderName +
               sContent.substring(iRTypeEndIndex);

         }
         iSNIndex = sContent.indexOf("snumber=\"" + sFromNode + "\"",iSNIndex + 1);
       }
      }
     return sContent;
   }

   public static String addingContent (String sContent, String sBuilderName, String sAdd){
     int iEntryPointIndex = sContent.indexOf("</" + sBuilderName + ">");
     sContent = sContent.substring(0,iEntryPointIndex) + sAdd +
     sContent.substring(iEntryPointIndex);

     return sContent;
   }


}
