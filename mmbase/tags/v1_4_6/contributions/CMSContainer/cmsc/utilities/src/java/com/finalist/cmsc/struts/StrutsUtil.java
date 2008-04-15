/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

public class StrutsUtil {

   public static String getMessage(HttpServletRequest request, String key) {
      return getMessage(request, key, null);
   }


   public static String getMessage(HttpServletRequest request, String key, Object[] values) {
      MessageResources messages = getMessageResources(request);
      Locale locale = getLocale(request);
      return messages.getMessage(locale, key, values);
   }


   public static MessageResources getMessageResources(HttpServletRequest request) {
      MessageResources messages = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
      return messages;
   }


   public static Locale getLocale(HttpServletRequest request) {
      Locale result = null;
      HttpSession session = request.getSession();
      if (session != null) {
         result = (Locale) session.getAttribute(Globals.LOCALE_KEY);
         if (result == null)
            result = Locale.getDefault();
      }
      else {
         result = Locale.getDefault();
      }
      return result;
   }


   public static void setLocale(HttpServletRequest request, Locale locale) {
      HttpSession session = request.getSession(true);
      session.setAttribute(Globals.LOCALE_KEY, locale);
   }
}
