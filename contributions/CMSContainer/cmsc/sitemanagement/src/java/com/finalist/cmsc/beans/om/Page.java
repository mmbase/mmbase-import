/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.beans.om;

import java.util.*;

import net.sf.mmapps.commons.beans.NodeBean;


/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Page extends NodeBean implements Comparable {
	
	private String title;
    private String description;
    private String urlfragment;
	private boolean inMenu;
    private boolean secure;
    private Date creationdate;
    private Date lastmodifieddate;
    private Date publishdate;
    private Date expirydate;
    private boolean use_expiry;
    private String lastmodifier;

    private Map<String,Integer> portlets = new HashMap<String,Integer>();
    private int layout;
    private List<Integer> stylesheet = new ArrayList<Integer>();
    private Map<String,String> pageImages = new HashMap<String,String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isInmsenu() {
		return inMenu;
	}

	public void setInmenu(boolean inMenu) {
		this.inMenu = inMenu;
	}

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }
    
    public List<Integer> getStylesheet() {
        return stylesheet;
    }
   
    public void addStylesheet(int stylesheet) {
        this.stylesheet.add(new Integer(stylesheet));
    }
    
    public String getPageImage(String name) {
    	return pageImages.get(name);
    }
    
    public void addPageImage(String name, String image) {
    	pageImages.put(name, image);
    }
    
    public void addPortlet(String layoutId, Integer p) {
        if (p != null) {
            portlets.put(layoutId, p);
        }
    }

    public Integer getPortlet(String layoutId) {
        if (portlets.containsKey(layoutId)) {
            return portlets.get(layoutId);
        }
        return -1;
    }
    
    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public Date getLastmodifieddate() {
        return lastmodifieddate;
    }

    public void setLastmodifieddate(Date lastmodifieddate) {
        this.lastmodifieddate = lastmodifieddate;
    }

    public Date getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(Date publishdate) {
        this.publishdate = publishdate;
    }

    public Date getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(Date expirydate) {
        this.expirydate = expirydate;
    }
    
    public boolean isUse_expiry() {
        return use_expiry;
    }
    
    public void setUse_expiry(boolean use_expiry) {
        this.use_expiry = use_expiry;
    }

    public String getLastmodifier() {
        return lastmodifier;
    }

    public void setLastmodifier(String lastmodifier) {
        this.lastmodifier = lastmodifier;
    }
    
    public boolean isSecure() {
        return secure;
    }

    
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    
    public String getUrlfragment() {
        return urlfragment;
    }

    
    public void setUrlfragment(String urlfragment) {
        this.urlfragment = urlfragment;
    }
    
    public int compareTo(Object o) {
        return title.compareTo(((Page) o).title);
    }

}
