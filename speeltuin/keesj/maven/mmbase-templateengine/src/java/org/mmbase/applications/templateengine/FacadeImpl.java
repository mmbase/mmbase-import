/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

/**
 * @author Kees Jongenburger
 */
public class FacadeImpl extends Facade {
	NavigationControl navigationComponent;

	public FacadeImpl(){
		navigationComponent = new XMLNavigationControl();
	}

    public NavigationControl getNavigationControl() {
        return navigationComponent;
    }

}
