package com.finalist.cmsc.openoffice.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.UploadUtil;
import net.sf.mmapps.commons.util.UploadUtil.BinaryData;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.openoffice.model.OdtDocument;

/**
 * openoffice one odt document or more ,persist
 * 
 * @author
 */
public class OODocUploadUtil {

	private static final Log log = LogFactory.getLog(OODocUploadUtil.class);
	/* a temp directory used for saving files uploaded */
	public static final String TEMP_PATH = "tempDir";

	public static final String SINGLE_FILE_PATH = "singlePath";
	// set max size allowed
	private static final int MAXSIZE = 16 * 1024 * 1024;

	private static final String OPENOFFICE_ODT_MIME_TYPES[] = new String[] {
			"application/vnd.oasis.opendocument.text",
			"application/x-vnd.oasis.opendocument.text" };

	private static final int INITIAL_CAPACITY = 10;

	private BinaryData binary;

	private String channel;

	private static final String CHANNEL_KEY = "channel";

	public static final String NODE_NUMBER = "node.number";

	private OODocUploadUtil() {
	}

	public static OODocUploadUtil getInstance() {
		return new OODocUploadUtil();
	}

	public BinaryData getBinaryData() {
		return binary;
	}

	public String getChannel() {
		return channel;
	}

	// public String getChannelbak() {
	// return channelbak;
	// }

	/**
	 * openoffice odt doc and put it in cache
	 * 
	 * @param request
	 */
	public boolean upload(HttpServletRequest request, String dir)
			throws IOException {
		try {
			uploadFiles(request, MAXSIZE);
		} catch (FileUploadException e) {
			log.error("openoffice file error :" + e.getMessage());
		}
		String realPath = "";
		if (request.getAttribute("dir") != null)
			realPath = (String) request.getAttribute("dir");
		if (StringUtils.isBlank(realPath))
			realPath = dir;

		realPath += File.separator + channel;
		if (realPath.endsWith("null"))
			realPath = realPath.substring(0, realPath.length() - 5);

		if (request.getAttribute("root") != null) {
			if (!realPath.endsWith(TEMP_PATH))
				realPath = realPath.substring(0, realPath.indexOf(TEMP_PATH)
						+ TEMP_PATH.length());
			realPath += File.separator + SINGLE_FILE_PATH;
		}

		if (binary != null) {
			if (log.isDebugEnabled()) {
				log.debug("originalFileName: " + binary.getOriginalFileName());
				log.debug("contentType: " + binary.getContentType());
			}

			if (!isOdtFile(binary))
				return false;
			persistOdtDoc(binary, realPath);
		}
		return true;
	}

	// private static void addRelToWorkFlow(WorkflowService service, Node node)
	// {
	// service.create(node, null);
	// }

	private static synchronized void persistOdtDoc(
			UploadUtil.BinaryData binary, String dir) throws IOException,
			RuntimeException {
		File directory = new File(dir);
		if (!directory.exists() && !directory.mkdirs()) {
			log.warn("   mkdir error while openoffice odt document!");
			return;
		}
		File file = new File(directory.getCanonicalPath() + File.separator
				+ binary.getOriginalFileName());
		if (!file.createNewFile()) {
			log
					.warn("   create empty file error while openoffice odt document!");
		}
		FileOutputStream out = new FileOutputStream(file);
		copyStream(binary.getInputStream(), out);
		out.close();

	}

	private static void copyStream(InputStream ins, OutputStream outs)
			throws IOException {
		int bufferSize = 1024;
		byte[] writeBuffer = new byte[bufferSize];
		BufferedOutputStream bos = new BufferedOutputStream(outs, bufferSize);
		int bufferRead;
		while ((bufferRead = ins.read(writeBuffer)) != -1)
			bos.write(writeBuffer, 0, bufferRead);
		bos.flush();
		bos.close();
		outs.flush();
		outs.close();
	}

	private static void deleteOdtFiles(String dir) {
		File directory = new File(dir);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();

			for (File file : files) {
				file.delete();
			}
		}
	}

	private String[] getOdtFileNames(String dir) {
		String[] files = new String[] {};
		File directory = new File(dir);
		if (directory.exists() && directory.isDirectory()) {
			files = directory.list(new OdtFilter());
		}
		return files;

	}

	/**
	 * get a List object of OdtDocument
	 * 
	 * @param dir
	 *            a directory ,file's real path
	 * @return
	 */
	public List<OdtDocument> getOdtDocuments(String dir) {
		List<OdtDocument> docs = new ArrayList<OdtDocument>(INITIAL_CAPACITY);
		OdtDocument odt = null;
		String[] files = getOdtFileNames(dir);

		for (String file : files) {
			odt = new OdtDocument();
			odt.setTitle(file);
			docs.add(odt);

		}

		return docs;
	}

	private static boolean isOdtFile(BinaryData binary) {

		for (String element : OPENOFFICE_ODT_MIME_TYPES) {
			if (element.equalsIgnoreCase(binary.getContentType())) {
				return true;
			}
		}
		if (binary.getOriginalFilePath() != null
				&& binary.getOriginalFilePath().toLowerCase().endsWith("odt"))
			return true;
		return false;
	}

	private void uploadFiles(HttpServletRequest request, int maxZize)
			throws FileUploadException {
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();
		// Create a new file openoffice handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxZize);
		// Parse the request
		List<FileItem> items = upload.parseRequest(request);

		Iterator<FileItem> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();

			if (item.isFormField()) {
				if (CHANNEL_KEY.equals(item.getFieldName())) {
					channel = item.getString();

				}

			} else if (!item.isFormField()) {
				String fullFileName = item.getName();
				if (item.get().length > 0) {
					binary = new BinaryData();
					binary.setData(item.get());
					binary.setOriginalFilePath(fullFileName);
					binary.setContentType(item.getContentType());
					if (log.isDebugEnabled())
						log.debug((new StringBuilder()).append(
								"Setting binary ").append(binary.getLength())
								.append(" bytes in type ").append(
										binary.getContentType()).append(
										" with ").append(
										binary.getOriginalFilePath()).append(
										" name").toString());

				}
			}
		}

	}

	public static void deleteSingleFile(String name, String dir) {
		File directory = new File(dir);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();

			for (File file : files) {
				if (file.getName().equals(name)) {
					file.delete();
					break;
				}
			}
		}
	}

	public class OdtFilter implements FilenameFilter {

		public boolean isFile(String file) {
			if (file.toLowerCase().endsWith(".odt")) {
				return true;
			} else {
				return false;
			}
		}

		public boolean accept(File dir, String fname) {
			return (isFile(fname));

		}

	}

}
