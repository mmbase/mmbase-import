/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.jsp.taglib.StringListTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * A way to make paging-mechanisms. Considers 'offset' and 'maxnumber' of surrounding query.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: QueryPreviousBatchesTag.java,v 1.1 2003-12-18 09:05:48 michiel Exp $
 */
public class QueryPreviousBatchesTag extends StringListTag implements QueryContainerReferrer {
    private static final Logger log = Logging.getLoggerInstance(QueryPreviousBatchesTag.class);

    protected Attribute container  = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    protected int indexOffset = 0;

    protected void  truncateList() throws JspTagException {
        if (max != Attribute.NULL) {
            int m = getMaxNumber();
            if (m > 0 && m < returnList.size()) {
                indexOffset += (returnList.size() - m);
                log.error("Setting index offset to " + indexOffset);
                returnList = returnList.subList(returnList.size() - m, returnList.size());
                
            }
        }        
    }

    public int getIndexOffset() {
        return indexOffset;
    }


    protected List getList() throws JspTagException {
        QueryContainer c = (QueryContainer) findParentTag(QueryContainer.class, (String) container.getValue(this));
        Query query = c.getQuery();
        int offset = query.getOffset();
        int maxNumber = query.getMaxNumber();
        if (maxNumber == Query.DEFAULT_MAX_NUMBER) {
            throw new JspTagException("No max-number set. Cannot batch results (use mm:maxnumber first)");
        }
        if (offset % maxNumber != 0) { // be paranoid, perhaps not necessary, but guarantees less queries in case of url-hacking (if 'offset' is used on url)
            throw new JspTagException("Offset (" + offset + ") is not a multipible of max-number (" + maxNumber + "): Cannot batch results.");
        }
        List result = new ArrayList();
        int maxSize = getMaxNumber();
        while (offset > 0) {
            offset -= maxNumber;
            if (offset < 0) offset = 0;
            result.add(0, "" + offset);
            if (maxSize > 0 && result.size() == maxSize) break;
        }
        if (offset > 0) {
            indexOffset = offset / maxNumber;
        } else {
            indexOffset = 0;
        }
        
        return result;
    }

}
