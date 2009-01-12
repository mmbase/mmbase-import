/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.cache.CacheHandlerInterceptor;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class WizardController implements Controller {

	private static final Logger log = Logging.getLoggerInstance(WizardController.class);
	private CommandFactory commandFactory;
	private CloudFactory cloudFactory;
	/**
	 * this class helps to resolve the return view. It produces an url that can
	 * be used with a RedirectView instance. Two things are wrong with this:
	 * TODO:make it that a ReturnViewResolver returns a View, and not an url.
	 * TODO:it should be possible to map different resolvers to different
	 * request types. i think of: html, xml and json. The first one works like
	 * the present, the second and third should generate xml or json response,
	 * and set http headers when an error has occurred. They are for ajax type
	 * requests.
	 */
	private ModelAndViewResolver viewResolver;
	
	private Locale locale;
	
	public WizardController(){
		setLocale(new Locale("nl"));/*default*/
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// TODO: this should not happen this way
		request.setCharacterEncoding("UTF-8");

		Command command = commandFactory.getNewInstance();
		Transaction transaction = cloudFactory.createTransaction(request);
		
		if(log.isDebugEnabled()){
			log.debug("*********************************");
			log.debug("Processing new request with transaction ["+transaction.getName()+"]");
			log.debug("*********************************");
		}

		// do the data binding
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
		binder.bind(request);

		// process all the actions.
		ResultContainer resultContainer = new ResultContainer(request, response, transaction, locale);
		command.processActions(request, response, resultContainer);

		if (resultContainer.hasGlobalErrors() || resultContainer.hasFieldErrors()) {
			log.debug("Errors found, transaction not committed.");
			transaction.cancel();
			
		} else {
			log.debug("No errors found. Commit transaction ["+transaction.getName()+"] and put the cache flush hints on the request.");
			resultContainer.getTransaction().commit();
			

			// create the request type cache flush hint
			// TODO: maybe this type of cache flush hint is totally useless. do
			// we need it at all?
			resultContainer.addCacheFlushHint(new CacheFlushHint(CacheFlushHint.TYPE_REQUEST));

			// set all the cache flush hints in the request.
			request.setAttribute(CacheHandlerInterceptor.PARAM_NAME, resultContainer.getCacheFlushHints());

		}

		// return the proper view.
		return viewResolver.getModelAndView(request, resultContainer);

	}


	public CommandFactory getCommandFactory() {
		return commandFactory;
	}

	public Locale getLocale() {
		return locale;
	}


	public CloudFactory getCloudFactory() {
		return cloudFactory;
	}

	public ModelAndViewResolver getViewResolver() {
		return viewResolver;
	}

	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public void setCloudFactory(CloudFactory cloudFactory) {
		this.cloudFactory = cloudFactory;
	}

	public void setViewResolver(ModelAndViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

}
