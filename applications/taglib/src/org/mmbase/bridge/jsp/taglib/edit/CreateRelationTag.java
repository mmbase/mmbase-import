/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.Relation;

import org.mmbase.bridge.jsp.taglib.CloudProvider;
import org.mmbase.bridge.jsp.taglib.ContextTag;
import org.mmbase.bridge.jsp.taglib.NodeTag;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* A tag lib to create relations. 
*
* @author Michiel Meeuwissen
*/
public class CreateRelationTag extends NodeTag {
    
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
        RelationManager rm = getCloudProviderVar().getRelationManager(role);
        ContextTag    con = getContextTag();
        Node sourceNode      = con.getNode(source);
        Node destinationNode = con.getNode(destination);        

        if (log.isDebugEnabled()) {
            log.debug("cloud from relationmanager " + rm.getCloud().getName());
            log.debug("cloud from source node " + sourceNode.getCloud().getName());
            log.debug("cloud from dest node " + destinationNode.getCloud().getName());
        }

        Relation r = rm.createRelation(sourceNode, destinationNode);
        r.commit();

        setNodeVar(r);
        return EVAL_BODY_TAG; 
    }
    
}
