/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.Globals;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.Locale;

/**
 * LanguageAction
 *
 * @author Jeoffrey Bakker
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 *
 * @struts:action
 *                path="/LanguageAction"
 *                scope="request"
 *                validate="true"
 *                input="/"
 *
 */
public class LanguageAction extends Action {

   private static final Logger log = Logging.getLoggerInstance(LanguageAction.class);


   /**
    * The actual perform function: MUST be implemented by subclasses.
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
      log.debug("LanguageAction - doPerform()");

      String language = request.getParameter("language");
      String referer = request.getParameter("referer");

      request.getSession().setAttribute(Globals.LOCALE_KEY, new Locale(language));
      Cookie cookie = new Cookie(Globals.LOCALE_KEY, language);
      cookie.setMaxAge(Integer.MAX_VALUE);
      response.addCookie(cookie);

      return new ActionForward(referer, true);
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.3  2003/11/14 14:17:00  jeoffrey
 * fixed redirect
 *
 * Revision 1.2  2003/11/07 12:39:06  jeoffrey
 * keep cookie as long as possible
 *
 * Revision 1.1  2003/10/29 07:11:56  jeoffrey
 * moved to java-web dir
 *
 * Revision 1.1  2003/10/28 09:30:49  jeoffrey
 * Added languageAction
 *
 */