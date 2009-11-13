/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import java.util.HashMap;
import java.util.Map;

import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.ResultContainer;

/**
 * This is a test action bean. It is created to test the databinding.
 * @author Ernst Bunders
 *
 */
public class TestAction extends Action {
	
	private Map<String,String> fields = new HashMap<String, String>();
	private boolean error;
	private String name = "testAction";
	private ResultContainer resultContainer;

	public Map<String,String> getFields() {
		return fields;
	}

	public void setFields(Map<String,String> fields) {
		this.fields = fields;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void process(ResultContainer resultContainer) {
		this.resultContainer = resultContainer;
		if(isError()){
			resultContainer.getGlobalErrors().add(new GlobalError(getName(), resultContainer.getLocale()));
		}
		resultContainer.getRequest().setAttribute("test", this);
		resultContainer.getRequest().setAttribute("result", resultContainer);
	}

	public ResultContainer getResultContainer() {
		return resultContainer;
	}

}
