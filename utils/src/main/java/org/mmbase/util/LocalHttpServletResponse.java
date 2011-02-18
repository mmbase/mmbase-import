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

    @Override
    public void  addCookie(Cookie cookie) {
        // TODO
    }
    @Override
    public void  addDateHeader(String name, long date) {
        // TODO
        headers.put(name, "" + date);
    }

    @Override
    public void  addHeader(String name, String value) {
        headers.put(name, value);
    }
    @Override
    public void  addIntHeader(String name, int value) {
        headers.put(name, "" + value);
    }
    @Override
    public boolean  containsHeader(String name) {
        return headers.containsKey(name);
    }
    @Override
    @Deprecated
    public String  encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }
    @Override
    public String  encodeRedirectURL(String url) {
        return url;
    }
    @Override
    @Deprecated
    public String  encodeUrl(String url) {
        return encodeURL(url);
    }
    @Override
    public String  encodeURL(String url) {
        return url;
    }
    @Override
    public void  sendError(int sc) {
        status = sc;
    }

    @Override
    public void  sendError(int sc, String msg) {
        status = sc;
    }
    @Override
    public void  sendRedirect(String location) {
        // TODO
    }
    @Override
    public void  setDateHeader(String name, long date) {
        // TODO
        headers.put(name, "" + date);
    }
    @Override
    public void  setHeader(String name, String value) {
        headers.put(name, value);
    }
    @Override
    public void  setIntHeader(String name, int value) {
        headers.put(name, "" + value);
    }

    @Override
    public void  setStatus(int sc) {
        status = sc;
    }
    @Override
    @Deprecated
    public  void  setStatus(int sc, String sm) {
        sendError(sc, sm);
    }


}
