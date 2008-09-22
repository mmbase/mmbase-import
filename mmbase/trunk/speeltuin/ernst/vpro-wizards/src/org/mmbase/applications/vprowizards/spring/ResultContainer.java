package org.mmbase.applications.vprowizards.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.bridge.Cloud;
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
	private List<String> newObjects = new ArrayList<String>(5);
    private List<CacheFlushHint> cacheFlushHints = new ArrayList<CacheFlushHint>();
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

	public List<String> getNewObject() {
		return newObjects;
	}

	public void addNewObject(String newObject) {
		this.newObjects.add(newObject);

	}
	
	public void addGlobalError(GlobalError e){
		if(log.isDebugEnabled()){
			log.debug("adding global error: "+e);
		}
		globalErrors.add(e);
	}
	
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
