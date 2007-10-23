package com.finalist.cmsc.navigation;

import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;

/**
 * Trivial implementation of a tree structure based on a cloud.
 * 
 * DO NOT OPTIMIZE THIS CODE unless it is measured to be slow!
 * ( premature optimization is the root of all evil - D. Knuth )
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public class NavigationTreeModel implements TreeModel {
   private Node site;

   public NavigationTreeModel(Node site) {
      this.site = site;
   }
   
   /**
    * @see javax.swing.tree.TreeModel#getRoot()
    */
   public Object getRoot() {
      return site;
   }

   /**
    * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
    */
   public int getChildCount(Object parent) {
	   int childCount = 0;
	   for(NavigationItemManager manager:NavigationManager.getNavigationManagers()) {
		   childCount += manager.getChildCount((Node)parent);	
	   }
	   return childCount;
   }

// [FP]
//	   int childCount = NavigationUtil.getChildCount((Node) parent);
//	   if(ModuleUtil.checkFeature(NavigationRenderer.FEATURE_RSSFEED)) {
//		   childCount += RssFeedUtil.getChildCount((Node) parent);
//	   }
//      return childCount;
//   }
 
   /**
    * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
    */
   public boolean isLeaf(Object node) {
      return getChildCount(node) == 0;
   }

   /**
    * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
    */
   public Object getChild(Object parent, int index) {
	   int offset = 0;
       Node parentNode = (Node)parent;

	   for(NavigationItemManager manager:NavigationManager.getNavigationManagers()) {
		   int numberOfClients =  manager.getChildCount((Node)parent);
		   if(index < offset + numberOfClients) {
			   return manager.getChild(parentNode, index - offset);
		   }
		   offset += numberOfClients;
	   }
       throw new IndexOutOfBoundsException("Child " + index + " is not available. Node " + parentNode.getNumber() + " has " + offset + " children.");
	   
//		[FP]
//      Node parentNode = (Node)parent;
//      NodeList pages = NavigationUtil.getOrderedChildren(parentNode); 
//
//      if (pages.size() > index) {
//         return pages.get(index);
//      }
//      
//      if(ModuleUtil.checkFeature(NavigationRenderer.FEATURE_RSSFEED)) {
//    	  NodeList feeds = RssFeedUtil.getOrderedChildren(parentNode); 
//    	  if (index - pages.size() < feeds.size()) {
//    		  return feeds.get(index - pages.size());
//    	  }
//      }
//      throw new IndexOutOfBoundsException("Child " + index + " is not available. Node " + parentNode.getNumber() + " has " + pages.size() + " children.");
      
   }

    public Object getNode(String id) {
        return site.getCloud().getNode(id);
    }

}
