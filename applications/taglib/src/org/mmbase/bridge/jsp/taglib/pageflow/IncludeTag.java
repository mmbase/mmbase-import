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

    protected int DEBUG_NONE=0;
    protected int DEBUG_HTML=1;
    protected int DEBUG_CSS=2;
    
    protected int debugtype = 0;
    
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
	    if (log.isDebugEnabled()) log.debug("Found nude url " + nudeUrl);
            javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();

            if (nudeUrl.indexOf('/') == 0) { // absolute on server                             
                urlString = 
                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    +  request.getContextPath() + nudeUrl + params;
            } else if (nudeUrl.indexOf(':') == -1) { // relative
		log.debug("URL was relative");
                urlString =
		    // find the parent directory of the relativily given URL:
		    // parent-file: the directory in which the current file is.
		    // nude-Url   : is the relative path to this dir
		    // canonicalPath: to get rid of /../../ etc
		    // replace:  windows uses \ as path seperator..., but they may not be in URL's.. 
		    new java.io.File(new java.io.File(request.getRequestURI()).getParentFile(), nudeUrl).getCanonicalPath().toString().replace('\\', '/');

		// getCanonicalPath gives also gives c: in windows:
		// take it off again if necessary:
		int colIndex = urlString.indexOf(':');
		if (colIndex == -1) {
		    urlString += params;
		} else {
		    urlString = urlString.substring(colIndex+1) + params;
		}
		urlString =     // add basic absolute URL part
                    request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
		    urlString;	      
            } else { // really absolute
                urlString = gotUrl;
            }
                           
            if (log.isDebugEnabled()) log.debug("found url: >" + urlString + "<");

            debugStart(urlString);
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
	   
	    int buffersize=10240;
	    char[] buffer = new char[buffersize];
	    StringBuffer string = new StringBuffer();
	    int len=0;
	    while ((len = in.read(buffer,0,buffersize))!=-1) {
		    string.append(buffer,0,len);
	    }
            bodyContent.print(string);
            
            if (getId() != null) {
                getContextTag().register(getId(), bodyContent.getString());
            }
            debugEnd(urlString);
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }        
        return SKIP_BODY;
    }
    
    public void setDebug(String p) throws JspTagException {
        String dtype = getAttributeValue(p); 
        if (dtype.toLowerCase().equals("html"))
            debugtype = DEBUG_HTML;
        if (dtype.toLowerCase().equals("css"))
            debugtype = DEBUG_CSS;
    }
    
    protected void debugStart(String url) {
        try {
            if (debugtype == DEBUG_HTML) {
                bodyContent.write("\n<!-- Include page = '" + url + "' -->\n");
            }
            if (debugtype == DEBUG_CSS) {
                bodyContent.write("\n/* Include page = '" + url + "' */\n");
            }
        }  catch (Exception e) {}
    }
     
    protected void debugEnd(String arg) {
        try {
            if (debugtype == DEBUG_HTML) {
                bodyContent.write("\n<!-- END Include page = '" + arg + "' -->\n");
            }
            if (debugtype == DEBUG_CSS) {
                bodyContent.write("\n/* END Include page = '" + arg + "' */\n");
            }
        } catch (Exception e) {}
    }
}
