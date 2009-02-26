/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.tree.html;

import javax.servlet.http.HttpServletResponse;

import com.finalist.tree.SelectRenderer;
import com.finalist.tree.TreeModel;

public abstract class SelectHtmlRenderer extends SelectRenderer implements HTMLTreeCellRenderer {

   public SelectHtmlRenderer(HttpServletResponse response, String linkPattern, String target) {
      super(response, linkPattern, target);
   }


   @Override
   public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
      return (HTMLTreeElement) super.getElement(model, node, id);
   }


   @Override
   protected HTMLTreeElement createElement(String icon, String id, String name, String fragment) {
      return new HTMLTreeElement(icon, id, name, fragment);
   }

}
