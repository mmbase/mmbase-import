/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

/**
 * Any number of objects of this type can be added to the WizardController.
 * They will be executed in chained fashon, in order to resolve where the request should
 * be directed to. This is a nice way to implement your own specialized behavour.
 * @author ebunders
 *
 */
public interface ModelAndViewResolver {
	public ModelAndView getModelAndView(HttpServletRequest request, ResultContainer resultContainer);
}
