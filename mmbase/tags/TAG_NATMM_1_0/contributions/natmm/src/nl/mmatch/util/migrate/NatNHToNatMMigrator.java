package nl.mmatch.util.migrate;

import java.util.*;
import java.io.*;
import java.text.*;

import org.mmbase.util.logging.*;

import nl.leocms.applications.NatNHConfig;

public class NatNHToNatMMigrator {

  private static final Logger log = Logging.getLoggerInstance(NatNHToNatMMigrator.class);

	public static void run() throws Exception {
	
    TreeMap tmRenamingFields = new TreeMap();
    ArrayList alDeletingFields = new ArrayList();
    String sFolder = NatNHConfig.rootDir + "MicroSites/";
    MigrateUtil mu = new MigrateUtil();
      
    try{
	
			tmRenamingFields.put("title", "titel");
			tmRenamingFields.put("body", "omschrijving");
			tmRenamingFields.put("displaydate", "embargo");
			tmRenamingFields.put("expiredate", "verloopdatum");
			tmRenamingFields.put("subtitle", "titel_fra");
			tmRenamingFields.put("quotetitle", "titel_eng");
			tmRenamingFields.put("id", "metatags");
			tmRenamingFields.put("name", "naam");
			tmRenamingFields.put("menu_name", "naam_eng");
			tmRenamingFields.put("description", "omschrijving");
			tmRenamingFields.put("menuname", "kortetitel");
	
			alDeletingFields.add("copyright");
			alDeletingFields.add("lastmodified");
			alDeletingFields.add("source");
			alDeletingFields.add("quote");
			alDeletingFields.add("create");
			alDeletingFields.add("ttl");
			alDeletingFields.add("email");
	
			//adding portal node into websites (then it will be rubriek)
			String sFileName = sFolder + "portals.xml";
			File file = new File(sFileName);
			String sAllContent = mu.readingFileRemoveFields(sFileName,alDeletingFields);
			int iIndBegin = sAllContent.indexOf("<node number");
			int iIndEnd = sAllContent.indexOf("</node>") + 7;
			if (iIndBegin > -1) {
				sAllContent = sAllContent.substring(iIndBegin, iIndEnd);
			}
			int iIndexBegAlias = sAllContent.indexOf("alias");
			if (iIndexBegAlias > -1) {
				int iIndexEndAlias = sAllContent.indexOf(">", iIndexBegAlias);
				sAllContent = sAllContent.substring(0, iIndexBegAlias - 1) +
				sAllContent.substring(iIndexEndAlias);
			}
	
			//getting portal number
			String sPortalNumber = (String)  mu.getNodesFromFile(sFolder + "portals.xml").toArray()[0];
	
			file.delete(); //deleting portal.xml
	
			//getting websites numbers
			sFileName = sFolder + "websites.xml";
			file = new File(sFileName);
	
			ArrayList alWebsites = new ArrayList();
			if (file.exists()) {
				alWebsites =  mu.getNodesFromFile(sFileName);
			}
	
			String sAllWebsitesContent = mu.readingFileRemoveFields(sFileName,alDeletingFields);
			sAllWebsitesContent = sAllWebsitesContent.substring(0,
			sAllWebsitesContent.indexOf("</websites>") - 1);
			sAllWebsitesContent += sAllContent + "</websites>";
			sAllWebsitesContent = sAllWebsitesContent.replaceAll("menuname","naam_eng");
	
			mu.writingFile(file, sFileName, sAllWebsitesContent);
	
			//treating files which we want to rename and change fields
			TreeMap tmRenamingFiles = new TreeMap();
			tmRenamingFiles.put("articles", "artikel");
			tmRenamingFiles.put("pages", "pagina");
			tmRenamingFiles.put("paragraphs", "paragraaf");
			tmRenamingFiles.put("templates", "paginatemplate");
			tmRenamingFiles.put("websites", "rubriek");
			tmRenamingFiles.put("groups", "menu");
			tmRenamingFiles.put("urls", "link");
	
			Set set = tmRenamingFiles.entrySet();
			Iterator itRenamingFiles = set.iterator();
	
			while (itRenamingFiles.hasNext()) {
				Map.Entry me = (Map.Entry) itRenamingFiles.next();
				sFileName = sFolder + me.getKey() + ".xml";
				file = new File(sFileName);
				if (file.exists()) {
					sAllContent = mu.readingFileRemoveFields(sFileName,alDeletingFields);
					if (!me.getKey().equals("jumpers")){
						sAllContent = sAllContent.replaceAll("<" + me.getKey(),
							"<" + me.getValue());
						sAllContent = sAllContent.replaceAll("</" + me.getKey() + ">",
							"</" + me.getValue() + ">");
					}
					if (me.getKey().equals("urls")){
						int iBegPosttextIndex = sAllContent.indexOf("<posttext>");
						while (iBegPosttextIndex>-1){
							int iBegPretextIndex = sAllContent.indexOf("<pretext>");
							int iEndPretextIndex = sAllContent.indexOf("</pretext>");
							String sPretext = sAllContent.substring(iBegPretextIndex +
								9, iEndPretextIndex);
							int iEndPosttextIndex = sAllContent.indexOf("</posttext>");
							String sPosttext = sAllContent.substring(iBegPosttextIndex +
								10, iEndPosttextIndex);
							sAllContent = sAllContent.substring(0,iBegPosttextIndex-1) +
								"\t<omschrijving>\n\t\t\t" + sPretext + "; " + sPosttext +
								"\n\t\t</omschrijving>\n" + sAllContent.substring(iEndPretextIndex+10);
							iBegPosttextIndex = sAllContent.indexOf("<posttext>");
						}
						sAllContent = sAllContent.replaceAll("<description","<titel");
						sAllContent = sAllContent.replaceAll("</description>","</titel>");
					}
					if (me.getKey().equals("templates")){
						sAllContent = sAllContent.replaceAll("<title","<naam");
						sAllContent = sAllContent.replaceAll("</title>","</naam>");
					}
					if (me.getKey().equals("websites")){
						sAllContent = sAllContent.replaceAll("</node>",
							"\t<sitestatname>natuurherstel</sitestatname>\n\t</node>");
					}
					sAllContent = mu.renamingFields(sAllContent,tmRenamingFields);
					mu.writingFile(file, sFolder + me.getValue() + ".xml", sAllContent);
				}
			}
	
			//renaming fields in images.xml
			sFileName = sFolder + "images.xml";
			file = new File(sFileName);
			sAllContent = mu.readingFileRemoveFields(sFileName,alDeletingFields);
			sAllContent = mu.renamingFields(sAllContent,tmRenamingFields);
			mu.writingFile(file, sFileName, sAllContent);
	
	
			//deleting data that we do not want to migrate
	
			ArrayList alDeletingFiles = new ArrayList();
			alDeletingFiles.add("editwizards.xml");
			alDeletingFiles.add("mmbaseusers.xml");
			alDeletingFiles.add("mmevents.xml");
			alDeletingFiles.add("jumpers.xml");
	
			Iterator itDeletingFiles = alDeletingFiles.iterator();
	
			ArrayList alDeletedNodes = new ArrayList(); //new
	
			while (itDeletingFiles.hasNext()) {
				sFileName = sFolder + itDeletingFiles.next();
				file = new File(sFileName);
				// building list of nodes to be deleted
				if (file.exists()) {
					alDeletedNodes.addAll(mu.getNodesFromFile(sFileName));
				}
				file.delete();
			}
	
			//getting pages numbers
			sFileName = sFolder + "pagina.xml";
			file = new File(sFileName);
			ArrayList alPaginas = new ArrayList();
			if (file.exists()) {
				alPaginas =  mu.getNodesFromFile(sFileName);
			}
	
			sFileName = sFolder + "posrel.xml";
			file = new File(sFileName);
			sAllContent = mu.readWholeFile(sFileName,alDeletedNodes);
			String sContentAllPosrel = sAllContent;
	
			Iterator itWebsites = alWebsites.iterator();
	
			//building correspondense list between websites(rubrieks) and pages
			TreeMap tmPaginaRubriek = new TreeMap();
			TreeMap tmRubriekPagina = new TreeMap();
	
			while (itWebsites.hasNext()) {
				String sRubriek = (String) itWebsites.next();
				int iSNumberIndex = sAllContent.indexOf("snumber=\"" + sRubriek);
				while (iSNumberIndex != -1) {
					int iDNIndex = sAllContent.indexOf("dnumber", iSNumberIndex);
					int iQuotIndex = sAllContent.indexOf("\"", iDNIndex + 9);
					String sPaginaNumber = sAllContent.substring(iDNIndex + 9,
						iQuotIndex);
					if (alPaginas.contains(sPaginaNumber)) {
						tmPaginaRubriek.put(sPaginaNumber, sRubriek);
						tmRubriekPagina.put(sRubriek,sPaginaNumber);
					}
					int iNewBeingIndex = sAllContent.indexOf("<node", iSNumberIndex);
					sAllContent = sAllContent.substring(iNewBeingIndex);
					iSNumberIndex = sAllContent.indexOf("snumber=\"" + sRubriek +
						"\"");
				}
				sAllContent = sContentAllPosrel;
			}
	
			//building correspondense list between websites(rubrieks) and articles
			// in TreeMap we'll put numbers of first articles related to rubrieks as keys
			// and artikel.intro field of the article related to rubriek as value
	
			TreeMap tmPaginaArticle = new TreeMap();
			ArrayList alArticles =  mu.getNodesFromFile(sFolder + "artikel.xml");
			String sArtikelContent = mu.readingFileRemoveFields(sFolder + "artikel.xml",alDeletingFields);
	
			itWebsites = alWebsites.iterator();
	
			while (itWebsites.hasNext()) {
			  String sRubriek = (String) itWebsites.next();
			  int iSNumberIndex = sAllContent.indexOf("snumber=\"" + sRubriek);
			  while (iSNumberIndex != -1) {
					int iDNIndex = sAllContent.indexOf("dnumber", iSNumberIndex);
					int iQuotIndex = sAllContent.indexOf("\"", iDNIndex + 9);
					String sArticleNumber = sAllContent.substring(iDNIndex + 9,
						iQuotIndex);
					if (alArticles.contains(sArticleNumber)) {
						String sPageNumber = (String)tmRubriekPagina.get(sRubriek);
						int iNodeBeg = sArtikelContent.indexOf("<node number=\"" + sArticleNumber + "\"") ;
						int iIntroBeg = sArtikelContent.indexOf("<intro>",iNodeBeg) + 7;
						int iIntroEnd = sArtikelContent.indexOf("</intro>",iIntroBeg);
						String sIntro = sArtikelContent.substring(iIntroBeg,iIntroEnd);
						tmPaginaArticle.put(sPageNumber,sIntro);
					}
					int iNewBeingIndex = sAllContent.indexOf("<node", iSNumberIndex);
					sAllContent = sAllContent.substring(iNewBeingIndex);
					iSNumberIndex = sAllContent.indexOf("snumber=\"" + sRubriek +
						"\"");
				}
				sAllContent = sContentAllPosrel;
			}
	
			sFileName = sFolder + "pagina.xml";
			file = new File(sFileName);
			String sPaginaContent = mu.readingFileRemoveFields(sFileName,alDeletingFields);
	
			set = tmPaginaArticle.entrySet();
			Iterator it = set.iterator();
	
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String sPaginaNumber = (String) me.getKey();
				String sIntro = (String) me.getValue();
				int iBegNodeIndex = sPaginaContent.indexOf("<node number=\"" + sPaginaNumber + "\"");
				int iEndNodeIndex = sPaginaContent.indexOf("</node>",iBegNodeIndex);
				sPaginaContent = sPaginaContent.substring(0,iEndNodeIndex) +
					 "\t<omschrijving>" + sIntro + "</omschrijving>\n\t" +
					 sPaginaContent.substring(iEndNodeIndex);
			}
	
			mu.writingFile(file, sFileName, sPaginaContent);
	
			//replacing relations page->page to rubriek->page
			set = tmPaginaRubriek.entrySet();
			it = set.iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				String sPaginaNumber = (String) me.getKey();
				String sRubriekNumber = (String) me.getValue();
				int iSNumberIndex = sContentAllPosrel.indexOf("snumber=\"" +
					sPaginaNumber);
				while (iSNumberIndex > -1) {
					int iDNIndex = sContentAllPosrel.indexOf("dnumber",
						iSNumberIndex);
					int iQuotIndex = sContentAllPosrel.indexOf("\"", iDNIndex + 9);
					String sNodeNumber = sContentAllPosrel.substring(iDNIndex + 9,
						iQuotIndex);
					if (alPaginas.contains(sNodeNumber)) {
						int iPosIndex = sContentAllPosrel.indexOf("<pos>",
							iSNumberIndex);
						int iPosCloseIndex = sContentAllPosrel.indexOf("</pos>",
							iSNumberIndex);
						String sPosrelPos = sContentAllPosrel.substring(iPosIndex + 5,
							iPosCloseIndex);
					  int iPosrelPos = Integer.parseInt(sPosrelPos);
					  String sPosrelPosNew = "";
					  if (iPosrelPos > -1) {
						 sPosrelPosNew = Integer.toString(iPosrelPos + 1);
					  } else {
						 sPosrelPosNew = Integer.toString(iPosrelPos - 1);
					  }
						sContentAllPosrel = sContentAllPosrel.substring(0,
							iPosIndex + 5) + sPosrelPosNew +
							sContentAllPosrel.substring(iPosCloseIndex);
						sContentAllPosrel = sContentAllPosrel.substring(0,
							iSNumberIndex + 9) + sRubriekNumber +
							sContentAllPosrel.substring(iDNIndex - 2);
						iSNumberIndex = sContentAllPosrel.indexOf("snumber=\"" +
							sPaginaNumber);
					}
					else {
						iSNumberIndex = sContentAllPosrel.indexOf("snumber=\"" +
							sPaginaNumber, iDNIndex);
					}
				}
			}
			/* We need to create contentrel.xml file and add there information about
				relations between pages and articles*/
	
			String sContentrelContent = "";
			ArrayList alPages =  mu.getNodesFromFile(sFolder + "pagina.xml");
			//ArrayList alArticles =  mu.getNodesFromFile(sFolder + "artikel.xml");
			TreeMap tmDeletedNodesFromPosrelXMLPositions = new TreeMap();
	
			String sPosrelContent = sContentAllPosrel;
	
			Iterator itPages = alPages.iterator();
	
			while (itPages.hasNext()) {
				String sPageNumber = (String) itPages.next();
				int iPNPos = sPosrelContent.indexOf("snumber=\"" + sPageNumber +
																"\"");
				while (iPNPos != -1) {
					int iRelatedNodeEndIndex = sPosrelContent.indexOf("\" rtype=",
						iPNPos);
					String sRelatedNodeNumber = sPosrelContent.substring(iPNPos + 20 +
						sPageNumber.length(), iRelatedNodeEndIndex);
					if (alArticles.contains(sRelatedNodeNumber)) {
						int iNewRecordBeginIndex = sPosrelContent.indexOf(
							"<node number", iPNPos - 45);
						int iNewRecordEndIndex = sPosrelContent.indexOf("</node>",
							iPNPos) + 7;
	
						int iDelRelationNumberBeginIndex = iNewRecordBeginIndex + 14;
						int iDelRelationNumberEndIndex = sPosrelContent.indexOf(
							"owner=", iDelRelationNumberBeginIndex) - 2;
						String sRelationNumber = sPosrelContent.substring(
							iDelRelationNumberBeginIndex, iDelRelationNumberEndIndex);
						int iRecordLength = iNewRecordEndIndex - iNewRecordBeginIndex;
						tmDeletedNodesFromPosrelXMLPositions.put(sRelationNumber,
							new Integer(iRecordLength));
						String sNewRecord = sPosrelContent.substring(
							iNewRecordBeginIndex, iNewRecordEndIndex);
						sContentrelContent += sNewRecord + "\n";
					}
					int iNewBeingIndex = sPosrelContent.indexOf("<node", iPNPos);
					if (iNewBeingIndex > -1) {
						sPosrelContent = sPosrelContent.substring(iNewBeingIndex);
					}
					else {
						sPosrelContent = "";
					}
					iPNPos = sPosrelContent.indexOf("snumber=\"" + sPageNumber + "\"");
				}
				sPosrelContent = sContentAllPosrel;
			}
	
			sContentrelContent = sContentrelContent.replaceAll("posrel","contentrel");
			mu.creatingNewXML(sFolder,"contentrel","natmmnh", sContentrelContent);
	
			/* We need to create childrel.xml file and add there information about
				relations between rubrieks */
	
			sPosrelContent = sContentAllPosrel;
	
			String sChildrelContent = "";
			itWebsites = alWebsites.iterator();
			while (itWebsites.hasNext()) {
				String sWebsiteNumber = (String) itWebsites.next();
				int iPNPos = sPosrelContent.indexOf("dnumber=\"" + sWebsiteNumber +	"\"");
				while (iPNPos != -1) {
					int iRelatedNodeBegIndex = sPosrelContent.indexOf("snumber=\"",
						iPNPos - 20) + 9;
					String sRelatedNodeNumber = sPosrelContent.substring(
						iRelatedNodeBegIndex, iPNPos - 2);
					if (sRelatedNodeNumber.equals(sPortalNumber)) {
						int iNewRecordBeginIndex = sPosrelContent.indexOf(
							"<node number", iPNPos - 55);
						int iNewRecordEndIndex = sPosrelContent.indexOf("</node>",
							iPNPos) + 7;
	
						int iDelRelationNumberBeginIndex = iNewRecordBeginIndex + 14;
						int iDelRelationNumberEndIndex = sPosrelContent.indexOf(
							"owner=", iDelRelationNumberBeginIndex) - 2;
						String sRelationNumber = sPosrelContent.substring(
							iDelRelationNumberBeginIndex, iDelRelationNumberEndIndex);
						int iRecordLength = iNewRecordEndIndex - iNewRecordBeginIndex;
						tmDeletedNodesFromPosrelXMLPositions.put(sRelationNumber,
							new Integer(iRecordLength));
	
						String sNewRecord = sPosrelContent.substring(
							iNewRecordBeginIndex, iNewRecordEndIndex);
						sChildrelContent += sNewRecord + "\n";
					}
					int iNewBeingIndex = sPosrelContent.indexOf("<node", iPNPos);
					sPosrelContent = sPosrelContent.substring(iNewBeingIndex);
					iPNPos = sPosrelContent.indexOf("dnumber=\"" + sWebsiteNumber +
															  "\"");
				}
				sPosrelContent = sContentAllPosrel;
				sChildrelContent = sChildrelContent.replaceAll("posrel", "parent");
			}
			mu.creatingNewXML(sFolder,"childrel","natmmnh",sChildrelContent);
	
			//we need to delete from posrel.xml records migrated to contentrel.xml and childrel.xml
	
			Set setDeletedNodesFromPosrelXMLPositions =
				tmDeletedNodesFromPosrelXMLPositions.entrySet();
			Iterator ItDeletedNodesFromPosrelXMLPositions =
				setDeletedNodesFromPosrelXMLPositions.iterator();
			while (ItDeletedNodesFromPosrelXMLPositions.hasNext()) {
				Map.Entry me = (Map.Entry) ItDeletedNodesFromPosrelXMLPositions.
					next();
				int iBeginDeletingPos = sContentAllPosrel.indexOf("<node number=\"" +
					me.getKey() + "\"");
				int iEndDeletingPos = iBeginDeletingPos +
					( (Integer) me.getValue()).intValue();
				sContentAllPosrel = sContentAllPosrel.substring(0,
					iBeginDeletingPos - 1) +
					sContentAllPosrel.substring(iEndDeletingPos + 1);
			}
	
			//renaming relation between paginas and paginatemplates from related to gebruikt
			ArrayList alTemplates =  mu.getNodesFromFile(sFolder + "paginatemplate.xml");
			sFileName = sFolder + "insrel.xml";
			file = new File(sFileName);
			sAllContent = mu.readingFile(sFileName, alDeletedNodes);
			Iterator itTemplates = alTemplates.iterator();
			while (itTemplates.hasNext()){
				String sNode = (String)itTemplates.next();
				while (sAllContent.indexOf("dnumber=\"" + sNode + "\" rtype=\"related\"")>-1){
					sAllContent = sAllContent.replaceAll("dnumber=\"" + sNode + "\" rtype=\"related\"",
																	 "dnumber=\"" + sNode + "\" rtype=\"gebruikt\"");
				}
			}
			//writing changed insrel.xml
			mu.writingFile(file, sFileName, sAllContent);
	
			//writing changed posrel.xml
			sFileName = sFolder + "posrel.xml";
			file = new File(sFileName);
			mu.writingFile(file, sFileName, sContentAllPosrel);
	
		} catch (Exception e){
			log.info("Changing xml files was already done");
		}

   }

}
