/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.*;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import java.util.*;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.StringSplitter;

/**
 * This class makes a tag which can list the fields of a NodeManager.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldListTag.java,v 1.39 2004-02-25 14:10:00 pierre Exp $
 */
public class FieldListTag extends FieldReferrerTag implements ListProvider, FieldProvider {

    private static final Logger log = Logging.getLoggerInstance(FieldListTag.class);

    private FieldList     returnList;
    private FieldIterator fieldIterator;
    private Field         currentField;
    private int           currentItemIndex= -1;

    private Attribute   nodeManagerAtt = Attribute.NULL;
    private NodeProvider nodeProvider = null;

    private  Attribute type = Attribute.NULL;

    private  Attribute comparator = Attribute.NULL;

    public int size(){
        return returnList.size();
    }
    public int getIndex() {
        return currentItemIndex;
    }

    public int getIndexOffset() {
        return 1;
    }

    public Object getCurrent() {
        return currentField;
    }

    public boolean isChanged() {
        return true;
    }
    public void remove() {
        fieldIterator.remove();
    }

    public Field getFieldVar() {
        return currentField;
    }

    public void setNodetype(String t) throws JspTagException {
        nodeManagerAtt = getAttribute(t);
    }

    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }
    protected int getType() throws JspTagException {
        if (type == Attribute.NULL) return NodeManager.ORDER_NONE;
        String t = type.getString(this).toLowerCase();
        if("create".equals(t)) {
           return NodeManager.ORDER_CREATE;
        } else if ("edit".equals(t)) {
            return  NodeManager.ORDER_EDIT;
        } else if ("list".equals(t)) {
            return NodeManager.ORDER_LIST;
        } else if ("search".equals(t)) {
            return NodeManager.ORDER_SEARCH;
        } else if ("all".equals(t)) {
            return  NodeManager.ORDER_NONE;
        } else {
            throw new JspTagException("Unknown field order type " + t);
        }
    }

    private String varType = null;
    private String jspVar   = null;
    public void setVartype(String t) {
        varType = t;
    }
    public void setJspvar(String j) {
        jspVar = j;
    }


    private Attribute fields = Attribute.NULL;

    public void setFields(String f) throws JspTagException {
        fields = getAttribute(f);
    }
    protected List getFields() throws JspTagException {
        return StringSplitter.split(getAttributeValue(fields.getString(this)));
    }

    public NodeProvider getNodeProvider() {
        return nodeProvider;
    }

    public Node getNodeVar() throws JspTagException {
        /*
        if (nodeManagerString != null) {
            return null;
        }
        */
        nodeProvider = findNodeProvider(false);
        if (nodeProvider == null) return null;
        return nodeProvider.getNodeVar();
    }


    public void setModified() {
        if (nodeProvider != null) {
            nodeProvider.setModified();
        }
    }

    public void setComparator(String c) throws JspTagException {
        comparator = getAttribute(c);
    }


    /**
     * Lists do implement ContextProvider
     */
    private   ContextCollector collector;



    // ContextProvider implementation
    public ContextContainer getContextContainer() {
        return collector.getContextContainer();
    }


    /**
    *
    **/
    public int doStartTag() throws JspTagException{
        collector = new ContextCollector(getContextProvider().getContextContainer());

        if (getReferid() != null) {
            if (nodeManagerAtt != Attribute.NULL || type != Attribute.NULL) {
                throw new JspTagException("Cannot specify referid attribute together with nodetype/type attributes");
            }
            Object o =  getObject(getReferid());
            if (! (o instanceof FieldList)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a FieldList");
            }
            returnList = (FieldList) o;
        } else {
            NodeManager nodeManager;

            if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer
                Node n = getNodeVar();
                if (n == null) throw new JspTagException("Fieldlist tag must be used either as node-referrer, or use the nodetype attribute");
                nodeManager = n.getNodeManager();
            } else {
                nodeManager = getCloud().getNodeManager(nodeManagerAtt.getString(this));
            }

            if (type != Attribute.NULL) {
                returnList = nodeManager.getFields(getType());
                if (fields != Attribute.NULL) {
                    Iterator i = getFields().iterator();
                    while (i.hasNext()) {
                        returnList.add(nodeManager.getField((String) i.next()));
                    }
                }

            } else {
                returnList = nodeManager.getFields();
                if (fields != Attribute.NULL) {
                    returnList.clear();
                    Iterator i = getFields().iterator();
                    while (i.hasNext()) {
                        String fieldName = (String) i.next();
                        if (fieldName.endsWith("?")) {
                            fieldName = fieldName.substring(0,fieldName.length()-1);
                            if (!nodeManager.hasField(fieldName)) continue;
                        }
                        returnList.add(nodeManager.getField(fieldName));
                    }
                }
            }
        }
        ListSorter.sort(returnList, (String) comparator.getValue(this), pageContext);
        fieldIterator = returnList.fieldIterator();

        //this is where we do the search
        currentItemIndex= -1;  // reset index

        // if we get a result from the query
        // evaluate the body , else skip the body
        if (fieldIterator.hasNext()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }

        collector.doAfterBody();

        if (fieldIterator.hasNext()){
            doInitBody();
            return EVAL_BODY_AGAIN;
        } else {
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


    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), returnList);
        }
        return  EVAL_PAGE;
    }

    public void doInitBody() throws JspTagException {
        if (fieldIterator.hasNext()){
            currentItemIndex ++;
            currentField = fieldIterator.nextField();
            if (jspVar != null) {
                switch (WriterHelper.stringToType(varType == null ? "field" : varType)) {
                case WriterHelper.TYPE_FIELD:      pageContext.setAttribute(jspVar, currentField); break;
                case WriterHelper.TYPE_FIELDVALUE: pageContext.setAttribute(jspVar, getNodeVar().getFieldValue(currentField)); break;
                default: throw new  JspTagException("Unsupported value for vartype attribute '" + varType + "'");
                }
            }
            if (getId() != null) {
                getContextProvider().getContextContainer().register(getId(), currentField);
            }
        }
    }
}

