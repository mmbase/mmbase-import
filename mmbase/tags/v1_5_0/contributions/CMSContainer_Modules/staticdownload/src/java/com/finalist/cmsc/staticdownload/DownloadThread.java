package com.finalist.cmsc.staticdownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

public class DownloadThread extends Thread {

	private static Log log = LogFactory.getLog(DownloadThread.class);

	private String url;
	private DownloadSettings downloadSettings;
	private String currentFile;
	private Exception exception;
	private long startTime;
	private long endTime = -1;
	private String fileName;
	private Node node;
	private static ArrayList<String> redownloadfiles = new ArrayList<String>();
	private String webPath;
	private String webappName = "";
	private String[] suffix;

	private static final String[] EXCLUDE_FILES = new String[] { "admin",
			"data", "editors", "htmlarea", "jsp", "META-INF", "mmbase",
			"WEB-INF", "xinha" };

	private HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();

	private static final int BUFFER = 2048;

	public DownloadThread(String url, DownloadSettings downloadSettings) {
		super("Downloading " + url);
		this.url = url;
		this.downloadSettings = downloadSettings;
		this.webappName = downloadSettings.getWebappName();
	}

	public void start() {
		startTime = System.currentTimeMillis();
		createNode();
		super.start();
	}

	/**Set the suffix according to the txt file*/
	private void setupSuffix() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("../config/subfix.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line;
		try {
			while (StringUtils.isNotEmpty(line = br.readLine())) {
				buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("Some errors happened while reading the subfix.txt");
		} finally {
			if (is != null) {
				is.close();
			}
			if (br!= null) {
			   br.close();
			}
		}
		suffix = buffer.toString().split(";");
	}

	public void run() {
		try {
			cleanUp();
			download();
			File file = new File(downloadSettings.getTempPath());//get the files which wget last download
			setupSuffix();
			modifyDownloadPath();
			findAssociatedFiles(file, new MyFilenameFilter(".css", ".js", ".html"));
			redownload(redownloadfiles);
			redownloadfiles.clear();//this will avoid to redownload picture in different request
			zip();
		} catch (Exception e) {
			exception = e;
		}
		endTime = System.currentTimeMillis();
		updateNode();
	}

	private void updateNode() {
		node.setDateValue("completed", new Date(endTime));
		node.setStringValue("report", getStatus());
		if (fileName != null) {
			node.setStringValue("filename", downloadSettings.getDownloadUrl()
					+ "/" + fileName);
		}

		if (exception != null) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			node.setStringValue("error", stacktrace);
		}
		node.commit();
	}

	private Node createNode() {
		Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
		node = cloud.getNodeManager("staticdownload").createNode();
		node.setDateValue("started", new Date(startTime));
		node.commit();
		return node;
	}

	private void cleanUp() {
		File f = new File(downloadSettings.getTempPath());
		deleteRecursive(f, true);
	}

	private void deleteRecursive(File f, boolean isFirst) {
		if (f.isDirectory()) {
			File files[] = f.listFiles();
			for (File file : files) {
				deleteRecursive(file, false);
			}
		}
		if (!isFirst) {
			f.delete();
		}
	}

	private void zip() throws IOException {
		fileName = "download_" + node.getNumber() + ".zip";
		FileOutputStream dest = new FileOutputStream(downloadSettings
				.getStorePath()
				+ "/" + fileName);
		ZipOutputStream out = new ZipOutputStream(
				new BufferedOutputStream(dest));
		// out.setMethod(ZipOutputStream.DEFLATED);
		// get a list of files from current directory
		File f = new File(downloadSettings.getTempPath());
		File containingDir = f.listFiles()[0];
		zipFile(containingDir, out,
				containingDir.getAbsolutePath().length() + 1);

		String realPath = downloadSettings.getServletContext().getRealPath("/");
		File realFile = new File(realPath);

		for (File file : realFile.listFiles()) {

			if (includeFile(file.getName()) && file.isDirectory()) {

				zipFile(file, out, realPath.length());
			}
		}

		out.close();
	}

	private void fixFile(File file) throws IOException {
		String inputData = readFile(file);

		String outputData = fixFlash(inputData);

		if (inputData == null || !outputData.equals(inputData)) {
			writeFile(file, outputData);
		}
	}

	private void writeFile(File file, String outputData) throws IOException {
	   PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
			writer.write(outputData);
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private String readFile(File file) throws IOException {
		BufferedReader reader = null;
		StringBuffer buffer = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
      if (buffer != null) {
         return buffer.toString();
      } else return null;
      
	}

	private boolean includeFile(String name) {
		for (String exclude : EXCLUDE_FILES) {
			if (name.equals(exclude)) {
				return false;
			}
		}
		return true;
	}

	private void zipFile(File f, ZipOutputStream out, int trimLength)
			throws IOException {
		File files[] = f.listFiles();
		byte data[] = new byte[BUFFER];

		for (File file : files) {

			log.info("Adding: " + file.getName());
			if (file.isDirectory()) {
				zipFile(file, out, trimLength);
			} else {
				if (file.getName().endsWith(".html")) {
					fixFile(file);
				}
				FileInputStream fi = new FileInputStream(file);
				BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(file.getAbsolutePath().substring(
						trimLength));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
		}
	}

	/** make the web address useful*/
	private void modifyDownloadPath() {
		if (!downloadSettings.getLiveUrl().endsWith("/")) {
			webPath = downloadSettings.getLiveUrl() + "/";
		} else {
			webPath = downloadSettings.getLiveUrl();
		}
	}

	/**redownload the things which the before had not done*/
	private void redownload(List<String> paths) throws IOException {
		for (String path : paths) {
			pubDownload(path);
		}
	}

	private void pubDownload(String urll) throws IOException {
		List<String> command = new ArrayList<String>();
		command.add(downloadSettings.getWgetPath());
		command.add(urll);
		for (String option : DownloadSettings.WGET_OPTIONS) {
			command.add(option);
		}
		command.add("--level=" + downloadSettings.getLevel());

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(downloadSettings.getTempPath()));
		final Process process = builder.start();

		int returnCode = -1;
		boolean stillRunning = true;
		while (stillRunning) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// nothing
			}

			String errors = readOutput(process.getErrorStream());
			processErrors(errors);

			String output = readOutput(process.getInputStream());
			processOutput(output);

			log.info(getStatus());

			try {
				returnCode = process.exitValue();
				stillRunning = false;
			} catch (IllegalThreadStateException e) {
				// nothing
			}
		}

		log.info("Program terminated! " + returnCode);

	}

	private void download() throws IOException {
		pubDownload(url);
	}

	private void processErrors(String text) {

		for (String line : text.split("\n")) {
			if (line.startsWith(DownloadSettings.DOWNLOADING_LINE)) {
				currentFile = line.substring(DownloadSettings.DOWNLOADING_LINE
						.length() + 1);
			} else if (line.startsWith(DownloadSettings.RESPONSE_LINE)) {
				String response = line.substring(DownloadSettings.RESPONSE_LINE
						.length() + 1);

				ArrayList<String> responseResult = results.get(response);
				if (responseResult == null) {
					responseResult = new ArrayList<String>();
					results.put(response, responseResult);
				}
				responseResult.add(currentFile);
			}
		}
	}

	public String getStatus() {
		StringBuffer status = new StringBuffer();
		for (String key : results.keySet()) {
			ArrayList<String> downloads = results.get(key);
			status.append(key);
			status.append(": #");
			status.append(downloads.size());
			status.append("\n");
			if (!key.contains("200") && !key.contains("302")) {
				for (String download : downloads) {
					status.append("- ");
					status.append(download);
					status.append("\n");
				}
			}
		}
		return status.toString();
	}

	private void processOutput(String output) {
		if (output != null) {
			log.info(output);
		}
	}

	private String readOutput(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		StringBuffer result = new StringBuffer();

		while (reader.ready()) {
			result.append(reader.readLine());
			result.append("\n");
		}
		return result.toString();
	}

	public final static String fixFlash(String string) {
		int startIndex = 0;
		while ((startIndex = string.indexOf("<object", startIndex)) != -1) {
			System.out.println("before >> " + string);
			int endIndex = string.indexOf("</object", startIndex);
			if (endIndex == -1) {
				endIndex = string.length();
			}

			int swfIndex[] = new int[2];
			swfIndex[0] = string.indexOf(".swf", startIndex);
			swfIndex[1] = string.indexOf(".swf", swfIndex[0] + 1);

			String swfString[] = new String[2];
			int swfStart[] = new int[2];
			int swfEnd[] = new int[2];
			for (int count = 0; count < 2; count++) {
				swfStart[count] = string.lastIndexOf("\"", swfIndex[count]) + 1;
				swfEnd[count] = string.indexOf("\"", swfIndex[count]);
				swfString[count] = string.substring(swfStart[count],
						swfEnd[count]);
			}

			boolean embedFirst = string.lastIndexOf("embed", swfIndex[0]) > string
					.lastIndexOf("param", swfIndex[0]);
			int from = (embedFirst) ? 0 : 1;
			int to = (embedFirst) ? 1 : 0;

			string = string.substring(0, swfStart[to]) + swfString[from]
					+ string.substring(swfEnd[to]);

			System.out.println("after >>> " + string);
			startIndex = string.indexOf("</object", startIndex);
		}
		return string;
	}

	/**
	 * the method is used to filter files by the suffixes
	 */
	static class MyFilenameFilter implements FilenameFilter {
		private String suffix = "";
		private String suffix1 = "";
		private String suffix2 = "";

		public MyFilenameFilter(String suffix, String suffix1, String suffix2) {
			this.suffix = suffix;
			this.suffix1 = suffix1;
			this.suffix2 = suffix2;
		}

		public boolean accept(File dir, String name) {
			// TODO Auto-generated method stub
			if (new File(dir, name).isFile()) {
				return name.endsWith(suffix) || name.endsWith(suffix1)
						|| name.endsWith(suffix2);
			} else {
				return true;
			}
		}
	}

	private void spellPath(String targetPath, String directoryName) {
		String regEx1 = "\\.\\.";
		Pattern p1 = Pattern.compile(regEx1);
		Matcher m1 = p1.matcher(targetPath);
		String str1 = m1.replaceAll(directoryName);
		String path = webPath + str1;
		if (!redownloadfiles.contains(path))
			redownloadfiles.add(path);
	}

	private String getUrl() {
		return downloadSettings.getLiveUrl();
	}

	/**Use the filter to find the associated files which last wget downloads,
	 * then pick up the getTargetStringAndWriteFile() method
	 * @throws IOException */
	private void findAssociatedFiles(File file, MyFilenameFilter myfilter)
			throws IOException {
		//String fileName = file.getAbsolutePath();
		//if (!fileName.endsWith(File.separator)) {
		//	fileName = fileName + File.separator;
		//}
		if (!file.exists()) {
			return;
		}
		File[] files = file.listFiles(myfilter);
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				for (String suf : suffix) {
					String outputData = this.getTargetStringAndWriteFile(
							files[i], suf);
					if (outputData.length() != 0
							&& files[i].length() != outputData.length()) {
						try {
							this.writeFile(files[i], outputData);
						} catch (IOException e) {
							log.info("Something wrong in writing data into files");
						}
					}
				}
			} else if (files[i].isDirectory()) {
				findAssociatedFiles(files[i], myfilter);
			}
		}
	}

	/**Regular expressions for judging*/
	private String regularJudge(String regEx, String targetString) {
		String targetPath = "";
		if (regEx != null && targetString != null) {
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(targetString);
			boolean rs = m.find();
			if (rs) {
				targetPath = m.group();
			}
		}
		return targetPath;
	}

	/**Regular expressions for replacing*/
	private String regularReplace(String target, String former, String newer) {
		String regEx = former;
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(target);
		return m.replaceAll(newer);
	}

	/**
	 *Spell the path of downloading , modify the paths in files
	 *@param targetStringNew : the path of pictures in former file
	 *@param tempString : the string in file at current line
	 *@param file : the current file
	 *@return the new string in current line
	 */
	private String downloadAndModifyPath(String targetStringNew,
			String tempString, File file) {
		String downloadPath;
		if (StringUtils.isEmpty(webappName)) {
			downloadPath = webPath + targetStringNew.substring(1);
		} else {
			downloadPath = webPath
					+ targetStringNew.substring(webappName.length() + 2);
		}
		if (!redownloadfiles.contains(downloadPath))
			redownloadfiles.add(downloadPath);
		
		String filePath = file.getParent();
		int beginIndex = filePath.indexOf(getUrl());
		String filePathIntercept = filePath.substring(beginIndex
				+ getUrl().length());
		String[] strs = filePathIntercept.split("\\\\");
		int num = strs.length;
		String docs = "";
		for (int i = 1; i < num; i++) {
			docs += "../";
		}
		if (StringUtils.isEmpty(filePathIntercept)
				&& StringUtils.isEmpty(webappName)) {
			String targetStringNews = targetStringNew.substring(1);
			tempString = this.regularReplace(tempString, targetStringNew,
					targetStringNews);
		} else if (StringUtils.isNotEmpty(filePathIntercept)
				&& StringUtils.isEmpty(webappName)) {
			tempString = this.regularReplace(tempString, targetStringNew, docs
					+ targetStringNew.substring(1));
		} else if (StringUtils.isEmpty(filePathIntercept)
				&& StringUtils.isNotEmpty(webappName)) {
			String targetStringNews = targetStringNew.substring(webappName
					.length() + 2);
			tempString = this.regularReplace(tempString, targetStringNew,
					targetStringNews);
		} else if (StringUtils.isNotEmpty(filePathIntercept)
				&& StringUtils.isNotEmpty(webappName)) {
			tempString = this.regularReplace(tempString,
					"/" + webappName + "/", docs);
		}
		return tempString;
	}

	private void addDownloadPath(String str, File file, String targetPath) {
		String filepath = file.getAbsolutePath();
		String url = downloadSettings.getLiveUrl();
		int tagStart = filepath.indexOf(url);
		String u = filepath.substring(tagStart);
		int tagEnd = u.indexOf("\\" + str);
		String m = u.substring(0, tagEnd);
		String ss = regularReplace(m, "\\\\", "/");
		String str1 = regularReplace(targetPath, "\\.\\.", ss);
		
		if (!redownloadfiles.contains(str1))
			redownloadfiles.add(str1);
	}

	/**
	 * Use regular expressions to get the paths of pictures
	 * and rewrite the path into the file
	 * @param file : the file to be read
	 * @param suffix : the messages about suffix of pictures
	 * @throws IOException 
	 */
	private String getTargetStringAndWriteFile(File file, String suffix)
			throws IOException {
		BufferedReader reader = null;
		String outputData = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null
					&& !file.getName().endsWith(".html")) {
				String regEx = "[\\w/.-]+\\." + suffix;
				String targetPath = regularJudge(regEx, tempString);
				if (targetPath != "") {
					int tagStart = -1;
					String fileParentPath = file.getParent() + File.separator;
					String directoryName = "";
					if (StringUtils.isNotEmpty(webappName)
							&& (tagStart = fileParentPath.indexOf(webappName)) >= 0
							&& fileParentPath.contains("js")) {
						int tagEnd = fileParentPath.indexOf("\\js", tagStart
								+ webappName.length() + 1);
						if (tagEnd >= 0) {
							directoryName = fileParentPath.substring(tagStart
									+ webappName.length() + 1, tagEnd);
							this.spellPath(targetPath, directoryName);
						}
					} else if (StringUtils.isNotEmpty(webappName)
							&& (tagStart = fileParentPath.indexOf(webappName)) >= 0
							&& fileParentPath.contains("css")) {
						int tagEnd = fileParentPath.indexOf("\\css", tagStart
								+ webappName.length() + 1);
						if (tagEnd >= 0) {
							directoryName = fileParentPath.substring(tagStart
									+ webappName.length() + 1, tagEnd);
							this.spellPath(targetPath, directoryName);
						}
					} else if (StringUtils.isEmpty(webappName)
							&& fileParentPath.contains("css")) {
						addDownloadPath("css", file, targetPath);
					} else if (StringUtils.isEmpty(webappName)
							&& fileParentPath.contains("js")) {
						addDownloadPath("js", file, targetPath);
					}
				}
			}
			while ((tempString = reader.readLine()) != null
					&& file.getName().endsWith(".html")) {
				String targetString = regularJudge("(?m)^\\s*var.*" + suffix
						+ ".*$", tempString);
				if (StringUtils.isNotEmpty(targetString)) {
					String targetStringNew = regularJudge("[\\w/\\.-]+\\."
							+ suffix, targetString);
					if (StringUtils.isNotEmpty(targetStringNew)) {
						if (targetStringNew.startsWith("/" + webappName)
								&& StringUtils.isNotEmpty(webappName)) {
							tempString = this.downloadAndModifyPath(
									targetStringNew, tempString, file);
						} else if (targetStringNew.startsWith("/")
								&& StringUtils.isEmpty(webappName)) {
							tempString = this.downloadAndModifyPath(
									targetStringNew, tempString, file);
						}
					}
				}
				String str = regularJudge("[\\s.]*background-image:\\s*url.*",
						tempString);
				if (StringUtils.isNotEmpty(str)) {
					String targetName = regularJudge("[\\w/]*\\." + suffix, str);
					if (StringUtils.isNotEmpty(targetName)) {
						if (targetName.startsWith("/" + webappName)
								&& StringUtils.isNotEmpty(webappName)) {
							tempString = this.downloadAndModifyPath(targetName,
									tempString, file);
						} else if (targetName.startsWith("/")
								&& StringUtils.isEmpty(webappName)) {
							tempString = this.downloadAndModifyPath(targetName,
									tempString, file);
						}
					}
				}
				outputData += tempString + "\r\n";
			}
		} catch (IOException e) {
			log.error("Something wrong while reading ,", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return outputData;
	}

	public boolean isDownloading() {
		return (endTime == -1);
	}

	public Exception getException() {
		return exception;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getStartTime() {
		return startTime;
	}
}
