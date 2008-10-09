/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.finalist.tree.*;
import com.finalist.tree.ajax.*;

public class RepositoryAjaxRenderer extends RepositoryRenderer implements AjaxTreeCellRenderer {

   public RepositoryAjaxRenderer(HttpServletRequest request, HttpServletResponse response, String target) {
      super(request, response, target);
   }


   @Override
   public AjaxTreeElement getElement(TreeModel model, Object node, String id) {
      return (AjaxTreeElement) super.getElement(model, node, id);
   }


   @Override
   protected TreeOption createOption(String icon, String label, String action, String target) {
      return new AjaxTreeOption(icon, label, action, target);
   }


   @Override
   protected TreeElement createElement(String icon, String id, String name, String fragment, String action,
         String target) {
      return new AjaxTreeElement(icon, id, name, fragment, action, target);
   }

}
