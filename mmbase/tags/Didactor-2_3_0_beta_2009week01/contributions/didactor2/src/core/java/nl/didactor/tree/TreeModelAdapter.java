package nl.didactor.tree;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Methods that should be implemented by subclasses:
 *   public Object getChild(Object parent, int index) 
 *   public Object getRoot() 
 *   public int getChildCount(Object parent) 
 *   public boolean isLeaf(Object node)    
 * 
 */
public abstract class TreeModelAdapter implements TreeModel {


   public void addTreeModelListener(TreeModelListener l) {
      
   }

   public void removeTreeModelListener(TreeModelListener l) {
      
   }

   public int getIndexOfChild(Object parent, Object child) {
      return 0;
   }

   public void valueForPathChanged(TreePath path, Object newValue) {
      
   }

}
