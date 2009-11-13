/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.*;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * A function tag for functions returning a collection.
 * The result is iterated. If a function does not return collection, or if the result requires sorting,
 * the result value is transformed into a List (using {@link org.mmbase.util.Casting#toCollection}).
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id$
 */
public class ListFunctionTag extends AbstractFunctionTag implements ListProvider, FunctionContainerReferrer, Writer {

    private static final Logger log = Logging.getLoggerInstance(ListFunctionTag.class);
    // implementation of ListProvider

    protected Collection    returnCollection;
    protected Iterator iterator;
    protected int      currentItemIndex= -1;

    private   ContextCollector collector;
    protected Attribute  comparator = Attribute.NULL;
    protected Attribute  varStatus = Attribute.NULL;
    protected Attribute  max = Attribute.NULL;
    protected Attribute  offset = Attribute.NULL;

    public void setComparator(String c) throws JspTagException {
        comparator = getAttribute(c);
    }
    public void setVarStatus(String s) throws JspTagException {
        varStatus = getAttribute(s);
    }

    public void setMax(String m) throws JspTagException {
        max = getAttribute(m);
    }
    public void setOffset(String o) throws JspTagException {
        offset = getAttribute(o);
    }

    public int size(){
        return returnCollection.size();
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


    public ContextContainer getContextContainer() {
        return collector;
    }

    public int doStartTag() throws JspTagException {
        initTag();
        Object value = getFunctionValue(false); // registration is done in doInitBody
        if (value instanceof Collection && comparator.equals(Attribute.NULL)) {
            returnCollection = (Collection) value;
        } else {
            returnCollection = Casting.toCollection(value);
        }
        returnCollection = useCollectionMethods(returnCollection);
        if (log.isDebugEnabled()) {
            log.debug("Using " + returnCollection);
        }

        collector = new ContextCollector(getContextProvider());
        helper.overrideWrite(false); // default behavior is not to write to page
        currentItemIndex = -1;  // reset index
        if (!comparator.equals(Attribute.NULL)) {
            ListSorter.sort((List)returnCollection, (String) comparator.getValue(this), this);
        }
        int o = offset.getInt(this, 0);
        iterator = returnCollection.iterator();
        while(currentItemIndex + 1 < o && iterator.hasNext()) {
            iterator.next();
            currentItemIndex++;
        }
        if (iterator.hasNext() && (currentItemIndex + 1) < (o + max.getInt(this, Integer.MAX_VALUE))) {
            return EVAL_BODY_BUFFERED;
        }
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspException {
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }

        helper.doAfterBody();
        collector.doAfterBody();

        if (! "".equals(varStatus.getString(this))) {
            getContextProvider().getContextContainer().unRegister(varStatus.getString(this));
        }

        if (iterator.hasNext() && (currentItemIndex + 1) < max.getInt(this, Integer.MAX_VALUE)) {
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
            getContextProvider().getContextContainer().register(getId(), returnCollection, false);
        }
        // dereference for gc.
        returnCollection = null;
        iterator = null;
        collector = null;
        return  super.doEndTag();
    }


    public void doInitBody() throws JspTagException {
        if (iterator.hasNext()){
            currentItemIndex ++;
            helper.setValue(iterator.next());
            if (getId() != null) {
                getContextProvider().getContextContainer().register(getId(), helper.getValue());
            }
            if (! "".equals(varStatus.getString(this))) {
                org.mmbase.bridge.jsp.taglib.util.ContextContainer cc = getContextProvider().getContextContainer();
                cc.register(varStatus.getString(this), getLoopStatus());
            }

        }
    }

    public javax.servlet.jsp.jstl.core.LoopTagStatus getLoopStatus() {
        return new ListProviderLoopTagStatus(this);
    }

}
