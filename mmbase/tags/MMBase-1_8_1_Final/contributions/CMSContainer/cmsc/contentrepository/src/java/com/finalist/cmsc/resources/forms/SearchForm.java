package com.finalist.cmsc.resources.forms;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.mmbase.storage.search.SortOrder;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SearchForm extends ActionForm {
    
	private String contenttypes;
	private List results = new ArrayList();
    private String offset;
	private int resultCount;
	private String order;
	private int direction;
	private String objectid;

	public ActionErrors validate(ActionMapping actionMapping, javax.servlet.http.HttpServletRequest httpServletRequest) {
		// ensure valid direction
		if (direction != SortOrder.ORDER_DESCENDING) {
			direction = SortOrder.ORDER_ASCENDING;
		}

		// set default order field
		if (StringUtil.isEmpty(order)) {
			order = "title";
		}
		

		return super.validate(actionMapping, httpServletRequest);
	}

	public String getContenttypes() {
		return contenttypes;
	}

	public void setContenttypes(String contenttypes) {
		this.contenttypes = contenttypes;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}

	public String getOffset() {
		return offset;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

}
