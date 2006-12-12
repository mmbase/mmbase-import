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
    
    public PageInfo(int pageNumber, String path, String windowName) {
        this.pageNumber = pageNumber;
        this.path = path;
        this.windowName = windowName;
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
}