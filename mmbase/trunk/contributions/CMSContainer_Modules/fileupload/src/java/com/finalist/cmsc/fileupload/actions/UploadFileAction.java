package com.finalist.cmsc.fileupload.actions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.remotepublishing.util.PublishUtil;

import com.finalist.cmsc.fileupload.Configuration;
import com.finalist.cmsc.fileupload.forms.UploadFileForm;
import com.finalist.cmsc.fileupload.mmbase.Builder;
import com.finalist.cmsc.fileupload.mmbase.Field;
import com.finalist.cmsc.struts.MMBaseAction;

/**
 * This action writes the uploaded file to disk and creates a MMBase object
 * (file) that is used as metadata for that particular file. The most crucial
 * thing about this meta data is that it contains a relative link to where the
 * file is stored on disk.
 *
 * @author Auke van Leeuwen
 */
public class UploadFileAction extends MMBaseAction {
	private static final Log log = LogFactory.getLog(UploadFileAction.class);

	/** {@inheritDoc} */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, Cloud cloud) throws Exception {

		// form information
		UploadFileForm uploadForm = (UploadFileForm) form;

		String relativeFilename = null;
		try {
			relativeFilename = saveFileToDisk(uploadForm.getFile());
		} catch (IOException e) {
			ActionMessages errors = new ActionMessages();
			ActionMessage ioError = new ActionMessage("fileupload.upload.error.ioerror", e.getMessage());
			errors.add(ActionMessages.GLOBAL_MESSAGE, ioError);
			saveErrors(request, errors);

			return mapping.getInputForward();
		}

		// create an mmbase 'meta-data' object.
		createFileNode(cloud, uploadForm, relativeFilename);

		return mapping.findForward(SUCCESS);
	}

	private void createFileNode(Cloud cloud, UploadFileForm uploadForm, String relativeFilename) {
		NodeManager fileNodeManager = cloud.getNodeManager(Builder.FILE.getName());

		Node fileNode = fileNodeManager.createNode();
		fileNode.setStringValue(Field.TITLE.getName(), uploadForm.getTitle());
		fileNode.setStringValue(Field.DESCRIPTION.getName(), uploadForm.getDescription());
		fileNode.setStringValue(Field.FILENAME.getName(), relativeFilename);
		fileNode.commit();

		PublishUtil.publishOrUpdateNode(fileNode);
	}

	private String saveFileToDisk(FormFile fileFormFile) throws IOException {
		String dateFormat = String.format("yyyy%1$sMM%1$s", File.separator);
		String dirStructure = new SimpleDateFormat(dateFormat).format(new Date());
		String storagePath = Configuration.getStoragePath() + dirStructure;
		File storageDirectory = new File(storagePath);

		// This should result in a <storagepath>/<year>/<month>/ structure.
		storageDirectory.mkdirs();

		if (!storageDirectory.exists()) {
			throw new IOException("Unable to create directory structure to save the file to.");
		}

		String fileFileName = fileFormFile.getFileName();
		File fileToCreate = new File(storageDirectory, fileFileName);
		log.debug(String.format("Saving %s to %s", fileFileName, storageDirectory));

		if (fileToCreate.exists()) {
			throw new IOException("File with the same name already exists!");
		}

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(fileToCreate));
			bos.write(fileFormFile.getFileData());
			bos.flush();
		} finally {
			bos.close();
		}

		// we want to return the 'relative' name + structure in a URL kind of
		// way, i.e. any File.separator should be converted to '/'.
		return dirStructure.replace(File.separatorChar, '/') + fileToCreate.getName();
	}
}
