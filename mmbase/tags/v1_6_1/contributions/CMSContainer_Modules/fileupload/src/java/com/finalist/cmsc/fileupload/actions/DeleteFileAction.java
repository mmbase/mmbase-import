package com.finalist.cmsc.fileupload.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.finalist.cmsc.fileupload.Configuration;
import com.finalist.cmsc.fileupload.mmbase.Field;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NotFoundException;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.struts.MMBaseFormlessAction;

/**
 * Struts action class to delete a file and it's corresponding file.
 *
 * @author Auke van Leeuwen
 */
public class DeleteFileAction extends MMBaseFormlessAction {
	private static final String FORWARD_ERROR = "error";
	private static final String FORWARD_CONFIRM = "confirm";

	private static final String ACTION_REMOVE = "remove";
	private static final String ACTION_CANCEL = "cancel";

	private static final String PARAM_ID = "id";

	/** {@inheritDoc} */
	@Override
	public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
		ActionMessages messages = new ActionMessages();

		// get the file node (possibly throw an error)
		Node fileNode = null;
		try {
			int fileId = Integer.parseInt(request.getParameter(PARAM_ID));
			fileNode = cloud.getNode(fileId);
		} catch (NumberFormatException nfe) {
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("fileupload.delete.error.invalidparam"));
			saveErrors(request, messages);

			return mapping.findForward(FORWARD_ERROR);
		} catch (NotFoundException nfe) {
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("fileupload.delete.error.notfound"));
			saveErrors(request, messages);

			return mapping.findForward(FORWARD_ERROR);
		}

		// delete or cancel action
		if(request.getParameter(ACTION_REMOVE) != null) {
			deleteFileNode(fileNode, messages);
			saveMessages(request, messages);

			return mapping.findForward(SUCCESS);
		} else if (request.getParameter(ACTION_CANCEL) != null) {
			return mapping.findForward(CANCEL);
		}

		// default action: confirm
		request.setAttribute("fileNode", fileNode);
		return mapping.findForward(FORWARD_CONFIRM);
	}

	/** {@inheritDoc} */
	@Override
	public String getRequiredRankStr() {
		return SITEADMIN;
	}

	private void deleteFile(Node fileNode) throws IOException {
		String filename = fileNode.getStringValue(Field.FILENAME.getName());
		String storePath = Configuration.getStoragePath();
		File file = new File(storePath + filename);

		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		file.delete();
	}

	private void deleteFileNode(Node fileNode, ActionMessages messages) throws IOException {
		try {
			deleteFile(fileNode);
		} catch (FileNotFoundException e) {
			// the node will be deleted anyway, this is a informational message
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("fileupload.delete.filenotdeleted", e
					.getMessage()));
		}

		// Remove the (MMBase) meta-data
		Publish.unpublish(fileNode);
		fileNode.delete(true);

		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("fileupload.delete.successful"));
	}
}
