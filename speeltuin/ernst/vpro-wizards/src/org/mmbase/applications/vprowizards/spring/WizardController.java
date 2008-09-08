package org.mmbase.applications.vprowizards.spring;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.cache.CacheHandlerInterceptor;
import org.mmbase.bridge.Node;
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
		setLocale(new Locale("nl-NL"));
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
		Transaction transaction = cloudFactory.getTransaction(request);
		Map<String, Node> nodeMap = new HashMap<String, Node>();

		// do the data binding
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command);
		binder.bind(request);

		// process all the actions.
		ResultContainer resultContainer = new ResultContainer(request, response, transaction, locale);
		command.processActions(request, response, resultContainer);

		if (resultContainer.hasGlobalErrors() || resultContainer.hasFieldErrors()) {
			log.debug("Errors found, transaction not committed.");
			
		} else {
			log.debug("No errors found. Commit the transaction and put the cache flush hints on the request.");
			transaction.commit();

			// create the request type cache flush hint
			// TODO: maybe this type of cache flush hint is totally useless. do
			// we need it at all?
			resultContainer.addCacheFlushHint(new CacheFlushHint(CacheFlushHint.TYPE_REQUEST));

			// set all the cache flush hints in the request.
			request.setAttribute(CacheHandlerInterceptor.PARAM_NAME, resultContainer.getCacheFlushHints());

			// Are there new objects?
			//TODO: this is wrong. hard coding that the id for new nodes is 'new' that way only one new node can be created. So: for now we must create an error when there are more create actions than one.
			if (nodeMap.containsKey("new")) {
				int number = nodeMap.get("new").getNumber();
				resultContainer.addNewObject("" + number);
			}
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

	public void setLocale(Locale locale2) {
		this.locale = locale2;
	}


	public void setCloudFactory(CloudFactory cloudFactory) {
		this.cloudFactory = cloudFactory;
	}

	public void setViewResolver(ModelAndViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

}
