package nl.didactor.taglib.table;

import java.io.IOException;
import java.util.*; import java.text.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.Servlet;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;


/**
 * This tag can generate a table, if &lt;headercell&gt; and
 * &lt;row&gt; subtags are present. It needs to be used within
 * any MMBase tag that is a 'QueryContainer', such as the
 * &lt;mm:listnodescontainer&gt; tag.
 * <p>
 * Based on the given 'maxitems' parameter, and data from the
 * request, it will set some constraints on the parent
 * query, so that only a subset of the data will be shown. It
 * will also add paging buttons.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class TableTag extends CloudReferrerTag { 

    public String PARAM_PAGE;
    public String PARAM_FIELD;
    public String PARAM_ORDER;

    private Attribute maxitems = Attribute.NULL;
    private Attribute name = Attribute.NULL;

    private String sortField;
    private String sortOrder;
    int size;
    int offset;
    private Map<String, String> htmlLabels;

    /**
     * Wrapper for the 'maxitems' attribute
     */
    public void setMaxitems(String maxitems) throws JspTagException {
        this.maxitems = getAttribute(maxitems);
    }

    /**
     * Wrapper for the 'id' attribute. This will also set all
     * the 'PARAM_' variables, so that you can have more than
     * one table on one page.
     */
    public void setName(String name) throws JspTagException {
        this.name = getAttribute(name);
    }

    /**
     * Return the field on which is being sorted. Return the
     * default field if none were defined and there is a
     * default field specified. Return "" otherwise.
     */
    public String getActiveSortfield() {
        return sortField;
    }

    /**
     * Return a label for a given identifier.
     */
    public String getLabel(String labelname) {
        return (htmlLabels.get(labelname)).replaceAll("\\$CONTEXT", ((HttpServletRequest) pageContext.getRequest()).getContextPath());
    }

    /**
     * Read the configuration for this table.
     */
    private void readConfig() {

    }

    /**
     * Add the sortfield to the query. This code can be called from
     * doStartTag(), but also from a 'HeaderCell.doStartTag()', in case
     * there is no sortfield set yet and that headercell has the
     * 'default' parameter set.
     */
    public void setSort(String sortfield, String sortorder) throws JspTagException {
        this.sortField = sortfield;
        QueryContainer ct = (QueryContainer) findParentTag(QueryContainer.class, null, true);
        Query nq = ct.getQuery();
        Queries.addSortOrders(nq, sortfield, sortorder);
    }

    public void setSort(String sortfield) throws JspTagException {
        setSort(sortfield, "up");
    }

    /**
     * Return the sortorder that was set for this table, either
     * "up" or "down". The integers correspond the values from
     * the org.mmbase.storage.search.SortOrder interface.
     */
    public int getSortOrder() {
        if ("down".equals(sortOrder)) {
            return 2; //org.mmbase.storage.search.SortOrder.DESCENDING;
        } else {
            return 1;// org.mmbase.storage.search.SortOrder.ASCENDING;
        }
    }

    /**
     * This method is called when the &lt;table&gt; tag is being found: it
     * alters the parent query.
     * It sends the 'maxitems' and 'offset' values to the parent query object.
     */
    public int doStartTag() throws JspTagException {
        PARAM_PAGE  = "of";  // constants  not?
        PARAM_FIELD = "sf";
        PARAM_ORDER = "so";
        if (!name.equals(Attribute.NULL)) {
            PARAM_PAGE += "_" + name.getString(this);
            PARAM_FIELD += "_" + name.getString(this);
            PARAM_ORDER += "_" + name.getString(this);
        }
        sortField = "";
        sortOrder = "";
        size = 0;
        offset = 0;
        // every time??
        htmlLabels = Util.getHtmlLabels("table/layout.properties");

        QueryContainer ct = (QueryContainer)findParentTag(QueryContainer.class, null, true);
        Query nq = ct.getQuery();

        // If there is a 'maxitems' specified, used that to set both
        // the offset and the max for the query.
        int max = maxitems.getInt(this, 0);
        if (max != 0) {
            size = Queries.count(nq);
            if (size > max) {   
                nq.setMaxNumber(max);
                String offsetStr = pageContext.getRequest().getParameter(PARAM_PAGE);
                if (offsetStr != null && !"".equals(offsetStr)) {
                    offset = Integer.parseInt(offsetStr);
                    nq.setOffset(offset * max);
                }
            }
        }

        String so = pageContext.getRequest().getParameter(PARAM_ORDER);
        if (so != null)
            this.sortOrder = so;

        String sf = pageContext.getRequest().getParameter(PARAM_FIELD);
        if (sf != null && !"".equals(sf)) {
            setSort(sf, so);
        }

        // We need to capture the data so we can add our <table> tag
        // around it. Therefore ask the engine to buffer all data.
        return EVAL_BODY_BUFFERED;
    }

    /**
     * When all the child tags are done, we need to add some HTML
     * code for paging etc. This all happens here.
     */
    public int doAfterBody() throws JspTagException {
        try {
            if (bodyContent != null) {
                getPreviousOut().print(getLabel("surrounding.start"));
                String pagingContent = "";

                // If there were more results then fit on one page, we need to
                // create a paging <div>
                int max = maxitems.getInt(this, 0);
                if (size > max) {
                    pagingContent = "<span style='width:80%'>";
                    ArrayList disallowed = new ArrayList();
                    disallowed.add(PARAM_PAGE);
                    
                    StringBuffer baseUrl = Util.getCurrentUrl(pageContext, disallowed);


                    // Add the '<' character at the beginning
                    if (offset > 0) {
                        pagingContent += "<a class=\"" + getLabel("paging.arrow.class") + "\" href=\"" + baseUrl + 
                                PARAM_PAGE + "=" + (offset-1) + 
                                "\">&lt;</a>&nbsp;";
                    } else {
                        pagingContent += "&lt;&nbsp;";
                    }
                    
                    // Add all page numbers
                    // if size is 25, and max is 5, we need 5 pages
                    int nrpages = ((size - 1) / max) + 1;
                    for (int i=0; i<nrpages; i++) {
                        if (i == offset) {
                            pagingContent += (i + 1) + " ";
                        } else {
                            pagingContent += "<a class=\"" + getLabel("paging.number.class") + "\" href=\"" + baseUrl + 
                                    PARAM_PAGE + "=" + i + 
                                    "\">" + (i + 1) + "</a> ";
                        }
                    }
                    
                    // Add the '>' character at the end
                    if (size > ((offset+1) * max)) {
                        pagingContent += "<a class=\"" + getLabel("paging.arrow.class") + "\" href=\"" + baseUrl + 
                                PARAM_PAGE + "=" + (offset+1) + 
                                "\">&gt;</a>";
                    } else {
                        pagingContent += "&gt;&nbsp;";
                    }
                    pagingContent += "</span>";
                }
                if (!"".equals(pagingContent)) {
                    getPreviousOut().print(getLabel("paging.top.start") + pagingContent + getLabel("paging.top.end"));
                }

                getPreviousOut().print(getLabel("table.start"));
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
                getPreviousOut().print(getLabel("table.end"));

                if (!"".equals(pagingContent)) {
                    getPreviousOut().print(getLabel("paging.bottom.start") + pagingContent + getLabel("paging.bottom.end"));
                }

                getPreviousOut().print(getLabel("surrounding.end"));
            }
        } catch (IOException ioe) {
           throw new TaglibException(ioe);
        }
        return SKIP_BODY;
    }

    /**
     * Release all variables, otherwise this tag cannot be pooled.
     */
    public void release() {
        super.release();
        name = Attribute.NULL;
        maxitems = Attribute.NULL;
    }
    
}
