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
import com.finalist.tree.html.*;

public class RepositoryHtmlRenderer extends RepositoryRenderer implements HTMLTreeCellRenderer {

   private boolean hideIcons;
   private boolean optionLabel;
   private String spanClass = "contentchannel";


   @Override
   public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
      return (HTMLTreeElement) super.getElement(model, node, id);
   }


   public RepositoryHtmlRenderer(HttpServletRequest request, HttpServletResponse response, String target,
         boolean hideIcons) {
      super(request, response, target);
      this.hideIcons = hideIcons;
      this.optionLabel = hideIcons;
   }


   public RepositoryHtmlRenderer(HttpServletRequest request, HttpServletResponse response, String target) {
      super(request, response, target);
   }


   @Override
   protected TreeOption createOption(String icon, String label, String action, String target) {
      return new HTMLTreeOption(icon, label, action, target, optionLabel);
   }


   @Override
   protected HTMLTreeElement createElement(String icon, String id, String name, String fragment, String action,
         String target) {
      HTMLTreeElement element;
      if (hideIcons) {
         element = new PopitElement(icon, id, name, fragment, spanClass);
      }
      else {
         element = new HTMLTreeElement(icon, id, name, fragment, spanClass);
      }
      element.setLink(action);
      element.setTarget(target);

      return element;
   }

}
