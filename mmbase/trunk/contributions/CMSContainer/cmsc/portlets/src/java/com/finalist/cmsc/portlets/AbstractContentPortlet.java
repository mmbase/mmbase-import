/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.portlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.portlet.*;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Relation;

import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.portalImpl.PortalConstants;
import com.finalist.cmsc.portalImpl.headerresource.MetaHeaderResource;
import com.finalist.cmsc.services.contentrepository.ContentRepository;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import com.finalist.pluto.portalImpl.aggregation.PortletFragment;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningException;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;


public abstract class AbstractContentPortlet extends CmscPortlet{

    protected static final String CONTENTELEMENT = "contentelement";
    protected static final String USE_LIFECYCLE = "useLifecycle";

    protected static final String ACTION_PARAM = "action";
    protected static final String CONTENT_PARAM = "content_";

    protected static final String ELEMENT_ID = "elementId";
    protected static final String VIEW = "view";
    
    protected static final String WINDOW = "window";
    protected static final String PAGE = "page";
    
    /** name of the map on the request that contains error messages */
    protected static final String ERROR_MESSSAGES = "errormessages";
    /** name of the map on the request that contains the original values of the form */
    protected static final String ORIGINAL_VALUES = "originalValues";
    /** name of the parameter that defines the mode the view is displayed in */
    protected static final String MODE = "mode";
    
    private static final int MAX_BODY_LENGTH = 1024;

    private DateFormat metaDateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
                                Cloud cloud = CloudUtil.getCloudFromThread();
                                Node node = cloud.getNode(number);
                                node.setValue(field, value);
                                nodesMap.put(number,node);
                            }
                            else {
                                Node node = nodesMap.get(number);
                                node.setValue(field, value);
                                nodesMap.put(number,node);
                            }
                        }
                    }
                }
                if (nodesMap.size() > 0) {
                   for (Node node : nodesMap.values()) {
                      getLogger().debug("==> updating node: " + node.getNumber());
                      if (ContentElementUtil.isContentElement(node)) {
                         try {
                            Versioning.addVersion(node);
                         } catch (VersioningException e) {
                            getLogger().error("Problem while adding version for node : " + node.getNumber(), e);
                         }
                      }
                      node.commit();
                      if (!Workflow.hasWorkflow(node)) {
                         Workflow.create(node,"");
                      }
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
            req.setAttribute("portletMode", "view");
            super.doView(req, res);
        }
    }
    
    protected void setMetaData(RenderRequest req, String elementId) {
        ContentElement element = ContentRepository.getContentElement(elementId);
        
        PortletFragment portletFragment = getPortletFragment(req);
        portletFragment.addHeaderResource(new MetaHeaderResource(true, "title", element.getTitle()));
        portletFragment.addHeaderResource(new MetaHeaderResource(true, "subject", element.getKeywords()));
        portletFragment.addHeaderResource(new MetaHeaderResource(true, "date", formatDate(element.getCreationdate())));
        portletFragment.addHeaderResource(new MetaHeaderResource(true, "identifier", elementId));
        portletFragment.addHeaderResource(new MetaHeaderResource(true, "coverage", formatDate(element.getPublishdate())+" - "+formatDate(element.getExpirydate())));
    }

	private String formatDate(Date date) {
		if(date == null) {
			return "";
		}
		else {
			return metaDateFormat.format(date);
		}
	}

    /** Processes reactions submitted by reactionform
     * TODO move this logic to a better place. 
     * */
    @Override
    public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        final String titleField = "title";
        final String nameField = "name";
        final String emailField = "email";
        final String bodyField = "body";
        Map<String, String> errorMessages = new Hashtable<String, String>();
        String action = request.getParameter(ACTION_PARAM);
        getLogger().debug("processView for action: " + action);

        if (action == null) {
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
        } else if (action.equals("react")) {
            PortletPreferences preferences = request.getPreferences();
            String contentelement = preferences.getValue(CONTENTELEMENT, null);
            if (contentelement == null) {
                contentelement = request.getParameter("elementId");
            }
            getLogger().debug("contentelement: " + contentelement);
            
            if (contentelement != null) {
                if (StringUtils.isBlank(request.getParameter(titleField))) {
                    errorMessages.put(titleField, "reactionform.field.title.error.empty");
                }
                if (StringUtils.isBlank(request.getParameter(nameField))) {
                    errorMessages.put(nameField, "reactionform.field.name.error.empty");
                }
                if (StringUtils.isBlank(request.getParameter(bodyField))) {
                    errorMessages.put(bodyField, "reactionform.field.body.error.empty");
                }
                
                if (errorMessages.size() > 0) {            
                    getLogger().debug("has errors: " + errorMessages);
                    request.getPortletSession().setAttribute(ERROR_MESSSAGES, errorMessages);
                    Map<String, String> originalValues = new Hashtable<String, String>();
                    originalValues.put(titleField, request.getParameter(titleField));
                    originalValues.put(nameField, request.getParameter(nameField));
                    originalValues.put(emailField, request.getParameter(emailField));
                    originalValues.put(bodyField, request.getParameter(bodyField));
                    request.getPortletSession().setAttribute(ORIGINAL_VALUES, originalValues);
                    request.getPortletSession().setAttribute(ELEMENT_ID, request.getParameter(ELEMENT_ID));
                } else {
                    getLogger().debug("storing message...");

                    CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
                    Cloud cloud = cloudProvider.getCloud();
                    Node element = cloud.getNode(contentelement);
                    
                    NodeManager messageMgr = cloud.getNodeManager("reaction");
                    Node message = messageMgr.createNode();
                    message.setStringValue(titleField, request.getParameter(titleField));
                    message.setStringValue(nameField, request.getParameter(nameField));
                    if (!StringUtil.isEmpty(request.getParameter(emailField))) {
                        message.setStringValue(emailField, request.getParameter(emailField));
                    }
                    String body = request.getParameter(bodyField);
                    if (body.length() > MAX_BODY_LENGTH) {
                        body = body.substring(0, MAX_BODY_LENGTH);
                    }
                    message.setStringValue(bodyField, body);
                    getLogger().debug("storing message: " + message);
                    message.commit();
                    
                    Relation posrel = element.createRelation(message, cloud.getRelationManager("posrel"));
                    getLogger().debug("storing posrel: " + posrel);
                    posrel.commit();
                }
            } else {
                getLogger().error("No contentelement");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            getLogger().error("Unknown action: '" + action + "'");
        }
    }

    @Override
    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, IOException {
        PortletSession session = req.getPortletSession(); 
        Object errormessages = session.getAttribute(ERROR_MESSSAGES);
        if (errormessages != null) {
            req.setAttribute(ERROR_MESSSAGES, errormessages);
            req.setAttribute(ORIGINAL_VALUES, session.getAttribute(ORIGINAL_VALUES));
            req.setAttribute(MODE, "reaction");
            req.setAttribute(ELEMENT_ID, session.getAttribute(ELEMENT_ID));
            session.removeAttribute(ERROR_MESSSAGES);
            session.removeAttribute(ORIGINAL_VALUES);
            session.removeAttribute(ELEMENT_ID);
        }           
        
        super.doView(req, res);
    }

    
}
