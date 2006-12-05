/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package com.finalist.tree;

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
 * 
 * @author Edwin van der Elst
 * Date :Sep 16, 2003
 * 
 */
public abstract class TreeModelAdapter implements TreeModel {


   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
    */
   public void addTreeModelListener(TreeModelListener l) {
      
   }

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
    */
   public void removeTreeModelListener(TreeModelListener l) {
      
   }

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
    */
   public int getIndexOfChild(Object parent, Object child) {
      return 0;
   }

   /* (non-Javadoc)
    * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
    */
   public void valueForPathChanged(TreePath path, Object newValue) {
      
   }

}
