package com.finalist.cmsc.beans.om;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.commons.beans.NodeBean;


/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Portlet extends NodeBean implements Comparable {
	
	private String title;

	private int definition;
	
	private List portletparameters = new ArrayList();

	private List<Integer> views = new ArrayList<Integer>();

	public int getDefinition() {
		return definition;
	}

	public void setDefinition(int definition) {
		this.definition = definition;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getView() {
        if (views.isEmpty()) {
            return -1;
        }
		return views.get(0).intValue();
	}

	public void addView(int view) {
		this.views.add(new Integer(view));
	}

	public List getPortletparameters() {
		return portletparameters;
	}

	public void addPortletparameter(PortletParameter parameter) {
		this.portletparameters.add(parameter);
	}
    
    public void addPortletparameter(NodeParameter parameter) {
        this.portletparameters.add(parameter);
    }

    public int compareTo(Object o) {
        return title.compareTo(((Portlet) o).title);
    }
}
