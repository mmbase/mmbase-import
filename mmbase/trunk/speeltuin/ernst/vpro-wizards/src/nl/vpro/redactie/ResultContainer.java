package nl.vpro.redactie;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.vpro.redactie.cache.CacheFlushHint;

/**
 * De wizardservice geeft deze container als resultaat terug.
 * Het bevat een lijstje met errors (als deze zijn opgetreden),
 * en het geeft aan welk object nieuw is aangemaakt (zodat mogelijke
 * een pagina geopend kan worden met dat nieuwe object).
 *
 * @author Rob Vermeulen (VPRO)
 */
public class ResultContainer {
	private Vector<FieldError> errors = new Vector<FieldError>();
	private String newObject = null;
    private List<CacheFlushHint> cacheFlushHints = new ArrayList<CacheFlushHint>();

	HttpServletRequest request;
	HttpServletResponse response;


	public ResultContainer(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public Vector<FieldError> getErrors() {
		return errors;
	}

	public void setErrors(Vector<FieldError> errors) {
		this.errors = errors;
	}

	public String getNewObject() {
		return newObject;
	}

	public void setNewObject(String newObject) {
		this.newObject = newObject;
	}

	public boolean containsErrors() {
		return !errors.isEmpty();
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
}