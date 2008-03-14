package org.jahia.portlet.jforum;

import javax.portlet.PortletSession;

/**
 * Created by Jahia.
 * User: ktlili
 * Date: 17 août 2007
 * Time: 10:30:17
 * To change this template use File | Settings | File Templates.
 */
public class HttpSessionWrapper extends portlet.wrappers.HttpSessionWrapper {
    private PortletSession pSession;

    public HttpSessionWrapper(javax.portlet.PortletSession portletSession) {
        super(portletSession);
        this.pSession = portletSession;
    }

    public void setAttribute(java.lang.String string, java.lang.Object object) {
        pSession.setAttribute(string, object, PortletSession.APPLICATION_SCOPE);
    }

    public java.lang.Object getAttribute(java.lang.String string) {
        return pSession.getAttribute(string, PortletSession.APPLICATION_SCOPE);
    }

    public void removeAttribute(java.lang.String string){
       pSession.removeAttribute(string,PortletSession.APPLICATION_SCOPE);
    }
}
