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

import nl.leocms.servlets.UrlConverter;
import nl.leocms.servlets.UrlCache;
import nl.leocms.applications.*;
import nl.leocms.util.tools.HtmlCleaner;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PaginaHelper {

   /** Logger instance. */
   private static Logger log = Logging.getLoggerInstance(PaginaHelper.class.getName());

   public final static int MAX_NUMBER_LINKLIJST_ELEMENTS = 7;
   public final static int MAX_NUMBER_DOSSIER_ELEMENTS = 7;
   final static int NAARDERMEER_LAYOUT_INDEX = 1; // index to Naardermeer layout entry, as defined in NatMMConfig.java
   
   Cloud cloud;
	 ApplicationHelper ap;
   public HashMap pathsFromPageToElements;
   public boolean urlConversion;

   public PaginaHelper(Cloud cloud) {
     this.cloud = cloud;
     this.ap = new ApplicationHelper(cloud);
     this.pathsFromPageToElements = ap.pathsFromPageToElements();
     this.urlConversion = NatMMConfig.urlConversion;
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

   public static Vector getBreadCrumbs(Cloud cloud, String objectNumber) {
      if (objectNumber==null || objectNumber.equals("-1")){
         log.error("No such pagina/rubriek with number: " +objectNumber + ". Returning null.");
         return null;
      }
      Node node = cloud.getNode(objectNumber);
      if (node==null) {
         log.error("No such pagina/rubriek with number: " + objectNumber + ". Returning null.");
         return null;
      }
      Vector breadcrumbs = new Vector();
      NodeList rubriekL = null;
      if(node.getNodeManager().getName().equals("pagina")) {
        rubriekL = node.getRelatedNodes("rubriek", "posrel", "SOURCE");
      } else {
        rubriekL = node.getRelatedNodes("rubriek", "parent", "SOURCE");
      }
      while (rubriekL.size() == 1 && !breadcrumbs.contains(rubriekL.getNode(0).getStringValue("number"))) {
         breadcrumbs.add(rubriekL.getNode(0).getStringValue("number"));
         rubriekL = rubriekL.getNode(0).getRelatedNodes("rubriek", "parent", "SOURCE");
      }
      return breadcrumbs;
   }

	public static String getOwners(Cloud cloud, String paginaNumber, Vector breadcrumbs) {
		ContentHelper ch = new ContentHelper(cloud);
		String owners = ch.getOwners(paginaNumber);
		for(int r=0; r<breadcrumbs.size() && owners.equals(""); r++) {
			owners = ch.getOwners((String) breadcrumbs.get(r));
		}
		return owners;
	}

	public static String getOwners(Cloud cloud, String paginaNumber) {
		Vector bc = getBreadCrumbs(cloud,paginaNumber);
		return getOwners(cloud,paginaNumber,bc);
	}

   public static String getSubsiteRubriek(Cloud cloud, String paginaNumber) {
      Vector breadcrumbs = getBreadCrumbs(cloud, paginaNumber);
      log.debug(paginaNumber + "->" + breadcrumbs);
		String subsiteRubriek = null;
      if (breadcrumbs!=null) {
         if (breadcrumbs.size() >= 2) {
            subsiteRubriek = (String) breadcrumbs.get(breadcrumbs.size() - 2);
         } else {
            log.error("Pagina " + paginaNumber +
               " does not belong to a subsite. Setting subsite rubriek to 'root'");
            subsiteRubriek = "root";
         }
      }
      return subsiteRubriek;
   }

   public static String getHoofdRubriek(Cloud cloud, String paginaNumber) {
      Vector breadcrumbs = getBreadCrumbs(cloud, paginaNumber);
      log.debug(paginaNumber + "->" + breadcrumbs);
		String hoofdRubriek = null;
      if (breadcrumbs!=null) {
         if (breadcrumbs.size() >= 3) {
            hoofdRubriek = (String) breadcrumbs.get(breadcrumbs.size() - 3);
         } else  if (breadcrumbs.size() >= 2) {
            hoofdRubriek = (String) breadcrumbs.get(breadcrumbs.size() - 2);
         } else {
            log.error("Pagina " + paginaNumber +
               " does not have a hoofd rubriek. Setting hoofd rubriek to 'root'");
            hoofdRubriek = "root";
         }
      }
      return hoofdRubriek;
   }

   /**
    * Returns the subDir where the templates of this page can be found
    * @param cloud
    * @param paginaNumber
    * @return subDir
    */
   public static String getSubDir(Cloud cloud, String paginaNumber) {
      String subsiteRubriek = getSubsiteRubriek(cloud,paginaNumber);
      return (subsiteRubriek==null) ? null : RubriekHelper.getSubDir(cloud.getNode(subsiteRubriek));
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
   public Node retrievePagina(String rubriekNumber, String pageName) {
      Node rubriek = cloud.getNode(rubriekNumber);
      Iterator bl = rubriek.getRelatedNodes("pagina","posrel",null).iterator();

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
     * Creates the slash seperated string for an object
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String getUrlPathToRootString(Node itemNode, Node paginaNode, String contextPath) {
      StringBuffer url = new StringBuffer();
      url.append(getUrlPathToRootString(paginaNode,contextPath));
      url.append('/');

      if(NatMMConfig.useCreationDateInURL) {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(itemNode.getLongValue("datumlaatstewijziging")*1000); // add lastmodifieddate

         if (cal.get(Calendar.YEAR) % 100 < 10) { url.append('0'); }
         url.append(cal.get(Calendar.YEAR) % 100);
         if (cal.get(Calendar.MONTH) + 1 < 10) { url.append('0'); }
         url.append(cal.get(Calendar.MONTH) + 1);
         if (cal.get(Calendar.DAY_OF_MONTH) < 10) { url.append('0'); }
         url.append(cal.get(Calendar.DAY_OF_MONTH));
      } else {
         url.append(UrlConverter.ITEM_DELIMITER);
      }
        
      String itemTitle = itemNode.getStringValue((new ContentHelper(cloud)).getTitleField(itemNode));
      url.append(HtmlCleaner.forURL(HtmlCleaner.stripText(itemTitle)));

      return url.toString();
   }

   /**
     * Creates the slash seperated string for a pagina
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String getUrlPathToRootString(Node paginaNode, String contextPath) {
      StringBuffer url = new StringBuffer();
      Node rubriek = getRubriek(paginaNode);
		if(rubriek==null) {
			log.error("pagina "  + paginaNode.getStringValue("titel") + " (" + paginaNode.getStringValue("number") + ") is not related to a rubriek.");
		} else {
			RubriekHelper rHelper = new RubriekHelper(cloud);
			url.append(rHelper.getUrlPathToRootString(rubriek,contextPath));
			url.append('/');
		}
      url.append(HtmlCleaner.stripText(paginaNode.getStringValue("titel")));
      log.debug("getPaginaUrlPathToRootString" + url.toString());
      return url.toString();
   }

   /**
     * Creates the url for a pagina.
     * The url consists of
     * 1. contextPath
     * 2. list of rubriek.name
     * 3. pagina.titel
     *  !! Only works for pagina objects that are related to a rubriek!!
     * @param paginaNumber
     * @return
     */
   public String createPaginaUrl(String paginaNumber, String contextPath) {

      StringBuffer url = getPaginaTemplate(paginaNumber, contextPath);
      url.append(UrlConverter.PAGE_PARAM);
      url.append('=');
      url.append(paginaNumber);

      if(urlConversion) {

         UrlCache cache = UrlConverter.getCache();
         String jspURL = url.toString();
         String userURL = cache.getURLEntry(jspURL);
         if (userURL!=null) {
            log.debug("processed from url cache: " + userURL);
            url = new StringBuffer(userURL);
         } else if (paginaNumber!=null && !paginaNumber.equals("-1")){
            url = new StringBuffer();
            url.append(getUrlPathToRootString(cloud.getNode(paginaNumber), contextPath));
            url.append(UrlConverter.PAGE_EXTENSION);
            cache.putURLEntry(jspURL, url.toString());
         }
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
   public String createItemUrl(String itemNumber, String pageNumber, String params, String contextPath) {
      Node itemNode = cloud.getNode(itemNumber);
      Node paginaNode = null;
      if(pageNumber!=null && !pageNumber.equals("-1")) {
         paginaNode = cloud.getNode(pageNumber);
      } else {
         paginaNode = getPaginaNode(itemNode);
         pageNumber = paginaNode.getStringValue("number");
      }
      StringBuffer url = getPaginaTemplate(pageNumber, contextPath);

      url.append(UrlConverter.ITEM_PARAM);
      url.append('=');
      url.append(itemNumber);

      if(urlConversion) {

         UrlCache cache = UrlConverter.getCache();
         String jspURL = url.toString();
         String userURL = cache.getURLEntry(jspURL);
         if (userURL!=null) {
            log.debug("processed from url cache: " + userURL);
            url = new StringBuffer(userURL);
         } else {
            url = new StringBuffer();
            url.append(getUrlPathToRootString(itemNode, paginaNode, contextPath));
            url.append(UrlConverter.PAGE_EXTENSION);
            cache.putURLEntry(jspURL, url.toString());
         }
      }

      if (params != null && !params.equals("")) {
         if(urlConversion) {
            url.append('?');
         } else {
            url.append('&');
         }
         url.append(params);
      }
      log.debug("createItemUrl " + url.toString());
      return url.toString();
   }

	public int getMaxSize() {
		// returns the maxsize of uploads (= images / attachments) per application
	   int maxsize = 2*1024*1024;
		if(ap.isInstalled("VanHam")) {
			maxsize = 3*1024*1024;
		}
		return maxsize;
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
            String ewNodePath = editwizardNode.getStringValue("nodepath");
            if(editwizardNode!=null) {
               String ewType = editwizardNode.getStringValue("type");
               if(ewType.equals("list")) {
                  boolean showEw = true;
                  // Commented out for NMCMS-121
                  /*if(ewNodePath.indexOf("pagina")>-1 && ap.isInstalled("NatMM")) {   // ** check whether the path is used at least once
                    showEw = false;
                    NodeList nl = null;
                    try {
                        nl = cloud.getList(pageNumber, ewNodePath, "pagina.number", null, null, null, null, false);
                    } catch (Exception thisException) {
                       log.error("The editwizard " + ewTitle + " could not be shown, because the path " + ewNodePath + " does not exist in the objectcloud.");
                    }
                    showEw = (nl!=null && nl.size()>0);
                  }*/
                  if(showEw) {
                     ewUrl += "/mmbase/edit/wizard/jsp/list.jsp?wizard=" + editwizardNode.getStringValue("wizard");
                     String startnodes = editwizardNode.getStringValue("startnodes");
                     if(startnodes!=null&&!startnodes.equals("")) {
                        ewUrl += "&startnodes=" + startnodes;
                     } else {
                        ewUrl += "&startnodes=" + pageNumber;
                     }
                     ewUrl += "&nodepath=" + ewNodePath
                         + "&fields=" + editwizardNode.getStringValue("fields")
                       //  + "&constraints=" + editwizardNode.getStringValue("constraints") *** empty constraint will result in don't panic ***
                       //  + "&age=" + editwizardNode.getStringValue("age")
                       //  + "&searchdir=" + editwizardNode.getStringValue("searchdir")
                         + "&orderby=" + editwizardNode.getStringValue("orderby")
                         + "&directions=" + editwizardNode.getStringValue("directions")
                         + "&pagelength=" + editwizardNode.getStringValue("pagelength")
                         + "&maxpagecount=" + editwizardNode.getStringValue("maxpagecount")
                         + "&maxsize=" + getMaxSize()
                         + "&searchfields=" + editwizardNode.getStringValue("searchfields")
                       //  + "&searchtype=" + editwizardNode.getStringValue("searchtype")
                       //  + "&searchvalue=" + editwizardNode.getStringValue("searchvalue")
                         + "&search=" + editwizardNode.getStringValue("search")
                         + "&origin=" + pageNumber
                         + "&referrer=/editors/empty.html";
                     if(editwizardNode.getStringValue("m_distinct").equals("1")) { ewUrl += "&distinct=true"; }
                     Node rubriek = getRubriek(pageNumber);
                     if(rubriek!=null) {
                         ewUrl += "&creatierubriek=" + rubriek.getNumber();
                     }
                  }
               } else if(ewType.equals("jsp")) {
                  ewUrl = editwizardNode.getStringValue("wizard")
                     + (ewUrl.indexOf("?")==-1 ? "?" : "&" ) + "p=" + pageNumber;
               } else {
                  ewUrl += "/mmbase/edit/wizard/jsp/wizard.jsp?language=nl&wizard=" + editwizardNode.getStringValue("wizard")
                     + "&nodepath=pagina&referrer=/editors/empty.html&objectnumber=" + pageNumber
                     + "&maxsize=" + getMaxSize();
               }
            }
            if(!ewUrl.equals(contextPath)) {
               // ewUrls for a page will be unique
               ewUrls.put(ewUrl,ewTitle);
            }
         }
      }
      return ewUrls;
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
      if (pageNumber!=null && !pageNumber.equals("-1")){
         Node pageNode = cloud.getNode(pageNumber);
         NodeList ptList =
            pageNode.getRelatedNodes("paginatemplate", "gebruikt",
                                     "DESTINATION");
         if (ptList.size() == 1) {
            return ptList.getNode(0);
         }
      }
      return null;
   }

   public StringBuffer getPaginaTemplate(String paginaNumber, String contextPath) {

      StringBuffer url = new StringBuffer();
      url.append(contextPath); // always start from the root
      url.append("/");
      url.append(getSubDir(cloud, paginaNumber));
      Node paginaTemplate = getPaginaTemplate(paginaNumber);
      if(paginaTemplate!=null) {
         url.append(paginaTemplate.getStringValue("url"));
      }
      url.append('?');
      return url;
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
         Node paginaNode = getPaginaNode(contentElement);
         NodeList remotePaginas = PublishManager.getPublishedNodes(paginaNode);
         if (remotePaginas.size() > 0) {
            Node remotePaginaNode = remotePaginas.getNode(0);
            if (remotePaginaNode != null) {
               NodeList rubrieken = remotePaginaNode.getRelatedNodes("rubriek", "posrel", "SOURCE");
               if (rubrieken.size() > 0) {
                  Node rubriekNode = rubrieken.getNode(0);

                  RubriekHelper rhelper = new RubriekHelper(cloud);
                  StringBuffer ret = rhelper.getUrlPathToRootString(rubriekNode,contextPath);
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
    * Returns the pagina to which this contentelement is related.
    *
    * @param contentElement
    * @return page
    */
   private Node getPaginaNode(Node contentElement) {

      Node page = null;
      for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext() && page==null;) {
         String objecttype = (String) it.next();
         String currentPath = (String) pathsFromPageToElements.get(objecttype);
         NodeList nlPages = cloud.getList(""+contentElement.getNumber(),
                                          currentPath,
                                          "pagina.number",
                                          null,null, null, null, true);

         if (nlPages.size() > 0) {
            page = cloud.getNode(nlPages.getNode(0).getStringValue("pagina.number"));
         }
      }
      return page;
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
      for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext() && winner==null;) {

         String objecttype = (String) it.next();
         String currentPath = (String) pathsFromPageToElements.get(objecttype);
         objecttype = objecttype.replaceAll("#","");
         String titleField = (new ContentHelper(cloud)).getTitleField(objecttype);
         currentPath = currentPath.replaceAll("object",objecttype);

         NodeIterator objects = cloud.getList(
            null,
            currentPath,
            objecttype + ".number," + objecttype + "." + titleField,
            "pagina.number='" + page.getNumber() + "'",
            null, null, "SOURCE", true).nodeIterator();
         while (winner==null && objects.hasNext()) {
            Node object = (Node) objects.next();
            if (HtmlCleaner.stripText(object.getStringValue(objecttype + "." + titleField)).equals(itemName)) {
               winner = cloud.getNode(object.getStringValue(objecttype + ".number"));
            }
         }
      }
      if (winner==null) {
         log.warn("Did not find a related contentelement with title " + itemName + " for page " + page.getStringValue("titel") + " (" + page.getNumber() + ")");
      }
      return winner;
   }

    public HashMap findIDs(HashMap ids, String template, String defaultPage) {

      // ID can be used as a generic reference to nodes of different nodetypes
      String ID =  (String) ids.get("object");
      String rubriekID = (String) ids.get("rubriek");
      String paginaID = (String) ids.get("pagina");

      if(!ID.equals("-1")) { // if ID is set, use it to set the ID of the related nodetype

         String nType = cloud.getNode(ID).getNodeManager().getName();
         if(nType.equals("rubriek")) {
            ids.put("rubriek", ID);
         } else if(nType.equals("pagina")) {
            ids.put("pagina", ID);
         } else {
            for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext();) {
               String objecttype = (String) it.next();
               if(nType.equals(objecttype)) {
                  ids.put(objecttype, ID);
               }
            }
         }
      } else { // if ID is not set, then set it

         if(!rubriekID.equals("-1")) {
            ID = rubriekID;
         } else if(!paginaID.equals("-1")) {
            ID = paginaID;
         } else {
				String objecttype = null;
            for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext() && ID.equals("-1");) {
               objecttype = (String) it.next();
					ID = (String) ids.get(objecttype.replaceAll("#",""));
					if(ID==null) {
						log.debug("Objecttype " + objecttype + " is not found in the list of IDs supplied to findIDs");
						ID = "-1";
					}
            }
				log.debug("ID is set to " + objecttype + " " + ID);
         }
      }

      if(ID.equals("-1")) {
			// if the ID is still empty, it means that not one ID is provided in the URL
			// set paginaID, rubriekID and ID from the template
         NodeList nlPages = cloud.getList("",
                                          "template,gebruikt,pagina,posrel,rubriek",
                                          "rubriek.number,pagina.number",
                                          "template.url='" + template + "'",
                                          null, null, null, false);

          if (nlPages.size() > 0) {
             paginaID = nlPages.getNode(0).getStringValue("pagina.number");
             rubriekID = nlPages.getNode(0).getStringValue("rubriek.number");;
             ID = rubriekID;
          }

      }

		if(paginaID.equals("-1")) {
			// update the rubriekID
			ids.put("rubriek", rubriekID);
			paginaID = findPageByIDs(ids,template,defaultPage);
		}

		// set the rubriekID on basis of the paginaID
		if(!paginaID.equals("-1")&&rubriekID.equals("-1")) {
		   // pages might be called with a url with anchors attached after #
          if (paginaID.indexOf("#") != -1) {
             paginaID = paginaID.substring(0, paginaID.indexOf("#"));
          }
          
			 NodeList nlRubriek = cloud.getList(paginaID,
														"pagina,posrel,rubriek",
														"rubriek.number",
														null,null, null, null, false);
			 if (nlRubriek.size() > 0) {
				 rubriekID = nlRubriek.getNode(0).getStringValue("rubriek.number");
			 }
		}

      if(!paginaID.equals("-1")) {
         // make sure paginaID is not an alias, otherwise constraints on paginaID won't work
         paginaID = cloud.getNode(paginaID).getStringValue("number");
      }
      ids.put("object", ID);
      ids.put("rubriek", rubriekID);
      ids.put("pagina", paginaID);
      
      // Checking if page belongs to the Neerdermeer rubriek layout
      String isNaardermeer = "false";
      String subRubriek = getSubsiteRubriek(cloud, paginaID); //node number of our sub rubriek
      // Naardermeer huisstijl is applied based on layout that can be selected in editors for top rubrieks
      // this is for some reason stored in variable naam_fra, as an index to an array defined in NatMMConfig.java
      int siteLayout = 0;
      Node subRubriekNode = cloud.getNode(subRubriek);
      if(subRubriekNode!=null) {
         siteLayout = Integer.parseInt(subRubriekNode.getStringValue("naam_fra"));
      }
      if (siteLayout == NAARDERMEER_LAYOUT_INDEX) {
         isNaardermeer = "true";
      }
      ids.put("isNaardermeer", isNaardermeer);

      return ids;
   }


   /**
    * Find a page number based on nodes of different types
    */
   public String findPageByIDs(HashMap ids, String template, String defaultPage) {
      String rubriekID =  (String) ids.get("rubriek");
      String paginaID =  (String) ids.get("pagina");
      log.debug("findPagesByIDs: rubriek = " + rubriekID + " pagina= " + paginaID + " template= " + template);

      // use rubriekID to set paginaID
      if (paginaID.equals("-1") && !rubriekID.equals("-1")) {

         RubriekHelper h = new RubriekHelper(cloud);
         paginaID = h.getFirstPage(rubriekID);
      }
		// use template and ID to set paginaID
      for (Iterator it=pathsFromPageToElements.keySet().iterator();it.hasNext() && paginaID.equals("-1");) {
         String objecttype = (String) it.next();
         String ID = (String) ids.get(objecttype.replaceAll("#",""));
			if(ID==null) {
				log.warn("Objecttype " + objecttype + " is not found in the list of IDs supplied to findPageByIDs");
				ID = "-1";
			}
         if (!ID.equals("-1")) {
				String currentPath = (String) pathsFromPageToElements.get(objecttype);
				NodeList nlPages = cloud.getList(ID,
													currentPath,
													"pagina.number",
													null, null, null, null, false);
				for(int p = 0; p < nlPages.size() && paginaID.equals("-1"); p++) {
					Node nPage = cloud.getNode(nlPages.getNode(p).getStringValue("pagina.number"));
					NodeList nlTemplates = nPage.getRelatedNodes("template","gebruikt",null);
					if (nlTemplates.size() == 1 && nlTemplates.getNode(0).getStringValue("url").equals(template)) {
						paginaID = nlPages.getNode(p).getStringValue("pagina.number");
						log.debug("found pagina " + paginaID + " on basis of object " + ID + ", path " + currentPath + ", and template " + template);
					}
				}
         }
      }
		// if nothing else helps use defaultPage
		if (paginaID.equals("-1")) {
			paginaID = defaultPage;
		}
      return paginaID;
   }

   /**
    * Check whether this node is of the specified type
    */
   public boolean isOfType(String ID, String nType) {
      boolean isOfType = false;
      if(!ID.equals("-1")) {
          Node node = cloud.getNode(ID);
          if(node!=null) {
             isOfType = (node.getNodeManager().getName()).equals(nType);
          }
      }
      return isOfType;
   }

   public String getTemplate(HttpServletRequest request) {
      String template =  request.getRequestURI();
      if(template.indexOf("/") > -1){
         template = template.substring(template.lastIndexOf("/")+1,template.length());
      }
      if(template.indexOf(".jsp") > -1){
         template = template.substring(0,template.lastIndexOf(".jsp")+4);
      }
      return template;
   }

}
