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
      String language = request.getParameter(PARAMETER_LANGUAGE);
      
    	if  (request.getParameter(PARAMETER_ID) == null || request.getParameter(PARAMETER_ID).equals("")) {
    		log.error("No id parameter given or empty.");
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No id parameter given or empty.");
    		return;
    	}
      int id = Integer.parseInt(request.getParameter(PARAMETER_ID));

      String result = LanguageRedirectUtil.translate(language, id);
      if(result == null) {
         response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not translate, see log file");
      }
      response.sendRedirect(request.getContextPath() + "/" + result);
    }

}
