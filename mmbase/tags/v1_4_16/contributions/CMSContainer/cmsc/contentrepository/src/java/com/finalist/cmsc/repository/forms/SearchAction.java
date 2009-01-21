package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.mmapps.commons.util.StringUtil;
import net.sf.mmapps.commons.util.KeywordUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.resources.forms.QueryStringComposer;
import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.services.workflow.Workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchAction extends PagerAction {

    public static final String GETURL = "geturl";

    public static final String PERSONAL = "personal";
    public static final String MODE = "mode";
    public static final String AUTHOR = "author";
    public static final String OBJECTID = "objectid";
    public static final String PARENTCHANNEL = "parentchannel";
    public static final String CONTENTTYPES = "contenttypes";

    public static final String REPOSITORY_SEARCH_RESULTS_PER_PAGE = "repository.search.results.per.page";

    /**
     * MMbase logging system
     */
    private static Logger log = Logging.getLoggerInstance(SearchAction.class.getName());


    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {

        log.debug("Starting the search:");

        // Initialize
        SearchForm searchForm = (SearchForm) form;

        String deleteContentRequest = request.getParameter("deleteContentRequest");

        if (StringUtils.isNotEmpty(deleteContentRequest)) {
            deleteContent(deleteContentRequest);
            
            // add a flag to let search result page refresh the channels frame,
            // so that the number of item in recyclebin can update
            request.setAttribute("refreshChannels", "refreshChannels");
        }

        // First prepare the typeList, we'll need this one anyway:
        List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

        List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);
        List<String> hiddenTypes = PropertiesUtil.getHiddenTypes();
        for (NodeManager manager : types) {
            String name = manager.getName();
            if (!hiddenTypes.contains(name)) {
                LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
                typesList.add(bean);
            }
        }
        addToRequest(request, "typesList", typesList);

        // Switching tab, no searching.
        if ("false".equalsIgnoreCase(searchForm.getSearch())) {
            return mapping.getInputForward();
        }

        NodeManager nodeManager = cloud.getNodeManager(searchForm.getContenttypes());
        QueryStringComposer queryStringComposer = new QueryStringComposer();
        if(!StringUtil.isEmpty(request.getParameter(MODE))) {
        	queryStringComposer.addParameter(MODE, request.getParameter(MODE));
        }
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
        String order = searchForm.getOrder();

        // set default order field
        if (StringUtil.isEmpty(order)) {
            if (nodeManager.hasField("title")) {
                order = "title";
            }
            if (nodeManager.hasField("name")) {
                order = "name";
            }
        }
        if (StringUtil.isEmpty(order)) {
            queryStringComposer.addParameter(ORDER, searchForm.getOrder());
            queryStringComposer.addParameter(DIRECTION, "" + searchForm.getDirection());
            query.addSortOrder(query.getStepField(nodeManager.getField(order)), searchForm.getDirection());
        }

        query.setDistinct(true);

        // Set some date constraints.
        queryStringComposer.addParameter(ContentElementUtil.CREATIONDATE_FIELD, "" + searchForm.getCreationdate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.CREATIONDATE_FIELD, searchForm
                .getCreationdate());
        queryStringComposer.addParameter(ContentElementUtil.PUBLISHDATE_FIELD, "" + searchForm.getPublishdate());
        SearchUtil
                .addDayConstraint(query, nodeManager, ContentElementUtil.PUBLISHDATE_FIELD, searchForm.getPublishdate());
        queryStringComposer.addParameter(ContentElementUtil.EXPIREDATE_FIELD, "" + searchForm.getExpiredate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.EXPIREDATE_FIELD, searchForm.getExpiredate());
        queryStringComposer
                .addParameter(ContentElementUtil.LASTMODIFIEDDATE_FIELD, "" + searchForm.getLastmodifieddate());
        SearchUtil.addDayConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIEDDATE_FIELD, searchForm
                .getLastmodifieddate());

        // Perhaps we have some more constraints if the nodetype was specified (=>
        // not
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

            queryStringComposer.addParameter(ContentElementUtil.TITLE_FIELD, searchForm.getTitle());
            Field field = nodeManager.getField(ContentElementUtil.TITLE_FIELD);
            Constraint titleConstraint = SearchUtil.createLikeConstraint(query, field, searchForm.getTitle());
            SearchUtil.addConstraint(query, titleConstraint);
        }

        // And some keyword searching
        if (!StringUtil.isEmpty(searchForm.getKeywords())) {
            queryStringComposer.addParameter(ContentElementUtil.KEYWORD_FIELD, searchForm.getKeywords());
            Field keywordField = nodeManager.getField(ContentElementUtil.KEYWORD_FIELD);
            List<String> keywords = KeywordUtil.getKeywords(searchForm.getKeywords());
            for (String keyword : keywords) {
                Constraint keywordConstraint = SearchUtil.createLikeConstraint(query, keywordField, keyword);
                SearchUtil.addORConstraint(query, keywordConstraint);
            }
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

        // Add the user personal:
        if (!StringUtil.isEmpty(searchForm.getPersonal())) {

            String useraccount = cloud.getUser().getIdentifier();
            if (ContentElementUtil.LASTMODIFIER_FIELD.equals(searchForm.getPersonal())) {
                SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIER_FIELD, useraccount);
            }
            if (AUTHOR.equals(searchForm.getPersonal())) {
                SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.CREATOR_FIELD, useraccount);
            }
            queryStringComposer.addParameter(PERSONAL, searchForm.getPersonal());
        }

        // Add the user
        if (!StringUtil.isEmpty(searchForm.getUseraccount())) {
            String useraccount = searchForm.getUseraccount();
            SearchUtil.addEqualConstraint(query, nodeManager, ContentElementUtil.LASTMODIFIER_FIELD, useraccount);
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

        int resultCount = Queries.count(query);
        NodeList results = cloud.getList(query);

        // Set everyting on the request.
        searchForm.setResultCount(resultCount);
        searchForm.setResults(results);
        request.setAttribute(GETURL, queryStringComposer.getQueryString());

        return super.execute(mapping, form, request, response, cloud);
    }

    private void deleteContent(String deleteContentRequest) {
        StringTokenizer commandAndNumber = new StringTokenizer(deleteContentRequest, ":");
        String command = commandAndNumber.nextToken();
        String nunmber = commandAndNumber.nextToken();

        if ("moveToRecyclebin".equals(command)) {
            moveContentToRecyclebin(nunmber);
        }

        if ("permanentDelete".equals(command)) {
            deleteContentPermanent(nunmber);
        }

    }

    private void deleteContentPermanent(String objectnumber) {
        CloudProvider provider = CloudProviderFactory.getCloudProvider();
        Cloud cloud = provider.getCloud();

        Node objectNode = cloud.getNode(objectnumber);
        if (Workflow.hasWorkflow(objectNode)) {
            // at this time complete is the same as remove
            Workflow.complete(objectNode);
        }
        objectNode.delete(true);

    }

    private void moveContentToRecyclebin(String nunmber) {
        CloudProvider provider = CloudProviderFactory.getCloudProvider();
        Cloud cloud = provider.getCloud();

        Node objectNode = cloud.getNode(nunmber);
        RepositoryUtil.removeCreationRelForContent(objectNode);
        RepositoryUtil.removeContentFromAllChannels(objectNode);
        RepositoryUtil.addContentToChannel(objectNode, RepositoryUtil.getTrash(cloud));

        // unpublish and remove from workflow
        Publish.remove(objectNode);
        Workflow.remove(objectNode);
        Publish.unpublish(objectNode);
    }

}
