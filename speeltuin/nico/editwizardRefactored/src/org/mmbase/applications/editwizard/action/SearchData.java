package org.mmbase.applications.editwizard.action;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */

/**
 * this class is used to store data which will be construct the dom used to 
 * produce final list html.
 * 
 */
public class SearchData {

    NodeList listResults = null;
    
    // the init
    int initStart = 0;
    int pageMaxSize = 0;
    int totalResultsSize = 0;
    
    public int getResultSize() {
        if (listResults==null) {
            return 0;
        }
        return listResults.size();
    }
    
    public int getTotalResultsSize() {
        return totalResultsSize;
    }
    
    /**
     * calculate current page number. 
     * @return
     */
    public int getCurrentPage(){
        int offsetStart = getOffsetStart();
        int currentPage = offsetStart / pageMaxSize;
        return currentPage;
    }
    
    public int getPageCount() {
        int pageCount = totalResultsSize / pageMaxSize;
        if (totalResultsSize % pageMaxSize > 0) {
            pageCount++;
        }
        return pageCount;
    }
    
    /**
     * get start number of the current page.
     * 
     * <pre>
     *  if (initStart &gt; totalResultSize-1) {
     *    start = totalResultSize-1
     *  }
     *  if (initStart &lt; 0) {
     *    start = 0;
     *  }
     * </pre>
     * 
     * @return
     */
    public int getStart() {
        int pageStart = initStart;
        pageStart = Math.min(pageStart,totalResultsSize-1);
        pageStart = Math.max(pageStart,0);
        return pageStart;
    }
    
    /**
     * get start number.
     * @return
     */
    public int getOffsetStart() {
        return getStart();
    }
    
    public int getOffsetEnd() {
        int offsetEnd = getStart();
        /*
        pageEnd += pageMaxSize;
        if (pageEnd > totalResultsSize)
            pageEnd = totalResultsSize;
        */
        offsetEnd += getResultSize();
        return offsetEnd;
    }
    
    public Node getResultNode(int i) {
        if (listResults==null) {
            return null;
        }
        return listResults.getNode(i);
    }
    
    public int getPageOffset() {
        int pageOffset = 0;
        int start = this.getStart();
        pageOffset = start / this.pageMaxSize - (this.pageMaxSize / 2);
        if (pageOffset < 0) {
            pageOffset = 0;
        }
        int pageCount = this.getPageCount();
        if (pageOffset + this.pageMaxSize > pageCount) {
            pageOffset = pageCount - this.pageMaxSize;
            if (pageOffset < 0) {
                pageOffset = 0;
            }
        }
        return pageOffset;
    }
    
}
