/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.select.forms;

@SuppressWarnings("serial")
public class SelectorLinkActionMapping extends SelectorActionMapping {

    private String resource;
    private String target;
    
    protected String getLinkPattern() {
        return resource + "?parentchannel={0}"; 
    }

    protected String getTarget() {
        return target;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }

    
    public void setTarget(String target) {
        this.target = target;
    }

    
    public String getResource() {
        return resource;
    }

}
