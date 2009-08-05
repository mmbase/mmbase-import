/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.util.URLParamMap;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This return page resolver will return the referrer url wiht a parameter 'nodenr' added when there is a new node in
 * the result container. if there is an error it will return the value of the errorPage field. TODO: make sure the error
 * page exists and shows the errors well. only global errors should trigger the error page. field errors should be shown
 * in the editor.
 * 
 * @author ebunders
 * 
 */
public class ReferrerResolver implements ModelAndViewResolver {
	private static Logger log = Logging.getLoggerInstance(ReferrerResolver.class);
	private String errorPage;

	public ModelAndView getModelAndView(HttpServletRequest request, ResultContainer result) {

		String callerPage;
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("idmap", result.getIdMap());
		ModelAndView errorMandv = new ModelAndView(errorPage);
		

		List<GlobalError> globalErrors = result.getGlobalErrors();
		if (result.hasGlobalErrors()) {
		    errorMandv.addObject(GlobalError.MODEL_MAPPING_KEY, globalErrors);
			log.debug("request has global errors, so the return page is: "+errorPage);
			return errorMandv;
		} 
		
		if (result.hasFieldErrors()) {
			model.put(FieldError.MODEL_MAPPING_KEY, result.getFieldErrors());
			// Field errors are not displayed in the error page but in the referrer page (the form)
		}

		// has a new object been created?
		// String newObject = result.getNewObjects();
		// set the new object in the request (why?)
		// if (newObject != null) {
		// request.setAttribute("newObject", newObject);
		// if (log.isDebugEnabled()) {
		// log.debug("object number " + newObject);
		// }
		// }
		callerPage = request.getHeader("referer");
		if(callerPage == null){
			//this is an error for this view resolver
			globalErrors.add(new GlobalError("error.no.referrer.header", result.getLocale()));
			log.error("REFERRER NOT SET! This request's redirection wil fail.");
            errorMandv.addObject(GlobalError.MODEL_MAPPING_KEY, globalErrors);
			return errorMandv;
		}
		// add the node number of the new object to the referer url.
		
//		if (result.getNewObject().size() > 0) {
//			if (log.isDebugEnabled()) {
//				log.debug("new object created.");
//			}
//			String newNodeNr = result.getNewObject().get(0);
//			//newPage = newPage.substring(0, newPage.indexOf("?") + 1) + "nodenr=" + newNodeNr;
//			String newPage = new URLParamMap(callerPage).addParam("nodenr", newNodeNr, true).toString();
//		}
		
		URLParamMap u = new URLParamMap(callerPage);
		if(result.getExtraParams().size() > 0){
		    for(String param: result.getExtraParams().keySet()){
		        u.addParam(param, result.getExtraParams().get(param), true);
		    }
		}
		
		RedirectView redirectView = new RedirectView(u.toString());
        return new ModelAndView(redirectView, model);
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

}
