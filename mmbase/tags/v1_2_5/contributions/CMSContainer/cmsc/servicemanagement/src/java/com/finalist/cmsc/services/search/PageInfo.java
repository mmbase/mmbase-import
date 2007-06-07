/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

public class PageInfo {
    
    private int pageNumber;
    private String path;
    private String windowName;
    private String layout;

    private int priority = -1;
    private String parametername;
    private boolean isSite;

    public PageInfo(int pageNumber, String path, String windowName, String layout, 
            int priority, String parametername, boolean isSite) {
        this.pageNumber = pageNumber;
        this.path = path;
        this.windowName = windowName;
        this.layout = layout;
        this.priority = priority;
        this.parametername = parametername;
        this.isSite = isSite;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getWindowName() {
        return windowName;
    }

    public String getLayout() {
        return layout;
    }
    
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getParametername() {
        return parametername;
    }

    public void setParametername(String parametername) {
        this.parametername = parametername;
    }
    
    public boolean isSite() {
        return isSite;
    }

    public void setSite(boolean isSite) {
        this.isSite = isSite;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + pageNumber;
        result = PRIME * result + ((windowName == null) ? 0 : windowName.hashCode());
        return result;
}
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final PageInfo other = (PageInfo) obj;
        if (pageNumber != other.pageNumber) return false;
        if (windowName == null) {
            if (other.windowName != null) return false;
        }
        else
            if (!windowName.equals(other.windowName)) return false;
        return true;
    }

}