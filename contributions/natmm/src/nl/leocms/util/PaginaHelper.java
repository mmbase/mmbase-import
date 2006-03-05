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
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import nl.leocms.versioning.PublishManager;
import nl.leocms.servlets.UrlConverter;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.mmatch.HtmlCleaner;

public class PaginaHelper {
   
   public final static int MAX_NUMBER_LINKLIJST_ELEMENTS = 7;
   public final static int MAX_NUMBER_DOSSIER_ELEMENTS = 7;
   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(PaginaHelper.class.getName());

   Cloud cloud;
   /**
     * @param cloud
     */
   public PaginaHelper(Cloud cloud) {
      super();
    /*  assert(cloud != null); */
      this.cloud = cloud;
   }

   ////////////// general utilities /////////////
   /**
    * Returns the rubriek node, belonging to the given pagina. Or null if the pagina has
    * no
    * @param paginaNumber
    * @return
    */
   public Node getRubriek(String paginaNumber) {
      Node paginaNode = cloud.getNode(paginaNumber);
      if (paginaNode==null) {
         log.error("No such pagina with number: " + paginaNumber + ". Returning null.");
         return null;
      }
      NodeList rubriekL = paginaNode.getRelatedNodes("rubriek", "posrel", "SOURCE");
      if (rubriekL.size() == 1) {
         log.debug("returning rubriek: " + rubriekL.getNode(0).getNumber());
         return rubriekL.getNode(0);
      }
      log.debug("No rubriek for pagina with number: " + paginaNumber + ". Returning null.");
      return null;
   }

   public static Vector getBreadCrumbs(Cloud cloud, String paginaNumber) {
      Node paginaNode = cloud.getNode(paginaNumber);
      if (paginaNode==null) {
         log.error("No such pagina with number: " + paginaNumber + ". Returning null.");
         return null;
      }
      Vector breadcrumbs = new Vector();
      NodeList rubriekL = paginaNode.getRelatedNodes("rubriek", "posrel", "SOURCE");
      while (rubriekL.size() == 1 && !breadcrumbs.contains(rubriekL.getNode(0).getStringValue("number"))) {
         breadcrumbs.add(rubriekL.getNode(0).getStringValue("number"));
         rubriekL = rubriekL.getNode(0).getRelatedNodes("rubriek", "parent", "SOURCE");
      }
      return breadcrumbs;
   }
   
   public static String getRootRubriek(Cloud cloud, String paginaNumber) {
      Vector breadcrumbs = getBreadCrumbs(cloud, paginaNumber);
      return (breadcrumbs.size()>2 ? (String) breadcrumbs.get(breadcrumbs.size()-2) : null);
   }

  /**
     * Retrieves the rubriek node related to the given pagina node.
     *
     * @param paginaNode
     * @return
     */
   private Node getRubriek(Node paginaNode) {
      NodeList nodes =
         paginaNode.getRelatedNodes("rubriek", "posrel", "SOURCE");
      if (nodes.size() > 0) {
         return nodes.getNode(0);
      }
      return null;
   }

   ///////// methods to retrieve pagina's using urls /////////
   /**
     * Method finds a Page with a page name and a rubrieknummer.
     *
     * @param rubriekNumber
     * @param pageName
     * @return pagina Node
     */
   public Node retrievePaginaNumber(String rubriekNumber, String pageName) {
     /* assert rubriekNumber != null;
      assert pageName != null; */
      Node rubriek = cloud.getNode(rubriekNumber);
      Iterator bl = rubriek.getRelatedNodes("pagina").iterator();
      
      Node winner = null;

      while (winner==null && bl.hasNext()) {
         Node pagina = (Node) bl.next();
         if (HtmlCleaner.stripText(pagina.getStringValue("titel")).equals(pageName)) {
            winner = pagina;
         }
      }

      return winner;
   }
   
   /**
     * Returns a node is a content page (attribute contentpagina = 1)and obeys
     * to the given urlfragment.
     *
     * @return
     */
   protected Node getContentPaginaByUrlfragment(String urlfragment) {
      NodeManager contentPagesManager = cloud.getNodeManager("pagina");
      String where = "contentpagina=1 AND urlfragment='" + urlfragment + "'";
      log.debug("where: " + where);
      NodeList nl = contentPagesManager.getList(where, null, null);
      if (nl.size() == 1) {
         return nl.getNode(0);
      }
      return null;
   }

   // /////// methods to create the url of pagina's /////////
   /**
     * Creates the url for a pagina.
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String createPaginaUrl(String paginaNumber, String requestURI) {
      return createPaginaUrl(paginaNumber, requestURI, false);
   }

   /**
     * Creates the slash seperated string for a pagina.
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String getUrlPathToRootString(String itemNumber, String requestURI, boolean relative) {
      StringBuffer url = new StringBuffer();
      Node itemNode = cloud.getNode(itemNumber); 
      Node paginaNode = getPaginaNode(itemNode);
      Node rubriek = getRubriek(paginaNode);
      RubriekHelper rHelper = new RubriekHelper(cloud);
      url.append(
         rHelper.getUrlPathToRootString(
            rubriek.getIntValue("number"),
            requestURI, relative));
      url.append('/');
      url.append(HtmlCleaner.stripText(paginaNode.getStringValue("titel")));
      log.debug(url.toString());
      return url.toString();
   }

   public String getPaginaUrlPathToRootString(String paginaNumber, String requestURI, boolean relative) {
      StringBuffer url = new StringBuffer();
      Node paginaNode = cloud.getNode(paginaNumber);
      Node rubriek = getRubriek(paginaNode);
      RubriekHelper rHelper = new RubriekHelper(cloud);
      url.append(rHelper.getUrlPathToRootString(rubriek.getIntValue("number"), requestURI, relative));
      url.append('/');
      url.append(HtmlCleaner.stripText(paginaNode.getStringValue("titel")));
      log.debug("getPaginaUrlPathToRootString" + url.toString());
      return url.toString();
   }


   /**
     * Creates the url for a pagina.
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String createPaginaUrl(String paginaNumber, String requestURI, boolean relative) {
      StringBuffer url = new StringBuffer();
      if(UrlConverter.URLCONVERSION) {
         url.append(getPaginaUrlPathToRootString(paginaNumber, requestURI, relative));
         url.append(UrlConverter.PAGE_EXTENSION);
      } else { // in this case, only relative path is supported
         Node paginaTemplate = getPaginaTemplate(paginaNumber);
         if(paginaTemplate!=null) {
            url.append(paginaTemplate.getStringValue("url"));   
         } else {
            url.append(UrlConverter.ROOT_TEMPLATE);
         }
         url.append('?');
         url.append(UrlConverter.PAGE_PARAM);
         url.append('=');
         url.append(paginaNumber);
      }
      log.debug("createPaginaUrl " + url.toString());
      return url.toString();
   }

   /**
     * Creates the url for an item on a pagina.
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @param itemNumber
     * @return
     */
 

   public String createItemUrl(String itemNumber, String pageNumber, String params, String requestURI) {
      StringBuffer url = new StringBuffer();
      if(UrlConverter.URLCONVERSION) {
         url.append(getUrlPathToRootString(itemNumber, requestURI, false));
         Node itemNode = cloud.getNode(itemNumber);
         url.append('/');
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(itemNode.getLongValue("datumlaatstewijziging")*1000); // add lastmodifieddate
         
         if(cal.get(Calendar.YEAR)%100<10) { url.append('0'); }
         url.append(cal.get(Calendar.YEAR)%100);
         if(cal.get(Calendar.MONTH)+1<10) { url.append('0'); }
         url.append(cal.get(Calendar.MONTH)+1);
         if(cal.get(Calendar.DAY_OF_MONTH)<10) { url.append('0'); }
         url.append(cal.get(Calendar.DAY_OF_MONTH));
    
         url.append(HtmlCleaner.forURL(HtmlCleaner.stripText(itemNode.getStringValue("titel"))));
         url.append(UrlConverter.PAGE_EXTENSION);
         if (params != null && !params.equals("")) { url.append('?').append(params); }
      } else {
         Node itemNode = cloud.getNode(itemNumber); 
         Node paginaNode = null;
         if(pageNumber!=null && !pageNumber.equals("-1")) {
            paginaNode = cloud.getNode(pageNumber); 
         } else {
            paginaNode = getPaginaNode(itemNode);
         }
         Node paginaTemplate = getPaginaTemplate(paginaNode.getStringValue("number"));
         if(paginaTemplate!=null) {
            url.append(paginaTemplate.getStringValue("url"));
         } else {
            url.append(UrlConverter.ROOT_TEMPLATE);
         }
         url.append('?');
         if (params != null && !params.equals("")) { url.append(params).append('&'); }
   
         url.append(UrlConverter.ITEM_PARAM);
         url.append('=');
         url.append(itemNumber);  
      }
      log.debug("createItemUrl " + url.toString());
      return url.toString();
   }
   
   /**
     * Creates the url to an editwizard of a page.
     * @param paginaNumber
     * @param contextPath
     * @param relative
     * @return url
     */
   public TreeMap createEditwizardUrls(String pageNumber, String contextPath) {
      TreeMap ewUrls = new TreeMap();
      
      Node templateNode = getPaginaTemplate(pageNumber);
      if(templateNode!=null) {
         NodeList editwizardNodes = templateNode.getRelatedNodes("editwizards", "related", "DESTINATION");
         for(int e = 0; e < editwizardNodes.size(); e++) {         
            Node editwizardNode = editwizardNodes.getNode(e);
            String ewTitle = editwizardNode.getStringValue("description");
            String ewUrl = contextPath;
            if(editwizardNode!=null) { // ** check whether the path is used at least once ***
               String ewType = editwizardNode.getStringValue("type");
               if(ewType.equals("list")) {
                  NodeList nl = cloud.getList(pageNumber, editwizardNode.getStringValue("nodepath"), "pagina.number", null, null, null, null, false);
                  if(nl.size()>0) {
                     ewUrl += "/mmbase/edit/wizard/jsp/list.jsp?wizard=" + editwizardNode.getStringValue("wizard")
                         + "&startnodes=" + pageNumber 
                         + "&nodepath=" + editwizardNode.getStringValue("nodepath") 
                         + "&fields=" + editwizardNode.getStringValue("fields")      
                       //  + "&constraints=" + editwizardNode.getStringValue("constraints") *** empty constraint will result in don't panic ***
                       //  + "&age=" + editwizardNode.getStringValue("age")
                       //  + "&searchdir=" + editwizardNode.getStringValue("searchdir")
                         + "&orderby=" + editwizardNode.getStringValue("orderby")
                         + "&directions=" + editwizardNode.getStringValue("directions")
                         + "&pagelength=" + editwizardNode.getStringValue("pagelength")
                         + "&maxpagecount=" + editwizardNode.getStringValue("maxpagecount")
                         + "&maxsize=" + 2*1024*1024
                         + "&searchfields=" + editwizardNode.getStringValue("searchfields")
                       //  + "&searchtype=" + editwizardNode.getStringValue("searchtype")
                       //  + "&searchvalue=" + editwizardNode.getStringValue("searchvalue")
                         + "&search=" + editwizardNode.getStringValue("search")
                         + "&origin=" + pageNumber
                         + "&referer=/editors/empty.html";
                     if(editwizardNode.getStringValue("m_distinct").equals("1")) { ewUrl += "&distinct=true"; }
                     Node rubriek = getRubriek(pageNumber);
                     if(rubriek!=null) {
                         ewUrl += "&creatierubriek=" + rubriek.getNumber();
                     }
                  }
               } else if(ewType.equals("jsp")) {
                  ewUrl = editwizardNode.getStringValue("wizard");
               } else {
                  ewUrl += "/mmbase/edit/wizard/jsp/wizard.jsp?language=nl&wizard=" + editwizardNode.getStringValue("wizard")
                     + "&nodepath=pagina&referrer=/editors/empty.html&objectnumber=" + pageNumber
                     + "&maxsize=" + 2*1024*1024;
               }
            }
            if(!ewUrl.equals(contextPath)) {
               ewUrls.put(ewTitle,ewUrl);
            }
         }
      }
      return ewUrls;
   }
   
   /**
     * Creates the url for a pagina.
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String getSiteStatCounterName(String rubriekNumber, String paginaNumber) {
      Node paginaNode = cloud.getNode(paginaNumber);
      boolean isContentPagina = paginaNode.getBooleanValue("contentpagina");
      if (isContentPagina) {
         // no sitestats for content pages
         return null;
      }
      RubriekHelper rubriekHelper = new RubriekHelper(cloud);
      String siteStatCounterName = rubriekHelper.getSiteStatCounterName(rubriekNumber, null);
      siteStatCounterName += paginaNode.getStringValue("urlfragment");
      return siteStatCounterName;
   }

   /**
     * Creates a url for the given content element in the given rubriek number.
     * If the cms url data is malformed, an exception is thrown
     *
     * @param contentElement
     * @param rubriekNumber
    * @param contextPath the context path of the server
     * @exception MalformedURLException
     *               the created url is malformed
     * @return
    * @deprecated do not use use @see createUrlForContentElement(Node, int, String, String) instead
     */
   public String createUrlForContentElement(Node contentElement, int rubriekNumber, String contextPath)
      throws MalformedURLException {
      /* hh
       assert contentElement != null;
      assert rubriekNumber > 0; 
      Node contentPage = getContentPagina(contentElement);
      RubriekHelper rhelper = new RubriekHelper(cloud);
      String rubriekUrl = rhelper.getUrlPathToRootString(rubriekNumber, contextPath);
      StringBuffer ret = new StringBuffer(rubriekUrl);
      ret.append('/');
      ret.append(contentPage.getStringValue("urlfragment"));
      ret.append(UrlConverter.PAGE_EXTENSION);
      URL url = new URL(ret.toString());
      ret.append('?');
      ret.append("objectnumber=");
      ret.append(contentElement.getStringValue("number"));
      return ret.toString();
      */
      return "";
   }

   /**
       * Creates a url for the given content element in the given rubriek number.
       * If the cms url data is malformed, an exception is thrown
       *
       * @param contentElement
       * @param rubriekNumber
       * @param referPagina pagina from which the url is created
       * @param contextPath the context path of the server
       * @exception MalformedURLException
       *               the created url is malformed
       * @return
       */
      public String createUrlForContentElement(Node contentElement, int rubriekNumber, String referPagina, String contextPath)
         throws MalformedURLException {
         /* hh
         assert contentElement != null;
         assert rubriekNumber > 0; 
         Node contentPage = getContentPagina(contentElement);
         RubriekHelper rhelper = new RubriekHelper(cloud);
         String rubriekUrl = rhelper.getUrlPathToRootString(rubriekNumber, contextPath);
         StringBuffer ret = new StringBuffer(rubriekUrl);
         ret.append('/');
         ret.append(contentPage.getStringValue("urlfragment"));
         ret.append(UrlConverter.PAGE_EXTENSION);
         URL url = new URL(ret.toString());
         ret.append('?');
         ret.append("objectnumber=");
         ret.append(contentElement.getStringValue("number"));
         ret.append("&referpagina="+referPagina);
         return ret.toString();
         */
         return "";
      }



   //////////////// content pagina's //////////////
   /**
     * Returns a node list with all pagina objecten that are content pages
     * (attribute contentpagina = 1).
     *
     * @return NodeList the node list
     */
   public NodeList getContentPaginaList() {
      NodeManager contentPagesManager = cloud.getNodeManager("pagina");
      return contentPagesManager.getList("contentpagina=1", null, null);
   }

   /**
     * Retrieves the content page for the given content element.
     *
     * @param contentElement
     * @return
     */
   public Node getContentPagina(Node contentElement) {
      String typedef = contentElement.getNodeManager().getName();
      log.debug("typedef: " + typedef);
      Node contentPagina = cloud.getNodeByAlias("contentpagina." + typedef);
      return contentPagina;
   }

   /**
     * Determines if a given content element has a valid content page.
     *
     * @param contentElement
     * @return
     */
   public boolean hasContentPagina(Node contentElement) {
      String typedef = contentElement.getNodeManager().getName();
      log.debug("typedef: " + typedef);
      return cloud.hasNode("contentpagina." + typedef);
   }

   /**
     * Determines if a given content element has a valid content page.
     *
     * @param contentElementNumber
     * @return
     */
   public boolean hasContentPagina(String contentElementNumber) {
      boolean hasContentPagina = false;
      if (cloud.hasNode(contentElementNumber)) {
         hasContentPagina = hasContentPagina(cloud.getNode(contentElementNumber));
      }
      return hasContentPagina;
   }

   /**
     * Determines if a given pagina is a contentpagina.
     *
     * @param paginaNumber
     * @return
     */
   public boolean isContentPagina(String paginaNumber) {
      boolean isContentPagina = false;
      if (cloud.hasNode(paginaNumber)) {
         Node paginaNode = cloud.getNode(paginaNumber);
         isContentPagina = paginaNode.getIntValue("contentpagina") == 1;
      }

      return isContentPagina;
   }
   /////////// templates ///////////
   /**
     * Returns the pagina template with the given pageNumber.
     *
     * @param pageNumber
     * @return
     */
   public Node getPaginaTemplate(String pageNumber) {
      Node pageNode = cloud.getNode(pageNumber);
      NodeList ptList =
         pageNode.getRelatedNodes("paginatemplate", "gebruikt", "DESTINATION");
      if (ptList.size() == 1) {
         return ptList.getNode(0);
      }
      return null;
   }

   /**
     * Returns the menu template with the given pageNumber.
     *
     * @param pageNumber
     * @return
     */
   public Node getMenuTemplate(String pageNumber) {
      Node pageNode = cloud.getNode(pageNumber);
      NodeList ptList = pageNode.getRelatedNodes("menutemplate", "related", "DESTINATION");
      if (ptList.size() > 0) {
         return ptList.getNode(0);
      }
      return null;
   }

   /**
     * Return the object template for the given pagina at the given position. Or
     * null if no object template is available
     *
     * @param paginaNumber
     * @param position
     * @return
     */
   public Node getObjectTemplate(String paginaNumber, String position) {
      Node paginaNode = cloud.getNode(paginaNumber);
      NodeList contentrels =
         cloud.getList(
            paginaNumber,
            "pagina,contentrel,contentelement",
            "contentrel.number",
            "contentrel.pos='" + position + "'",
            null,
            null,
            "destination",
            true);
      if (contentrels.size() > 0) {
         Node contentrelNode =
            cloud.getNode(
               contentrels.getNode(0).getStringValue("contentrel.number"));
         NodeList objTemplates =
            contentrelNode.getRelatedNodes(
               "objecttemplate",
               "related",
               "destination");
         if (objTemplates.size() > 0) {
            return objTemplates.getNode(0);
         }
      }
      return null;
   }

   /**
     * Returns the number of the object template of the given contentrel. Or -1
     * if no template is defined.
     *
     * @param contentrelNumber
     * @return
     */
   public String getObjectTemplateNumber(String contentrelNumber) {
      Node contentrelNode = cloud.getNode(contentrelNumber);
      RelationManager contentrelRelMan = cloud.getRelationManager("related");
      RelationList rels = contentrelRelMan.getRelations(contentrelNode);
      RelationIterator relsIter = rels.relationIterator();
      Relation tr;
      if (relsIter.hasNext()) {
         // max of 1
         tr = relsIter.nextRelation();
         return tr.getDestination().getStringValue("number");
      }
      return null;
   }

   /**
     * Returns the number of the hoofdpagina of the given rubriek, or null if
     * the rubriek does not have a hoofdpagina.
     *
     * @param rubriekNumber
     * @return
     */
   public String getHoofdpagina(String rubriekNumber) {
     /* assert rubriekNumber != null; */
      Node rubriekNode = cloud.getNode(rubriekNumber);
      NodeList list = cloud.getList(rubriekNumber,
            "rubriek,posrel,pagina",
            "pagina.number",
            null,
            "posrel.pos",
            "UP",
            "DESTINATION",
            true);
      if (list.size() > 0) {
         return list.getNode(0).getStringValue("pagina.number");
      }
      return null;
   }

   /**
     * Returns a sorted list of pagenodes in this rubriek
     *
     * @param rubriekNumber
     * @return
     */
   public List getPaginaList(String rubriekNumber) {
      ArrayList paginaNodeList = null;
      NodeList nodeList = cloud.getList(rubriekNumber, "rubriek,posrel,pagina", "pagina.number", "",
            "posrel.pos", null, "DESTINATION", true);
      if ((nodeList != null) && (nodeList.size() > 0)) {
         paginaNodeList = new ArrayList(nodeList.size());
         for (int i = 0; i < nodeList.size(); i++) {
            Node tempPaginaNode = nodeList.getNode(i);
            String tempPaginaNodeNumber = tempPaginaNode.getStringValue("pagina.number");
            paginaNodeList.add(cloud.getNode(tempPaginaNodeNumber));
         }
      }
      return paginaNodeList;
   }

   ///////// pagina contentelement methods //////////

   /**
     * Determines if the given pagina has a contentrel relation at the given
     * position.
     *
     * @param paginaNumber
     * @param position
     * @return
     */
   public boolean hasContentrel(String paginaNumber, String position) {
      Node paginaNode = cloud.getNode(paginaNumber);
      NodeList contentrels =
         cloud.getList(
            paginaNumber,
            "pagina,contentrel,contentelement",
            "contentrel.number",
            "contentrel.pos='" + position + "'",
            null,
            null,
            "destination",
            true);
      return contentrels.size() > 0;
   }

   public String getContentrel(String paginaNumber, String position) {
      Node paginaNode = cloud.getNode(paginaNumber);
      NodeList contentrels =
         cloud.getList(
            paginaNumber,
            "pagina,contentrel,contentelement",
            "contentrel.number",
            "contentrel.pos='" + position + "'",
            null,
            null,
            "destination",
            true);
      return contentrels.getNode(0).getStringValue("contentrel.number");
   }

   /**
     * Returns the number of the content element for the given pagina at the
     * given position. Or null if there is no content element at the given
     * params.
     *
     * @param paginaNumber
     * @param position
     * @return
     */
   public String getContentElementNumber(
      String paginaNumber,
      String position) {
      Node paginaNode = cloud.getNode(paginaNumber);
      NodeList contentrels =
         cloud.getList(
            paginaNumber,
            "pagina,contentrel,contentelement",
            "contentelement.number",
            "contentrel.pos='" + position + "'",
            null,
            null,
            "destination",
            true);
      if (contentrels.size() == 1) {
         String nr =
            contentrels.getNode(0).getStringValue("contentelement.number");
         //System.out.println("numbertje: " + nr);
         return nr;
      }
      return null;
   }

   /**
     * Checks if the given page number has a content element at the given
     * content position.
     *
     * @param pageNumber
     * @param contentPosition
     * @return
     */
   public boolean hasContentElement(int pageNumber, int contentPosition) {
      Node pageNode = cloud.getNode(pageNumber);
      RelationManager contentrelRelMan = cloud.getRelationManager("contentrel");
      RelationList rels = contentrelRelMan.getRelations(pageNode);
      RelationIterator relsIter = rels.relationIterator();
      Relation tr;
      while (relsIter.hasNext()) {
         tr = relsIter.nextRelation();
         if (contentPosition == tr.getIntValue("pos")) {
            return true;
         }
      }
      return false;
   }

   ///////// dossier methods ///////////
   /**
     * Returns the link lit node belonging to the given page at the given link
     * list position. Returns null if no link list is specified.
     *
     * @param pageNumber
     * @return
     */
   public Node getDossier(String pageNumber) {
      NodeList dossierList = cloud.getList(pageNumber,
            "pagina,related,dossier",
            "dossier.number",
            null, null, null, "destination", true);
      if ((dossierList != null) && (dossierList.size() > 0)){
         Node tempNode = dossierList.getNode(0);
         String dossierNodeNumber = tempNode.getStringValue("dossier.number");
         return cloud.getNode(dossierNodeNumber);
      }
      else {
         return null;
      }
   }

   /**
     * Create a dossier for the given page, if there is
     * none yet defined! Returns the dossier
     *
     * @param pageNumber
     */
   public Node createDossier(String pageNumber) {
      log.debug("creating new dossier");
      Node pageNode = cloud.getNode(pageNumber);
      NodeManager newDossierNodeManager = cloud.getNodeManager("dossier");
      Node newDossierNode = newDossierNodeManager.createNode();
      newDossierNode.commit();
      RelationManager relatedRelMan = cloud.getRelationManager("related");
      Relation relatedRel = relatedRelMan.createRelation(pageNode, newDossierNode);
      relatedRel.commit();
      return newDossierNode;
   }

   /**
     * Returns true if the given pagina has a dossier
     *
     * @param paginaNumber
     * @return
     */
   public boolean hasDossier(String pageNumber) {
      NodeList dossierList = cloud.getList(pageNumber,
            "pagina,related,dossier",
            "dossier.number",
            null, null, null, "destination", true);
      return dossierList.size() == 1;
   }

   ///////// link lijst methods ///////////
   /**
     * Returns the link lit node belonging to the given page at the given link
     * list position. Returns null if no link list is specified.
     *
     * @param pageNumber
     * @param linklistPosition
     * @return
     */
   public Node getLinklijst(String pageNumber, int linklistPosition) {
      log.debug(
         "Getting link lijst for pagina number: "
            + pageNumber
            + " at position "
            + linklistPosition);
      Node pageNode = cloud.getNode(pageNumber);
      NodeList linkLijstList =
         cloud.getList(
            pageNumber,
            "pagina,posrel,linklijst",
            "linklijst.number",
            "posrel.pos=" + linklistPosition,
            null,
            null,
            "destination",
            true);
      if (linkLijstList.size() > 0) {
         String number =
            linkLijstList.getNode(0).getStringValue("linklijst.number");
         return cloud.getNode(number);
      }
      return null;
   }

   /**
     * Create a linklijst for the given page at the given position, if there is
     * none yet defined! Returns the link lijst
     *
     * @param pageNumber
     * @param linklijstPosition
     */
   public Node createLinklijst(String pageNumber, int linklijstPosition) {
      if (!hasLinklijst(pageNumber, linklijstPosition)) {
         log.debug("creating new link lijst");
         Node pageNode = cloud.getNode(pageNumber);
         NodeManager newLinklijstNodeManager =
            cloud.getNodeManager("linklijst");
         Node newLinklijstNode = newLinklijstNodeManager.createNode();
         newLinklijstNode.commit();
         RelationManager posrelRelMan = cloud.getRelationManager("posrel");
         Relation newPosrel =
            posrelRelMan.createRelation(pageNode, newLinklijstNode);
         newPosrel.setIntValue("pos", linklijstPosition);
         newPosrel.commit();
         return newLinklijstNode;
      } else {
         log.debug("NOT creating new link lijst. Returning existing!");
         return getLinklijst(pageNumber, linklijstPosition);
      }
   }

   /**
     * Returns true if the given pagina has a linklijst at the given position.
     *
     * @param paginaNumber
     * @param linklijstPosition
     * @return
     */
   public boolean hasLinklijst(String paginaNumber, int linklijstPosition) {
      NodeList linkLijstList =
         cloud.getList(
            paginaNumber,
            "pagina,posrel,linklijst",
            "linklijst.number",
            "posrel.pos=" + linklijstPosition,
            null,
            null,
            "destination",
            true);
      return linkLijstList.size() == 1;
   }

   //////////// helpers ////////////////
   
   /**
    * 
    *
    * @param paginaNode
    * @return
    */
   public String getStartTemplate(String paginaNodeNumber, String rubriekNodeNumber) {
      // for now always return index.jsp;
      // if there wil be more structure jsps, this should be changed
      return "index.jsp";
   }


   /**
    *
    * @param paginaNumber
    * @param contentElementPagePosition
    * @return
    */
   public String createContentRelatedImageUrl(String paginaNumber, String contentElementPagePosition, String template, HttpServletRequest request, HttpServletResponse response) {
      String url = null;
      if (hasContentrel(paginaNumber, contentElementPagePosition)) {
         String contentElementNumber = getContentElementNumber(paginaNumber, contentElementPagePosition);
         Node contentElementNode = cloud.getNode(contentElementNumber);
         NodeList relatedImagesList = contentElementNode.getRelatedNodes("images");
         if (relatedImagesList != null && relatedImagesList.size() > 0) {
            Node imageNode = relatedImagesList.getNode(0);
            url = createImageUrl(template, imageNode, request, response);
         }
      }
      return url;
   }

   /**
    *
    * @param paginaNumber
    * @param contentElementPagePosition
    * @return
    */
   public String createContentImageUrl(String paginaNumber, String contentElementPagePosition, String template, HttpServletRequest request, HttpServletResponse response) {
      String url = null;
      if (hasContentrel(paginaNumber, contentElementPagePosition)) {
         String contentElementNumber = getContentElementNumber(paginaNumber, contentElementPagePosition);
         Node contentElementNode = cloud.getNode(contentElementNumber);
         if (ContentTypeHelper.isContentOfType(contentElementNode, "images")) {
            url = createImageUrl(template, contentElementNode, request, response);
         }
      }
      return url;
   }

   /**
    * Creates a url to an image.
    * @param template
    * @param imageNode
    * @param request
    * @param response
    * @return an url
    */
   public String createImageUrl(String template, Node imageNode, HttpServletRequest request, HttpServletResponse response) {
      String url;
      String number;
      if (template == null || "".equals(template)) {
          // the node/image itself
          number = imageNode.getStringValue("number");
      } else {
          // the cached image
          List args = new ArrayList();
          args.add(template);
          number = imageNode.getFunctionValue("cache", args).toString();
      }

      String context = request.getContextPath();

      List args = new ArrayList();
      args.add("");
      args.add("");
      args.add(context);
      String servletPath = imageNode.getFunctionValue("servletpath", args).toString();

      String fileName = imageNode.getStringValue("filename");
      if (servletPath.endsWith("?") ||  "".equals(fileName)) {
          url = servletPath + number;
      } else {
          url = servletPath + fileName + "?" + number;
      }
      url =  response.encodeURL(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + url);
      return url;
   }

   /**
       * Creates a url for the given content element in the given rubriek number.
       * If the cms url data is malformed, an exception is thrown
       *
       * @param contentElement
       * @param rubriekNumber
       * @param referPagina pagina from which the url is created
       * @param contextPath the context path of the server
       * @exception MalformedURLException
       *               the created url is malformed
       * @return
       */
      public String createUrlForContentElement(Node contentElement, String contextPath)
         throws MalformedURLException {
        /* assert contentElement != null; */
         Node paginaNode = getPaginaNode(contentElement);
         NodeList remotePaginas = PublishManager.getPublishedNodes(paginaNode);
         if (remotePaginas.size() > 0) {
            Node remotePaginaNode = remotePaginas.getNode(0);
            if (remotePaginaNode != null) {
               NodeList rubrieken = remotePaginaNode.getRelatedNodes("rubriek", "posrel", "SOURCE");
               if (rubrieken.size() > 0) {
                  Node rubriekNode = rubrieken.getNode(0);
                  
                  RubriekHelper rhelper = new RubriekHelper(cloud);
                  String rubriekUrl = rhelper.getLiveUrlPathToRootString(rubriekNode, contextPath);
                  StringBuffer ret = new StringBuffer(rubriekUrl);
                  NodeList remoteContentList = PublishManager.getPublishedNodes(contentElement);
                  if (remoteContentList.size() > 0) {
                     Node contentPage = getContentPagina(contentElement);
                     ret.append('/');
                     ret.append(contentPage.getStringValue("urlfragment"));
                     ret.append(UrlConverter.PAGE_EXTENSION);
                     URL url = new URL(ret.toString());
                     ret.append('?');
                     ret.append("objectnumber=");
                     ret.append(remoteContentList.getNode(0).getStringValue("number"));
                     ret.append("&referpagina="+remotePaginaNode.getNumber());
                     return ret.toString();
                  }
               }
            }
         }
         return null;
      }

    /**
    * Checks if the given page object contains content elements or nonempty link lijsten.
    *
    * @param page
    * @return
    */
   private Node getPaginaNode(Node contentElement) {
        NodeList paginas = contentElement.getRelatedNodes("pagina", "contentrel", "SOURCE");
        if (paginas.size() > 0) {
            return paginas.getNode(0);
        }
        NodeList natuurgebieden = cloud.getList(""+contentElement.getNumber(),"natuurgebieden,posrel,provincies,contentrel,pagina", "pagina.number", null, null, null, "SOURCE", true);
        if (natuurgebieden.size() > 0) {
            Cloud cloud = contentElement.getCloud();
            return cloud.getNode(natuurgebieden.getNode(0).getStringValue("pagina.number"));
        }
        NodeList dossiers = cloud.getList(""+contentElement.getNumber(),"artikel,posrel,dossier,posrel,pagina", "pagina.number", null, null, null, "SOURCE", true);
        if (dossiers.size() > 0) {
           Cloud cloud = contentElement.getCloud();
           return cloud.getNode(dossiers.getNode(0).getStringValue("pagina.number"));
        }
        log.warn("Did not find a related pagina for contentelement " + contentElement.getStringValue("titel") + " (" + contentElement.getNumber() + ")");
        return null;
    }

   /**
     * Method finds an contentelement with an item name and a page Node.
     *
     * @param page Node
     * @param itemName
     * @return contentelement Node
     */
   public Node getContentElementNode(Node page, String itemName) {
      
      Node winner = null;
      NodeIterator contentelementen = page.getRelatedNodes("contentelement", "contentrel", "DESTINATION").nodeIterator();
      while (winner==null && contentelementen.hasNext()) {
         Node contentElement = (Node) contentelementen.next();
         if (HtmlCleaner.stripText(contentElement.getStringValue("titel")).equals(itemName)) {
            winner = contentElement;
         }
      }
      if (winner==null) {
         winner = getContentElementNode(page, itemName, "pagina,contentrel,provincies,posrel,natuurgebieden", "natuurgebieden");
      }
      if (winner==null) {
         winner = getContentElementNode(page, itemName, "pagina,posrel,dossier,posrel,artikel", "artikel");
      }
      if (winner==null) {
         log.warn("Did not find a related contentelement with title " + itemName + " for page " + page.getStringValue("titel") + " (" + page.getNumber() + ")");
      }
      return winner;
   }
   
   public Node getContentElementNode(Node page, String itemName, String path, String type) {
      Node winner = null;
      NodeIterator contentelementen = cloud.getList(""+ page.getNumber(),path, type+".number,"+type+".titel", null, null, null, "DESTINATION", true).nodeIterator();
      while (winner==null && contentelementen.hasNext()) {
         Node contentElement = (Node) contentelementen.next();
         if (HtmlCleaner.stripText(contentElement.getStringValue(type + ".titel")).equals(itemName)) {
            winner = cloud.getNode(contentElement.getStringValue(type+".number"));
         }
      }
      return winner;
   }

}
