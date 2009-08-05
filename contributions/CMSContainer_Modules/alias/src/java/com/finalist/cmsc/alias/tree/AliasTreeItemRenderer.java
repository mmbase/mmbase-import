/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.alias.tree;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.alias.util.AliasUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;


public class AliasTreeItemRenderer implements NavigationTreeItemRenderer {

    private static final int COLLAPSE_AMOUNT = 5;
    private static final String RESOURCEBUNDLE = "cmsc-modules-alias";
    private static final String FEATURE_WORKFLOW = "workflowitem";
    
    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
         Node parentParentNode = NavigationUtil.getParent(parentNode);
         UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentParentNode, false);
         
         TreeElement element = null;
         String id = null;
         
         NodeList parentParentAliasses = parentParentNode.getRelatedNodes("pagealias");
         if(parentParentAliasses.size() >= COLLAPSE_AMOUNT) {
	         if(parentNode.getNumber() == parentParentAliasses.getNode(0).getNumber()) {
	        	 String icon = renderer.getIcon("pagealias_stacked", role); 
	        	 String label = parentParentAliasses.size()+" "+renderer.getLabel("site.alias.stacked", RESOURCEBUNDLE);
	        	 element = renderer.createElement(icon, "", label, label, "../alias/stacked.jsp?parent="+parentParentNode.getNumber(), "content");
	        	 id = ""+parentParentNode.getNumber();
	         }
	         else {
	        	 return null;
	         }
         }
         else {
	         String name = parentNode.getStringValue(AliasUtil.TITLE_FIELD);
	         String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );
	
	         id = String.valueOf(parentNode.getNumber());
	         element = renderer.createElement(parentNode, role, name, fragment, false);
	
	         if (SecurityUtil.isEditor(role)) {
	            element.addOption(renderer.createTreeOption("edit_defaults.png", "site.alias.edit",
	                        RESOURCEBUNDLE, "../alias/AliasEdit.do?number=" + id+"&parentpage="+parentParentNode.getNumber()));
	            element.addOption(renderer.createTreeOption("delete.png", "site.alias.remove", 
	                    RESOURCEBUNDLE, "../alias/AliasDelete.do?number=" + id+"&parentpage="+parentParentNode.getNumber()));
	         }
	         
	         if (SecurityUtil.isChiefEditor(role)) {
	             element.addOption(renderer.createTreeOption("cut.png", "site.page.cut", "javascript:cut('" + id + "');"));
	             /** Not a good idea until this is fully implemented for every scenario
	             * element.addOption(renderer.createTreeOption("copy.png", "site.page.copy", "javascript:copy('" + id + "');"));
	             * element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", "javascript:paste('" + id + "');"));
	             */
	          }
	         
	         if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
	             element.addOption(renderer.createTreeOption("publish.png", "site.page.publish",
	                   "../workflow/publish.jsp?number=" + id));
	         }
	         
         }
         element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
                 "../usermanagement/pagerights.jsp?number=" + id));
         
         return element;
      }

   public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
      element.addOption(renderer.createTreeOption("alias_new.png", "site.alias.new",
    		  RESOURCEBUNDLE, "../alias/AliasCreate.do?parentpage=" + parentId));
   }

   public boolean showChildren(Node parentNode) {
      return false; //Do not show children, because a Alias can not have kids.
   }

}
