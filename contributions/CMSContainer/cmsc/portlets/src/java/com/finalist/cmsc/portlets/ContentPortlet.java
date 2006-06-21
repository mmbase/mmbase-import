package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.util.*;

import javax.portlet.*;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.services.contentrepository.ContentRepository;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;
/**
 * Portlet to edit content elements
 * 
 * @author Wouter Heijke
 */
public class ContentPortlet extends CmscPortlet {
	
	private static final String ACTION_PARAM = "action";
    private static final String CONTENT_PARAM = "content_";

    private static final String ELEMENT_ID = "elementId";
    private static final String CONTENTELEMENT = "contentelement";
    private static final String USE_LIFECYCLE = "useLifecycle";
    
    private static final String VIEW = "view";
    
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
                response.setPortletMode(PortletMode.VIEW);
            } else {
                getLogger().error("No portletId");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
    }
    
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
                setPorltetNodeParameter(portletId, CONTENTELEMENT, request.getParameter(CONTENTELEMENT));
                setPorltetParameter(portletId, USE_LIFECYCLE, request.getParameter(USE_LIFECYCLE));
                
                setPortletView(portletId, request.getParameter(VIEW));
            } else {
                getLogger().error("No portletId");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
    }

    @Override
    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
        String elementId = req.getParameter(ELEMENT_ID);
        if (StringUtil.isEmpty(elementId)) {
            PortletPreferences preferences = req.getPreferences();
            elementId = preferences.getValue(CONTENTELEMENT, null);
        }
        if (!StringUtil.isEmpty(elementId)) {
            setAttribute(req, ELEMENT_ID, elementId);
            super.doView(req, res);
        }
    }
    
    
    protected void doEdit(RenderRequest req, RenderResponse res) throws IOException, PortletException {
        String elementId = req.getParameter(ELEMENT_ID);
        if (StringUtil.isEmpty(elementId)) {
            PortletPreferences preferences = req.getPreferences();
            elementId = preferences.getValue(CONTENTELEMENT, null);
        }
        if (!StringUtil.isEmpty(elementId)) {
            setAttribute(req, ELEMENT_ID, elementId);
            if (ContentRepository.mayEdit(elementId)) {
                super.doEdit(req, res);
            }
            else {
                super.doView(req, res);
            }
        }
    }
    
    protected void doEditDefaults(RenderRequest req, RenderResponse res)
        throws IOException, PortletException {
        addViewInfo(req);
        super.doEditDefaults(req, res);
    }

}
