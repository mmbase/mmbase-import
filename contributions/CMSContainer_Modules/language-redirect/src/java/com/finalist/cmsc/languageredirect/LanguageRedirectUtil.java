package com.finalist.cmsc.languageredirect;

import java.util.LinkedHashMap;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.SiteUtil;

public class LanguageRedirectUtil {

   private static Log log = LogFactory.getLog(LanguageRedirectUtil.class);
   
   public static String translate(String language, int id)  {
      
      // Get the cloud. 
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      
      // Is there a site with the given language? 
      Node foreignSite = SearchUtil.findNode(cloud, SiteUtil.SITE, "language", language);
      
      if (foreignSite == null) {
         log.error("No site was found with the language\"" + language + "\".");
         return null;
      }
      
      // Get the node with the id given in the id parameter.
      Node pageOrSiteNode = cloud.getNode(id);
      NodeManager nodeManager = pageOrSiteNode.getNodeManager();
      log.debug("The nodemanager of the current node's name is: " + nodeManager.getName());
      
      // Is the current Node a page or a site?    
      if (PagesUtil.isPage(pageOrSiteNode)) {
         log.debug("The current node is a page(id: " + pageOrSiteNode.getNumber() + ").");         
         // Look for a page that has the same liname as the currentpage. Do this
         // with a query on the database otherwise we would have to loop through
         // the pages of the found sites to find the page ourselves.
         Field liField = nodeManager.getField("liname");
         NodeList pageList = getCorrespondingPages(pageOrSiteNode, liField, nodeManager);
         while (pageList == null || pageList.size() == 0){           
            /*
             * If no page was found and the parent is not a site, get the parent
             * of the current node and find the corresponding page for that
             * site. If the parent is a site, go to the corresponding site
             * instead.
            */             
            NodeList parentList = pageOrSiteNode.getRelatedNodes(nodeManager, NavigationUtil.NAVREL, "SOURCE");         
            if (parentList.size() > 0) {
               // The parentList should only contain one item since a page can
               // always only have one source.
               pageOrSiteNode = parentList.getNode(0);
               if (SiteUtil.isSite(pageOrSiteNode)) {
                  // The parent is a site, redirect to the site that corresponds
                  // to the language given.
                  return foreignSite.getStringValue(SiteUtil.FRAGMENT_FIELD);
               }
            }
            else {
               // There is no parent. 
               log.error("Page with number: " + pageOrSiteNode.getFieldValue("number") + ", doesn't have a parent.");
               return null;
            }
            
            pageList = getCorrespondingPages(pageOrSiteNode, liField, nodeManager);             
            }
         
         /*
          * We now have a list of pages that have the same liname as the page
          * that was given as the id in the request or we have a list of pages
          * that have the same liname as one of the parents of the page that was
          * given as the id in the request. Find the site of this page and if it
          * corresponds with the site found previously, redirect to that page.
          */
         if (pageList != null && pageList.size() > 0) {
             LinkedHashMap<String, String> treeManagers = new LinkedHashMap<String, String>();
             treeManagers.put(SiteUtil.SITE, SiteUtil.FRAGMENT_FIELD);
             treeManagers.put(PagesUtil.PAGE, PagesUtil.FRAGMENT_FIELD);
             
             NodeIterator pageIt = pageList.nodeIterator();
            while (pageIt.hasNext()) {
               Node curPage = (Node)pageIt.next();
               String path = TreeUtil.getPathToRootStringWithoutCache(curPage, treeManagers, NavigationUtil.NAVREL);
               if (path != null) {
                   Node currentSite = NavigationUtil.getSiteFromPath(cloud, path);
                   if (currentSite != null 
                           && currentSite.getIntValue("number") == foreignSite.getIntValue("number") 
                           && PagesUtil.isPage(curPage)) {
                      return path;
                   }
               }
            }        
         }
         // Return with an error. There was no site that had the same language
         // as the given language parameter.
         log.debug("None of the found pages belong to the site with langauge " + language + ".");
         return foreignSite.getStringValue(SiteUtil.FRAGMENT_FIELD);
         
      }
      if (SiteUtil.isSite(pageOrSiteNode)) {
         // We're in the root, the current node is a site, so go directly to the
         // corresponding site in the given language.
         log.debug("The current node is a site(id: " + pageOrSiteNode.getNumber() + ").");
         return foreignSite.getStringValue(SiteUtil.FRAGMENT_FIELD);
      }                 
      
      return null;
   }
   

   private static NodeList getCorrespondingPages (Node pageNode, Field liField, NodeManager pageManager) {        
      NodeQuery pageQuery = pageManager.createQuery();
      FieldValueConstraint liConstraint = pageQuery.createConstraint((pageQuery.getStepField(liField)),
               FieldCompareConstraint.EQUAL, pageNode.getValue("liname"));
      
      // Do not include the current number of the pageNode.
      Field numberField = pageManager.getField("number");
      FieldValueConstraint numberConstraint = pageQuery.createConstraint((pageQuery.getStepField(numberField)), 
            FieldCompareConstraint.NOT_EQUAL, pageNode.getValue("number"));
      
      Constraint composite = pageQuery
            .createConstraint(liConstraint, CompositeConstraint.LOGICAL_AND, numberConstraint);
      pageQuery.setConstraint(composite);
      
      return pageManager.getList(pageQuery);
   }   
}
