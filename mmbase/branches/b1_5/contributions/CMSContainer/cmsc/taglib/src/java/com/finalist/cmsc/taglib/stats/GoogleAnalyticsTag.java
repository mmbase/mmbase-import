/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.stats;

import java.io.IOException;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.navigation.ServerUtil;

public class GoogleAnalyticsTag extends SimpleTagSupport {

   private String account;

   @Override
   public void doTag() throws IOException {
      if (ServerUtil.isProduction() && (ServerUtil.isLive() || ServerUtil.isSingle())) {
         if (StringUtils.isBlank(account)) {
            account = PropertiesUtil.getProperty("googleanalytics.account");
         }

         if (StringUtils.isNotBlank(account)) {
            String javascript = "<script type=\"text/javascript\">\r\n"
                  + "var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\r\n"
                  + "document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\r\n"
                  + "</script>\r\n" 
                  + "<script type=\"text/javascript\">\r\n" + "var pageTracker = _gat._getTracker(\"" + account + "\");\r\n" 
                  + "pageTracker._initData();\r\n" 
                  + "pageTracker._trackPageview();\r\n"
                  + "</script>\r\n";

            PageContext ctx = (PageContext) getJspContext();
            ctx.getOut().write(javascript);
         }
      }
   }

   public String getAccount() {
      return account;
   }

   public void setAccount(String account) {
      this.account = account;
   }

}
