package org.mmbase.applications.vprowizards.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
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

		String newPage;
		Map<String, Object> model = new HashMap<String, Object>();
		
		//mainly for testing?
		model.put("idmap", result.getIdMap());

		List<GlobalError> globalErrors = result.getGlobalErrors();
		if (result.hasGlobalErrors()) {
			model.put("globalerrors", globalErrors);
			return new ModelAndView(errorPage, model);
		} 
		
		if (result.hasFieldErrors()) {
			model.put("fielderrors", result.getFieldErrors());
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

		newPage = request.getHeader("Referrer");
		if(newPage == null){
			//this is an error for this view resolver
			globalErrors.add(new GlobalError("error.no.referrer.header", result.getLocale()));
			model.put("globalerrors", globalErrors);
			log.error("REFERRER NOT SET! This request's redirection wil fail.");
			return new ModelAndView(errorPage, model);
		}
		// add the node number of the new object to the referer url.
		if (result.getNewObject().size() > 0) {
			if (log.isDebugEnabled()) {
				log.debug("new object created.");
			}

			// if (referer.indexOf('?') == -1) {
			// newPage = referer + "?nodenr=" + result.getNewObject();
			// } else {
			// newPage = referer + "&nodenr=" + result.getNewObject();
			// }

			// if we put the new node in the model, it should be added to the query string for redirect views.
			model.put("nodenr", result.getNewObject().get(0));
		}

		return new ModelAndView(new RedirectView(newPage), model);
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

}
