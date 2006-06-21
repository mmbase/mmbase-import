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
import com.finalist.cmsc.portalImpl.services.contentrepository.ContentRepository;
import com.finalist.cmsc.portalImpl.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

/**
 * @author Wouter Heijke
 */
public class ContentChannelPortlet extends CmscPortlet {

    private static final String ACTION_PARAM = "action";
    
    private static final String ELEMENT_ID = "elementId";
    
    private static final String USE_PAGING = "usePaging";
    private static final String OFFSET = "pager.offset";
	private static final String SHOW_PAGES = "showPages";
    private static final String ELEMENTS_PER_PAGE = "elementsPerPage";
    private static final String PAGES_INDEX = "pagesIndex";
    private static final String INDEX_POSITION = "position";

    private static final String ELEMENTS = "elements";
    private static final String TYPES = "types";
    private static final String TOTAL_ELEMENTS = "totalElements";
    
    private static final String USE_LIFECYCLE = "useLifecycle";

    private static final String MAX_ELEMENTS = "maxElements";
    private static final String DIRECTION = "direction";
    private static final String ORDERBY = "orderby";
    private static final String CONTENTCHANNEL = "contentchannel";

    private static final String CONTENT_PARAM = "content_";

	private static final String VIEW = "view";
    private static final String VIEW_TYPE = "viewtype";

    private static final String WINDOW = "window";
    private static final String PAGE = "page";
    
    /**
     * @see net.sf.mmapps.commons.portlets.CmscPortlet#processEditDefaults(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processEditDefaults(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        getLogger().debug("===>ContentChannelPortlet.EDIT_DEFAULTS mode");

        String action = request.getParameter(ACTION_PARAM);
        if (action == null) {
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
        } else if (action.equals("edit")) {
            PortletPreferences preferences = request.getPreferences();
            String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
            if (portletId != null) {
                // get the values submitted with the form
                setPorltetNodeParameter(portletId, CONTENTCHANNEL, request.getParameter(CONTENTCHANNEL));

                setPorltetParameter(portletId, ORDERBY, request.getParameter(ORDERBY));
                setPorltetParameter(portletId, DIRECTION, request.getParameter(DIRECTION));
                setPorltetParameter(portletId, USE_LIFECYCLE, request.getParameter(USE_LIFECYCLE));
                setPorltetParameter(portletId, ELEMENTS_PER_PAGE, request.getParameter(ELEMENTS_PER_PAGE));
                setPorltetParameter(portletId, MAX_ELEMENTS, request.getParameter(MAX_ELEMENTS));
                setPorltetParameter(portletId, SHOW_PAGES, request.getParameter(SHOW_PAGES));
                setPorltetParameter(portletId, USE_PAGING, request.getParameter(USE_PAGING));
                setPorltetParameter(portletId, PAGES_INDEX, request.getParameter(PAGES_INDEX));
                setPorltetParameter(portletId, INDEX_POSITION, request.getParameter(INDEX_POSITION));
                setPorltetParameter(portletId, VIEW_TYPE, request.getParameter(VIEW_TYPE));
                
                setPortletView(portletId, request.getParameter(VIEW));

                setPorltetNodeParameter(portletId, PAGE, request.getParameter(PAGE));
                setPorltetParameter(portletId, WINDOW, request.getParameter(WINDOW));
            } else {
                getLogger().error("No portletId");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
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
                if (nodesMap.size() == 1) {
                    Iterator nodesIt = nodesMap.values().iterator();
                    Node n = (Node) nodesIt.next();
                    response.setRenderParameter(ELEMENT_ID, String.valueOf(n.getNumber()));
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
    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
        PortletPreferences preferences = req.getPreferences();
        String channel = preferences.getValue(CONTENTCHANNEL, null);
        if (!StringUtil.isEmpty(channel)) {
            addContentElements(req);
            super.doView(req, res);
        }
    }

    @Override
    protected void doEdit(RenderRequest req, RenderResponse res) throws PortletException, IOException {
        String elementId = req.getParameter(ELEMENT_ID);
        if (StringUtil.isEmpty(elementId)) {
            addContentElements(req);
            PortletPreferences preferences = req.getPreferences();
            String channel = preferences.getValue(CONTENTCHANNEL, null);
            if (!StringUtil.isEmpty(channel)) {
                if (ContentRepository.mayEdit(channel)) {
                    super.doEdit(req, res);
                }
                else {
                    super.doView(req, res);
                }
            }
        }
        else {
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
    
    private void addContentElements(RenderRequest req) {
        String elementId = req.getParameter(ELEMENT_ID);
        if (StringUtil.isEmpty(elementId)) {
            PortletPreferences preferences = req.getPreferences();
            String portletId = preferences.getValue(PortalConstants.CMSC_OM_PORTLET_ID, null);
            List<String> contenttypes = SiteManagement.getContentTypes(portletId);
            
            String channel = preferences.getValue(CONTENTCHANNEL, null);
            
            int offset = 0;
            String currentOffset = req.getParameter(OFFSET);
            if (!StringUtil.isEmpty(currentOffset)) {
                offset = Integer.parseInt(currentOffset);
            }
            setAttribute(req, "offset", offset);
            
            String orderby = preferences.getValue(ORDERBY, null);
            String direction = preferences.getValue(DIRECTION, null);
            String useLifecycle = preferences.getValue(USE_LIFECYCLE, null);
            
            int maxElements = Integer.parseInt( preferences.getValue(MAX_ELEMENTS, "-1") );
            if (maxElements <= 0) {
                maxElements = Integer.MAX_VALUE;
            }
            int elementsPerPage = Integer.parseInt( preferences.getValue(ELEMENTS_PER_PAGE, "-1") );
            if (elementsPerPage <= 0) {
                elementsPerPage = Integer.MAX_VALUE;
            }
            elementsPerPage = Math.min(elementsPerPage, maxElements);
            
            int totalItems = ContentRepository.countContentElements(channel, contenttypes, orderby, direction, 
                    Boolean.valueOf(useLifecycle).booleanValue(), offset, elementsPerPage);
            
            List<ContentElement> elements = ContentRepository.getContentElements(channel, contenttypes, orderby, direction, 
                    Boolean.valueOf(useLifecycle).booleanValue(), offset, elementsPerPage);
            
            setAttribute(req, ELEMENTS, elements);
            if (contenttypes != null && !contenttypes.isEmpty()) {
                setAttribute(req, TYPES, contenttypes);
            }
            setAttribute(req, TOTAL_ELEMENTS, Math.min(maxElements, totalItems));
            setAttribute(req, ELEMENTS_PER_PAGE, elementsPerPage);
    
            String pagesIndex = preferences.getValue(PAGES_INDEX, null);
            if (StringUtil.isEmpty(pagesIndex)) {
                setAttribute(req, PAGES_INDEX, "center");
            }

            String showPages = preferences.getValue(SHOW_PAGES, null);
            if (StringUtil.isEmpty(showPages)) {
                setAttribute(req, SHOW_PAGES, 10);
            }
            
            boolean usePaging = Boolean.valueOf( preferences.getValue(USE_PAGING, "true") );
            if (usePaging) {
                usePaging = totalItems > elementsPerPage;
            }
            setAttribute(req, USE_PAGING, usePaging);
            
            String indexPosition = preferences.getValue(INDEX_POSITION, null);
            if (StringUtil.isEmpty(indexPosition)) {
                setAttribute(req, INDEX_POSITION, "bottom");
            }
            String viewType = preferences.getValue(VIEW_TYPE, null);
            if (StringUtil.isEmpty(viewType)) {
                setAttribute(req, VIEW_TYPE, "list");
            }
            else {
                if ("oneDetail".equalsIgnoreCase(viewType) && totalItems == 1) {
                    setAttribute(req, VIEW_TYPE, "detail");
                }
            }
        }
    }
    
    public int getOffset(int currentPage, int pageSize) {
        return ((currentPage - 1) * pageSize) + 1 ;
    }
    
}
