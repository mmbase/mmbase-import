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
package nl.leocms.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author Gerard van de Weerd
 * @{date}
 * Static Util class for request stuff; mostly mapping paths.
 */
public class RequestUtil {
   /**
    * Determines the relative path to the root (e.g. ../../..) for the given request. Does not end 
    * with '/'!
    * 
    * @param request 
    * @param uri the uri to use or request.getRequestURL() if null
    * @return
    */
   public static String determinePathToRoot(HttpServletRequest request, String uri) {
      if (uri==null || "".equals(uri)) {
         uri = request.getRequestURL().toString();
      }
      StringBuffer ret = new StringBuffer();
      String appName = request.getContextPath();
      //System.out.println("appName: " + appName);
      String stepUrl = uri.substring(uri.indexOf(appName) + appName.length() + 1);
      //System.out.println("stepUrl: " + stepUrl);
      int nr = StringUtils.countMatches(stepUrl, "/");
      for (int i=0; i<nr; i++) {         
         if (i==nr-1) {
            ret.append("..");            
         }
         else {
            ret.append("../");
         }
      }           
      return "".equals(ret.toString())?".":ret.toString();
   }
}
/*
$Log: not supported by cvs2svn $
Revision 1.3  2003/10/31 15:14:00  Gerard van de Weerd
getHoofdpagina method + some minor changes

Revision 1.2  2003/10/31 07:14:56  Gerard van de Weerd
url interceptor changes

Revision 1.1  2003/10/30 10:21:02  Gerard van de Weerd
url interceptor changed to work with page templates

*/