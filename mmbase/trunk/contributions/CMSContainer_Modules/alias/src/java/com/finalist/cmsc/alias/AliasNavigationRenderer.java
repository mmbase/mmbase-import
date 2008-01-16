package com.finalist.cmsc.alias;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.alias.beans.om.Alias;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.navigation.NavigationManager;
import com.finalist.cmsc.portalImpl.registry.PortalRegistry;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.ScreenFragment;
import com.finalist.pluto.portalImpl.core.PortalEnvironment;

public class AliasNavigationRenderer implements NavigationItemRenderer {

	protected static String CONTENT_TYPE = "text/html";
	   
   private Log log = LogFactory.getLog(AliasNavigationRenderer.class);

   public void render(NavigationItem item, HttpServletRequest request, HttpServletResponse response,
           ServletConfig servletConfig) {
       
      if (item instanceof Alias) {
    	  Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
    	  Node itemNode = cloud.getNode(item.getId());
    	  NodeList relatedNodes = itemNode.getRelatedNodes("page", "related", "destination");
    	  if(relatedNodes.size() > 0) {
    		  Node page = relatedNodes.getNode(0);
    		
    		  String path = page.getStringValue("path");
		     NavigationItem pageItem = SiteManagement.getNavigationItemFromPath(path);

             HttpServletRequest aliasRequest = new AliasHttpServletRequest(request, path); 
             PortalEnvironment aliasEnv = new PortalEnvironment(aliasRequest, response, servletConfig);
             PortalRegistry registry = PortalRegistry.getPortalRegistry(request);
             response.setContentType(CONTENT_TYPE); 
             ScreenFragment oldScreen = registry.getScreen();
             
             if (pageItem != null) {
                NavigationItemRenderer manager = NavigationManager.getRenderer(pageItem);
                if (manager != null) {
                    manager.render(pageItem, aliasRequest, response, servletConfig);
                }
             }
             
             registry.setScreen(oldScreen);    		  
    	  }
    	  else {
	         throw new IllegalArgumentException(
    	                 "Trying to resolve Alias without related pages id:"+item.getId());
    	  }
      }
      else {
         throw new IllegalArgumentException(
               "Got a wrong type in the AliasNavigationRenderer (only wants Alias), was" + item.getClass());
      }
   }
   
   


   class AliasHttpServletRequest extends HttpServletRequestWrapper {

	      private String pagePath;


	      public AliasHttpServletRequest(HttpServletRequest request, String pagePath) {
	         super(request);
	         this.pagePath = pagePath;
	      }


	      @Override
	      public String getServletPath() {
	         return pagePath;
	      }

	   }
}
