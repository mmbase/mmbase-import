/*
 * Created on Sep 15, 2003 by edwin
 * 
 */
package com.finalist.tree.html;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeModel;

/**
 * @author edwin Date :Sep 15, 2003
 */
public class UrlRenderer implements HTMLTreeCellRenderer {

   /**
    * @see com.finalist.tree.TreeCellRenderer#getElement(TreeModel, Object,
    *      String)
    */
   public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
      Node n = (Node) node;
      return new HTMLTreeElement(id, node + "(" + n.getNumber() + ")", null, null, "#");
   }

   public boolean showChildren(Object node) {
      return true;
   }

}
