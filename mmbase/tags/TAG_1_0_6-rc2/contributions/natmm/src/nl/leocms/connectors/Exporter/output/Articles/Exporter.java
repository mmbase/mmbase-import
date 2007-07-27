/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */

package nl.leocms.connectors.Exporter.output.Articles;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;
import javax.xml.parsers.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mmbase.bridge.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.ApplicationHelper;
import nl.leocms.util.tools.*;
import nl.leocms.connectors.Exporter.creatingXML;


public class Exporter implements Runnable

{
   private static final Logger log = Logging.getLoggerInstance(Exporter.class);
   private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

   public Exporter()
   {
   }


   public void run() {

      log.info("exporting articles");

      try {
         DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
         Document document = domBuilder.newDocument();
         Element elemRoot = document.createElement("articles");
         document.appendChild(elemRoot);

         SearchUtil su = new SearchUtil();
         Cloud cloud = CloudFactory.getCloud();
         NodeList nl = su.ArtikelsRelatedToPagina(cloud,"thuispagina","");
         creatingXML cxml = new creatingXML();
         ApplicationHelper ap = new ApplicationHelper(cloud);
         String tempDir = ap.getTempDir();
         for (int i = 0; i < nl.size(); i++){
            Node nArticle = cloud.getNode(nl.getNode(i).getStringValue("artikel.number"));
            Element elemArticle = document.createElement("article");
            elemRoot.appendChild(elemArticle);

            Element elemArticleId = document.createElement("id");
            elemArticleId.appendChild(document.createTextNode(nArticle.getStringValue("number")));
            elemArticle.appendChild(elemArticleId);

            Element elemArticleTitle = document.createElement("title");
            String sTitel_zichtbaar = nArticle.getStringValue("titel_zichtbaar");
            if (sTitel_zichtbaar!=null&&!sTitel_zichtbaar.equals("0")){
               elemArticleTitle.appendChild(document.createTextNode(nArticle.getStringValue("titel")));
            }
            elemArticle.appendChild(elemArticleTitle);

            Element elemArticleIntro = document.createElement("intro");
            elemArticleIntro.appendChild(document.createTextNode(nArticle.getStringValue("intro")));
            elemArticle.appendChild(elemArticleIntro);

            Element elemArticleDate = document.createElement("showDate");
            elemArticleDate.appendChild(document.createTextNode(DATE_TIME_FORMAT.format(
            new Date(nArticle.getLongValue("begindatum")*1000))));
            elemArticle.appendChild(elemArticleDate);

            Element elemArticleEmbargo = document.createElement("embargoDate");
            elemArticleEmbargo.appendChild(document.createTextNode(DATE_TIME_FORMAT.format(
            new Date(nArticle.getLongValue("embargo")*1000))));
            elemArticle.appendChild(elemArticleEmbargo);

            Element elemArticleExpire = document.createElement("embargoDate");
            elemArticleExpire.appendChild(document.createTextNode(DATE_TIME_FORMAT.format(
            new Date(nArticle.getLongValue("verloopdatum")*1000))));
            elemArticle.appendChild(elemArticleExpire);

            Element elemPools = document.createElement("pools");
            elemArticle.appendChild(elemPools);

            NodeList nlPools = nArticle.getRelatedNodes("pools");
            for (int j = 0 ; j < nlPools.size(); j++){
               Element elemPool = document.createElement("pool");
               elemPools.appendChild(elemPool);

               Element elemPoolId = document.createElement("id");
               elemPoolId.appendChild(document.createTextNode(nlPools.getNode(j).getStringValue("number")));
               elemPool.appendChild(elemPoolId);

               Element elemPoolName = document.createElement("name");
               elemPoolName.appendChild(document.createTextNode(nlPools.getNode(j).getStringValue("name")));
               elemPool.appendChild(elemPoolName);
            }

            Element elemParagraphs = document.createElement("paragraphs");
            elemArticle.appendChild(elemParagraphs);

            NodeList nlParagraphs = nArticle.getRelatedNodes("paragraaf");
            for (int j = 0 ; j < nlParagraphs.size(); j++){
               Element elemParagraph = document.createElement("paragraph");
               elemParagraphs.appendChild(elemParagraph);

               Element elemParagraphId = document.createElement("id");
               elemParagraphId.appendChild(document.createTextNode(nlParagraphs.getNode(j).getStringValue("number")));
               elemParagraph.appendChild(elemParagraphId);

               Element elemParagraphTitle = document.createElement("title");
               sTitel_zichtbaar = nlParagraphs.getNode(j).getStringValue("titel_zichtbaar");
               if (sTitel_zichtbaar!=null&&!sTitel_zichtbaar.equals("0")){
                  elemParagraphTitle.appendChild(document.createTextNode(
                  nlParagraphs.getNode(j).getStringValue("title")));
               }
               elemParagraph.appendChild(elemParagraphTitle);

               Element elemParagraphText = document.createElement("text");
               elemParagraphText.appendChild(document.createTextNode(nlParagraphs.getNode(j).getStringValue("tekst")));
               elemParagraph.appendChild(elemParagraphText);

               Element elemLinks = document.createElement("links");
               elemParagraph.appendChild(elemLinks);

               NodeList nlLinks = nlParagraphs.getNode(j).getRelatedNodes("link");
               for (int k = 0; k < nlLinks.size(); k++){
                  Element elemLink = document.createElement("link");
                  elemLinks.appendChild(elemLink);

                  Element elemLinkId = document.createElement("id");
                  elemLinkId.appendChild(document.createTextNode(nlLinks.getNode(k).getStringValue("number")));
                  elemLink.appendChild(elemLinkId);

                  Element elemLinkTitle = document.createElement("title");
                  elemLinkTitle.appendChild(document.createTextNode(nlLinks.getNode(k).getStringValue("titel")));
                  elemLink.appendChild(elemLinkTitle);

                  Element elemLinkAltTekst = document.createElement("alt_tekst");
                  elemLinkAltTekst.appendChild(document.createTextNode(nlLinks.getNode(k).getStringValue("alt_tekst")));
                  elemLink.appendChild(elemLinkAltTekst);

                  Element elemLinkUrl = document.createElement("url");
                  elemLinkUrl.appendChild(document.createTextNode(nlLinks.getNode(k).getStringValue("url")));
                  elemLink.appendChild(elemLinkUrl);

                  Element elemLinkTarget = document.createElement("target");
                  String sTargetValue = nlLinks.getNode(k).getStringValue("target");
                  if (sTargetValue.equals("")){
                     sTargetValue = "_top";
                  }
                  elemLinkTarget.appendChild(document.createTextNode(sTargetValue));
                  elemLink.appendChild(elemLinkTarget);

               }

               Element elemImages = document.createElement("images");
               elemParagraph.appendChild(elemImages);

               NodeList nlImages = nlParagraphs.getNode(j).getRelatedNodes("images");

               nl.leocms.connectors.Exporter.output.People.Exporter attPeople = new nl.leocms.connectors.Exporter.output.People.Exporter();
               document = attPeople.getImages(nlImages,document,elemImages,false,tempDir,cxml);

               Element elemAttachments = document.createElement("attachments");
               elemParagraph.appendChild(elemAttachments);
               NodeList nlAttachments = nlParagraphs.getNode(j).getRelatedNodes("attachments");

               nl.leocms.connectors.Exporter.output.Attachments.Exporter attExp = new nl.leocms.connectors.Exporter.output.Attachments.Exporter();
               document = attExp.getAttachments(nlAttachments,document,elemAttachments,tempDir,cxml);
            }
         }

         String[] sDirs = {"attachments","images"};
         cxml.create(document,"articles",sDirs);
      }
      catch (Exception e){
         log.info(e.toString());
      }
   }
}
