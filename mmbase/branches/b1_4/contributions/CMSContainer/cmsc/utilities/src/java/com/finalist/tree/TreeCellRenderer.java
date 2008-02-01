/*
 * Created on Sep 15, 2003 by edwin
 * 
 */
package com.finalist.tree;

/**
 * @author edwin Date :Sep 15, 2003
 */
public interface TreeCellRenderer {
   public TreeElement getElement(TreeModel model, Object node, String id);

   boolean showChildren(Object node);
}
