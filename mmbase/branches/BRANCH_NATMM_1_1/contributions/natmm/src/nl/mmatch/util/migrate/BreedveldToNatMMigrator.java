package nl.mmatch.util.migrate;

import java.io.*;
import java.util.*;
import nl.leocms.applications.BreedveldConfig;
import org.mmbase.util.logging.*;

/*

Converts the Breedveld XML to XML that fits the NatMM objectmodel.

This is a script that will only be used once, so it does not conform to any coding standard.

*/

public class BreedveldToNatMMigrator {

  private static final Logger log = Logging.getLoggerInstance(BreedveldToNatMMigrator.class);


  public static void run() throws Exception{

     String sFolder = BreedveldConfig.rootDir + "BreedveldXML/";
     MigrateUtil mu = new MigrateUtil();
     
     log.info("BreedveldToNatMMigrator.run()");
     log.info("Importing files from " + sFolder);


     log.info("deleting data that we do not want to migrate");

     ArrayList alDeletingFiles = new ArrayList();
     alDeletingFiles.add("dposrel.xml");
     alDeletingFiles.add("imagestranslation.xml"); //empty
     alDeletingFiles.add("language.xml"); //empty
     alDeletingFiles.add("list.xml");
     alDeletingFiles.add("pictures.xml");
     alDeletingFiles.add("pijlerpage.xml");
     alDeletingFiles.add("poslang.xml");
     alDeletingFiles.add("projecttranslation.xml");
     alDeletingFiles.add("style.xml");
     alDeletingFiles.add("typedef.xml"); //is system data

     String sDPosrelContent = mu.readingFile(sFolder + "dposrel.xml");

     ArrayList alList = getNodesF(sFolder + "list.xml");

     ArrayList alPictures = getNodesF(sFolder + "pictures.xml");
     String sPijlerPageContent = mu.readingFile(sFolder + "pijlerpage.xml");

     log.info("deleting pictures relation from posrel");
     String sPosrelContent = mu.readingFile(sFolder + "posrel.xml");
     sPosrelContent = deletingRelation(alList,alPictures,sPosrelContent);
     String sPoslangContent = mu.readingFile(sFolder + "poslang.xml");

     String sProjecttranslationContent = mu.readingFile(sFolder + "projecttranslation.xml");

     log.info("deleting relation site,related,style");
     ArrayList alStyle = getNodesF(sFolder + "style.xml");
     ArrayList alSite = getNodesF(sFolder + "site.xml");
     String sInsrelContent = mu.readingFile(sFolder + "insrel.xml");
     sInsrelContent = deletingRelation(alSite,alStyle,sInsrelContent);

     Iterator itDeletingFiles = alDeletingFiles.iterator();

     while (itDeletingFiles.hasNext()) {
        String sFileName = sFolder + itDeletingFiles.next();
        File file = new File(sFileName);
        file.delete();
     }

     log.info("deleting not necessary fields in files");
      TreeMap tmDeletingFields = new TreeMap();
      tmDeletingFields.put("article","author;creditline;quote;quote_title");
      tmDeletingFields.put("contact","type");
      tmDeletingFields.put("images","quality;note;type");
      tmDeletingFields.put("project","type");
      tmDeletingFields.put("piece","source");
      tmDeletingFields.put("pijler","description");
      tmDeletingFields.put("site","description");

      Set set = tmDeletingFields.entrySet();
      Iterator it = set.iterator();
      String sContent = "";
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

        sContent = mu.readingFile(sFolder + sBuilderName + ".xml");

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

       mu.writingFile(sFolder + sBuilderName + ".xml", sContent);

      }

      //building article-paragraf corresponding;
      String sArticleContent = mu.readingFile(sFolder + "article.xml");
      ArrayList alArticle = getNodesS(sArticleContent);
      String sParagrafContent = mu.readingFile(sFolder + "paragraph.xml");
      ArrayList alParagraf = getNodesS(sParagrafContent);

      TreeMap tmArticleParagraf = new TreeMap();
      it = alArticle.iterator();
      while(it.hasNext()){
         String sArticleNumber = (String)it.next();
         int iBegSNIndex = sPosrelContent.indexOf("snumber=\"" + sArticleNumber + "\"");
         while (iBegSNIndex>-1){
            int iBegDNIndex = sPosrelContent.indexOf("dnumber=\"", iBegSNIndex);
            int iEndDNIndex = sPosrelContent.indexOf("\"", iBegDNIndex + 9);
            String sNodeNumber = sPosrelContent.substring(iBegDNIndex + 9,
               iEndDNIndex);
            if (alParagraf.contains(sNodeNumber)) {
               int iBegPosIndex = sPosrelContent.indexOf("<pos>", iEndDNIndex);
               int iEndPosIndex = sPosrelContent.indexOf("</pos>", iBegPosIndex);
               String sPosValue = sPosrelContent.substring(iBegPosIndex + 5,
                  iEndPosIndex);
               tmArticleParagraf.put(sArticleNumber + ":" + sPosValue,sNodeNumber);
            }
            iBegSNIndex = sPosrelContent.indexOf("snumber=\"" + sArticleNumber + "\"",iBegDNIndex);
         }

      }

      log.info("joining several different language articles into one multilanguage");
      String sProjectContent = mu.readingFile(sFolder + "project.xml");
      ArrayList alProject = getNodesS(sProjectContent);

      TreeMap tmLangs = new TreeMap();
      tmLangs.put("dutch","");
      tmLangs.put("french","_fra");
      tmLangs.put("english","_eng");
      TreeMap tmOldNewArticles = new TreeMap();
      TreeMap tmProjectArticle = new TreeMap();
      TreeMap tmNewArticles = new TreeMap();
      TreeMap tmArtLang = new TreeMap();
      it = alProject.iterator();
      int iNewIndex = 1000000;
      LinkedList llConstFields = new LinkedList();
      llConstFields.add("transmissiondate");
      llConstFields.add("expiredate");
      llConstFields.add("status");
      while(it.hasNext()){
         String sProjectNumber = (String)it.next();
         int iBegSNIndex = sPoslangContent.indexOf("snumber=\"" + sProjectNumber + "\"");
         while (iBegSNIndex>-1){
            int iBegDNIndex = sPoslangContent.indexOf("dnumber=\"", iBegSNIndex);
            int iEndDNIndex = sPoslangContent.indexOf("\"", iBegDNIndex + 9);
            String sNodeNumber = sPoslangContent.substring(iBegDNIndex + 9,
               iEndDNIndex);
            if (alArticle.contains(sNodeNumber)) {
               int iBegPosIndex = sPoslangContent.indexOf("<pos>", iEndDNIndex);
               int iEndPosIndex = sPoslangContent.indexOf("</pos>",
                  iBegPosIndex);
               String sPosValue = sPoslangContent.substring(iBegPosIndex + 5,
                  iEndPosIndex);
               String sKey = sProjectNumber + ":" + sPosValue;
               int iBegNodeIndex = sArticleContent.indexOf("<node number=\"" +
                  sNodeNumber + "\"");
               String sNewText = "";
               String sNewNumber = "";
               if (!tmProjectArticle.containsKey(sKey)) {
                  sNewNumber = Integer.toString(iNewIndex);
                  iNewIndex++;
                  tmProjectArticle.put(sKey, sNewNumber);
                  tmOldNewArticles.put(sNewNumber,sNodeNumber);
                  sNewText = "<node number=\"" + sNewNumber +
                     "\" owner=\"admin\">\n\t\t";
                  Iterator it1 = llConstFields.iterator();
                  while (it1.hasNext()) {
                     String sField = (String) it1.next();
                     int iBegIndex = sArticleContent.indexOf("<" + sField + ">",
                        iBegNodeIndex);
                     int iEndIndex = sArticleContent.indexOf("</" + sField +
                        ">", iBegIndex);
                     String sValue = sArticleContent.substring(iBegIndex,
                        iEndIndex + sField.length() + 6);
                     sNewText += sValue;
                  }
               }
               else {
                  sNewNumber = (String) tmProjectArticle.get(sKey);
                  sNewText = (String) tmNewArticles.get(sNewNumber);
                  String sArticlesNodes = (String)tmOldNewArticles.get(sNewNumber);
                  if (!sArticlesNodes.equals(sNodeNumber)){
                     sArticlesNodes += "," + sNodeNumber;
                  }
                  tmOldNewArticles.put(sNewNumber,sArticlesNodes);
               }
               int iBegLangIndex = sPoslangContent.indexOf("<language>",
                  iEndDNIndex);
               int iEndLangIndex = sPoslangContent.indexOf("</language>",
                  iBegLangIndex + 10);
               String sLanguage = sPoslangContent.substring(iBegLangIndex + 10,
                  iEndLangIndex);
               if (!tmArtLang.containsKey(sNodeNumber)){
                  tmArtLang.put(sNodeNumber,sLanguage);
               }
               int iBegIndex = sArticleContent.indexOf("<title>", iBegNodeIndex);
               int iEndIndex = sArticleContent.indexOf("</title>", iBegIndex);
               String sValue = sArticleContent.substring(iBegIndex,
                  iEndIndex + 8) + "\n\t\t";
               sValue = sValue.replaceAll("title",
                                          "titel" + tmLangs.get(sLanguage));
               sNewText += sValue;
               iBegIndex = sArticleContent.indexOf("<subtitle>", iBegNodeIndex);
               iEndIndex = sArticleContent.indexOf("</subtitle>", iBegIndex);
               sValue = sArticleContent.substring(iBegIndex, iEndIndex + 11) + "\n\t\t";
               sValue = sValue.replaceAll("subtitle",
                                          "omschrijving" + tmLangs.get(sLanguage));
               sNewText += sValue;
               tmNewArticles.put(sNewNumber, sNewText);

            }
            iBegSNIndex = sPoslangContent.indexOf("snumber=\"" + sProjectNumber + "\"",iBegDNIndex);
         }
      }

      set = tmNewArticles.entrySet();
      it = set.iterator();
      String sArticleAdd = "";
      while(it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         String sValue = (String)me.getValue() + "</node>\n\n";
         sValue.replaceAll("\n\t\t</node>","\n</node>");
         sArticleAdd += sValue ;
      }

      File file = new File(sFolder + "article.xml");
      file.delete();
      mu.creatingNewXML(sFolder,"article","breedveld",sArticleAdd);

      set = tmProjectArticle.entrySet();
      it = set.iterator();
      String sPosrelAdd = "";
      while(it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         String sKey = (String)me.getKey();
         String sValue = (String)me.getValue();
         int iColonIndex = sKey.indexOf(":");
         String sProjectNumber = sKey.substring(0,iColonIndex);
         String sPosValue = sKey.substring(iColonIndex+1);
         String sNewNumber = Integer.toString(iNewIndex);
         iNewIndex++;
         sPosrelAdd += "\t<node number=\"" + sNewNumber + "\" owner=\"admin\"" +
            " snumber=\"" + sProjectNumber + "\" dnumber=\"" + sValue +
            "\"" + " rtype=\"posrel\" dir=\"bidirectional\">\n\t\t<pos>" +
            sPosValue + "</pos>\n\t</node>\n\n";
      }

      sPosrelContent = deletingRelation(alProject,alArticle,sPosrelContent);

      log.info("joining several different language paragraphs into one multilanguage");
      TreeMap tmParNewContent = new TreeMap();
      TreeMap tmNewArtPars = new TreeMap();
      set = tmProjectArticle.entrySet();
      it = set.iterator();
      while (it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         String sKey = (String)me.getKey();
         String sValue = (String)me.getValue();
         String sArts = (String)tmOldNewArticles.get(sValue);
         ArrayList alArtNumbers = new ArrayList();
         int iCommaIndex = sArts.indexOf(",");
         while(iCommaIndex>-1){
            alArtNumbers.add(sArts.substring(0,iCommaIndex));
            sArts = sArts.substring(iCommaIndex + 1);
            iCommaIndex = sArts.indexOf(",");
         }
         alArtNumbers.add(sArts);
         Iterator it1 = alArtNumbers.iterator();
         while (it1.hasNext()){
            int iParagrafCount = 1;
            String sArticleNumber = (String)it1.next();
            while (tmArticleParagraf.containsKey(sArticleNumber + ":" + iParagrafCount)){
               String sNewText = "";
               String sNewNumber = "";
               if (!tmNewArtPars.containsKey(sKey + ":" + iParagrafCount)){
                  sNewNumber = Integer.toString(iNewIndex);
                  iNewIndex++;
                  sNewText = "<node number=\"" + sNewNumber +
                     "\" owner=\"admin\">\n\t\t";
                  tmNewArtPars.put(sKey + ":" + iParagrafCount,sNewNumber);
               } else{
                  sNewNumber = (String)tmNewArtPars.get(sKey + ":" + iParagrafCount);
                  sNewText = (String)tmParNewContent.get(sNewNumber);
               }
               String sParNumber = (String)tmArticleParagraf.get(sArticleNumber + ":" + iParagrafCount);
               int iBegNodeIndex = sParagrafContent.indexOf("<node number=\"" +
               sParNumber + "\"");
               int iBegIndex = sParagrafContent.indexOf("<title>", iBegNodeIndex);
               int iEndIndex = sParagrafContent.indexOf("</title>", iBegIndex);
               String sPValue = sParagrafContent.substring(iBegIndex,
               iEndIndex + 8) + "\n\t\t";
               sPValue = sPValue.replaceAll("title",
                                       "titel" + tmLangs.get(tmArtLang.get(sArticleNumber)));
               sNewText += sPValue;
               iBegIndex = sParagrafContent.indexOf("<body>", iBegNodeIndex);
               iEndIndex = sParagrafContent.indexOf("</body>", iBegIndex);
               sPValue = sParagrafContent.substring(iBegIndex, iEndIndex + 7) + "\n\t\t";
               sPValue = sPValue.replaceAll("body",
                                       "omschrijving" + tmLangs.get(tmArtLang.get(sArticleNumber)));
               sNewText += sPValue;
               tmParNewContent.put(sNewNumber, sNewText);

               iParagrafCount++;
            }
         }
      }

      set = tmParNewContent.entrySet();
      it = set.iterator();
      String sParagrafAdd = "";
      while(it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         String sValue = (String)me.getValue() + "</node>\n\n";
         sValue.replaceAll("\n\t\t</node>","\n</node>");
         sParagrafAdd += sValue ;
      }

      file = new File(sFolder + "paragraph.xml");
      file.delete();
      mu.creatingNewXML(sFolder,"paragraph","breedveld",sParagrafAdd);

      set = tmNewArtPars.entrySet();
      it = set.iterator();
      while(it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         String sKey = (String)me.getKey();
         String sValue = (String)me.getValue();
         int iColonIndex = sKey.lastIndexOf(":");
         String sProjectNumber = sKey.substring(0,iColonIndex);
         String sArticleNumber = (String)tmProjectArticle.get(sProjectNumber);
         String sPosValue = sKey.substring(iColonIndex+1);
         String sNewNumber = Integer.toString(iNewIndex);
         iNewIndex++;
         sPosrelAdd += "\t<node number=\"" + sNewNumber + "\" owner=\"admin\"" +
            " snumber=\"" + sArticleNumber + "\" dnumber=\"" + sValue +
            "\"" + " rtype=\"posrel\" dir=\"bidirectional\">\n\t\t<pos>" +
            sPosValue + "</pos>\n\t</node>\n\n";
      }

      sPosrelContent = deletingRelation(alArticle,alParagraf,sPosrelContent);

      log.info("treating status field into titel_zichtbar field");
      ArrayList alZichbarContaingBuilders = new ArrayList();
      alZichbarContaingBuilders.add("article");
      alZichbarContaingBuilders.add("contact");
      alZichbarContaingBuilders.add("images");
      alZichbarContaingBuilders.add("piece");
      alZichbarContaingBuilders.add("project");
      alZichbarContaingBuilders.add("urls");

      it = alZichbarContaingBuilders.iterator();
      while(it.hasNext()){
         treatingZichtbaar(sFolder + it.next() + ".xml",mu);
      }

      log.info("renaming fields");
      TreeMap tmRenamingFields = new TreeMap();
      tmRenamingFields.put("article","copyright:titel_de;editors_note:metatags;" +
      "expiredate:verloopdatum;introduction:intro;source:bron;subtitle:titel_fra;" +
      "title:titel;transmissiondate:embargo");
      tmRenamingFields.put("attachments","title:titel;description:omschrijving");
      tmRenamingFields.put("contact","address:bezoekadres;city:plaatsnaam;" +
      "comments:omschrijving;country:land;fax:faxnummer;postcode:bezoekadres_postcode");
      tmRenamingFields.put("contactcategory","title:naam");
      tmRenamingFields.put("editwizards","title:name");
      tmRenamingFields.put("groups","name:naam;description:omschrijving");
      tmRenamingFields.put("images","title:titel;description:omschrijving;" +
      "subtitle:titel_fra;source:bron;layout:screensize");
      tmRenamingFields.put("mmbaseusers","username:account");
      tmRenamingFields.put("page","title:titel;subtitle:titel_fra");
      tmRenamingFields.put("paragraph","title:titel;body:tekst");
      tmRenamingFields.put("piece","title:titel;description:omschrijving");
      tmRenamingFields.put("pijler","title:naam;subtitle:naam_eng");
      tmRenamingFields.put("project","title:titel;text:omschrijving;fromdate:embargo;" +
      "todate:verloopdatum");
      tmRenamingFields.put("projectcategory","title:name");
      tmRenamingFields.put("site","title:naam");
      tmRenamingFields.put("template","title:naam;description:omschrijving");
      tmRenamingFields.put("urls","description:omschrijving;linktext:alt_tekst");

      String sEditwizardsContent = mu.readingFile(sFolder + "editwizards.xml");

      log.info("deleting editwizards of deleted objects");
      ArrayList alDelEd = new ArrayList();
      alDelEd.add("list");
      alDelEd.add("pictures");

      ArrayList alDelRel = new ArrayList();

      it = alDelEd.iterator();
      while (it.hasNext()){
         String sBuilderName = (String)it.next();
         int index = sEditwizardsContent.indexOf("archief/" + sBuilderName + "&");
         int iBegNodeIndex = sEditwizardsContent.indexOf("<node number=\"",index - 110);
         int iBegRelNumberIndex = iBegNodeIndex + 14;
         int iEndRelNumberIndex = sEditwizardsContent.indexOf("\"",iBegRelNumberIndex + 1);
         String sNodeNumber = sEditwizardsContent.substring(iBegRelNumberIndex,iEndRelNumberIndex);
         alDelRel.add(sNodeNumber);
         int iEndNodeIndex = sEditwizardsContent.indexOf("</node>",iBegNodeIndex) + 9;
         sEditwizardsContent = sEditwizardsContent.substring(0,iBegNodeIndex-1) +
         sEditwizardsContent.substring(iEndNodeIndex);
      }

      log.info("deleting relation of deleted editwizards from insrel.xml");

      it = alDelRel.iterator();
      while (it.hasNext()) {
         String sNodeNumber = (String) it.next();
         int iDNIndex = sInsrelContent.indexOf("dnumber=\"" + sNodeNumber + "\"");
         int iBegNodeIndex = sInsrelContent.indexOf("<node number=", iDNIndex - 70);
         int iEndNodeIndex = sInsrelContent.indexOf("</node>", iBegNodeIndex) +
            9;
         sInsrelContent = sInsrelContent.substring(0, iBegNodeIndex-1) +
            sInsrelContent.substring(iEndNodeIndex);
      }

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
            sContent = mu.readingFile(sFolder + sBuilderName + ".xml");
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

         mu.writingFile(sFolder + sBuilderName + ".xml",sContent);
        }
      }

      log.info("treating editwizard.url field into many fields");
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

      log.info("treating contact.xml to join fields: " +
               "business_phone, mobile and private_phone into telefoonnummer");
      sContent = mu.readingFile(sFolder + "contact.xml");
      ArrayList alContact = getNodesS(sContent);
      LinkedList llJoiningFields = new LinkedList();
      llJoiningFields.add("business_phone");
      llJoiningFields.add("private_phone");
      llJoiningFields.add("mobile");

      int iBegNodeIndex = sContent.indexOf("<node");
      while (iBegNodeIndex>-1){
         String sNewValue = "";
         it = llJoiningFields.listIterator();
         while (it.hasNext()){
            String sFieldName = (String)it.next();
            iBegIndex = sContent.indexOf("<" + sFieldName + ">",iBegNodeIndex);
            int iEndIndex = sContent.indexOf("</" + sFieldName + ">",iBegIndex);
            String sAddValue = sContent.substring(iBegIndex + sFieldName.length() + 2,iEndIndex);
            if (!sAddValue.equals("")){
               if (!sNewValue.equals("")) {
                  sNewValue += ", ";
               }
               sNewValue += sAddValue;
            }
            sContent = sContent.substring(0,iBegIndex) + sContent.substring(iEndIndex + sFieldName.length() + 6);
         }
         int iEndNodeIndex = sContent.indexOf("</node",iBegNodeIndex);
         sContent = sContent.substring(0,iEndNodeIndex) + "\t<telefoonnummer>" +
            sNewValue + "</telefoonnummer>\n\t" + sContent.substring(iEndNodeIndex);
         iBegNodeIndex = sContent.indexOf("<node",iEndNodeIndex);
      }

      llJoiningFields.clear();

      log.info("treating contact to join fields: " +
               "companyname, firstname, secondname, initials  and lastname into naam");
      llJoiningFields.add("companyname");
      llJoiningFields.add("firstname");
      llJoiningFields.add("second_name");
      llJoiningFields.add("initials");
      llJoiningFields.add("lastname");
      iBegNodeIndex = sContent.indexOf("<node");
      while (iBegNodeIndex>-1){
         String sNewValue = "";
         it = llJoiningFields.listIterator();
         while (it.hasNext()){
            String sFieldName = (String)it.next();
            iBegIndex = sContent.indexOf("<" + sFieldName + ">",iBegNodeIndex);
            int iEndIndex = sContent.indexOf("</" + sFieldName + ">",iBegIndex);
            String sAddValue = sContent.substring(iBegIndex + sFieldName.length() + 2,iEndIndex);
            if (!sAddValue.equals("")){
               if (sFieldName.equals("companyname")){ sAddValue += ","; }
               if (!sNewValue.equals("")) {
                  sNewValue += " ";
               }
               sNewValue += sAddValue;
            }
            sContent = sContent.substring(0,iBegIndex) + sContent.substring(iEndIndex + sFieldName.length() + 6);
         }
         if (sNewValue.endsWith(",")) {
            sNewValue = sNewValue.substring(0,sNewValue.length()-1);
         }
         int iEndNodeIndex = sContent.indexOf("</node",iBegNodeIndex);
         sContent = sContent.substring(0,iEndNodeIndex) + "\t<naam>" +
            sNewValue + "</naam>\n\t" + sContent.substring(iEndNodeIndex);
         iBegNodeIndex = sContent.indexOf("<node",iEndNodeIndex);
      }

      sContent = sContent.replace('\u001A','e'); //deleting special symbol
     mu.writingFile(sFolder + "contact.xml",sContent);

      log.info("joining pijler and site into rubriek");

      String sPijlerContent = mu.readingFile(sFolder + "pijler.xml");
      int iBegInfoIndex = sPijlerContent.indexOf("<node number");
      int iEndInfoIndex = sPijlerContent.indexOf("</pijler>");
      sPijlerContent = sPijlerContent.substring(iBegInfoIndex-1,iEndInfoIndex);
      ArrayList alPijler = getNodesS(sPijlerContent);

      String sSiteContent = mu.readingFile(sFolder + "site.xml");
      iBegInfoIndex = sSiteContent.indexOf("<node number");
      iEndInfoIndex = sSiteContent.indexOf("</site>");
      sSiteContent = sSiteContent.substring(iBegInfoIndex-1,iEndInfoIndex);

      String sRubriekContent = sPijlerContent + sSiteContent;

      mu.creatingNewXML(sFolder,"rubriek","breedveld",sRubriekContent);
      file = new File(sFolder + "pijler.xml");
      file.delete();

      file = new File(sFolder + "site.xml");
      file.delete();

      log.info("adding pijlerpage content into posrel.xml");
      iBegInfoIndex = sPijlerPageContent.indexOf("<node number");
      iEndInfoIndex = sPijlerPageContent.indexOf("</pijlerpage>");
      sPijlerPageContent = sPijlerPageContent.substring(iBegInfoIndex,iEndInfoIndex);
      sPijlerPageContent = sPijlerPageContent.replaceAll("pijlerpage","posrel");
      sPosrelContent = addingContent(sPosrelContent,"posrel",sPijlerPageContent);

      log.info("analyzing projecttranslation.xml and adding data into projects.xml");
      TreeMap tmProjectProjecttranlation = new TreeMap();
      sProjectContent = mu.readingFile(sFolder + "project.xml");
      alProject = getNodesS(sProjectContent);
      it = alProject.iterator();
      while (it.hasNext()){
         String sProjectNumber = (String)it.next();
         String sTranslations = "";
         int iSNIndex = sPoslangContent.indexOf("snumber=\"" + sProjectNumber + "\"");
         while (iSNIndex>-1){
            int iDNBegIndex = sPoslangContent.indexOf("dnumber=\"",iSNIndex);
            int iDNEndIndex = sPoslangContent.indexOf("\"",iDNBegIndex + 10);
            int iBegLanguageIndex = sPoslangContent.indexOf("<language>",iDNBegIndex);
            int iEndLanguageIndex = sPoslangContent.indexOf("</language>",iBegLanguageIndex);
            String sLanguage = sPoslangContent.substring(iBegLanguageIndex + 10,iEndLanguageIndex);
            if (!sTranslations.equals("")){ sTranslations += ";"; }
            sTranslations += sPoslangContent.subSequence(iDNBegIndex + 9,iDNEndIndex) + ":" + sLanguage;
            iSNIndex = sPoslangContent.indexOf("snumber=\"" + sProjectNumber + "\"",iDNEndIndex);
         }
         tmProjectProjecttranlation.put(sProjectNumber,sTranslations);
      }

      set = tmProjectProjecttranlation.entrySet();
      it = set.iterator();
      while (it.hasNext()){
         Map.Entry me = (Map.Entry) it.next();
         String sProjectNumber = (String)me.getKey();
         String sNodes = (String)me.getValue();
         int iSemicolonIndex = sNodes.indexOf(";");
         while (iSemicolonIndex>-1){
          String sThisPair = sNodes.substring(0,iSemicolonIndex);
          sProjectContent = addingProject(sProjecttranslationContent,sThisPair,sProjectContent,sProjectNumber);
          sNodes = sNodes.substring(iSemicolonIndex+1);
          iSemicolonIndex = sNodes.indexOf(";");
        }
        sProjectContent = addingProject(sProjecttranslationContent,sNodes,sProjectContent,sProjectNumber);
      }

     mu.writingFile(sFolder + "project.xml",sProjectContent);

      log.info("making changes in renaming files and writing them");

      TreeMap tmRenamingFiles = new TreeMap();
      tmRenamingFiles.put("article","artikel");
      tmRenamingFiles.put("contactcategory","organisatie_type");
      tmRenamingFiles.put("contact","organisatie");
      tmRenamingFiles.put("groups","menu");
      tmRenamingFiles.put("mmbaseusers","users");
      tmRenamingFiles.put("page","pagina");
      tmRenamingFiles.put("paragraph","paragraaf");
      tmRenamingFiles.put("piece","items");
      tmRenamingFiles.put("projectcategory","projecttypes");
      tmRenamingFiles.put("project","projects");
      tmRenamingFiles.put("template","paginatemplate");
      tmRenamingFiles.put("urls","link");

      set = tmRenamingFiles.entrySet();
      it = set.iterator();
      while (it.hasNext()){
        Map.Entry me = (Map.Entry)it.next();
        String sOldBuilderName = (String)me.getKey();
        String sNewBuilderName = (String)me.getValue();
        sEditwizardsContent = sEditwizardsContent.replaceAll(sOldBuilderName,sNewBuilderName);
        sContent = mu.readingFile(sFolder + sOldBuilderName + ".xml");
        sContent = sContent.replaceAll("<" + sOldBuilderName + " ","<" + sNewBuilderName + " ");
        sContent = sContent.replaceAll("</" + sOldBuilderName + ">","</" + sNewBuilderName + ">");
        file = new File(sFolder + sOldBuilderName + ".xml");
       mu.writingFile(file,sFolder + sNewBuilderName + ".xml",sContent);
      }

      TreeMap tmEditwizardsReplacings = new TreeMap();
      tmEditwizardsReplacings.put("archief/","config/");
      tmEditwizardsReplacings.put("organisatiecategory","organisatie_type");
      tmEditwizardsReplacings.put("itemssize","piecesize");
      tmEditwizardsReplacings.put("image<","images<");
      tmEditwizardsReplacings.put("projectscategory","projecttypes");
      tmEditwizardsReplacings.put("author,","");
      tmEditwizardsReplacings.put("organisatie_type.title","organisatie_type.naam");
      tmEditwizardsReplacings.put("organisatie.companyname","organisatie.naam");
      tmEditwizardsReplacings.put("organisatie.lastname,","");
      tmEditwizardsReplacings.put("quality,","");
      tmEditwizardsReplacings.put("projects.title","projects.titel");
      tmEditwizardsReplacings.put("projects.fromdate","projects.embargo");

      set = tmEditwizardsReplacings.entrySet();
      it = set.iterator();
      while (it.hasNext()){
         Map.Entry me = (Map.Entry)it.next();
         sEditwizardsContent = sEditwizardsContent.replaceAll((String)me.getKey(),(String)me.getValue());
      }

      log.info("changing wizard path in editwizatrds.xml");
      iBegIndex = sEditwizardsContent.indexOf("config/");
      while(iBegIndex>-1){
         int iEndIndex = sEditwizardsContent.indexOf("<",iBegIndex + 7);
         String sBuilderName = sEditwizardsContent.substring(iBegIndex + 7,iEndIndex);
         sEditwizardsContent = sEditwizardsContent.substring(0,iBegIndex + 7)
            + sBuilderName + "/" + sBuilderName + sEditwizardsContent.substring(iEndIndex);
         iBegIndex = sEditwizardsContent.indexOf("config/",iEndIndex);
      }

     mu.writingFile(sFolder + "editwizards.xml",sEditwizardsContent);

      log.info("changing relation page,poslang,list,posrel,images to pagina,posrel,images " +
       "and page,poslang,list,posrel,projectcategory to pagina,posrel,projectcategory" + 
       "and page,poslang,list,readmore,page to pagina1,readmore,pagina2");
      ArrayList alPages = getNodesF(sFolder + "pagina.xml");
      String sReadmoreContent = mu.readingFile(sFolder + "readmore.xml");

      String sReadmoreAdd = "";
      it = alList.iterator();
      while (it.hasNext()){
         String sListNumber = (String)it.next();
         int iSNIndex = sPosrelContent.indexOf("snumber=\"" + sListNumber + "\"");
         if (iSNIndex>-1){
            int iDNIndex = sPoslangContent.indexOf("dnumber=\"" + sListNumber + "\"");
            int iBegSNIndex = sPoslangContent.indexOf("snumber=\"", iDNIndex - 30);
            int iEndSnIndex = sPoslangContent.indexOf("\"", iBegSNIndex + 10);
            String sPageNumber = sPoslangContent.substring(iBegSNIndex + 9,  iEndSnIndex);
            sPosrelContent = sPosrelContent.replaceAll("\"" + sListNumber + "\"",
                                                       "\"" + sPageNumber + "\"");
	 }
         iSNIndex = sReadmoreContent.indexOf("snumber=\"" + sListNumber + "\"");
         if (iSNIndex>-1){
            if (sReadmoreAdd.equals("")){
               int iBegDNIndex = sReadmoreContent.indexOf("dnumber=\"",
                  iSNIndex);
               int iEndDNIndex = sReadmoreContent.indexOf("\"", iBegDNIndex + 9);
               String sPage2Number = sReadmoreContent.substring(iBegDNIndex + 9,
                  iEndDNIndex);
               int iDNIndex = sPoslangContent.indexOf("dnumber=\"" +
                  sListNumber + "\"");
               iBegNodeIndex = sPoslangContent.indexOf("<node number",
                  iDNIndex - 60);
               int iEndNodeIndex = sPoslangContent.indexOf("</node>",
                  iBegNodeIndex);
               String sAdding = sPoslangContent.substring(iBegNodeIndex,
                  iEndNodeIndex + 10);
               sAdding = sAdding.replaceAll(sListNumber, sPage2Number);
               sReadmoreAdd += sAdding;
            }

         }

      }
      sReadmoreAdd = removingLanguageTag(sReadmoreAdd);
      sReadmoreAdd = sReadmoreAdd.replaceAll("poslang","readmore");
      sReadmoreContent = deletingRelation(alList,alPages,sReadmoreContent);

      log.info("changing relation site,posrel,pijler to rubriek,parent,rubriek");
      String [] sResultRels = movingRelations(alSite, alPijler, sPosrelContent,
                                   "posrel", "parent");
      sPosrelContent = sResultRels[0];
      String sParentContent = sResultRels[1];
      mu.creatingNewXML(sFolder,"childrel","breedveld",sParentContent);

      log.info("changing relation project,posrel,contact to projects,readmore,organisatie");
      sResultRels = movingRelations(alProject, alContact, sPosrelContent,
                                   "posrel", "readmore");
      sPosrelContent = sResultRels[0];
      sReadmoreAdd += sResultRels[1];
      sReadmoreContent = addingContent(sReadmoreContent,"readmore",sReadmoreAdd);

      log.info("deleting relations of not exists objetcs from readmore.xml");

      ArrayList alReadmoreFrom = new ArrayList(1);
      alReadmoreFrom.add("1316");
      ArrayList alReadmoreTo = new ArrayList(1);
      alReadmoreTo.add("193");
      sReadmoreContent = deletingRelation(alReadmoreFrom,alReadmoreTo,sReadmoreContent);

     mu.writingFile(sFolder + "readmore.xml",sReadmoreContent);

      log.info("changing relation page,related,template to pagina,gebruikt,paginatemplate");
      ArrayList alPaginaTemplate = getNodesF(sFolder + "paginatemplate.xml");
      sInsrelContent = movingRelations(alPages, alPaginaTemplate, sInsrelContent,
                                       "gebruikt");

      log.info("changing relation mmbaseusers,related,groups to users,gebruikt,menu");
      ArrayList alUsers = getNodesF(sFolder + "users.xml");
      ArrayList alMenu = getNodesF(sFolder + "menu.xml");
      sInsrelContent = movingRelations(alUsers, alMenu, sInsrelContent,
                                       "gebruikt");

      log.info("changing relation page,poslang,contact to pagina,contentrel,organisatie");
      sResultRels = movingRelations(alPages, alContact, sPoslangContent,
                                   "poslang", "contentrel");
      String sContentrelContent = sResultRels[1];

      log.info("removing language tag from sContentrelContent");
      sContentrelContent = removingLanguageTag(sContentrelContent);
      mu.creatingNewXML(sFolder,"contentrel","breedveld",sContentrelContent);

      sPosrelAdd += sResultRels[1];

      log.info("changing relation groups,related,editwizards to menu,posrel,editwizards");
      ArrayList alEditwizards = getNodesF(sFolder + "editwizards.xml");
      sResultRels = movingRelations(alMenu, alEditwizards, sInsrelContent,
                                   "related", "posrel");
      sInsrelContent = sResultRels[0];
     mu.writingFile(sFolder + "insrel.xml",sInsrelContent);
      sPosrelAdd += sResultRels[1];

      log.info("changing relation project,dposrel,project to projects,posrel,projects");
      sDPosrelContent = sDPosrelContent.replaceAll("dposrel","posrel");
      sDPosrelContent = sDPosrelContent.replaceAll("unidirectional","bidirectional");
      iBegIndex = sDPosrelContent.indexOf("<node");
      int iEndIndex = sDPosrelContent.indexOf("</posrel>");
      sPosrelAdd += sDPosrelContent.substring(iBegIndex,iEndIndex);

      log.info("deleting relation list,posrel,pictures");
      sPosrelContent = deletingRelation(alList,alPictures,sPosrelContent);

      log.info("removing language tag from sPosrelAdd");
      sPosrelAdd = removingLanguageTag(sPosrelAdd);

      log.info("deleting relations of not exists objetcs from posrel.xml");
      ArrayList alNotExistingNodes = new ArrayList();
      alNotExistingNodes.add("155");
      alNotExistingNodes.add("395");
      alNotExistingNodes.add("395");
      alNotExistingNodes.add("686");
      alNotExistingNodes.add("878");
      alNotExistingNodes.add("1019");
      alNotExistingNodes.add("1023");
      alNotExistingNodes.add("1095");
      alNotExistingNodes.add("1099");
      alNotExistingNodes.add("1202");
      alNotExistingNodes.add("1316");
      alNotExistingNodes.add("1669");
      alNotExistingNodes.add("1839");
      alNotExistingNodes.add("2147");
      alNotExistingNodes.add("5971");

      it = alNotExistingNodes.iterator();
      while (it.hasNext()){
         String sNodeNumber = (String)it.next();
         iBegIndex = sPosrelContent.indexOf("number=\"" + sNodeNumber + "\"");
         while (iBegIndex>-1){
            iBegNodeIndex = sPosrelContent.indexOf("<node",iBegIndex - 60);
            int iEndNodeIndex = sPosrelContent.indexOf("</node>",iBegNodeIndex);
            sPosrelContent = sPosrelContent.substring(0,iBegNodeIndex) +
               sPosrelContent.substring(iEndNodeIndex + 10);
            iBegIndex = sPosrelContent.indexOf("number=\"" + sNodeNumber + "\"",iBegNodeIndex);
         }
      }


      sPosrelContent = addingContent(sPosrelContent,"posrel",sPosrelAdd);
     mu.writingFile(sFolder + "posrel.xml",sPosrelContent);

      log.info("deleting relations of not exists objetcs from stock.xml");
      sContent = mu.readingFile(sFolder + "stock.xml");
      ArrayList alStockFrom = new ArrayList(1);
      alStockFrom.add("395");

      ArrayList alStockTo = new ArrayList(1);
      alStockTo.add("778");

      sContent = deletingRelation(alStockFrom,alStockTo,sContent);

     mu.writingFile(sFolder + "stock.xml",sContent);

   }
   public static void main(String args[]) throws Exception{

    run();
  }

  public static ArrayList getNodesS (String sContent) throws Exception{

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

   public static ArrayList getNodesF (String sFileName) throws Exception{

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

   public static String addingContent (String sContent, String sBuilderName, String sAdd){
     int iEntryPointIndex = sContent.lastIndexOf("</" + sBuilderName + ">");
     sContent = sContent.substring(0,iEntryPointIndex) + sAdd +
     sContent.substring(iEntryPointIndex);

     return sContent;
   }

   public static String addingProject(String sProjectTranslationContent,
   String sThisPair, String sProjectContent, String sProjectNumber){
      String sAdd = "";

      int iColonIndex = sThisPair.indexOf(":");
        if (iColonIndex>-1){
           String sTranslation = sThisPair.substring(0,iColonIndex);
           String sLanguage = sThisPair.substring(iColonIndex+1);
           if (!sLanguage.equals("")&&!sLanguage.equals("dutch")){//? что делать с dutch?
              String sLangLabel = "";
              if (sLanguage.equals("french")){
                 sLangLabel = "fra";
              }
              else if (sLanguage.equals("english")){
                 sLangLabel = "eng";
              }
              int iBegNodeIndex = sProjectTranslationContent.indexOf("<node number=\"" +
                 sTranslation + "\"");
              TreeMap tmFields = new TreeMap();
              tmFields.put("title","titel");
              tmFields.put("subtitle","subtitle");
              tmFields.put("text","omschrijving");
              Set set = tmFields.entrySet();
              Iterator it = set.iterator();
              while (it.hasNext()){
                 Map.Entry me = (Map.Entry)it.next();
                 String sOld = (String)me.getKey();
                 String sNew = (String)me.getValue();
                 int iBegIndex = sProjectTranslationContent.indexOf("<" + sOld + ">",
                    iBegNodeIndex);
                 int iEndIndex = sProjectTranslationContent.indexOf("</" + sOld +">",
                    iBegIndex);
                 sAdd += "\t<" + sNew + "_" + sLangLabel + ">" +
                    sProjectTranslationContent.substring(iBegIndex + sOld.length() + 2, iEndIndex) +
                    "</" + sNew + "_" + sLangLabel + ">\n\t";
              }
              iBegNodeIndex = sProjectContent.indexOf("<node number=\"" + sProjectNumber + "\"");
              int iEntryPointIndex = sProjectContent.indexOf("</node>",iBegNodeIndex);
              sProjectContent = sProjectContent.substring(0,iEntryPointIndex) +
                 sAdd + sProjectContent.substring(iEntryPointIndex);
           }
        }

      return sProjectContent;
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

   public static String removingLanguageTag(String sContent){
      int iBegIndex = sContent.indexOf("<language>");
      while (iBegIndex>-1){
         int iEndIndex = sContent.indexOf("</language>",iBegIndex);
         sContent = sContent.substring(0,iBegIndex) + sContent.substring(iEndIndex + 13);
         iBegIndex = sContent.indexOf("<language>");
      }
      return sContent;
   }

   public static void treatingZichtbaar(String sFileName, MigrateUtil mu) throws Exception{
      String sContent = mu.readingFile(sFileName);
      sContent = sContent.replaceAll("<status>Zichtbaar</status>","<titel_zichtbaar>1</titel_zichtbaar>");
      sContent = sContent.replaceAll("<status>Verborgen</status>","<titel_zichtbaar>0</titel_zichtbaar>");
      sContent = sContent.replaceAll("<status></status>\n\t\t","");
      sContent = sContent.replaceAll("<status>-</status>\n\t\t","");
      mu.writingFile(sFileName,sContent);
   }
}
