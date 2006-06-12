package com.finalist.cmsc.struts;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeList;


import javax.servlet.http.HttpServletRequest;

/**
 * @author Nico Klasens
 */
public class WizardListAction extends MMBaseFormlessAction {

    private static String DEFAULT_SESSION_KEY = "editwizard";

    public ActionForward execute(ActionMapping mapping,
            HttpServletRequest request, Cloud cloud) throws Exception {

        String nodetype = request.getParameter("nodetype");
        if (nodetype == null) { 
            throw new RuntimeException(" Provide a nodetype"); 
        }
        String sessionkey = request.getParameter("sessionkey");
        if (sessionkey == null || sessionkey.length() == 0) {
            sessionkey = DEFAULT_SESSION_KEY;
        }

        NodeManager manager = cloud.getNodeManager("editwizards");
        NodeList list = manager.getList("nodepath = '" + nodetype + "'", null, null);
        if (list.isEmpty()) { 
            throw new RuntimeException("Unable to find a wizard for nodetype " + nodetype);
        }
        Node wizard = list.getNode(0);

        StringBuffer forward = new StringBuffer();
        forward.append(mapping.findForward("list").getPath() + "?language=" + cloud.getLocale().getLanguage());
        addParameter(forward, sessionkey, "sessionkey", null);
        addParameter(forward, mapping, "templates", "templates", null);
        addParameter(forward, mapping, "referrer", "referrer", null);
        addParameter(forward, wizard, "wizard", "wizard", null);
        addParameter(forward, wizard, "nodepath", "nodepath", null);
        addParameter(forward, wizard, "fields", "fields", null);
        addParameter(forward, wizard, "pagelength", "pagelength", "50");
        addParameter(forward, wizard, "maxpagecount", "maxpagecount", "100");
        addParameter(forward, wizard, "constraints", "constraints", null);
        addParameter(forward, wizard, "age", "age", null);
        addParameter(forward, wizard, "distinct", "distinctlist", null);
        addParameter(forward, wizard, "searchdir", "searchdir", null);
        addParameter(forward, wizard, "orderby", "orderby", null);
        addParameter(forward, wizard, "directions", "directions", null);
        addParameter(forward, wizard, "maxupload", "maxupload", null);
        addParameter(forward, wizard, "searchfields", "searchfields", null);
        addParameter(forward, wizard, "searchtype", "searchtype", null);
        addParameter(forward, wizard, "searchvalue", "searchvalue", null);
        addParameter(forward, wizard, "search", "search", null);
        addParameter(forward, wizard, "origin", "origin", null);
        addParameter(forward, wizard, "title", "title", null);
        
        // Editwizard starten:
        ActionForward ret = new ActionForward(forward.toString());
        ret.setRedirect(true);
        return ret;
    }

    private void addParameter(StringBuffer forward, ActionMapping mapping, String paramname, String mappingName, String defaultvalue) {
        String value = mapping.findForward(mappingName).getPath();
        addParameter(forward, value, paramname, defaultvalue);
    }

    private void addParameter(StringBuffer forward, Node node, String paramname, String fieldname, String defaultvalue) {
        String value = node.getStringValue(fieldname);
        addParameter(forward, value, paramname, defaultvalue);
    }
    
    private void addParameter(StringBuffer forward, String value, String paramname, String defaultvalue) {
        if (StringUtil.isEmptyOrWhitespace(value)) {
            value = defaultvalue;
        }
        if (!StringUtil.isEmptyOrWhitespace(value)) {
            forward.append("&").append(paramname).append("=").append(value);
        }
    }
    
}