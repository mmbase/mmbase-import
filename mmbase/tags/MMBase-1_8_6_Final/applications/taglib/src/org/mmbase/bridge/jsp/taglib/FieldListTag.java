/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.containers.*;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagStatus;

import java.util.*;
import org.mmbase.bridge.*;
/**
 * This class makes a tag which can list the fields of a NodeManager.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldListTag.java,v 1.54.2.3 2008-04-10 16:24:11 michiel Exp $
 */
public class FieldListTag extends FieldReferrerTag implements ListProvider, FieldProvider, QueryContainerReferrer {

    private FieldList     returnList;
    private FieldIterator fieldIterator;
    private Field         currentField;
    private int           currentItemIndex= -1;

    private Attribute   nodeManagerAtt = Attribute.NULL;
    private Attribute   container     = Attribute.NULL;
    private NodeProvider nodeProvider = null;

    private  Attribute type = Attribute.NULL;

    protected Attribute  add= Attribute.NULL;
    protected Attribute  retain = Attribute.NULL;
    protected Attribute  remove = Attribute.NULL;
    private  Attribute comparator = Attribute.NULL;
    private  Attribute varStatus = Attribute.NULL;

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
    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setVarStatus(String s) throws JspTagException {
        varStatus = getAttribute(s);
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
            throw new JspTagException("Unknown field order type '" + t + "'");
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
        return fields.getList(this);
    }

    public NodeProvider getNodeProvider() {
        return nodeProvider;
    }

    public Node getNodeVar() throws JspTagException {

        Node node =  parentNodeId == Attribute.NULL ? getNodeFromPageContext() : null;
        if (node != null) return node;

        /*
        if (nodeManagerString != null) {
            return null;
        }
        */
        nodeProvider = findNodeProvider(false);
        if (nodeProvider == null) return null;
        return nodeProvider.getNodeVar();
    }


    public void setAdd(String a) throws JspTagException {
        add = getAttribute(a);
    }

    public void setRetain(String r) throws JspTagException {
        retain = getAttribute(r);
    }

    public void setRemove(String r) throws JspTagException {
        remove = getAttribute(r);
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
     * @since MMBase-1.8.1
     */
    protected NodeManager getNodeManagerFromQuery(String id, boolean exception) throws JspTagException {
        NodeQueryContainer qc = (NodeQueryContainer) findParentTag(NodeQueryContainer.class, container.getString(this), exception);
        if (qc != null) {
            NodeQuery query = qc.getNodeQuery();
            return query.getNodeManager();
        } else {
            return null;
        }
    }

    /**
     *
     **/
    public int doStartTag() throws JspTagException{
        collector = new ContextCollector(getContextProvider());

        if (varStatus != Attribute.NULL) {
            org.mmbase.bridge.jsp.taglib.util.ContextContainer cc = this.getContextProvider().getContextContainer();
            cc.register(varStatus.getString(this), new ListProviderLoopTagStatus(this));
        }

        if (getReferid() != null) {
            if (nodeManagerAtt != Attribute.NULL || type != Attribute.NULL) {
                throw new JspTagException("Cannot specify referid attribute together with nodetype/type attributes");
            }
            Object o =  getObject(getReferid());
            if (! (o instanceof FieldList)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a FieldList, but  " + (o == null ? "NULL" : "a " + o.getClass().getName()));
            }
            if (getReferid().equals(getId())) { // in such a case, don't whine
                getContextProvider().getContextContainer().unRegister(getId());
            }
            returnList = (FieldList) o;
        } else {
            NodeManager nodeManager;

            if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer, or Query-referrer
                if (container != Attribute.NULL) {
                    nodeManager = getNodeManagerFromQuery(container.getString(this), true);
                    if (nodeManager == null) {
                        throw new JspTagException("Explicit container '" + container.getString(this) + "' for " + this + "' but no query container found");
                    }
                } else {
                    Node n = getNodeVar();
                    if (n == null) {
                        nodeManager = getNodeManagerFromQuery(null, false);
                        if (nodeManager == null) {
                            throw new JspTagException("Fieldlist tag must be used either as node-referrer, or use the nodetype attribute, or 'container' attribute.");
                        }
                    } else {
                        nodeManager = n.getNodeManager();
                    }
                }
            } else {
                nodeManager = getCloudVar().getNodeManager(nodeManagerAtt.getString(this));
            }

            if (! "".equals(type.getString(this))) {
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
        if (add != Attribute.NULL) {
            Object addObject = getObject(add.getString(this));
            if (addObject instanceof Collection) {
                returnList.addAll((Collection) addObject);
            } else {
                returnList.add(addObject);
            }

        }
        if (retain != Attribute.NULL) {
            Object retainObject = getObject(retain.getString(this));
            if (retainObject instanceof Collection) {
                returnList.retainAll((Collection) retainObject);
            } else {
                returnList.retainAll(Collections.singletonList(retainObject));
            }
        }
        if (remove != Attribute.NULL) {
            Object removeObject = getObject(remove.getString(this));
            if (removeObject instanceof Collection) {
                returnList.removeAll((Collection) removeObject);
            } else {
                returnList.remove(removeObject);
            }
        }

        ListSorter.sort(returnList, (String) comparator.getValue(this), this);
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
            getContextProvider().getContextContainer().register(getId(), returnList, false);
        }
        if (varStatus != Attribute.NULL) {
            getContextProvider().getContextContainer().unRegister(varStatus.getString(this));
        }
        super.doEndTag();
        return EVAL_PAGE;
    }

    public void doFinally() {
        collector = null;
        returnList = null;
        fieldIterator = null;
        currentField = null;
        nodeProvider = null;
        super.doFinally();
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
    public LoopTagStatus getLoopStatus() {
        return new ListProviderLoopTagStatus(this);
    }
}

