/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

/**
 * @author keesj
 * @version $Id: NavigationResolver.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 * 
 * when a request comes in the Engine tries to find the navigation that matches the request
 * this is done by delegating to a navigation resolver. the default navigation resolver is the Root navigation controler.
 * some Navigations might be able to resolve the path futher . they then must implements the NavigationResolver  
 */
public interface NavigationResolver {
	public Navigation resolveNavigation(Path path);
}
