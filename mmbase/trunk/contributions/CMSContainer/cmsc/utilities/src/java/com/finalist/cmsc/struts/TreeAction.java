/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.util.HttpUtil;
import com.finalist.tree.TreeInfo;
import com.finalist.tree.ajax.AjaxTree;

public abstract class TreeAction extends MMBaseAction {

   /** MMbase logging system */
   private static final Logger log = Logging.getLoggerInstance(TreeAction.class);

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      TreeInfo info = getTreeInfo(cloud);

      String action = request.getParameter("action");
      if (StringUtils.isNotEmpty(action)) {
         response.setContentType("text/xml");
         if ("expand".equals(action)) {
            String persistentid = request.getParameter("persistentid");
            info.expand(Integer.parseInt(persistentid));
         }
         if ("collapse".equals(action)) {
            String persistentid = request.getParameter("persistentid");
            info.collapse(Integer.parseInt(persistentid));
         }
         if ("inittree".equals(action)) {
            PrintWriter out = HttpUtil.getWriterForXml(response);
            String persistentid = request.getParameter("persistentid");
            AjaxTree t = getTree(request, response, cloud, info, persistentid);
            t.render(out);
         }
         if ("loadchildren".equals(action)) {
            PrintWriter out = HttpUtil.getWriterForXml(response);
            String persistentid = request.getParameter("persistentid");
            AjaxTree t = getTree(request, response, cloud, info, persistentid);
            t.renderChildren(out, persistentid);
         }
         if ("autocomplete".equals(action)) {
            String path = request.getParameter("path");
            if (path != null && path.indexOf(TreeUtil.PATH_SEPARATOR) < 0) {
               path = null;
            }
            List<String> children = getChildren(cloud, path);

            PrintWriter out = HttpUtil.getWriterForXml(response);
            out.write("<options>");
            for (String element : children) {
               out.write("<option>" + element + "</option>");
            }
            out.write("</options>");
         }
         return null;
      }

      String channel = getChannelId(request, cloud);
      if (StringUtils.isNotEmpty(channel) && !"notfound".equals(channel) 
            && cloud.hasNode(channel)) {

         Node channelNode = cloud.getNode(channel);
         List<Node> openChannels = getOpenChannels(channelNode);
         if (openChannels != null) {
            for (Node node : openChannels) {
               info.expand(node.getNumber());
            }
            addToRequest(request, "channel", channelNode);
         }
      }
      else {
         Node rootNode = getRootNode(cloud);
         if (rootNode != null) {
            addToRequest(request, "channel", rootNode);
         }
      }

      ActionForward ret = mapping.findForward(SUCCESS);
      return ret;
   }

   @Override
   protected ActionForward redirectLogin(HttpServletRequest req, HttpServletResponse resp) {
      try {
         resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      
         return null;
      } catch (IOException e) {
         log.error("Failed to handle redirecting to the login page", e);
      }
      
      return super.redirectLogin(req, resp);
   }


   protected String getChannelId(HttpServletRequest request, Cloud cloud) {
      return request.getParameter("channel");
   }


   protected abstract TreeInfo getTreeInfo(Cloud cloud);


   protected abstract Node getRootNode(Cloud cloud);


   protected abstract List<Node> getOpenChannels(Node channelNode);


   protected abstract AjaxTree getTree(HttpServletRequest request, HttpServletResponse response, Cloud cloud,
         TreeInfo info, String persistentid);


   protected abstract List<String> getChildren(Cloud cloud, String path);

}
