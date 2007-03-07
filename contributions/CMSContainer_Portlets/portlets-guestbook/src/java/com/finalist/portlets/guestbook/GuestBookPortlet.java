/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.portlets.guestbook;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.portlet.*;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;

import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class GuestBookPortlet extends ContentPortlet {
    
    private static final Log log = LogFactory.getLog(GuestBookPortlet.class);

    protected static final String ACTION_PARAM = "action";
    protected static final String CONTENTELEMENT = "contentelement";
    private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String EMAIL_FIELD = "email";
    private static final String BODY_FIELD = "body";
    private static final int MAX_BODY_LENGTH = 1024;

    /** name of the parameter that defines the mode the view is displayed in */

    public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        Map<String, String> errorMessages = new Hashtable<String, String>();
        String action = request.getParameter(ACTION_PARAM);
        log.debug("Action: " + action);
        if (action == null) {
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
        } else if (action.equals("edit")) {
            PortletPreferences preferences = request.getPreferences();
            String contentelement = preferences.getValue(CONTENTELEMENT, null);
            log.debug("contentelement: " + contentelement);

            if (contentelement != null) {
                if (StringUtils.isBlank(request.getParameter(TITLE_FIELD))) {
                    errorMessages.put(TITLE_FIELD, "guestbook.field.title.error.empty");
                }
                if (StringUtils.isBlank(request.getParameter(NAME_FIELD))) {
                    errorMessages.put(NAME_FIELD, "guestbook.field.name.error.empty");
                }
                if (StringUtils.isBlank(request.getParameter(BODY_FIELD))) {
                    errorMessages.put(BODY_FIELD, "guestbook.field.body.error.empty");
                }
                if (errorMessages.size() > 0) {
                    log.debug("has errors: " + errorMessages);
                    request.getPortletSession().setAttribute(ERROR_MESSSAGES, errorMessages);
                    Map<String, String> originalValues = new Hashtable<String, String>();
                    originalValues.put(TITLE_FIELD, request.getParameter(TITLE_FIELD));
                    originalValues.put(NAME_FIELD, request.getParameter(NAME_FIELD));
                    originalValues.put(EMAIL_FIELD, request.getParameter(EMAIL_FIELD));
                    originalValues.put(BODY_FIELD, request.getParameter(BODY_FIELD));
                    request.getPortletSession().setAttribute(ORIGINAL_VALUES, originalValues);
                    request.getPortletSession().setAttribute(ELEMENT_ID, contentelement);
                } else {
                    CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
                    Cloud cloud = cloudProvider.getCloud();
                    Node element = cloud.getNode(contentelement);

                    NodeManager messageMgr = cloud.getNodeManager("guestmessage");
                    Node message = messageMgr.createNode();
                    message.setStringValue(TITLE_FIELD, request.getParameter(TITLE_FIELD));
                    message.setStringValue(NAME_FIELD, request.getParameter(NAME_FIELD));
                    if (!StringUtil.isEmpty(request.getParameter(EMAIL_FIELD))) {
                        message.setStringValue(EMAIL_FIELD, request.getParameter(EMAIL_FIELD));
                    }
                    String body = request.getParameter(BODY_FIELD);
                    if (body.length() > MAX_BODY_LENGTH) {
                        body = body.substring(0, MAX_BODY_LENGTH);
                    }
                    message.setStringValue(BODY_FIELD, request.getParameter(BODY_FIELD));
                    log.debug("saving new guestmessage: " + message);
                    message.commit();

                    Relation posrel = element.createRelation(message, cloud.getRelationManager("posrel"));
                    posrel.commit();
                    log.debug("saving of new guestmessage is successful: " + message);
                }
            } else {
                log.error("No contentelement");
            }
            // switch to View mode
            response.setPortletMode(PortletMode.VIEW);
        } else {
            log.error("Unknown action: '" + action + "'");
        }
    }

    public void processEdit(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        super.processEdit(request, response);

        String action = request.getParameter(ACTION_PARAM);
        if (action == null) {
            response.setPortletMode(PortletMode.EDIT);
        } else if (action.equals("delete")) {
            String deleteNumber = request.getParameter("deleteNumber");
            CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
            Cloud cloud = cloudProvider.getCloud();
            Node element = cloud.getNode(deleteNumber);
            element.delete(true);
        }
    }

}
