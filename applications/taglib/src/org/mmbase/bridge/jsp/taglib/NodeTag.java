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

import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * NodeTag provides the fields of a node
 *
 * @author Rob Vermeulen
 * @author Michiel Meeuwissen
 * @version $Id: NodeTag.java,v 1.50 2003-11-20 13:58:22 pierre Exp $
 */

public class NodeTag extends AbstractNodeProviderTag implements BodyTag {

    private static final Logger log = Logging.getLoggerInstance(NodeTag.class);

    private Attribute number    = Attribute.NULL;
    private Attribute element   = Attribute.NULL;

    private final static int NOT_FOUND_THROW = 0;
    private final static int NOT_FOUND_SKIP  = 1;
    private final static int NOT_FOUND_PROVIDENULL  = 2;


    private Attribute notfound = Attribute.NULL;


    private int getNotfound() throws JspTagException {
        if (notfound == Attribute.NULL) {
            return  NOT_FOUND_THROW;
        }
        String is = notfound.getString(this).toLowerCase();
        if ("skip".equals(is)) {
            return NOT_FOUND_SKIP;
        } else if ("skipbody".equals(is)) {
            return NOT_FOUND_SKIP;
        } else if ("throw".equals(is)) {
            return NOT_FOUND_THROW;
        } else if ("exception".equals(is)) {
            return NOT_FOUND_THROW;
        } else if ("throwexception".equals(is)) {
            return NOT_FOUND_THROW;
        } else if ("null".equals(is)) {
            return NOT_FOUND_PROVIDENULL;
        } else if ("providenull".equals(is)) {
            return  NOT_FOUND_PROVIDENULL;
        } else {
            throw new JspTagException("Invalid value for attribute 'notfound' " + is + "(" + notfound + ")");
        }
    }
    /**
     * Release all allocated resources.
     */
    public void release() {
        log.debug("releasing");
        super.release();
        number = Attribute.NULL;
        element = Attribute.NULL;
    }


    public void setNumber(String number) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("setting number to " + number);
        }
        this.number = getAttribute(number);
    }


    public void setNotfound(String i) throws JspTagException {
        notfound = getAttribute(i);
    }
    /**
     * This function cannot be added because of Orion.
     * It will call this function even if you use an attribute without <%= %>, stupidly.

     public void setNumber(int number) throws JspTagException {
     this.number = new Integer(number).toString();
     }

    */


    /**
     * The element attribute is used to access elements of
     * clusternodes.
     */
    public void setElement(String e) throws JspTagException {
        element = getAttribute(e);
    }


    public int doStartTag() throws JspTagException{
        Node node = null;
        if (referid != Attribute.NULL) {
            String referString = (String) referid.getValue(this);
            // try to find if already in context.
            if (log.isDebugEnabled()) {
                log.debug("Looking up Node with " + referString + " in context");
            }
            switch(getNotfound()) {
            case NOT_FOUND_SKIP:         {
                node = getNodeOrNull(referString);
                if (node == null) return SKIP_BODY;
                break;
            }
            case NOT_FOUND_PROVIDENULL:  {
                node = getNodeOrNull(referString);
                break;
            }
            default: node = getNode(referString);
            }

            if(referString.equals(getId())) {
                getContextProvider().getContextContainer().unRegister(referString);
                // register it again, but as node
                // (often referid would have been a string).
            }
        }

        if (node == null) {
            String n = number.getString(this);
            if (log.isDebugEnabled()) {
                log.debug("node is null, number attribute: '" + n + "'");
            }
            if (number != Attribute.NULL) { // if (! n.equals("")) {   // if empty string should mean 'not present'. Not sure what is most conventient
                // explicity indicated which node (by number or alias)
                if (! getCloud().hasNode(n)) {
                    switch(getNotfound()) {
                    case NOT_FOUND_SKIP:
                        return SKIP_BODY;
                    case NOT_FOUND_PROVIDENULL:
                        node = null;
                        break;
                    default:
                        node = getCloud().getNode(n); // throws Exception
                    }
                } else {
                    node = getCloud().getNode(n); // does not throw Exception
                }
            } else {
                // get the node from a parent element.
                NodeProvider nodeProvider = findNodeProvider(false);
                if (nodeProvider == null) {
                    throw new JspTagException("Could not find parent of type org.mmbase.bridge.jsp.taglib.NodeProvider, and no 'number' or 'referid' attribute specified.");
                }
                if (element != Attribute.NULL) {
                    node = nodeProvider.getNodeVar().getNodeValue(element.getString(this));
                    if (node == null) {
                        switch(getNotfound()) {
                        case NOT_FOUND_SKIP:
                            return SKIP_BODY;
                        case NOT_FOUND_PROVIDENULL:
                            node = null;
                            break;
                        default:
                            throw new JspTagException("Could not find 'element' node.");
                        }
                    }
                } else {
                    node = nodeProvider.getNodeVar();
                }

            }
        }

        setNodeVar(node);

        // if direct parent is a Formatter Tag, then communicate
        FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class, null, false);
        if (f!= null && f.wantXML() && node != null) {
            f.getGenerator().add(node);
        }

        fillVars();
        //log.debug("found node " + node.getValue("gui()"));
        return EVAL_BODY_BUFFERED;
    }

    public void doInitBody() throws JspTagException { // in case it is called, do nothing.
    }

    /**
     * this method writes the content of the body back to the jsp page
     **/
    public int doAfterBody() throws JspTagException { // write the body if there was one
        if (bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new TaglibException(ioe);
            }
        }
        return SKIP_BODY;
    }


    public int doEndTag() throws JspTagException {
        super.doAfterBody(); // if modified
        super.doEndTag();
        return EVAL_PAGE;
    }

}
