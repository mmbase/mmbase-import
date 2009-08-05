package com.finalist.cmsc.fileupload.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.cmsc.fileupload.Configuration;
import com.finalist.cmsc.fileupload.Configuration.ConfigurationException;
import com.finalist.cmsc.fileupload.forms.ListFilesForm;
import com.finalist.cmsc.fileupload.mmbase.Builder;
import com.finalist.cmsc.fileupload.mmbase.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.struts.PagerAction;
import com.finalist.cmsc.struts.PagerForm;

/**
 * This action is responsible for retrieving the list of matching files and
 * showing them in a 'paged' way.
 *
 * @author Auke van Leeuwen
 */
public class ListFilesAction extends PagerAction {
	private static final Log log = LogFactory.getLog(ListFilesAction.class);
	private static final String FORWARD_CONFIG_ERROR = "configerror";

	/** {@inheritDoc} */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Cloud cloud) throws Exception {

		// This is more of a check for configuration errors
		try {
			request.setAttribute("storagePath", Configuration.getStoragePath());
			request.setAttribute("urlPrefix", Configuration.getUrlPrefix());
		} catch (ConfigurationException ce) {
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, ce.getActionMessage());
			saveErrors(request, messages);

			return mapping.findForward(FORWARD_CONFIG_ERROR);
		}

		ListFilesForm ListFilesForm = (ListFilesForm) form;
		List<Node> results = getPagedResults(cloud, ListFilesForm);
		ListFilesForm.setResults(results);

		return super.execute(mapping, ListFilesForm, request, response, cloud);
	}

	/**
	 * Returns a list of results based on the current cloud and (pager-)form.
	 * The pager form contains information about the current orderning and page
	 * offset etc.
	 *
	 * @param cloud
	 *            the cloud
	 * @param actionForm
	 *            the form (normally a subclass of {@link PagerForm}).
	 * @return a list of nodes based on the information in the form.
	 */
	@SuppressWarnings("unchecked")
	protected List<Node> getPagedResults(Cloud cloud, ActionForm actionForm) {
		ListFilesForm form = (ListFilesForm) actionForm;

		NodeManager fileNodeManager = cloud.getNodeManager(Builder.FILE.getName());

		if (fileNodeManager == null) {
			log.warn(String.format("'%s' nodemanager not found!", Builder.FILE.getName()));

			return new ArrayList<Node>();
		}

		// Construct the query
		NodeQuery query = fileNodeManager.createQuery();
		SearchUtil.addSortOrder(query, fileNodeManager, "creationdate", "down");

		// filter(s)
		if(!StringUtils.isBlank(form.getSearchTitle())) {
			org.mmbase.bridge.Field field = fileNodeManager.getField(Field.TITLE.getName());
			SearchUtil.addLikeConstraint(query, field, form.getSearchTitle());
		}

		if(!StringUtils.isBlank(form.getSearchFilename())) {
			org.mmbase.bridge.Field field = fileNodeManager.getField(Field.FILENAME.getName());
			SearchUtil.addLikeConstraint(query, field, form.getSearchFilename());
		}

		// offset + max results
		int pageOffset = 0;
		if (form.getOffset() != null && form.getOffset().matches("\\d+")) {
			pageOffset = Integer.parseInt(form.getOffset());
			// I have no clue why this is a string, but since it is...
			form.setOffset(Integer.toString(pageOffset));
		}
		SearchUtil.addLimitConstraint(query, pageOffset, form.getResultsPerPage());

		// run the query
		List<Node> results = fileNodeManager.getList(query);

		// safe some data in the form
		form.setResultCount(Queries.count(query));

		return results;
	}
}
