/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.*;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.tree.TreeContainerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;

/**
 * The size of a list or of a nodelistcontainer (then the query is consulted).
 *
 * @author Michiel Meeuwissen
 * @version $Id: SizeTag.java,v 1.29 2009-02-03 13:12:27 michiel Exp $
 */
public class SizeTag extends ListReferrerTag implements Writer, QueryContainerReferrer {

    private Attribute container = Attribute.NULL;

    /**
     * @since MMBase-1.7
     */
    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * When in a list-container only, the size can be predicted by altering the query with "count()".
     * @since MMBase-1.7
     */
    protected void nodeListContainerSize(QueryContainer c) throws JspTagException {
        Query query = c.getQuery();
        int res = Queries.count(query) - query.getOffset();
        int max = query.getMaxNumber();
        if (max > -1 && res > max) { res = max; }
        helper.setValue(res);
    }

    /**
     * When in a looptag, the size can simply be asked from the List
     * @since MMBase-1.7
     */
    protected void listProviderSize(LoopTag list) throws JspTagException {
        helper.setValue(list.getLoopStatus().getCount());
    }


    public int doStartTag() throws JspTagException{
        if (container != Attribute.NULL) {
            if (parentListId != Attribute.NULL) {
                throw new JspTagException("Cannot specify both 'container' and 'list' attributes");
            }
            QueryContainer c = findParentTag(QueryContainer.class, (String) container.getValue(this));
            if (c instanceof TreeContainerTag) {
                helper.setValue(((TreeContainerTag)c).getTree().size());
            } else {
                nodeListContainerSize(c);
            }
        } else if (parentListId != Attribute.NULL) {
            listProviderSize(getList());
        } else {
            Tag tag = findLoopOrQuery(null, false);
            if (tag != null) {
                if (tag instanceof TreeContainerTag) {
                    helper.setValue(((TreeContainerTag)tag).getTree().size());
                } else if (tag instanceof QueryContainer) {
                    nodeListContainerSize((QueryContainer) tag);
                } else {
                    listProviderSize((LoopTag) tag);
                }
            } else {
                Query q = getQuery(container);
                helper.setValue(Queries.count(q));
            }
        }

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     *
     **/
    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }
}
