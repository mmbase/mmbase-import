/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * Needs to live under a ListRelationsTag
 *
 * @author Michiel Meeuwissen
 */
public class RelatedNodeTag extends AbstractNodeProviderTag implements BodyTag {

    private static Logger log = Logging.getLoggerInstance(RelatedNodeTag.class.getName());

    private String listRelationsId = null;

    public void release() {
        super.release();
        listRelationsId = null;
    }

    public void setListrelations(String l) {
        listRelationsId = l;
    }

    public int doStartTag() throws JspTagException{
        Node node = null;
        
        // get the parent ListRelationsTag
        ListRelationsTag lr = (ListRelationsTag) findParentTag("org.mmbase.bridge.jsp.taglib.ListRelationsTag", listRelationsId);
        Node otherNode    = lr.getRelatedfromNode();
        Node relationNode = lr.getNodeVar();

        int number;
        // now look what node would have been.
        if (otherNode.getNumber() == relationNode.getIntValue("dnumber")) {
            number = relationNode.getIntValue("snumber");
        } else {
            number = relationNode.getIntValue("dnumber");
        }
        node = getCloud().getNode(number);       
        setNodeVar(node);
        return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspTagException {
        log.debug("fillvars");
        fillVars();
        if (id != null) {
            getContextTag().registerNode(id, getNodeVar());
        }

    }

    /**
    * this method writes the content of the body back to the jsp page
    **/
    public int doAfterBody() throws JspTagException {
        super.doAfterBody();
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY;
    }

}
