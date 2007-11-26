/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.TreeUtil;

public abstract class QuickSearchAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      String channel = "notfound";

      QuickSearchForm qForm = (QuickSearchForm) form;

      // check if there was a quick search
      String quicksearch = qForm.getPath();
      if (quicksearch != null && quicksearch.trim().length() > 0) {
         Integer intValue = null;
         try {
            intValue = Integer.valueOf(quicksearch);
         }
         catch (Exception e) {
            // not an integer then it is a path
         }

         if (intValue != null && isValidChannel(cloud, intValue)) {
            channel = intValue.toString();
         }
         else {
            Node node = getChannelFromPath(cloud, quicksearch);
            String path = quicksearch;
            int index = path.lastIndexOf(TreeUtil.PATH_SEPARATOR);
            while (node == null && index != -1) {
               path = path.substring(0, index);
               node = getChannelFromPath(cloud, path);
               index = path.lastIndexOf(TreeUtil.PATH_SEPARATOR);
            }

            if (node != null) {
               channel = node.getStringValue("number");
            }
         }
      }

      String url = mapping.findForward(SUCCESS).getPath() + "?channel=" + channel;
      return new ActionForward(url, true);
   }


   protected abstract Node getChannelFromPath(Cloud cloud, String quicksearch);


   protected abstract boolean isValidChannel(Cloud cloud, int channelNumber);
}
