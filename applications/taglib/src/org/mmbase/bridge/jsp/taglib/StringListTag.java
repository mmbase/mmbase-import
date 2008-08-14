/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.*;
import javax.servlet.jsp.jstl.core.*;

import org.mmbase.util.Casting;
import org.mmbase.bridge.jsp.taglib.util.*;

/**
 * This class makes a tag which can list strings.
 *
 * @author Michiel Meeuwissen
 * @version $Id: StringListTag.java,v 1.37 2008-08-14 13:59:34 michiel Exp $
 * @since MMBase-1.7
 */

public class StringListTag extends NodeReferrerTag implements ListProvider, Writer {
    // need to extend NodeReferrer because of AliasListTag, no MI in java.

    protected List<String> returnList;
    protected Iterator<String> iterator;
    protected int      currentItemIndex = -1;

    protected Attribute  max        = Attribute.NULL;
    protected Attribute  comparator = Attribute.NULL;
    protected Attribute  add        = Attribute.NULL;
    protected Attribute  retain     = Attribute.NULL;
    protected Attribute  remove     = Attribute.NULL;
    protected Attribute  varStatus  = Attribute.NULL;
    protected String varStatusName  = null;

    public int size(){
        return returnList.size();
    }
    public int getIndex() {
        return currentItemIndex;
    }

    public int getIndexOffset() {
        return 1;
    }

    public boolean isChanged() {
        return true;
    }
    public Object getCurrent() {
        return getWriterValue();
    }

    public void remove() {
        iterator.remove();
    }


    public void setMax(String m) throws JspTagException {
        max = getAttribute(m);
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

    protected int getMaxNumber() throws JspTagException {
        return max.getInt(this, -1);
    }

    public void setComparator(String c) throws JspTagException {
        comparator = getAttribute(c);
    }

    /**
     * @since MMBase-1.9
     */
    public void setVarStatus(String s) throws JspTagException {
        varStatus = getAttribute(s);
    }


    /**
     * Lists do implement ContextProvider
     */
    private   ContextCollector collector;



    // ContextProvider implementation
    public ContextContainer getContextContainer() {
        return collector;
    }


    /**
     * Creates the actual list of strings.
     */
    protected List<String> getList() throws JspTagException {
        throw new JspTagException("Should use 'referid' attribute on liststrings tag");
    }

    /**
     * For use with referid
     */
    protected void  truncateList() throws JspTagException {
        if (max != Attribute.NULL) {
            int m = getMaxNumber();
            if (m > 0 && m < returnList.size()) {
                returnList = returnList.subList(0, m);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException{
        initTag();
        collector = new ContextCollector(getContextProvider());
        varStatusName = (String) varStatus.getValue(this);

        helper.overrideWrite(false); // default behavior is not to write to page

        if (getReferid() != null) {
            Object o =  getObject(getReferid());
            if (! (o instanceof Collection)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a Collection");
            }
            if (o instanceof List) {
                returnList = (List<String>) o;
            } else {
                returnList = new ArrayList<String>((Collection<String>) o);
            }
            truncateList();
            if (getReferid().equals(getId())) { // in such a case, don't whine
                getContextProvider().getContextContainer().unRegister(getId());
            }
        } else {
            returnList = getList();
        }

        if (getId() != null) {
            returnList = new ArrayList<String>(returnList);
        }
        if (add != Attribute.NULL) {
            Object addObject = getObjectConditional(add.getString(this));
            if (addObject != null) {
                if (addObject instanceof Collection) {
                    returnList.addAll((Collection<String>) addObject);
                } else {
                    returnList.add(Casting.toString(addObject));
                }
            }
        }
        if (retain != Attribute.NULL) {
            Object retainObject = getObjectConditional(retain.getString(this));
            if (retainObject != null) {
                if (retainObject instanceof Collection) {
                    returnList.retainAll((Collection<String>) retainObject);
                } else {
                    returnList.retainAll(Collections.singletonList(Casting.toString(retainObject)));
                }
            }
        }
        if (remove != Attribute.NULL) {
            Object removeObject = getObjectConditional(remove.getString(this));
            if (removeObject != null) {
                if (removeObject instanceof Collection) {
                    returnList.removeAll((Collection<String>) removeObject);
                } else {
                    returnList.remove(Casting.toString(removeObject));
                }
            }
        }

        currentItemIndex = - 1;  // reset index

        ListSorter.sort(returnList, (String) comparator.getValue(this), this);
        iterator = returnList.iterator();
        // if we get a result from the query
        // evaluate the body , else skip the body
        if (iterator.hasNext()) {
            setNext();
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspException {
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }

        if (varStatusName != null) {
            getContextProvider().getContextContainer().unRegister(varStatusName);
        }

        helper.doAfterBody();
        collector.doAfterBody();

        if (iterator.hasNext()){
            setNext();
            return EVAL_BODY_AGAIN;
        } else {
            if (EVAL_BODY == EVAL_BODY_BUFFERED) {
                if (bodyContent != null) {
                    try {
                        bodyContent.writeOut(bodyContent.getEnclosingWriter());
                        bodyContent.clearBody();
                    } catch (IOException ioe){
                        throw new TaglibException(ioe);
                    }
                }
            }
            return SKIP_BODY;
        }
    }
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), returnList, false);
        }
        // dereference for gc
        returnList = null;
        iterator   = null;
        if (collector != null) {
            collector.release(pageContext, getContextProvider().getContextContainer());
            collector  = null;
        }
        bodyContent = null;
        helper.doEndTag();
        return  super.doEndTag();
    }

    public void doFinally() {
        returnList = null;
        iterator   = null;
        if (collector != null) {
            try {
                collector.release(pageContext, getContextProvider().getContextContainer());
            } catch (Exception e) {
            }
            collector  = null;
        }
        super.doFinally();
    }

    protected void setNext() throws JspTagException {
        currentItemIndex++;
        helper.setValue(iterator.next());
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        if (varStatusName != null) {
            getContextProvider().getContextContainer().register(varStatusName, getLoopStatus());
        }
    }

    public LoopTagStatus getLoopStatus() {
        return new ListProviderLoopTagStatus(this);
    }

}

