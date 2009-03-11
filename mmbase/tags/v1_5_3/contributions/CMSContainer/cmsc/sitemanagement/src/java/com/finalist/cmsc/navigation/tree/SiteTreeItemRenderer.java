/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation.tree;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.util.module.ModuleUtil;


public class SiteTreeItemRenderer implements NavigationTreeItemRenderer {

    protected static final String FEATURE_WORKFLOW = "workflowitem";

    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
       UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentNode, false);
       boolean secure = parentNode.getBooleanValue(PagesUtil.SECURE_FIELD);
       
       String name = parentNode.getStringValue(SiteUtil.TITLE_FIELD);
       String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );

       String id = String.valueOf(parentNode.getNumber());
       TreeElement element = renderer.createElement(parentNode, role, name, fragment, secure);

       if (SecurityUtil.isEditor(role)) {

          if (SecurityUtil.isChiefEditor(role)) {
             element.addOption(renderer.createTreeOption("edit_defaults.png", 
                         "site.site.edit", "SiteEdit.do?number=" + id));
          }

          renderer.addParentOptions(element, id);

          if (SecurityUtil.isChiefEditor(role)
                && ((model.getChildCount(parentNode) == 0) || SecurityUtil.isWebmaster(role))) {
             element.addOption(renderer.createTreeOption("delete.png", "site.site.remove", 
                         "SiteDelete.do?number=" + id));
          }

          if (NavigationUtil.getChildCount(parentNode) >= 2) {
             element.addOption(renderer.createTreeOption("reorder.png", "site.page.reorder", 
                         "reorder.jsp?parent=" + id));
          }

          if (SecurityUtil.isChiefEditor(role)) {
             element.addOption(renderer.createTreeOption("paste.png", "site.page.paste", 
                         "javascript:paste('" + id + "');"));
          }

          if (SecurityUtil.isWebmaster(role) && ModuleUtil.checkFeature(FEATURE_WORKFLOW)) {
             element.addOption(renderer.createTreeOption("publish.png", "site.page.publish",
                   "../workflow/publish.jsp?number=" + id));
             element.addOption(renderer.createTreeOption("masspublish.png", "site.page.masspublish",
                   "../workflow/masspublish.jsp?number=" + id));
          }
       }
       element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
             "../usermanagement/pagerights.jsp?number=" + id));
       
       return element;
    }

	public void addParentOption(NavigationRenderer renderer, TreeElement element, String parentId) {
		// has no parent
	}

	public boolean showChildren(Node parentNode) {
		return true;
	}
    
}
