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
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.SearchQuery;

import java.util.*;
import javax.servlet.jsp.JspTagException;
//import org.mmbase.util.logging.*;


/**
 * A way to make paging-mechanisms. Considers 'offset' and 'maxnumber' of surrounding query.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 */
public class QueryNextBatchesTag extends StringListTag implements QueryContainerReferrer {
    //private static final Logger log = Logging.getLoggerInstance(QueryNextBatchesTag.class);


    protected Attribute container  = Attribute.NULL;
    protected Attribute indexOffsetOffset = Attribute.NULL;
    protected Attribute maxtotal = Attribute.NULL;

    protected int indexOffSet = 0;

    public void setIndexoffset(String i ) throws JspTagException {
        indexOffsetOffset = getAttribute(i);
    }

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setMaxtotal(String m) throws JspTagException {
        maxtotal = getAttribute(m);
    }

    public int getIndexOffset() {
        return indexOffSet;
    }



    protected List<String> getList() throws JspTagException {
        Query query = getQuery(container);
        int offset = query.getOffset();
        int maxNumber = query.getMaxNumber();
        if (maxNumber == SearchQuery.DEFAULT_MAX_NUMBER) {
            throw new JspTagException("No max-number set. Cannot batch results (use mm:maxnumber first)");
        }
        if (offset % maxNumber != 0) { // be paranoid, perhaps not necessary, but guarantees less queries in case of url-hacking (if 'offset' is used on url)
            throw new JspTagException("Offset (" + offset + ") is not a multipible of max-number (" + maxNumber + "): Cannot batch results.");
        }

        int totalSize = Queries.count(query);

        indexOffSet = offset / maxNumber + 1 + indexOffsetOffset.getInt(this, 0);
        List<String> resultList = new ArrayList<String>();

        int maxTotalSize = maxtotal.getInt(this, -1);

        int maxSize; // the size of this list.

        if (maxTotalSize > 0) {
            maxSize = (maxTotalSize - 1) / 2; // half for both, 1 for current
            int numberOfPreviousBatches = offset / maxNumber;
            int availableForPrevious = maxTotalSize / 2;   // == maxSize in QueryPreviousBatches
            if (numberOfPreviousBatches < availableForPrevious) { // previousbatches did not use all
                maxSize += (availableForPrevious - numberOfPreviousBatches);
            }

            int max = getMaxNumber();
            if (max > 0 && maxSize > max) maxSize = max;

        } else {
            maxSize = getMaxNumber();
        }


        while (offset + maxNumber < totalSize) {
            offset += maxNumber;
            resultList.add(String.valueOf(offset));
            if (maxSize > 0 && resultList.size() == maxSize) break;
        }
        return resultList;
    }

}
