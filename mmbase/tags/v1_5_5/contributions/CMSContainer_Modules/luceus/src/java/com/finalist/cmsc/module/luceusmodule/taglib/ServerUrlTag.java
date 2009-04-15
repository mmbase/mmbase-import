/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.module.luceusmodule.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.module.luceusmodule.LuceusModule;

/**
 * Tag to return the Luceus server URL from the Luceusmodule
 * 
 * @author Wouter Heijke
 */
public class ServerUrlTag extends LuceusmoduleTag {
   private static Log log = LogFactory.getLog(ServerUrlTag.class);


   @Override
   public void doTag() throws IOException {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      LuceusModule module = getModule();
      if (module != null) {
         String url = module.getServerUrl();

         // handle result
         if (var != null) {
            // put in variable
            if (url != null) {
               request.setAttribute(var, url);
            }
            else {
               request.removeAttribute(var);
            }
         }
         else {
            // write
            ctx.getOut().print(url);
         }
      }
      else {
         log.warn("Luceusmodule not running");
      }
   }
}
