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

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.Node;

/**
 * Needs to live under a ListRelationsTag
 *
 * @author Michiel Meeuwissen
 * @version $Id$ 
 */
public class RelatedNodeTag extends AbstractNodeProviderTag implements BodyTag {

    private Attribute listRelationsId = Attribute.NULL;

    public void setListrelations(String l) throws JspTagException {
        listRelationsId = getAttribute(l);
    }

    public int doStartTag() throws JspTagException{
        // get the parent ListRelationsTag
        ListRelationsTag lr = findParentTag(ListRelationsTag.class, (String) listRelationsId.getValue(this));


        Node node = lr.getRelatedNode();
        setNodeVar(node);
        // if direct parent is a Formatter Tag, then communicate
        FormatterTag f = findParentTag(FormatterTag.class, null, false);
        if (f!= null && f.wantXML() && node != null) {
            f.getGenerator().add(node);
            f.setCloud(node.getCloud());
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
