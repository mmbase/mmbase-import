/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public abstract class SearchAction extends MMBaseAction {

    public static final String NUMBER_FIELD = "number";
    
    private static final String GETURL = "geturl";
    private static final String RESULTS = "results";
    private static final String RESULT_COUNT = "resultCount";
    
    private static final String OBJECTID = "objectid";
    private static final String DIRECTION = "direction";
    private static final String ORDER = "order";
    private static final String CONTENTTYPES = "contenttypes";
    private static final String OFFSET = "offset";

    private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

    /**
     * MMbase logging system
     */
    private static Logger log = Logging.getLoggerInstance(ImageAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        // Initialize
        SearchForm searchForm = (SearchForm) form;
        NodeManager nodeManager = cloud.getNodeManager(searchForm.getContenttypes());
        QueryStringComposer queryStringComposer = new QueryStringComposer();
        NodeQuery query = cloud.createNodeQuery();

        // First we add the contenttype parameter
        queryStringComposer.addParameter(CONTENTTYPES, searchForm.getContenttypes());

        // First add the proper step to the query.
        Step theStep = query.addStep(nodeManager);
        query.setNodeStep(theStep);

        // Order the result by:
        queryStringComposer.addParameter(ORDER, searchForm.getOrder());
        queryStringComposer.addParameter(DIRECTION, "" + searchForm.getDirection());
        query.addSortOrder(query.getStepField(nodeManager.getField(searchForm.getOrder())),
                searchForm.getDirection());
        query.setDistinct(true);

        addConstraints(searchForm, nodeManager, queryStringComposer, query);
        
        // Set the objectid constraint
        if (!StringUtil.isEmpty(searchForm.getObjectid())) {
            Integer objectId = null;
            if (searchForm.getObjectid().matches("^\\d+$")) {
                objectId = new Integer(searchForm.getObjectid());
            }
            else {
                if (cloud.hasNode(searchForm.getObjectid())) {
                    objectId = new Integer(cloud.getNode(searchForm.getObjectid()).getNumber());
                }
                else {
                    objectId = new Integer(-1);
                }
            }
            SearchUtil.addEqualConstraint(query, nodeManager, NUMBER_FIELD, objectId);
            queryStringComposer.addParameter(OBJECTID, searchForm.getObjectid());
        }

        // Set the maximum result size.
        String resultsPerPage = PropertiesUtil.getProperty(REPOSITORY_SEARCH_RESULTS_PER_PAGE);
        if (resultsPerPage == null || !resultsPerPage.matches("\\d+")) {
            query.setMaxNumber(25);
        }
        else {
            query.setMaxNumber(Integer.parseInt(resultsPerPage));
        }

        // Set the offset (used for paging).
        if (searchForm.getOffset() != null && searchForm.getOffset().matches("\\d+")) {
            query.setOffset(query.getMaxNumber() * Integer.parseInt(searchForm.getOffset()));
            queryStringComposer.addParameter(OFFSET, searchForm.getOffset());
        }

        log.debug("QUERY: " + query);

        // Set everyting on the request.
        request.setAttribute(RESULT_COUNT, new Integer(Queries.count(query)));
        request.setAttribute(RESULTS, cloud.getList(query));
        request.setAttribute(GETURL, queryStringComposer.getQueryString());

        return mapping.getInputForward();
    }

    protected abstract void addConstraints(SearchForm searchForm, NodeManager nodeManager, QueryStringComposer queryStringComposer, NodeQuery query);

    protected void addField(NodeManager nodeManager, QueryStringComposer queryStringComposer, NodeQuery query, String fieldname, String value) {
        if (!StringUtil.isEmpty(value)) {
            Field field = nodeManager.getField(fieldname);
            SearchUtil.addLikeConstraint(query, field, value);
            queryStringComposer.addParameter(fieldname, value);
        }
    }

    
    class QueryStringComposer {

        private StringBuffer queryString = null;

        public void addParameter(String key, String value) {
            if (value == null || key == null) { return; }

            if (queryString == null) {
                queryString = new StringBuffer("?");
            }
            else {
                queryString.append("&");
            }

            queryString.append(key);
            queryString.append("=");
            queryString.append(value);
        }

        public String getQueryString() {
            if (queryString != null) {
                return queryString.toString();
            }
            else {
                return "";
            }
        }
    }

}
