package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import nl.leocms.applications.NMIntraConfig;
import org.mmbase.util.logging.*;

/*

Converts the NMIntra XML to XML that fits the NatMM objectmodel.
This script is called from RelationMigrator.run()

This is a script that will only be used once, so it does not conform to any coding standard.

*/

public class NMIntraToNatMMigrator {

  private static final Logger log = Logging.getLoggerInstance(NMIntraToNatMMigrator.class);

  public static void run() throws Exception{

      String sFolder = NMIntraConfig.getIncomingDir() + "NMIntraXML/";

      MigrateUtil mu = new MigrateUtil();

      log.info("NMIntraToNatMMigrator.run()");
      log.info("Importing files from " + sFolder);

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
      alDeletingFiles.add("email.xml"); // email that already has been send so it can be deleted
      alDeletingFiles.add("empupdates.xml"); // not in use anymore, replaced by persoon_update
      alDeletingFiles.add("typedef.xml"); //is system data
      alDeletingFiles.add("urls.xml"); // doesn't contain any info
      alDeletingFiles.add("responses.xml"); // responses contain node numbers, which wont be valid after conversion

      Iterator itDeletingFiles = alDeletingFiles.iterator();

      while (itDeletingFiles.hasNext()) {
         String sFileName = sFolder + itDeletingFiles.next();
         File file = new File(sFileName);
         file.delete();
      }

      log.info("find articles that should become formulier");
      String sContent = mu.readingFile(sFolder + "templates.xml");
      int index = sContent.indexOf("<linktext>formulier</linktext>");
      int iBegNodeIndex = sContent.indexOf("<node number=\"",index - 150);
      int iBegNodeNumberIndex = iBegNodeIndex + 14;
      int iEndNodeNumberIndex = sContent.indexOf("\"",iBegNodeNumberIndex + 1);
      String sTemplateNumber = sContent.substring(iBegNodeNumberIndex,iEndNodeNumberIndex);
      log.info("number of formulier template is " + sTemplateNumber);

      sContent = mu.readingFile(sFolder + "page.xml");
      ArrayList alPages = mu.getNodes(sContent);
      ArrayList alPageRelatedTemplate = new ArrayList();
      String sInsrelContent = mu.readingFile(sFolder + "insrel.xml");
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

      String sArticleContent = mu.readingFile(sFolder + "article.xml");
      ArrayList alArticle = mu.getNodes(sArticleContent);
      String sPosrelContent = mu.readingFile(sFolder + "posrel.xml");
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

      mu.writingFile(sFolder + "article.xml",sArticleContent);

      log.info("deleting not necessary fields in files");
      TreeMap tmDeletingFields = new TreeMap();
      tmDeletingFields.put("answer","layout;total_answers");
      tmDeletingFields.put("formulier","source;quote;creditline;quote_title;" +
      "transmissiondate;expiredate");
      tmDeletingFields.put("article","creditline;quote;quote_title;copyright");
      tmDeletingFields.put("employees","deptdescr;progdescr");
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

        if (sBuilderName.equals("formulier")){
           sContent = sFormulierContent;
        } else {
           sContent = mu.readingFile(sFolder + sBuilderName + ".xml");
        }

        sContent = mu.deleteFields(sContent, alThisDeletedFields);

        if (sBuilderName.equals("readmore")){
           log.info("in readmore.xml replacing & to &amp;");
           sContent = sContent.replaceAll("&","&amp;");
           log.info("in readmore.xml replacing \"inactive\" value readmore2 field to empty");
           sContent = sContent.replaceAll("<readmore2>inactive</readmore2>","<readmore2></readmore2>");

        }
        if (sBuilderName.equals("formulier")){
           sFormulierContent = sContent ;
        } else {
           mu.writingFile(sFolder + sBuilderName + ".xml", sContent);
        }
      }
      String sParagraphContent = mu.readingFile(sFolder + "paragraph.xml");
      ArrayList alParagraphs = mu.getNodes(sParagraphContent);
      sPosrelContent = mu.deletingRelation(alFormulier,alParagraphs,sPosrelContent);

      log.info("finding page nodes that should become rubriek nodes and creating new rubrieks with such names");
      TreeMap tmPaginaToRubriek = new TreeMap();
      ArrayList alPaginaToRubriek = new ArrayList();
      int iNewNumber = 1000000;
      String sPosrelAdd = "";
      String sPijlerContent = mu.readingFile(sFolder + "pijler.xml");
      ArrayList alPijler = mu.getNodes(sPijlerContent);
      int iDelRelIndex = sPosrelContent.indexOf("dposrel");
      while (iDelRelIndex>-1){
        int iBegPageIndex = sPosrelContent.indexOf("snumber=\"",iDelRelIndex - 75) + 9;
        int iEndPageIndex = sPosrelContent.indexOf("\"",iBegPageIndex + 1) ;
        String sPaginaToRubriek = sPosrelContent.substring(iBegPageIndex,iEndPageIndex);
        if (!tmPaginaToRubriek.containsKey(sPaginaToRubriek)){
          String sNewRubriekNumber = (new Integer(iNewNumber)).toString();
          tmPaginaToRubriek.put(sPaginaToRubriek,sNewRubriekNumber);
          alPaginaToRubriek.add(sPaginaToRubriek);
          iNewNumber++;
          log.info("adding to posrel.xml new relation between pagina and newly created rubriek");
          String sNewPosrelNumber = (new Integer(iNewNumber)).toString();
          sPosrelAdd += "\t<node number=\"" + sNewPosrelNumber +
             "\" owner=\"anonymous\" snumber=\"" + sNewRubriekNumber +
             "\" dnumber=\"" + sPaginaToRubriek + "\" rtype=\"posrel\" dir=\"bidirectional\">\n\t\t" +
             "<pos>0</pos>\n\t</node>\n\n";
          iNewNumber++;
        }
        iDelRelIndex = sPosrelContent.indexOf("dposrel",iDelRelIndex + 1);
      }

      log.info("changing relation pijler-posrel-page to rubriek-parent-page");
      String [] sResultParRel = mu.movingRelations(alPijler, alPaginaToRubriek, sPosrelContent,
                         "posrel", "parent");
      sPosrelContent = sResultParRel[0];
      String sParentContent = sResultParRel[1];

      String sPageContent = mu.readingFile(sFolder + "page.xml");
      String sRubriekContent = "";
      set = tmPaginaToRubriek.entrySet();
      it = set.iterator();
      while (it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         String sPage = (String)me.getKey();
         String sRubriek = (String)me.getValue();
         iBegNodeIndex = sPageContent.indexOf("<node number=\"" + sPage);
         int iEndNodeIndex = sPageContent.indexOf("</node>",iBegNodeIndex) + 9;
         sRubriekContent += sPageContent.substring(iBegNodeIndex,iEndNodeIndex);
         log.info("in sRubriekContent changing pages numbers to rubriek numbers");
         sRubriekContent = sRubriekContent.replaceAll(sPage,sRubriek);
         log.info("in sParentContent changing pages numbers to rubriek numbers");
         sParentContent = sParentContent.replaceAll(sPage,sRubriek);
         log.info("changing relation page-dposrel-page to rubriek-posrel-page for existing pages");
         iBegNodeIndex = sPosrelContent.indexOf("snumber=\"" + sPage + "\"");
         while (iBegNodeIndex>-1){
            iEndNodeIndex = sPosrelContent.indexOf("</node>",iBegNodeIndex);
            String sNodeContent = sPosrelContent.substring(iBegNodeIndex,iEndNodeIndex);
            if (sNodeContent.indexOf("dposrel")>-1){
               sNodeContent = sNodeContent.replaceAll(sPage,sRubriek);
               sPosrelContent = sPosrelContent.substring(0,iBegNodeIndex) + sNodeContent
                  + sPosrelContent.substring(iEndNodeIndex);
            }
            iBegNodeIndex = sPosrelContent.indexOf("snumber=\"" + sPage + "\"",iEndNodeIndex);
         }
      }

      sPosrelContent = sPosrelContent.replaceAll("dposrel","posrel");
      sPosrelContent = sPosrelContent.replaceAll("unidirectional","bidirectional");
      sPosrelContent = mu.addingContent(sPosrelContent,"posrel",sPosrelAdd);

      mu.writingFile(sFolder + "posrel.xml",sPosrelContent);
      mu.creatingNewXML(sFolder, "childrel","intranet",sParentContent);
      mu.writingFile(sFolder + "page.xml",sPageContent);
      
      log.info("renaming fields");
      TreeMap tmRenamingFields = new TreeMap();
      tmRenamingFields.put("answer","answer:waarde;description:tekst");
      tmRenamingFields.put("article","editors_note:metatags;expiredate:verloopdatum;" +
      "introduction:intro;source:bron;subtitle:titel_fra;title:titel;transmissiondate:embargo");
      tmRenamingFields.put("attachments","title:titel;description:omschrijving");
      tmRenamingFields.put("companies","name:naam;description:omschrijving");
      tmRenamingFields.put("departments","name:naam;description:omschrijving");
      tmRenamingFields.put("editwizardgroups","name:naam;description:omschrijving");
      tmRenamingFields.put("editwizards","title:name");
      tmRenamingFields.put("employees","position:job;showinfo:titel_zichtbaar;location:account;birthday:reageer;description:omschrijving;intro:omschrijving_fra");
      tmRenamingFields.put("formulier","copyright:titel_de;subtitle:titel_fra;title:titel;" +
      "editors_note:emailadressen;introduction:omschrijving");
      tmRenamingFields.put("images","title:titel;description:omschrijving");
      tmRenamingFields.put("items","name:naam;description:omschrijving");
      tmRenamingFields.put("locations","name:naam;address:bezoekadres;postalcode:bezoekadres_postcode;" +
      "city:plaatsnaam;country:land;address2:postbus;postalcode2:postbus_postcode;" +
      "phone:telefoonnummer;fax:faxnummer;description:omschrijving");
      tmRenamingFields.put("mmbaseusers","username:account");
      tmRenamingFields.put("page","title:titel;subtitle:titel_fra");
      tmRenamingFields.put("paragraph","title:titel;body:tekst");
      tmRenamingFields.put("pijler","title:naam;subtitle:naam_eng");
      tmRenamingFields.put("products","name:titel;description:omschrijving");
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

      String sEditwizardsContent = mu.readingFile(sFolder + "editwizards.xml");

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

      log.info("setting all editwizard titles to lowercase");
      int iBegIndex = sEditwizardsContent.indexOf("<title>");
      while (iBegIndex>-1){
         int iEndIndex = sEditwizardsContent.indexOf("</title>",iBegIndex);
         String sTitle = sEditwizardsContent.substring(iBegIndex + 7,iEndIndex);
         sEditwizardsContent = sEditwizardsContent.substring(0,iBegIndex + 7) +
            sTitle.toLowerCase() + sEditwizardsContent.substring(iEndIndex);
         iBegIndex = sEditwizardsContent.indexOf("<title>",iEndIndex);
      }

     set = tmRenamingFields.entrySet();
     it = set.iterator();
     while (it.hasNext()){
        sContent = "";
        Map.Entry me = (Map.Entry)it.next();
        String sBuilderName = (String)me.getKey();
        if (sBuilderName.equals("editwizards")){
          sContent = sEditwizardsContent;
        } else {
          if (sBuilderName.equals("formulier")) {
            sContent = sFormulierContent;
          } else if (sBuilderName.equals("rubriek")){
             sContent = sRubriekContent;
          } else if (sBuilderName.equals("pijler")){
             sContent = sPijlerContent;
          }
          else {
            sContent = mu.readingFile(sFolder + sBuilderName + ".xml");
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
        sContent = mu.renamingFields(sContent, tmThisRenamingFields);
        if (sBuilderName.equals("editwizards")){
           sEditwizardsContent = sContent;
        } else if (sBuilderName.equals("pijler")){
           sPijlerContent = sContent;
        }

        if (!sBuilderName.equals("editwizards")){
          iBegIndex = sEditwizardsContent.indexOf("nodepath=" + sBuilderName + "&amp;");
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

          if (sBuilderName.equals("formulier")) {
            sFormulierContent = sContent;
          } else if (sBuilderName.equals("rubriek")){
             sRubriekContent = sContent;
          }
          else {
             mu.writingFile(sFolder + sBuilderName + ".xml",sContent);
          }
        }
      }

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
      tmRenamingFiles.put("items","linklijst");
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
      sEditwizardsContent = sEditwizardsContent.replaceAll("shoplinklijst","items");
      sEditwizardsContent = sEditwizardsContent.replaceAll("shop_linklijst","shop_items");
      sEditwizardsContent = sEditwizardsContent.replaceAll("paginalength","pagelength");
      sEditwizardsContent = sEditwizardsContent.replaceAll("maxpaginacount","maxpagecount");
      sEditwizardsContent = sEditwizardsContent.replaceAll("homepagina","homepage");

      sEditwizardsContent = sEditwizardsContent.replaceAll("wizards/","config/");

      mu.writingFile(sFolder + "editwizards.xml",sEditwizardsContent);

      log.info("treating educations.xml");
      sContent = mu.readingFile(sFolder + "educations.xml");
      sContent = mu.buildingUrlsTitels(sContent);
      mu.writingFile(sFolder + "educations.xml",sContent);

      log.info("treating exturls.xml");
      sContent = mu.readingFile(sFolder + "exturls.xml");
      sContent = mu.buildingUrlsTitels(sContent);

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

      mu.writingFile(sFolder + "exturls.xml",sContent);

      log.info("analyzing enrolldate");

      sContent = mu.readingFile(sFolder + "employees.xml");
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

      mu.writingFile(sFolder + "employees.xml",sContent);

      log.info("joining rubriek, pijler and site into rubriek");

      int iBegInfoIndex = sPijlerContent.indexOf("<node number");
      int iEndInfoIndex = sPijlerContent.indexOf("</pijler>");
      sPijlerContent = sPijlerContent.substring(iBegInfoIndex,iEndInfoIndex);

      String sSiteContent = mu.readingFile(sFolder + "site.xml");
      iBegInfoIndex = sSiteContent.indexOf("<node number");
      iEndInfoIndex = sSiteContent.indexOf("</site>");
      sSiteContent = sSiteContent.substring(iBegInfoIndex,iEndInfoIndex);

      sRubriekContent += sPijlerContent + sSiteContent;

      mu.creatingNewXML(sFolder, "rubriek","intranet",sRubriekContent);
      File file = new File(sFolder + "pijler.xml");
      file.delete();

      file = new File(sFolder + "site.xml");
      file.delete();

      log.info("joining mmbaseusers and users into users");
      String sUsersContent = mu.readingFile(sFolder + "users.xml");
      String sMMBaseUsersContent = mu.readingFile(sFolder + "mmbaseusers.xml");

      iBegInfoIndex = sMMBaseUsersContent.indexOf("<node number");
      iEndInfoIndex = sMMBaseUsersContent.indexOf("</mmbaseusers>");
      sMMBaseUsersContent = sMMBaseUsersContent.substring(iBegInfoIndex,iEndInfoIndex);

      sUsersContent = mu.addingContent(sUsersContent,"users",sMMBaseUsersContent);

      mu.writingFile(sFolder + "users.xml",sUsersContent);
      file = new File(sFolder + "mmbaseusers.xml");
      file.delete();

      log.info("making changes in renaming files and writing them");

      set = tmRenamingFiles.entrySet();
      it = set.iterator();
      while (it.hasNext()){
        Map.Entry me = (Map.Entry)it.next();
        String sOldBuilderName = (String)me.getKey();
        String sNewBuilderName = (String)me.getValue();
        sContent = mu.readingFile(sFolder + sOldBuilderName + ".xml");
        if (sOldBuilderName.equals("answer")){
          index = sContent.lastIndexOf("</waarde>");
          sContent = sContent.substring(0,index) + "</answer>";
        }
        sContent = sContent.replaceAll("<" + sOldBuilderName,"<" + sNewBuilderName);
        sContent = sContent.replaceAll("</" + sOldBuilderName,"</" + sNewBuilderName);
        file = new File(sFolder + sOldBuilderName + ".xml");
        mu.writingFile(file,sFolder + sNewBuilderName + ".xml",sContent);
      }

      mu.creatingNewXML(sFolder, "formulier","intranet", sFormulierContent);

  }

}
