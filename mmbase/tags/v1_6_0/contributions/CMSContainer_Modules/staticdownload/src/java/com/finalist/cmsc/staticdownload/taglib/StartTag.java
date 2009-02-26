package com.finalist.cmsc.staticdownload.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.staticdownload.DownloadSettings;
import com.finalist.cmsc.staticdownload.StaticDownload;

public class StartTag extends SimpleTagSupport {

   private String startedVar;


   public void doTag() {

      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      String liveUrl = PropertiesUtil.getProperty("staticdownload.liveurl");
      String tempPath = PropertiesUtil.getProperty("staticdownload.temppath");
      String storePath = PropertiesUtil.getProperty("staticdownload.storepath");
      String wgetPath = PropertiesUtil.getProperty("staticdownload.wgeturl");
      String downloadUrl = PropertiesUtil.getProperty("staticdownload.downloadurl");
      String webappName = PropertiesUtil.getProperty("staticdownload.webappName");
      DownloadSettings downloadSettings = new DownloadSettings(50,liveUrl, tempPath, storePath, wgetPath, downloadUrl,webappName, 
    		  ctx.getServletContext());

      // DownloadThread downloadThread = new
      // DownloadThread("http://www.cmscontainer.org", downloadSettings);
      // DownloadThread downloadThread = new
      // DownloadThread("http://nijmegen-demo.finalist.com/nijmegen-live/www.nijmegen.nl",
      // downloadSettings);
      boolean started = StaticDownload.startDownload(liveUrl, downloadSettings);

      if (startedVar != null) {
         request.setAttribute(startedVar, Boolean.valueOf(started));
      }
   }


   public void setStartedVar(String startedVar) {
      this.startedVar = startedVar;
   }

}
