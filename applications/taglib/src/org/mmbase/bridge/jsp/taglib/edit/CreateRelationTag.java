/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.BodyContent;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.Relation;

import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.CloudProvider;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
* A tag lib to create relations. 
*
* @author Michiel Meeuwissen
*/
public class CreateRelationTag extends CloudReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(CreateRelationTag.class.getName());
    
    private String role;
    private String source;
    private String destination;
    
    public void setRole(String r){
        role = r;
    }
    
    public void setSource(String s) {
        source = s;
    }
    public void setDestination(String d) {
        destination = d;        
    }
       
    public int doStartTag() throws JspTagException{            
        CloudProvider c = findCloudProvider();
        RelationManager rm = c.getCloudVar().getRelationManager(role);
        Node sourceNode      = c.getNode(source);
        Node destinationNode = c.getNode(destination);
        
        Relation r = rm.createRelation(sourceNode, destinationNode);
        r.commit();
        return SKIP_BODY;
    }
    
}
