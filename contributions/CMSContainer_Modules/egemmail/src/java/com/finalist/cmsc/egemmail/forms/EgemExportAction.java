package com.finalist.cmsc.egemmail.forms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.util.http.HttpUtil;

@SuppressWarnings("serial")
public class EgemExportAction extends EgemSearchAction {

   private static final String EGEMMAIL_URL = "egemmail.url";
   private static final String EGEMMAIL_ADMIN_USER = "egemmail.admin.user";
   private static final String EGEMMAIL_ADMIN_PASSWORD = "egemmail.admin.password";
   // private static final String EGEMMAIL_BEHEER_URL = "egemmail.beheer.url";

   private static final Logger log = Logging.getLoggerInstance(EgemExportAction.class.getName());


   private String buildTeaser(Node node) {
      if (node.getNodeManager().hasField("intro")) {
         String intro = node.getStringValue("intro");
         if (intro != null && intro.length() > 0) {
            return replaceHtml(intro);
         }
      }

      if (node.getNodeManager().hasField("body")) {
         String body = node.getStringValue("body");
         if (body != null && body.length() > 0) {
            String messageBody = replaceHtml(body);
            if (messageBody.length() > 300) {
               int bestIndex = Math.max(messageBody.lastIndexOf(" ", 300), messageBody.lastIndexOf(".", 300) + 1);
               messageBody = messageBody.substring(0, bestIndex);
            }
            return messageBody;
         }
      }

      // no field found, just use an empty field
      return "";
   }


   protected ActionForward doExport(ActionMapping mapping, EgemExportForm form, HttpServletRequest request, Cloud cloud)
         throws Exception {

      String egemmailUrl = PropertiesUtil.getProperty(EGEMMAIL_URL);
      String egemmailUser = PropertiesUtil.getProperty(EGEMMAIL_ADMIN_USER);
      String egemmailPassword = PropertiesUtil.getProperty(EGEMMAIL_ADMIN_PASSWORD);

      int good = 0;
      int wrong = 0;
      int notOnLive = 0;

      for (Map.Entry<Integer, Boolean> entry : form.getSelectedNodes().entrySet()) {
         if (entry.getValue() == true) {
            Map<String, Object> postParams = new HashMap<String, Object>();

            Node node = cloud.getNode(entry.getKey());
            String liveUrl = Publish.getRemoteContentUrl(node);
            if (liveUrl != null) {
               postParams.put("url", liveUrl);

               postParams.put("user", egemmailUser);
               postParams.put("password", egemmailPassword);

               postParams.put("title", node.getStringValue("title"));
               postParams.put("teaser", buildTeaser(node));

               String response = HttpUtil.doPost(egemmailUrl, postParams).trim();

               if (response.equals("ok")) {
                  good++;
               }
               else {
                  wrong++;
                  log.warn("Received error response:\n" + response);
               }
            }
            else {
               log.warn("Cloud not find live node for: node.number");
               notOnLive++;
            }
         }
      }

      request.setAttribute("good", good);
      request.setAttribute("wrong", wrong);
      request.setAttribute("notOnLive", notOnLive);

      return mapping.findForward(EgemExportForm.EXPORT);
   }


   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      if (!(form instanceof EgemExportForm)) {
         throw new IllegalArgumentException("The form is not an " + EgemExportForm.class);
      }

      return execute(mapping, (EgemExportForm) form, request, response, cloud);
   }


   @SuppressWarnings("unchecked")
   protected ActionForward execute(ActionMapping mapping, EgemExportForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      // If the request has been forwarded from by EgemSearchAction
      NodeList results = (NodeList) request.getAttribute(RESULTS);
      if (results != null) {
         form.setForward(EgemExportForm.SEARCH);
         form.setPage(null);

         form.getSelectedNodes().clear();

         for (Node node : (Iterable<Node>) results) {
            form.getSelectedNodes().put(node.getNumber(), form.isSelectResults());
         }

         return mapping.findForward(EgemExportForm.SEARCH);
      }

      mergeState(form, request);

      String forward = form.getForward();
      if (EgemExportForm.SEARCH.equals(forward)) {
         return doSearch(mapping, form, request, response, cloud);
      }
      else if (EgemExportForm.EXPORT.equals(forward)) {
         return doExport(mapping, form, request, cloud);
      }
      else {
         throw new IllegalStateException("Unknown forward action: " + forward);
      }
   }

   @SuppressWarnings("unchecked")
   protected void mergeState(EgemExportForm form, HttpServletRequest request) {
      Set<Integer> newlySelectedNodes = new HashSet<Integer>();

      Map<String, Object> parameters = request.getParameterMap();
      for (String key : parameters.keySet()) {
         if (key.startsWith("export_")) { // A node selection box that is
                                          // selected
            String nodeNumberString = key.substring(7); // The part after
                                                         // 'export_'
            int nodeNumber = Integer.parseInt(nodeNumberString);

            newlySelectedNodes.add(nodeNumber);
         }
      }

      Set<Integer> nodesToRemove = form.getNodesOnScreen(); // Assume no nodes
                                                            // are selected
      nodesToRemove.removeAll(newlySelectedNodes); // Do not remove nodes that
                                                   // are selected

      for (Integer n : nodesToRemove) { // Mark all nodes to remove as not
                                          // selected
         form.getSelectedNodes().put(n, false);
      }

      for (Integer n : newlySelectedNodes) { // Mark all nodes that are selected
         form.getSelectedNodes().put(n, true);
      }
   }


   private String replaceHtml(String str) {
      String message = str.replaceAll("<.*?>", "");
      message = message.replace("&quot;", "\"");
      message = message.replace("&#039;", "\'");
      return message;
   }
}
