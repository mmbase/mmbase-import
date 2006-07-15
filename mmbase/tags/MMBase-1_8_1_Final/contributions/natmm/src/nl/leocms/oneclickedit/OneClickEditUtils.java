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
package nl.leocms.oneclickedit;

import nl.leocms.util.ServerUtil;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jeoffrey Bakker
 * @version $Revision: 1.2 $, $Date: 2006-03-08 22:23:51 $
 */
public class OneClickEditUtils {

   private static final Logger log = Logging.getLoggerInstance(OneClickEditUtils.class);

   public static boolean isEditMode(HttpServletRequest request) {

      String contextPath = request.getContextPath();
      log.debug("contextPath found is " + contextPath);

      String modus = "";
      
      if ( ServerUtil.isLive()) {
         modus = (String) request.getSession().getServletContext().getAttribute(Constants.ATTRIBUTE_EDITMODE);
         if (modus == null) {
            request.getSession().getServletContext().setAttribute(Constants.ATTRIBUTE_EDITMODE, "false");
         }
      }
      else if ( ServerUtil.isStaging()) {
         modus = (String) request.getSession().getAttribute(Constants.ATTRIBUTE_EDITMODE);
         if (modus == null) {
            request.getSession().setAttribute(Constants.ATTRIBUTE_EDITMODE, "true");
         }
      }
      else { // CANNOT HAPPEN?
         log.warn("Strange contextPath found " + contextPath);
      }

      log.debug("Modus found is " + modus);
      return modus != null && modus.equals("true");
   }
}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/05 21:43:58  henk
 * First version of the NatMM contribution.
 *
 * Revision 1.3  2003/12/12 08:54:49  nico
 * unused imports and other small issues
 *
 * Revision 1.2  2003/11/14 16:37:08  edwin
 * check op live of staging via ServerUtil ipv contentPath
 *
 * Revision 1.1  2003/10/23 06:49:50  jeoffrey
 * added oneclickedit editmode tag
 *
 */