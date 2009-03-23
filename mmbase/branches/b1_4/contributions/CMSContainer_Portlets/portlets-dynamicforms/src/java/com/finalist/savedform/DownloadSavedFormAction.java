package com.finalist.savedform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.struts.MMBaseAction;

public class DownloadSavedFormAction extends MMBaseAction {

   private static final String NO_VALUE = "-";


   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      Locale locale = request.getLocale();
      Map<String, String> headers = new TreeMap<String, String>();
      List<String> values = new ArrayList<String>();
      String nodeNumber = request.getParameter("nodenumber");
      String formTitle;
      // construct headers
      if (!StringUtil.isEmptyOrWhitespace(nodeNumber) && cloud.hasNode(nodeNumber)) {
         Node responseForm = cloud.getNode(nodeNumber);
         formTitle = responseForm.getStringValue("title");
         NodeList savedFormNodeList = responseForm.getRelatedNodes("savedform");
         NodeIterator savedFormIterator = savedFormNodeList.nodeIterator();
         while (savedFormIterator.hasNext()) {
            Node savedForm = savedFormIterator.nextNode();
            NodeList savedFieldsList = savedForm.getRelatedNodes("savedfieldvalue");
            NodeIterator savedFieldIterator = savedFieldsList.nodeIterator();
            while (savedFieldIterator.hasNext()) {
               Node savedField = savedFieldIterator.nextNode();
               String fieldNumber = savedField.getStringValue("field");
               if (!headers.containsKey(fieldNumber)) {
                  String labelValue = NO_VALUE;
                  Node formField = cloud.getNode(fieldNumber);
                  if (formField != null) {
                     labelValue = formField.getStringValue("label");
                  }
                  headers.put(fieldNumber, labelValue);
               }
            }
         }

         // construct values
         savedFormIterator = savedFormNodeList.nodeIterator();
         Set<String> headerKeys = headers.keySet();
         while (savedFormIterator.hasNext()) {
            Node savedForm = savedFormIterator.nextNode();
            for (String fieldNumber : headerKeys) {
               String fieldValue = NO_VALUE;
               NodeList savedFieldsList = savedForm.getRelatedNodes("savedfieldvalue");
               NodeIterator savedFieldIterator = savedFieldsList.nodeIterator();
               while (savedFieldIterator.hasNext()) {
                  Node savedField = savedFieldIterator.nextNode();
                  if (savedField.getIntValue("field") == Integer.valueOf(fieldNumber)) {
                     fieldValue = savedField.getStringValue("value");
                  }
               }
               values.add(fieldValue);
            }
         }
         response.setContentType("application/vnd.ms-excel");
         
         formTitle = formTitle.replace(" ", "_"); //Replace spaces with _ before our pattern matching.

         //Remove all non normal characters
         Pattern pattern = Pattern.compile("[^\\w].*?", Pattern.DOTALL);
         Matcher matcher = pattern.matcher(formTitle);
         formTitle = matcher.replaceAll("");
         
         if (formTitle.length() > 31) { //POI does not accept sheet titles > 31 chars
            formTitle = formTitle.substring(0, 31); 
         }
         String filename = formTitle + ".xls";
         response.addHeader("Content-disposition", "attachment; filename=" + filename);
         ExcelUtils.getInstance().generate(formTitle, response.getOutputStream(), headers.values(),
               savedFormNodeList.size(), values);
         return null;
      }
      else {
         String message = getResources(request, "SAVEDFORM").getMessage(locale, "incorrect.nodenumber", nodeNumber);
         request.setAttribute("error", message);
         String returnurl = request.getParameter("returnurl");
         return new ActionForward(returnurl);
      }
   }
}
