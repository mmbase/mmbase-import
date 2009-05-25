/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import org.mmbase.bridge.Cloud;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The PageInfo is a container class.
 * Needed for the ProcessorInterface.
 *
 * @since MMBase 1.8
 * @author Pierre van Rooden
 * @version $Id$
 */
public class PageInfo {
    /**
     * The request object associated with the current page.
     * Not a very good name - and should not be public, but needed for
     * backward compatibility witH SCAN
     */
    public HttpServletRequest req;

    /**
     * The response object associated with the current page.
     * Not a very good name - and should not be public, but needed for
     * backward compatibility witH SCAN
     */
    public HttpServletResponse res;

    private Cloud cloud;

    /**
     * Empty constructor, needed for call from scanpage
     */
    protected PageInfo() {}

    /**
     * Creates a pagecontext with a user's request information.
     * @param request the HttpServletRequest object for this request
     * @param response the HttpServletResponse object for this request
     */
    public PageInfo(HttpServletRequest request, HttpServletResponse response, Cloud cloud) {
        setRequest(request);
        setResponse(response);
        this.cloud = cloud;
    }
    public PageInfo(HttpServletRequest request, HttpServletResponse response) {
        setRequest(request);
        setResponse(response);
    }

    /**
     * Returns the HttpServletRequest object for this request.
     * @return a HttpServletRequest object, or <code>null</code> if none is available
     */
    public HttpServletRequest getRequest() {
        return req;
    }

    /**
     * Sets the HttpServletRequest object for this request.
     * @param request a HttpServletRequest object, or <code>null</code> if none is available
     */
    protected void setRequest(HttpServletRequest request) {
        req = request;
    }

    /**
     * Returns the HttpServletResponse object for this request.
     * @return a HttpServletResponse object, or <code>null</code> if none is available
     */
    public HttpServletResponse getResponse() {
        return res;
    }

    /**
     * Sets the HttpServletResponse object for this request.
     * @param response a HttpServletResponse object, or <code>null</code> if none is available
     */
    protected void setResponse(HttpServletResponse response) {
        res = response;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public String toString() {
        return "" + req + " " + res + " " + cloud + (cloud != null ? ("" + cloud.getUser()) : "");
    }

}
