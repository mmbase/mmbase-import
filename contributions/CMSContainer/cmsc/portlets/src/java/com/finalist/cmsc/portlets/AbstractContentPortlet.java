/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.*;

import javax.portlet.*;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.headerresource.HeaderResourceUtil;
import com.finalist.cmsc.services.contentrepository.ContentRepository;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;


public abstract class AbstractContentPortlet extends CmscPortlet{

    protected static final String ACTION_PARAM = "action";
    protected static final String CONTENT_PARAM = "content_";

    protected static final String ELEMENT_ID = "elementId";
    protected static final String VIEW = "view";
    
    protected static final String WINDOW = "window";
    protected static final String PAGE = "page";

    /**
     * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter(ACTION_PARAM);
        if (action == null) {
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
        } else if (action.equals("edit")) {
            PortletPreferences preferences = request.getPreferences();
            String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
            if (portletId != null) {
                // get the values submitted with the form
                saveParameters(request, portletId);
                
                setPortletView(portletId, request.getParameter(VIEW));
                
                setPortletNodeParameter(portletId, PAGE, request.getParameter(PAGE));
                setPortletParameter(portletId, WINDOW, request.getParameter(WINDOW));
            } else {
                getLogger().error("No portletId");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
    }

    protected void saveParameters(ActionRequest request, String portletId) {
    }

    /**
     * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEdit(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        getLogger().debug("===>ContentChannelPortlet.EDIT mode");
        String action = request.getParameter(ACTION_PARAM);
        if (action == null) {
            response.setPortletMode(PortletMode.EDIT);
        } else if (action.equals("edit")) {
            PortletPreferences preferences = request.getPreferences();
            String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
            
            if (portletId != null) {
                // get the values submitted with the form
                Enumeration parameterNames = request.getParameterNames();

                //currently supperting one Node
                HashMap<String,Node> nodesMap = new HashMap<String,Node>();
                while(parameterNames.hasMoreElements()){
                    // the parameterformat is "content_NUMBER_FIELD"
                    // for example "content_123_title"
                    String name  = (String) parameterNames.nextElement();
                    int index = name.indexOf("_");
                    int secondIndex = -1;
                    if(index > 0){
                        secondIndex= name.indexOf("_", index+1);
                    }
                    if(name.startsWith(CONTENT_PARAM) && secondIndex > 0){
                        String number = name.substring(index + 1, secondIndex);
                        String field = name.substring(secondIndex + 1);
                        String value = request.getParameter(name);
                        if(!StringUtil.isEmpty(number) ){
                            if( ! nodesMap.containsKey(number)){
                                CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
                                Cloud cloud = cloudProvider.getCloud();
                                Node node = cloud.getNode(number);
                                node.setObjectValue(field, value);
                                nodesMap.put(number,node);
                            }
                            else {
                                Node node = nodesMap.get(number);
                                node.setObjectValue(field, value);
                                nodesMap.put(number,node);
                            }
                        }
                    }
                }
                if (nodesMap.size() > 0) {
                    Iterator nodesIt = nodesMap.values().iterator();
                    while (nodesIt.hasNext()) {
                        Node n = (Node) nodesIt.next();
                        getLogger().debug("==> updating node: " +n.getNumber());
                        n.commit();
                    }
                }
                setEditResponse(request, response, nodesMap);
            } else {
                getLogger().error("No portletId");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
    }

    protected void setEditResponse(ActionRequest request, ActionResponse response,
            HashMap<String, Node> nodesMap) throws PortletModeException {
        response.setPortletMode(PortletMode.VIEW);
    }
    
    protected void doEditDefaults(RenderRequest req, RenderResponse res)
            throws IOException, PortletException {
        addViewInfo(req);
        
        PortletPreferences preferences = req.getPreferences();
        String pageid = preferences.getValue(PAGE, null);
        if (!StringUtil.isEmpty(pageid)) {
            
            String pagepath = SiteManagement.getPath(Integer.valueOf(pageid), true);
            setAttribute(req, "pagepath", pagepath);
            
            Set<String> positions = SiteManagement.getPagePositions(pageid);
            ArrayList<String> orderedPositions = new ArrayList<String>(positions);
            Collections.sort(orderedPositions);
            setAttribute(req, "pagepositions", new ArrayList<String>(orderedPositions));
        }
        super.doEditDefaults(req, res);
    }
    
    protected void doEdit(RenderRequest req, RenderResponse res, String elementId) throws IOException, PortletException {
        if (ContentRepository.mayEdit(elementId)) {
            super.doEdit(req, res);
        }
        else {
            super.doView(req, res);
        }
    }
    
    protected void setMetaData(RenderRequest req, String elementId) {
        ContentElement element = ContentRepository.getContentElement(elementId);
        HeaderResourceUtil.addMeta(req, "title", element.getTitle());
        HeaderResourceUtil.addMeta(req, "description", element.getDescription());
        HeaderResourceUtil.addMeta(req, "keywords", element.getKeywords());
        HeaderResourceUtil.addMeta(req, "source", element.getSource());
        //HeaderResourceUtil.addMeta(req, "date", element.getPublishdate());
    }

}
