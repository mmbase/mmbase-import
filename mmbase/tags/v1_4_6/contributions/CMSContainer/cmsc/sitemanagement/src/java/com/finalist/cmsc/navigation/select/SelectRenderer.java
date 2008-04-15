/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation.select;

import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.ajax.SelectAjaxRenderer;

public class SelectRenderer extends SelectAjaxRenderer {

   public SelectRenderer(HttpServletResponse response, String linkPattern, String target) {
      super(response, linkPattern, target);
   }


   @Override
   protected String getName(Node parentNode) {
      return parentNode.getStringValue(PagesUtil.TITLE_FIELD);
   }


   @Override
   protected String getFragment(Node parentNode) {
      return parentNode.getStringValue(NavigationUtil.getFragmentFieldname(parentNode));
   }


   @Override
   public String getIcon(Object node) {
      Node n = (Node) node;
      UserRole role = NavigationUtil.getRole(n.getCloud(), n, false);
      return "type/" + n.getNodeManager().getName() + "_" + role.getRole().getName() + ".png";
   }
}
