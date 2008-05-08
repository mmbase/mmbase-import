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

         if (!StringUtils.isBlank(account)) {
            String javascript = "<script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\">\r\n"
                  + "</script>\r\n"
                  + "<script type=\"text/javascript\">\r\n"
                  + "_uacct = \""
                  + account
                  + "\";\r\n"
                  + "urchinTracker();\r\n" + "</script>\r\n";

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
