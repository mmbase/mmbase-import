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

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Notfound;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * NodeTag provides the fields of a node
 *
 * @author Rob Vermeulen
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class NodeTag extends AbstractNodeProviderTag implements BodyTag {

    private static final Logger log = Logging.getLoggerInstance(NodeTag.class);

    private Attribute number    = Attribute.NULL;
    private Attribute element   = Attribute.NULL;
    private Attribute notfound  = Attribute.NULL;

    /**
     * Release all allocated resources.
     */
    public void doFinally() {
        log.debug("releasing");
        super.doFinally();
        number   = Attribute.NULL;
        element  = Attribute.NULL;
        notfound = Attribute.NULL;
    }


    public void setNumber(String number) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("setting number to " + number);
        }
        this.number = getAttribute(number, true);
    }


    public void setNotfound(String i) throws JspTagException {
        notfound = getAttribute(i, true);
    }

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
            switch(Notfound.get(notfound, this)) {
            case Notfound.MESSAGE:
                node = getNodeOrNull(referString);
                if (node == null) {
                    try {
                        getPageContext().getOut().write("Could not find node element '" + element.getString(this) + "'");
                    } catch (java.io.IOException ioe) {
                        log.warn(ioe);
                    }
                    return SKIP_BODY;
                }
                break;
            case Notfound.LOG: {
                node = getNodeOrNull(referString);
                if (node == null) {
                    log.warn("Could not find node element '" + element.getString(this) + "'");
                    return SKIP_BODY;
                }
                break;
            }
            case Notfound.SKIP:         {
                node = getNodeOrNull(referString);
                if (node == null) return SKIP_BODY;
                break;
            }
            case Notfound.PROVIDENULL:  {
                node = getNodeOrNull(referString);
                break;
            }
            default: node = getNode(referString);
            }
            if (node != null) {
                if (node.getCloud().hasNodeManager(node.getNodeManager().getName())) { // rather clumsy way to check virtuality
                    nodeHelper.setGeneratingQuery(Queries.createNodeQuery(node));
                }
            }

            if(referString.equals(getId())) {
                getContextProvider().getContextContainer().unRegister(referString);
                // register it again, but as node
                // (often referid would have been a string).
            }
        }

        if (node == null) { // found no node by referid
            String n = number.getString(this);
            if (log.isDebugEnabled()) {
                log.debug("node is null, number attribute: '" + n + "'");
            }
            // if (! n.length() == 0) {
            // if empty string should mean 'not present'. Not sure what is most conventient
            // We don't change this, becuase it was always like follows.
            // It would not be backwards compatible.

            if (number != Attribute.NULL) {
                // explicity indicated which node (by number or alias)
                Cloud c = getCloudVar();
                if (! c.hasNode(n) || ! c.mayRead(n)) {
                    switch(Notfound.get(notfound, this)) {
                    case Notfound.MESSAGE:
                        try {
                            getPageContext().getOut().write("Node '" + n + "' does not exist or may not be read");
                        } catch (java.io.IOException ioe) {
                            log.warn(ioe);
                        }
                    case Notfound.SKIP:
                        return SKIP_BODY;
                    case Notfound.PROVIDENULL:
                        node = null;
                        break;
                    default:
                        node = c.getNode(n); // throws Exception
                    }
                } else {
                    node = c.getNode(n); // does not throw Exception
                }
                if (node != null) {
                    if (node.getCloud().hasNodeManager(node.getNodeManager().getName())) { // rather clumsy way to check virtuality
                        nodeHelper.setGeneratingQuery(Queries.createNodeQuery(node));
                    }
                }
            } else {
                // get the node from a parent element.
                NodeProvider nodeProvider = null;

                node = parentNodeId == Attribute.NULL ? (Node) pageContext.findAttribute(NodeProviderHelper._NODE) : null;
                // get the node from a parent element.
                if (node == null) {
                    nodeProvider = findNodeProvider();
                    node = nodeProvider.getNodeVar();
                } else {
                    node = (Node) org.mmbase.util.Casting.unWrap(node);
                }

                String elString = element.getString(this);
                if (elString.length() != 0) {
                    try {
                        node = node.getNodeValue(elString);
                    } catch (org.mmbase.bridge.NotFoundException nfe) {
                        node = null;
                    }
                    if (node == null) {
                        switch(Notfound.get(notfound, this)) {
                        case Notfound.LOG:
                            log.warn("Could not find node element '" + elString + "'");
                            return SKIP_BODY;
                        case Notfound.MESSAGE:
                            try {
                                getPageContext().getOut().write("Could not find node element '" + elString + "'");
                            } catch (java.io.IOException ioe) {
                                log.warn(ioe);
                            }
                        case Notfound.SKIP:
                            return SKIP_BODY;
                        case Notfound.PROVIDENULL:
                            node = null;
                            break;
                        default:
                            throw new JspTagException("Could not find node element '" + elString + "'");
                        }
                    }
                    if (nodeProvider != null && nodeProvider.getNodeVar() != null) {
                        if (nodeProvider.getNodeVar().getCloud().hasNodeManager(nodeProvider.getNodeVar().getNodeManager().getName())) {
                            nodeHelper.setGeneratingQuery(nodeProvider.getGeneratingQuery());
                        }
                    }
                } else {
                    if (node.getCloud().hasNodeManager(node.getNodeManager().getName())) { // rather clumsy way to check virtuality
                        nodeHelper.setGeneratingQuery(Queries.createNodeQuery(node));
                    }
                }
            }
        }

        setNodeVar(node);

        // if direct parent is a Formatter Tag, then communicate
        FormatterTag f = findParentTag(FormatterTag.class, null, false);
        if (f!= null && f.wantXML() && node != null) {
            f.getGenerator().add(node);
            f.setCloud(node.getCloud());
        }

        fillVars();
        //log.debug("found node " + node.getValue("gui()"));
        return EVAL_BODY;
    }


    /**
     * this method writes the content of the body back to the jsp page
     **/
    public int doAfterBody() throws JspTagException { // write the body if there was one
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
        }
        return SKIP_BODY;
    }


    public int doEndTag() throws JspTagException {
        super.doAfterBody(); // if modified
        return super.doEndTag();
    }

}
