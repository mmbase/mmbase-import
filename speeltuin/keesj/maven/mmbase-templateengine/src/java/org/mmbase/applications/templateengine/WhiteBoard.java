/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import javax.servlet.http.*;
import java.util.*;
/**
 * shared shored living object to share data over different templates 
 * @author Kees Jongenburger
 */
public class WhiteBoard {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private Navigation navigation;
    private Facade facade;
    private Properties props = new Properties();

    public WhiteBoard(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }
    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public void setCurrentNavigation(Navigation navigation) {
        this.navigation = navigation;
    }
    
    public Navigation getCurrentNavigation() {
        return navigation;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public Facade getFacade() {
        return facade;
    }

    public Navigation getRootNavigation() {
        return getFacade().getNavigationControl().getNavigation();
    }
    
    public void setProperty(String key , String value){
    	props.setProperty(key,value);
    }
	public String  getProperty(String key){
		return props.getProperty(key);
	}

}