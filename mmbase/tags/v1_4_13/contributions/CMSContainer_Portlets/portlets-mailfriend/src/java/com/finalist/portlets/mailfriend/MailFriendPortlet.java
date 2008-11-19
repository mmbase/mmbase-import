/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.portlets.mailfriend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.portlet.*;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.*;

import com.finalist.cmsc.mmbase.EmailUtil;
import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.portlets.ContentPortlet;
import com.finalist.pluto.portalImpl.core.CmscPortletMode;

public class MailFriendPortlet extends ContentPortlet {

    protected static final String ACTION_PARAM = "action";
    protected static final String CONTENTELEMENT = "contentelement";

    public void processView(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter(ACTION_PARAM);
        Map<String, String> errorMessages = new Hashtable<String, String>();
        if (action == null) {
            response.setPortletMode(CmscPortletMode.EDIT_DEFAULTS);
        } else if (action.equals("edit")) {
            PortletPreferences preferences = request.getPreferences();
            String contentelement = preferences.getValue(CONTENTELEMENT, null);

            if (contentelement != null) {
                String fromname = request.getParameter("fromname");
                String fromemail = request.getParameter("fromemail");
                String toemail = request.getParameter("toemail");
                String toname = request.getParameter("toname");
                String message = request.getParameter("message");
                String articleNumber = request.getParameter("articleNumber");
                String articleUrlPath = request.getParameter("articleUrlPath");
                if (StringUtil.isEmptyOrWhitespace(articleNumber)) {
                    errorMessages.put("article", "view.error.noarticle");
                }
                String emailRegex = PropertiesUtil.getProperty("email.regex");
                if (StringUtil.isEmptyOrWhitespace(fromname)) {
                    errorMessages.put("fromname", "view.fromname.empty");
                }
                if (StringUtil.isEmptyOrWhitespace(fromemail)) {
                    errorMessages.put("fromemail", "view.fromemail.empty");
                } else if (!fromemail.matches(emailRegex)) {
                    errorMessages.put("fromemail", "view.fromemail.invalid");
                }
                if (StringUtil.isEmptyOrWhitespace(toname)) {
                    errorMessages.put("toname", "view.toname.empty");
                }
                if (StringUtil.isEmptyOrWhitespace(toemail)) {
                    errorMessages.put("toemail", "view.toemail.empty");
                } else if (!toemail.matches(emailRegex)) {
                    errorMessages.put("toemail", "view.toemail.invalid");
                }

                if (errorMessages.size() == 0) {
                    CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
                    Cloud cloud = cloudProvider.getCloud();
                    Node mailfriend = cloud.getNode(contentelement);
                    boolean sent = sendEmail(cloud, toname, toemail, fromname, fromemail, mailfriend, request, articleNumber, message);
                    if (!sent) {
                        errorMessages.put("sendemail", "view.error.sendemail");
                    }
                }
                if (errorMessages.size() > 0) {
                    Map<String, String> parameterMap = new HashMap<String, String>();
                    parameterMap.put("fromname", fromname);
                    parameterMap.put("fromemail", fromemail);
                    parameterMap.put("toemail", toemail);
                    parameterMap.put("toname", toname);
                    parameterMap.put("articleNumber", articleNumber);
                    parameterMap.put("message", message);
                    parameterMap.put("articleUrlPath", articleUrlPath);
                    request.getPortletSession().setAttribute("errormessages", errorMessages);
                    request.getPortletSession().setAttribute("parameterMap", parameterMap);
                } else {
                    request.getPortletSession().setAttribute("confirm", "confirm");
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

    private boolean sendEmail(Cloud cloud, String toname, String toemail, String fromname, String fromemail, Node mailfriend, ActionRequest request, String articleNumber, String tomessage) {
        boolean sent = false;
        StringBuffer link = new StringBuffer();
        link.append(request.getScheme());
        link.append("://");
        link.append(request.getServerName());
        link.append(":");
        link.append(request.getServerPort());
        link.append(request.getContextPath());
        link.append("/content/");
        link.append(articleNumber);

        String message = mailfriend.getStringValue("message");
        String subject = mailfriend.getStringValue("subject");
        message = message.replace("#VRIEND#", toname);
        message = message.replace("#URL#", link.toString());
        message = message.replace("#BERICHT#", tomessage);
        message = message.replace("#AFZENDER#", fromname);
        subject = subject.replace("#AFZENDER#", fromname);
        try {
            EmailUtil.send(cloud, null, toemail, fromname, fromemail, subject, message);
            sent = true;
        }
        catch (Exception e) {
            getLogger().error("error sending email", e);
        }
        return sent;
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletPreferences preferences = request.getPreferences();
        String contentelement = preferences.getValue(CONTENTELEMENT, null);
        PortletSession portletSession = request.getPortletSession();

        if (contentelement != null) {
            if (portletSession.getAttribute("confirm") != null) {
                String confirm = (String) portletSession.getAttribute("confirm");
                portletSession.removeAttribute("confirm");
                request.setAttribute("confirm", confirm);
            }
            if (portletSession.getAttribute("errormessages") != null) {
                Hashtable errormessages = (Hashtable) portletSession.getAttribute("errormessages");
                portletSession.removeAttribute("errormessages");
                request.setAttribute("errormessages", errormessages);
            }
            if (portletSession.getAttribute("parameterMap") != null) {
                Map parameterMap = (HashMap) portletSession.getAttribute("parameterMap");
                portletSession.removeAttribute("parameterMap");
                Iterator keyIterator = parameterMap.keySet().iterator();
                while (keyIterator.hasNext()) {
                    String keyValue = (String) keyIterator.next();
                    String entryValue = (String) parameterMap.get(keyValue);
                    request.setAttribute(keyValue, entryValue);
                }
            }
        } else {
            getLogger().error("No contentelement");
        }
        super.doView(request, response);
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
