/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.alias.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

public class AliasEdit extends MMBaseFormlessAction {

   public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

	  String parentpage = getParameter(request, "parentpage", true);
      String action = getParameter(request, "action");
      boolean stacked=(request.getParameter("stacked") != null && request.getParameter("stacked").equals("true"));

      if (StringUtils.isBlank(action)) {
          request.getSession().setAttribute("parentpage", parentpage);

         String objectnumber = getParameter(request, "number", true);

         ActionForward ret = new ActionForward(mapping.findForward("openwizard").getPath() + "?objectnumber="
               + objectnumber + "&returnurl=" + mapping.findForward("returnurl").getPath() + "?stacked="+stacked);
         ret.setRedirect(true);
         return ret;
      }
      else {
         String ewnodelastedited = getParameter(request, "ewnodelastedited");
         
         addToRequest(request, "showalias", ewnodelastedited);
         if(!stacked) {
	            return mapping.findForward(SUCCESS);
         }
         else {
         	return new ActionForward(mapping.findForward("stacked").getPath()+"?parent="+parentpage);
         }
      }
   }

}
