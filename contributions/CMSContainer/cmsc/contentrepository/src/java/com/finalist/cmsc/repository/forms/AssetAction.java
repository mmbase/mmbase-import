/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.repository.AssetElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseAction;

public class AssetAction extends MMBaseAction {

   private final static String MOVEASSETTOCHANNEL = "moveAssetToChannel";
   private static final String IMAGES = "images";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      String action = request.getParameter("action");
      if (StringUtils.isNotEmpty(action) && action.equals(MOVEASSETTOCHANNEL)) {
         return mapping.findForward(MOVEASSETTOCHANNEL);
      }
      List<LabelValueBean> typesList = new ArrayList<LabelValueBean>();

      List<NodeManager> types = AssetElementUtil.getAssetTypes(cloud);
      List<String> hiddenTypes = AssetElementUtil.getHiddenAssetTypes();
      for (NodeManager manager : types) {
         String name = manager.getName();
         if (!hiddenTypes.contains(name)) {
            LabelValueBean bean = new LabelValueBean(manager.getGUIName(), name);
            typesList.add(bean);
         }
      }
      addToRequest(request, "typesList", typesList);

      String parentchannel = request.getParameter("parentchannel");
      String orderby = request.getParameter("orderby");
      String direction = request.getParameter("direction");
      String show = request.getParameter("show");
      String exist = request.getParameter("exist");
      String imageOnly = request.getParameter("imageOnly");

      if (StringUtils.isEmpty(orderby)) {
         orderby = null;
      }
      if (StringUtils.isEmpty(direction)) {
         direction = null;
      }
      if (StringUtils.isEmpty(show)) {
         show = (String) request.getSession().getAttribute("show");
         if (StringUtils.isEmpty(show)) {
            show = "list";
         }
      }
      if (StringUtils.isEmpty(imageOnly)) {
         imageOnly = (String) request.getSession().getAttribute("imageOnly");
         if (StringUtils.isEmpty(imageOnly)) {
            imageOnly = "no";
         }
      }

      // Set the offset (used for paging).
      String offsetString = request.getParameter("offset");
      int offset = 0;
      if (offsetString != null && offsetString.matches("\\d+")) {
         offset = Integer.parseInt(offsetString);
      }

      // Set the maximum result size.
      String resultsPerPage = PropertiesUtil.getProperty(AssetSearchAction.REPOSITORY_SEARCH_RESULTS_PER_PAGE);
      int maxNumber = 25;
      if (resultsPerPage != null && resultsPerPage.matches("\\d+")) {
         maxNumber = Integer.parseInt(resultsPerPage);
      }
      addToRequest(request, "resultsPerPage", Integer.toString(maxNumber));

      if (StringUtils.isNotEmpty(parentchannel)) {
         Node channel = cloud.getNode(parentchannel);
         NodeList assets;
         NodeList created;
         if ("yes".equals(imageOnly)) {
            assets = RepositoryUtil.getCreatedAssets(channel, Arrays.<String> asList(IMAGES), orderby, direction,
                  false, offset * maxNumber, maxNumber, -1, -1, -1);
            created = RepositoryUtil.getCreatedAssets(channel, IMAGES);
         } else {
            assets = RepositoryUtil.getCreatedAssets(channel, null, orderby, direction, false, offset * maxNumber,
                  maxNumber, -1, -1, -1);
            created = RepositoryUtil.getCreatedAssets(channel);
         }
         int assetCount = 0;
         if (!created.isEmpty()) {
            assetCount = created.size();
         }
         addToRequest(request, "direction", direction);
         addToRequest(request, "orderby", orderby);
         addToRequest(request, "elements", assets);
         addToRequest(request, "elementCount", Integer.toString(assetCount));
         addToRequest(request, "exist", exist);
         request.getSession().setAttribute("show", show);
         request.getSession().setAttribute("imageOnly", imageOnly);

         Map<String, Node> createdNumbers = new HashMap<String, Node>();
         for (Iterator<Node> iter = created.iterator(); iter.hasNext();) {
            Node createdElement = iter.next();
            createdNumbers.put(String.valueOf(createdElement.getNumber()), createdElement);
         }
         addToRequest(request, "createdNumbers", createdNumbers);
      }
      return mapping.findForward(SUCCESS);
   }

}
