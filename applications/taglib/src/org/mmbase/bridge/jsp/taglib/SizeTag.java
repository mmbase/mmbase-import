/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.*;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;


/**
 * The size of a list or of a nodelistcontainer (then the query is consulted).
 *
 * @author Michiel Meeuwissen
 * @version $Id: SizeTag.java,v 1.20 2003-12-03 06:57:39 keesj Exp $ 
 */

public class SizeTag extends ListReferrerTag implements Writer, NodeListContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(SizeTag.class);

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
    protected void nodeListContainerSize(NodeListContainer c) throws JspTagException {       
        Query query = c.getQuery();
        int res = Queries.count(query) - query.getOffset();
        int max = query.getMaxNumber();
        if (max > -1 && res > max) { res = max; }
        helper.setValue(new Integer(res));
    }

    /**
     * When in a list-provider, the size can simply be asked from the List
     * @since MMBase-1.7
     */
    protected void listProviderSize(ListProvider list) throws JspTagException {
        helper.setValue(new Integer(list.size()));
    }


    public int doStartTag() throws JspTagException{

        

        if (container != Attribute.NULL) {
            if (parentListId != Attribute.NULL) {
                throw new JspTagException("Cannot specify both 'container' and 'list' attributes");
            }
            NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));
            nodeListContainerSize(c);            
        } else if (parentListId != Attribute.NULL) {
            listProviderSize(getList());            
        } else {
            NodeListContainerOrListProvider tag = (NodeListContainerOrListProvider) findParentTag(NodeListContainerOrListProvider.class, null);
            if (tag instanceof NodeListContainer) {
                nodeListContainerSize((NodeListContainer) tag);
            } else {
                listProviderSize((ListProvider) tag);
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
        return helper.doEndTag();
    }

}
