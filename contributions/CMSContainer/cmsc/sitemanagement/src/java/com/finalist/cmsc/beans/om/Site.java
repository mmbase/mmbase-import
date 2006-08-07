/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.beans.om;

import com.finalist.cmsc.navigation.ServerUtil;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Site extends Page {

    private String stagingfragment;

    
    public String getStagingfragment() {
        return stagingfragment;
    }

    
    public void setStagingfragment(String stagingfragment) {
        this.stagingfragment = stagingfragment;
    }
    
    @Override
    public String getUrlfragment() {
        return ServerUtil.isLive() ? super.getUrlfragment() : stagingfragment; 
    }
}
