/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.Cookie;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like UrlTag, but does not spit out an URL, but the page itself.
 * 
 * @author Michiel Meeuwissen
 */
public class IncludeTag extends UrlTag {

    private static Logger log = Logging.getLoggerInstance(IncludeTag.class.getName()); 

    public int doAfterBody() throws JspTagException {
        if (page == null) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
    	return includePage();
    }

    protected int includePage() throws JspTagException {

        try {
            bodyContent.clear(); // newlines and such must be removed            

            String gotUrl = getUrl(false);// false: don't write &amp; tags but real &.
            // if not absolute, make it absolute:
            // (how does one check something like that?)
            String urlString;

            // Do some things to make the URL absolute.            
            String nudeUrl; // url withouth the params
            String params;  // only the params.
            int paramsIndex = gotUrl.indexOf('?');
            if (paramsIndex != -1) {
                nudeUrl = gotUrl.substring(0, paramsIndex);
                params  = gotUrl.substring(paramsIndex);
            } else {
                nudeUrl = gotUrl;
                params  = "";
            }

            javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();

            if (nudeUrl.indexOf('/') == 0) { // absolute on server                             
                urlString = 
                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    +  request.getContextPath() + nudeUrl + params;
            } else if (nudeUrl.indexOf(':') == -1) { // relative
                urlString = 
                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + new java.io.File(new java.io.File(request.getRequestURI()).getParent().toString() + "/" + nudeUrl + params).getCanonicalPath();
            } else { // really absolute
                urlString = gotUrl;
            }
                           
            if (log.isDebugEnabled()) log.debug("found url: >" + urlString + "<");
    	    URL includeURL = new URL(urlString); 
            
            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();

            // Also propagate the cookies (like the jsession...)
            // Then these, and the session,  also can be used in the include-d page
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String koekjes = "";
                for (int i=0; i < cookies.length; i++) {
                    if (log.isDebugEnabled()) {
                        log.debug("setting cookie " + i + ":" + cookies[i].getName() + "=" + cookies[i].getValue());
                    }
                    koekjes += (i > 0 ? ";" : "") + cookies[i].getName() + "=" + cookies[i].getValue(); 
                }                     
                connection.setRequestProperty("Cookie", koekjes); 
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader (connection.getInputStream()));
            
            String line = null;
            while ((line = in.readLine()) != null ) {
                bodyContent.println(line);
            }
	    
            if (getId() != null) {
                getContextTag().register(getId(), bodyContent.getString());
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }        
        return SKIP_BODY;
    }
}
