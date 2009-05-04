/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;


public class LocalHttpServletResponse extends LocalServletResponse implements HttpServletResponse {

    private Map<String, String> headers = new HashMap<String, String>(); // TODO a map probably
                                                                         // does not suffice
    private int status;

    public LocalHttpServletResponse(Writer w) {
        super(w);
    }

    public LocalHttpServletResponse(OutputStream output) {
        super(output);
    }

    public void  addCookie(Cookie cookie) {
        // TODO
    }
    public void  addDateHeader(String name, long date) {
        // TODO
        headers.put(name, "" + date);
    }

    public void  addHeader(String name, String value) {
        headers.put(name, value);
    }
    public void  addIntHeader(String name, int value) {
        headers.put(name, "" + value);
    }
    public boolean  containsHeader(String name) {
        return headers.containsKey(name);
    }
    @Deprecated public String  encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }
    public String  encodeRedirectURL(String url) {
        return url;
    }
    @Deprecated public String  encodeUrl(String url) {
        return encodeURL(url);
    }
    public String  encodeURL(String url) {
        return url;
    }
    public void  sendError(int sc) {
        status = sc;
    }

    public void  sendError(int sc, String msg) {
        status = sc;
    }
    public void  sendRedirect(String location) {
        // TODO
    }
    public void  setDateHeader(String name, long date) {
        // TODO
        headers.put(name, "" + date);
    }
    public void  setHeader(String name, String value) {
        headers.put(name, value);
    }
    public void  setIntHeader(String name, int value) {
        headers.put(name, "" + value);
    }

    public void  setStatus(int sc) {
        status = sc;
    }
    @Deprecated public  void  setStatus(int sc, String sm) {
        sendError(sc, sm);
    }

}
