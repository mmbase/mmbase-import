/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.util;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

    public static String getParam(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }

    public static boolean getParam(HttpServletRequest request, String paramName, boolean def) {
        String b = getParam(request, paramName);
        if (b == null) return def;
        return Boolean.valueOf(b).booleanValue();
    }

    public static Boolean getParam(HttpServletRequest request, String paramName, Boolean def) {
        String b = getParam(request, paramName);
        if (b == null) return def;
        return Boolean.valueOf(b);
    }

    public static int getParam(HttpServletRequest request, String paramName, int def) {
        String i = getParam(request, paramName);
        if (i == null || i.equals("")) return def;
        return new Integer(i).intValue();
    }

    public static Integer getParam(HttpServletRequest request, String paramName, Integer def) {
        String i = getParam(request, paramName);
        if (i == null || i.equals("")) return def;
        return new Integer(i);
    }

    public static String getParam(HttpServletRequest request, String paramName, String defaultValue) {
        String value = getParam(request, paramName);
        if (value == null) value = defaultValue;
        return value;
    }
    
}
