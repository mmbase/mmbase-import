/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * Needs to live under a ListRelationsTag
 *
 * @author Michiel Meeuwissen
 * @version $Id: RelatedNodeTag.java,v 1.13 2003-09-10 11:16:08 michiel Exp $ 
 */

public class RelatedNodeTag extends AbstractNodeProviderTag implements BodyTag {

    private static final Logger log = Logging.getLoggerInstance(RelatedNodeTag.class.getName());

    private Attribute listRelationsId = Attribute.NULL;

    public void release() {
        super.release();
    }

    public void setListrelations(String l) throws JspTagException {
        listRelationsId = getAttribute(l);
    }

    public int doStartTag() throws JspTagException{
        Node node = null;

        // get the parent ListRelationsTag
        ListRelationsTag lr = (ListRelationsTag) findParentTag(ListRelationsTag.class, (String) listRelationsId.getValue(this));
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
        // if direct parent is a Formatter Tag, then communicate
        FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class.getName(), null, false);
        if (f!= null && f.wantXML() && node != null) {
            f.getGenerator().add(node);
        }
        
        fillVars();
        return EVAL_BODY_BUFFERED;
    }

    public void doInitBody() throws JspTagException {
    }

    /**
    * this method writes the content of the body back to the jsp page
    **/
    public int doAfterBody() throws JspTagException {
        super.doAfterBody();
        if (bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new TaglibException(ioe);
            }
        }
        return SKIP_BODY;
    }

}
