/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.subsite.forms;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class SubSiteEdit extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

      String action = getParameter(request, "action");

      if (StringUtil.isEmptyOrWhitespace(action)) {
         String objectnumber = getParameter(request, "number", true);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber="
               + objectnumber + "&returnurl=" + mapping.findForward("returnurl").getPath());
         ret.setRedirect(true);
         return ret;
      }
      else {
         String ewnodelastedited = getParameter(request, "ewnodelastedited");
         addToRequest(request, "showsubsite", ewnodelastedited);
         ActionForward ret = mapping.findForward(SUCCESS);
         return ret;
      }
   }

}
