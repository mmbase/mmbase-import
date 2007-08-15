/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.search;

import java.util.Comparator;


public class PageInfoComparator implements Comparator<PageInfo> {

    private static final String CONTENTELEMENT = "contentelement";

    public int compare(PageInfo info1, PageInfo info2) {
        int priority = info1.getPriority() - info2.getPriority();
        if (priority != 0) {
           return - priority;
        }
        else {
            int parameter = 0;
            if (CONTENTELEMENT.equals(info1.getParametername())) {
                if (!CONTENTELEMENT.equals(info2.getParametername())) {
                    parameter = -1;
                }
            }
            else {
                if (CONTENTELEMENT.equals(info2.getParametername())) {
                    parameter = 1;
                }
            }
         
            if (parameter != 0) {
                return parameter;
            }
            else {
                if (info1.isSite()) {
                    if (!info2.isSite()) {
                        return 1;
                    }
                }
                else {
                    if (info2.isSite()) {
                        return -1;
                    }
                }
            }
        }
        
        return 0;
    }

}
