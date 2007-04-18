/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.languageredirect;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.FieldValueConstraint;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.navigation.SiteUtil;

public class LanguageRedirectServlet extends BridgeServlet {
	
	private static final long serialVersionUID = -6415261962186866668L;
	
	private static Log log = LogFactory.getLog(LanguageRedirectServlet.class);
	private static final String PARAMETER_ID = "id";
	private static final String PARAMETER_LANGUAGE = "lan";
	@Override
    protected Map getAssociations() {
        Map a = super.getAssociations();
        a.put("language", new Integer(50));
        return a;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doRedirect(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doRedirect(request, response);
    }
    
    private void doRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {    	
    	// Check if the parameters are filled.
    	if (request.getParameter(PARAMETER_LANGUAGE) == null || request.getParameter(PARAMETER_LANGUAGE).equals("")) {
    		log.error("No language parameter given or empty.");
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No language parameter given or empty.");
    		return;    		
    	}
    	if  (request.getParameter(PARAMETER_ID) == null || request.getParameter(PARAMETER_LANGUAGE).equals("")) {
    		log.error("No id parameter given or empty.");
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No id parameter given or empty.");
    		return;
    	}
    	// Get the cloud. 
    	Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
    	
    	// Is there a site with the given language? 
		Node foreignSite = SearchUtil.findNode(cloud, SiteUtil.SITE, "language", request.getParameter("lan"));
		
		if (foreignSite == null) {
			log.error("No site was found with the language\"" + request.getParameter("lan") + "\".");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No site was found with the language\"" + request.getParameter("lan") + "\".");
			return;
		}
		
    	// Get the node with the id given in the id parameter.
    	Node pageOrSiteNode = cloud.getNode(Integer.parseInt(request.getParameter("id")));
    	NodeManager nodeManager = pageOrSiteNode.getNodeManager();
    	log.debug("The nodemanager of the current node's name is: " + nodeManager.getName());
    	
    	// Is the current Node a page or a site?    
    	if (PagesUtil.isPage(pageOrSiteNode)) {
    		log.debug("The current node is a page(id: " + pageOrSiteNode.getNumber() + ").");    		
    		// Look for a page that has the same liname as the currentpage. Do this with a query on the database otherwise we would have to loop through the pages of the found sites to find the page ourselves.    		
    		Field liField = nodeManager.getField("liname");
    		NodeList pageList = getCorrespondingPages(pageOrSiteNode, liField, nodeManager);
    		while (pageList == null || pageList.size() == 0){    			
    			/*
    			*  If no page was found and the parent is not a site, get the parent of the current node and find the corresponding page for that site.
    			*  If the parent is a site, go to the corresponding site instead.
    		 	*/     			
    			NodeList parentList = pageOrSiteNode.getRelatedNodes(nodeManager, NavigationUtil.NAVREL, "SOURCE");        	
    			if (parentList.size() > 0) {
    				// The parentList should only contain one item since a page can always only have one source.
    				pageOrSiteNode = parentList.getNode(0);
     				if (SiteUtil.isSite(pageOrSiteNode)) {
        				// The parent is a site, redirect to the site that corresponds to the language given.
     					response.sendRedirect(foreignSite.getStringValue(SiteUtil.FRAGMENT_FIELD));
     					return;
     				}
    			}
    			else {
    				// There is no parent. 
    				log.error("Page with number: " + pageOrSiteNode.getFieldValue("number") + ", doesn't have a parent.");
    				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Page with number: " + pageOrSiteNode.getFieldValue("number") + ", doesn't have a parent.");
    				return;
    			}
    			
    			pageList = getCorrespondingPages(pageOrSiteNode, liField, nodeManager);    			
            }
    		
    		/*
    		 * We now have a list of pages that have the same liname as the page that was given as the id in the request or
    		 * we have a list of pages that have the same liname as one of the parents of the page that was given as the id in the request.
    		 * Find the site of this page and if it corresponds with the site found previously, redirect to that page. 
    		 */
    		if (pageList != null && pageList.size() > 0) {
    			NodeIterator pageIt = pageList.nodeIterator();
    			while (pageIt.hasNext()) {
    				Node curPage = (Node)pageIt.next();
    				String[] treeManagers = new String[] { PagesUtil.PAGE, SiteUtil.SITE };    	
    				String[] fragmentFieldnames = new String[] { PagesUtil.FRAGMENT_FIELD, SiteUtil.FRAGMENT_FIELD };    		    	
    				String path = TreeUtil.getPathToRootStringWithoutCache(curPage, treeManagers, NavigationUtil.NAVREL, fragmentFieldnames);
    				Node currentSite = NavigationUtil.getSiteFromPath(cloud, path);
    				if (currentSite.getIntValue("number") == foreignSite.getIntValue("number")) {
    					String link = request.getContextPath() + "/" + path;
    					response.sendRedirect(link);
    					return;
    				}
    			}    		
    		}
    		// Return with an error. There was no site that had the same language as the given language parameter.
    		log.error("None of the found pages belong to the site with langauge " + request.getParameter(PARAMETER_LANGUAGE) + ".");
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "None of the found pages belong to the site with langauge " + request.getParameter(PARAMETER_LANGUAGE) + ".");
        	
    	}
    	if (SiteUtil.isSite(pageOrSiteNode)) {
    		// We're in the root, the current node is a site, so go directly to the corresponding site in the given language. 
    		log.debug("The current node is a site(id: " + pageOrSiteNode.getNumber() + ").");
    		response.sendRedirect(foreignSite.getStringValue(SiteUtil.FRAGMENT_FIELD));
    	}        	    	
    }

    private NodeList getCorrespondingPages (Node pageNode, Field liField, NodeManager pageManager) {        
    	NodeQuery pageQuery = pageManager.createQuery();
    	FieldValueConstraint liConstraint = pageQuery.createConstraint((pageQuery.getStepField(liField)),
                FieldCompareConstraint.EQUAL, pageNode.getValue("liname"));
    	
    	// Do not include the current number of the pageNode.
    	Field numberField = pageManager.getField("number");
    	FieldValueConstraint numberConstraint = pageQuery.createConstraint((pageQuery.getStepField(numberField)), 
    			FieldCompareConstraint.NOT_EQUAL, pageNode.getValue("number"));
    	
    	Constraint composite = pageQuery.createConstraint(liConstraint, CompositeConstraint.LOGICAL_AND, numberConstraint);
    	pageQuery.setConstraint(composite);
    	
    	return pageManager.getList(pageQuery);
    }
}
