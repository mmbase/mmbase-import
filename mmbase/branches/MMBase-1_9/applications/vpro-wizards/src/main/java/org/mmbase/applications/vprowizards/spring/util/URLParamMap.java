/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.util;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;


/**
 * @javadoc
 * @version $Id$
 * @author Ernst Bunders
 */

public class URLParamMap extends LinkedHashMap<String, String> {

    private String URLBase;

    public URLParamMap(String url) {
        url = url.trim();
        if (url.indexOf("?") > -1) {
            URLBase = url.substring(0, url.indexOf("?") + 1);
            String queryString = url.substring(url.indexOf("?") + 1).trim();
            if (!StringUtils.isBlank(queryString)) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    String[] p = param.split("=");
                    if (p.length == 2) {
                        put(p[0], p[1]);
                    }else if(p.length == 1){
                        put(p[0], "");
                    }
                }
            }
        } else {
            URLBase = url;
        }
    }

    /**
     * Adds a param to the existing params
     *
     * @param name
     *            the parameter name
     * @param value
     *            the parameter value
     * @param override
     *            should an existing parameter by this name be overridden?
     */
    public URLParamMap addParam(String name, String value, boolean override) {
        if ((get(name) != null && override) || get(name) == null) {
            put(name, value);
        }
        return this;
    }

    public String toString() {
        StringBuilder result = new StringBuilder(URLBase);
        if (size() > 0) {
            boolean first = true;
            for (String key : this.keySet()) {
                if (first) {
                    if(URLBase.indexOf("?") == -1){
                        result.append("?");
                    }
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(key);
                result.append("=");
                result.append(get(key));
            }
        }
        return result.toString();
    }

}
