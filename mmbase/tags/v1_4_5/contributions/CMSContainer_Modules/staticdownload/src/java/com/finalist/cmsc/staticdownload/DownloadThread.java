package com.finalist.cmsc.staticdownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

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

   private static final String[] EXCLUDE_FILES = new String[] { "admin", "data", "editors", "htmlarea", "jsp",
         "META-INF", "mmbase", "WEB-INF", "xinha" };

   private HashMap<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();

   private static final int BUFFER = 2048;


   public DownloadThread(String url, DownloadSettings downloadSettings) {
      super("Downloading " + url);
      this.url = url;
      this.downloadSettings = downloadSettings;
   }


   public void start() {
      startTime = System.currentTimeMillis();
      createNode();
      super.start();
   }


   public void run() {
      try {
         cleanUp();
         download();
         zip();
      }
      catch (Exception e) {
         exception = e;
      }
      endTime = System.currentTimeMillis();
      updateNode();
   }


   private void updateNode() {
      node.setDateValue("completed", new Date(endTime));
      node.setStringValue("report", getStatus());
      if (fileName != null) {
         node.setStringValue("filename", downloadSettings.getDownloadUrl() + "/" + fileName);
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
      FileOutputStream dest = new FileOutputStream(downloadSettings.getStorePath() + "/" + fileName);
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
      // out.setMethod(ZipOutputStream.DEFLATED);
      // get a list of files from current directory
      File f = new File(downloadSettings.getTempPath());
      File containingDir = f.listFiles()[0];
      zipFile(containingDir, out, containingDir.getAbsolutePath().length() + 1);

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

      if (!outputData.equals(inputData)) {
         writeFile(file, outputData);
      }
   }


   private void writeFile(File file, String outputData) throws IOException {
      FileWriter writer = null;
      try {
         writer = new FileWriter(file);
         writer.write(outputData);
         writer.flush();
      }
      finally {
         if (writer != null) {
            writer.close();
         }
      }
   }


   private String readFile(File file) throws IOException {
      BufferedReader reader = null;
      try {
         reader = new BufferedReader(new FileReader(file));

         StringBuffer buffer = new StringBuffer();
         String line;
         while ((line = reader.readLine()) != null) {
            buffer.append(line);
         }
         return buffer.toString();
      }
      finally {
         if (reader != null) {
            reader.close();
         }
      }
   }


   private boolean includeFile(String name) {
      for (String exclude : EXCLUDE_FILES) {
         if (name.equals(exclude)) {
            return false;
         }
      }
      return true;
   }


   private void zipFile(File f, ZipOutputStream out, int trimLength) throws IOException {
      File files[] = f.listFiles();
      byte data[] = new byte[BUFFER];

      for (File file : files) {

         log.info("Adding: " + file.getName());
         if (file.isDirectory()) {
            zipFile(file, out, trimLength);
         }
         else {
            if (file.getName().endsWith(".html")) {
               fixFile(file);
            }
            FileInputStream fi = new FileInputStream(file);
            BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(file.getAbsolutePath().substring(trimLength));
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
               out.write(data, 0, count);
            }
            origin.close();
         }
      }
   }


   private void download() throws IOException {

      List<String> command = new ArrayList<String>();
      command.add(downloadSettings.getWgetPath());
      command.add(url);
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
         }
         catch (InterruptedException e) {
         }

         String errors = readOutput(process.getErrorStream());
         processErrors(errors);

         String output = readOutput(process.getInputStream());
         processOutput(output);

         log.info(getStatus());

         try {
            returnCode = process.exitValue();
            stillRunning = false;
         }
         catch (IllegalThreadStateException e) {
         }
      }

      log.info("Program terminated! " + returnCode);

   }


   private void processErrors(String text) {

      for (String line : text.split("\n")) {
         if (line.startsWith(DownloadSettings.DOWNLOADING_LINE)) {
            currentFile = line.substring(DownloadSettings.DOWNLOADING_LINE.length() + 1);
         }
         else if (line.startsWith(DownloadSettings.RESPONSE_LINE)) {
            String response = line.substring(DownloadSettings.RESPONSE_LINE.length() + 1);

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
      for (Iterator<String> i = results.keySet().iterator(); i.hasNext();) {
         String key = i.next();
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
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
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
            swfString[count] = string.substring(swfStart[count], swfEnd[count]);
         }

         boolean embedFirst = string.lastIndexOf("embed", swfIndex[0]) > string.lastIndexOf("param", swfIndex[0]);
         int from = (embedFirst) ? 0 : 1;
         int to = (embedFirst) ? 1 : 0;

         string = string.substring(0, swfStart[to]) + swfString[from] + string.substring(swfEnd[to]);

         System.out.println("after >>> " + string);
         startIndex = string.indexOf("</object", startIndex);
      }
      return string;
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
