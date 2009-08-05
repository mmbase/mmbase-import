package com.finalist.cmsc.staticdownload;

import javax.servlet.ServletContext;

public class DownloadSettings {

   public final static String[] WGET_OPTIONS = "-rkpmE -erobots=off".split(" ");
   public final static String RESPONSE_LINE = "HTTP request sent, awaiting response...";
   public final static String DOWNLOADING_LINE = "           =>";

   private int level;
   private String liveUrl;
   private String tempPath;
   private String storePath;
   private String wgetPath;
   private String downloadUrl;
   private ServletContext servletContext;
   private String webappName;


   public DownloadSettings(int level, String liveUrl , String targetPath, String storePath, String wgetPath, String downloadUrl,String webappName,
         ServletContext servletContext) {
      this.level = level;
      this.tempPath = targetPath;
      this.storePath = storePath;
      this.wgetPath = wgetPath;
      this.downloadUrl = downloadUrl;
      this.servletContext = servletContext;
      this.webappName = webappName; 
      this.liveUrl = liveUrl;
   }


   protected String getWgetPath() {
      return wgetPath;
   }


   protected int getLevel() {
      return level;
   }


   protected String getTempPath() {
      return tempPath;
   }


   public String getDownloadUrl() {
      return downloadUrl;
   }


   public String getStorePath() {
      return storePath;
   }


   public ServletContext getServletContext() {
      return servletContext;
   }


   public String getWebappName() {
	  return webappName;
   }
   public String getLiveUrl(){
       return liveUrl;	
   }



}
