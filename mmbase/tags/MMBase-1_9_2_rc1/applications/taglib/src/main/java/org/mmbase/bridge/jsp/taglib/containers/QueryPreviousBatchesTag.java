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
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.StringListTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.util.logging.*;

/**
 * A way to make paging-mechanisms. Considers 'offset' and 'maxnumber' of surrounding query.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 */
public class QueryPreviousBatchesTag extends StringListTag implements QueryContainerReferrer {
    private static final Logger log = Logging.getLoggerInstance(QueryPreviousBatchesTag.class);

    protected Attribute container  = Attribute.NULL;
    protected Attribute indexOffsetOffset = Attribute.NULL;
    protected Attribute maxtotal = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setMaxtotal(String m) throws JspTagException {
        maxtotal = getAttribute(m);
    }

    public void setIndexoffset(String i ) throws JspTagException {
        indexOffsetOffset = getAttribute(i);
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


        int maxTotalSize = maxtotal.getInt(this, -1);

        int maxSize; // the size of this list.

        if (maxTotalSize > 0) {
            maxSize = maxTotalSize / 2;              // first guess

            int totalSize = Queries.count(query);
            int nextSize  = totalSize - offset - maxNumber;
            int numberOfNextBatches = nextSize/ maxNumber;         // number of complete pages
            if (nextSize % maxNumber > 0) numberOfNextBatches++;   // last page may be incomplete
            int availableForNext = (maxTotalSize - 1) / 2; //   // == maxSize in QueryNextBatches
            if (numberOfNextBatches < availableForNext) { // nextbatches will not use all
                maxSize += (availableForNext - numberOfNextBatches);
            }

            int max = getMaxNumber();
            if (max > 0 && maxSize > max) maxSize = max;

        } else {
            maxSize = getMaxNumber();
        }


        List<String> result = new ArrayList<String>();

        while (offset > 0) {
            offset -= maxNumber;
            if (offset < 0) offset = 0;
            result.add(0, String.valueOf(offset));
            if (maxSize > 0 && result.size() == maxSize) break;
        }
        if (offset > 0) {
            indexOffset = offset / maxNumber + indexOffsetOffset.getInt(this, 0);
        } else {
            indexOffset = indexOffsetOffset.getInt(this, 0);
        }

        return result;
    }

}
