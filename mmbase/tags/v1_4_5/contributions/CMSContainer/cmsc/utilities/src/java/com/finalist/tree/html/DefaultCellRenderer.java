/*
 * Created on Sep 15, 2003 by edwin
 * 
 */
package com.finalist.tree.html;

import com.finalist.tree.TreeModel;

/**
 * @author edwin Date :Sep 15, 2003
 */
public class DefaultCellRenderer implements HTMLTreeCellRenderer {

   /**
    * @see com.finalist.tree.html.HTMLTreeCellRenderer#getElement(javax.swing.tree.TreeModel,
    *      java.lang.Object, java.lang.String)
    */
   public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
      return new HTMLTreeElement(null, id, null, node.toString());
   }

   public boolean showChildren(Object node) {
      return true;
   }

}
