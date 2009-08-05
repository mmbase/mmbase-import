/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.ParamValueResolver;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Transaction;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;




/**
 * this class acts as a wrapper for everything that needs to be passed to all
 * the actions that need execution for a request. 
 *
 * @author Rob Vermeulen (VPRO)
 * @author Ernst Bunders
 */
public class ResultContainer {
	private List<FieldError> fieldErrors = new ArrayList<FieldError>();
	private List<GlobalError> globalErrors = new ArrayList<GlobalError>();
    private List<CacheFlushHint> cacheFlushHints = new ArrayList<CacheFlushHint>();
    private Map<String,ParamValueResolver> extraParams = new LinkedHashMap<String, ParamValueResolver>();  
    private Transaction transaction = null;
    private Locale locale;
    private static final Logger log = Logging.getLoggerInstance(ResultContainer.class);

	HttpServletRequest request;
	HttpServletResponse response;
	private Map<String,Node>idMap = new HashMap<String, Node>();


	public ResultContainer(HttpServletRequest request, HttpServletResponse response, Transaction transaction, Locale locale) {
		this.request = request;
		this.response = response;
		this.transaction = transaction;
		this.locale = locale;
	}

	public List<FieldError> getFieldErrors() {
		return fieldErrors;
	}
	
	public List<GlobalError> getGlobalErrors() {
		return globalErrors;
	}

	/**
	 * Add a parameter to the url the request is forwarded to.
	 * this should be done by the {@link ModelAndViewResolver}.
	 * @param name the name of the parameter
	 * @param valueResolver
	 */
	public void addParamToReturnURL(String name, ParamValueResolver valueResolver){
	    extraParams.put(name, valueResolver);
	}
	
	/**
	 * Add a parameter to the url the request is forwarded to.
     * this should be done by the {@link ModelAndViewResolver}.
     * Use this method if you want to add the number of a newly created node to the
     * request. This is done by the action class, but the node is only commited yet when the 
     * action is executed. So we need the node to get the number after the transaction is committed.
	 * @param name the name of the parameter
	 * @param node the  number of this node will be the value of the param
	 */
	public void addParamToReturnURL(String name, final Node node){
        extraParams.put(name, new ParamValueResolver(){
            public String getValue() {
                return ""+node.getNumber();
            }
        });
    }
	
	/**
	 * Add a parameter to the url the request is forwarded to.
     * this should be done by the {@link ModelAndViewResolver}.
	 * @param name the name of the parameter
	 * @param value the value for this new parameter
	 */
	public void addParamToReturnURL(String name, final String value){
        extraParams.put(name, new ParamValueResolver(){
            public String getValue() {
                return value;
            }});
    }
	
	
	/**
	 * @return a map of parameters that should be added to the return request.
	 */
	public Map<String,String>getExtraParams(){
	    Map<String,String> p = new LinkedHashMap<String, String>();
	    for (String pname: extraParams.keySet()){
	        p.put(pname, extraParams.get(pname).getValue());
	    }
	    return p;
	}
	
	/**
	 * Add a global error to this request. Global errors will cause the request to
	 * be redirected to the error page.
	 * @param e
	 */
	public void addGlobalError(GlobalError e){
		if(log.isDebugEnabled()){
			log.debug("adding global error: "+e);
		}
		globalErrors.add(e);
	}
	
	/**
	 * Add a field error to this request. Field errors will be displayed in the page that 
	 * caused the error and should be displayed in this context. Mainly for validation. 
	 * @param e
	 */
	public void addFieldError(FieldError e){
		if(log.isDebugEnabled()){
			log.debug("adding field error: "+e);
		}
		fieldErrors.add(e);
	}
	

	public boolean hasFieldErrors() {
		return !fieldErrors.isEmpty();
	}
	public boolean hasGlobalErrors() {
		return !globalErrors.isEmpty();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

    public void addCacheFlushHint(CacheFlushHint hint){
        cacheFlushHints.add(hint);
    }

    public List<CacheFlushHint> getCacheFlushHints(){
        return cacheFlushHints;
    }

	public Map<String, Node> getIdMap() {
		return idMap;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public Locale getLocale() {
		return locale;
	}

	
}
