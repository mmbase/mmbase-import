/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListPreviousBatchesTag.java,v 1.2 2003-08-06 07:41:48 michiel Exp $
 */
public class NodeListPreviousBatchesTag extends StringListTag implements NodeListContainerReferrer {
    private static Logger log = Logging.getLoggerInstance(NodeListPreviousBatchesTag.class);

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

    public int getOffset() {
        return indexOffset;
    }


    protected List getList() throws JspTagException {
        NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));
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
