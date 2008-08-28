/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.subsite.tree;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationRenderer;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;


public class PersonalPageTreeItemRenderer implements NavigationTreeItemRenderer {

    private static final String RESOURCEBUNDLE = "cmsc-modules-subsite";

    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
         Node parentParentNode = NavigationUtil.getParent(parentNode);
         UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentParentNode, false);
         
         String name = parentNode.getStringValue(PagesUtil.TITLE_FIELD);
         String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );
         
         String id = String.valueOf(parentNode.getNumber());
         TreeElement element = renderer.createElement(parentNode, role, name, fragment, false);
         
         if (SecurityUtil.isEditor(role)) {
            element.addOption(renderer.createTreeOption("edit_defaults.png", "site.sub.edit",
                  RESOURCEBUNDLE, "../subsite/PersonalPageEdit.do?number=" + id));
            element.addOption(renderer.createTreeOption("delete.png", "site.personal.remove.page", 
                  RESOURCEBUNDLE, "../subsite/SubSiteDelete.do?number=" + id));
            element.addOption(renderer.createTreeOption("subsite_new.png", "site.personal.new.page",
            		RESOURCEBUNDLE, "../subsite/PersonalPageCreate.do?parentpage=" + id));
         }
         
         if (SecurityUtil.isChiefEditor(role)) {
        	 /** Not a good idea until this is fully implemented for every scenario
             * element.addOption(renderer.createTreeOption("cut.png", "site.page.cut", "javascript:cut('" + id + "');"));
             * element.addOption(renderer.createTreeOption("copy.png", "site.page.copy", "javascript:copy('" + id + "');"));
             * element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", "javascript:paste('" + id + "');"));
             */
          }
         
         if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(SubSiteTreeItemRenderer.FEATURE_WORKFLOW)) {
             element.addOption(renderer.createTreeOption("publish.png", "site.page.publish",
                   "../workflow/publish.jsp?number=" + id));
             element.addOption(renderer.createTreeOption("masspublish.png", "site.page.masspublish",
                   "../workflow/masspublish.jsp?number=" + id));
          }

         element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
                 "../usermanagement/pagerights.jsp?number=" + id));
         
         return element;
      }

	public void addParentOption(NavigationRenderer renderer,
			TreeElement element, String parentId) {
		//Do not add options to parents
	}

	public boolean showChildren(Node parentNode) {
		return false;
	}

}
