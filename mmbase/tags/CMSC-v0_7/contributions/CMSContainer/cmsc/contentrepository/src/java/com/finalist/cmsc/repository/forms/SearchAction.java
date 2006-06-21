package com.finalist.cmsc.repository.forms;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.struts.MMBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchAction extends MMBaseAction {

    private static final String GETURL = "geturl";
    private static final String RESULTS = "results";
    private static final String RESULT_COUNT = "resultCount";
    
    private static final String PERSONAL = "personal";
    private static final String AUTHOR = "author";
    private static final String OBJECTID = "objectid";
    private static final String DIRECTION = "direction";
    private static final String ORDER = "order";
    private static final String PARENTCHANNEL = "parentchannel";
    private static final String CONTENTTYPES = "contenttypes";
    private static final String OFFSET = "offset";

    private static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

    /**
     * MMbase logging system
     */
    private static Logger log = Logging.getLoggerInstance(SearchAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        log.debug("Starting the search:");

        // Initialize
        SearchForm searchForm = (SearchForm) form;
        NodeManager nodeManager = cloud.getNodeManager(searchForm.getContenttypes());
        QueryStringComposer queryStringComposer = new QueryStringComposer();
        NodeQuery query = cloud.createNodeQuery();

        // First we add the contenttype parameter
        queryStringComposer.addParameter(CONTENTTYPES, searchForm.getContenttypes());

        // First add the proper step to the query.
        Step theStep = null;
        if (!StringUtil.isEmpty(searchForm.getParentchannel())) {
            Step step = query.addStep(cloud.getNodeManager(RepositoryUtil.CONTENTCHANNEL));
            query.addNode(step, cloud.getNode(searchForm.getParentchannel()));
            theStep = query.addRelationStep(nodeManager, RepositoryUtil.CONTENTREL, "DESTINATION").getNext();
            query.setNodeStep(theStep);
            queryStringComposer.addParameter(PARENTCHANNEL, searchForm.getParentchannel());
        }
        else {
            theStep = query.addStep(nodeManager);
            query.setNodeStep(theStep);
        }

        // Order the result by:
        queryStringComposer.addParameter(ORDER, searchForm.getOrder());
        queryStringComposer.addParameter(DIRECTION, "" + searchForm.getDirection());
        query.addSortOrder(query.getStepField(nodeManager.getField(searchForm.getOrder())),
                searchForm.getDirection());
        query.setDistinct(true);

        // Set some date constraints.
        queryStringComposer.addParameter(ContentElementUtil.CREATIONDATE_FIELD, "" + searchForm.getCreationdate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.CREATIONDATE_FIELD, searchForm.getCreationdate());
        queryStringComposer.addParameter(ContentElementUtil.EMBARGODATE_FIELD, "" + searchForm.getEmbargodate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.EMBARGODATE_FIELD, searchForm.getEmbargodate());
        queryStringComposer.addParameter(ContentElementUtil.EXPIREDATE_FIELD, "" + searchForm.getExpiredate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.EXPIREDATE_FIELD, searchForm.getExpiredate());
        queryStringComposer.addParameter(ContentElementUtil.LASTMODIFIEDDATE_FIELD, "" + searchForm.getLastmodifieddate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIEDDATE_FIELD, searchForm.getLastmodifieddate());

        // Perhaps we have some more constraints if the nodetype was specified (=> not
        // contentelement).
        if (!ContentElementUtil.CONTENTELEMENT.equalsIgnoreCase(nodeManager.getName())) {
            FieldList fields = nodeManager.getFields();
            FieldIterator fieldIterator = fields.fieldIterator();

            while (fieldIterator.hasNext()) {
                Field field = fieldIterator.nextField();
                String paramName = nodeManager.getName() + "." + field.getName();
                String paramValue = request.getParameter(paramName);
                if (!StringUtil.isEmpty(paramValue)) {
                    SearchUtil.addLikeConstraint(query, field, paramValue);
                }
                queryStringComposer.addParameter(paramName, paramValue);
            }
        }

        // Add the title constraint:
        if (!StringUtil.isEmpty(searchForm.getTitle())) {
            Field field = nodeManager.getField(ContentElementUtil.TITLE_FIELD);
            SearchUtil.addLikeConstraint(query, field, searchForm.getTitle());
            queryStringComposer.addParameter(ContentElementUtil.TITLE_FIELD, searchForm.getTitle());
        }
        
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
            SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.NUMBER_FIELD, objectId);
            queryStringComposer.addParameter(OBJECTID, searchForm.getObjectid());
        }

        // Add the personal constraint:
        if (!StringUtil.isEmpty(searchForm.getPersonal())) {
            String useraccount = searchForm.getUseraccount();

            if (ContentElementUtil.LASTMODIFIER_FIELD.equals(searchForm.getPersonal())) {
                if (StringUtil.isEmpty(useraccount)) {
                    useraccount = cloud.getUser().getIdentifier();
                }
                SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIER_FIELD, useraccount);
            }
            if (AUTHOR.equals(searchForm.getPersonal())) {
                Node user = null;
                if (StringUtil.isEmpty(useraccount)) {
                    user = SecurityUtil.getUserNode(cloud);
                }
                else {
                    user = SecurityUtil.getUserNode(cloud, useraccount);
                }
                NodeManager userManager = cloud.getNodeManager(ContentElementUtil.USER);
                Step authorStep = query.addRelationStep(userManager, ContentElementUtil.AUTHORREL, "DESTINATION").getNext();
                query.addNode(authorStep, user);
            }
            queryStringComposer.addParameter(PERSONAL, searchForm.getPersonal());
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
