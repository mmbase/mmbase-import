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


public class SubSiteTreeItemRenderer implements NavigationTreeItemRenderer {

    private static final String RESOURCEBUNDLE = "cmsc-modules-subsite";
    protected static final String FEATURE_WORKFLOW = "workflowitem";

    public TreeElement getTreeElement(NavigationRenderer renderer, Node parentNode, TreeModel model) {
         Node parentParentNode = NavigationUtil.getParent(parentNode);
         UserRole role = NavigationUtil.getRole(parentNode.getCloud(), parentParentNode, false);
         
         String name = parentNode.getStringValue(PagesUtil.TITLE_FIELD);
         String fragment = parentNode.getStringValue( NavigationUtil.getFragmentFieldname(parentNode) );

         String id = String.valueOf(parentNode.getNumber());
         TreeElement element = renderer.createElement(parentNode, role, name, fragment, false);

         /* Not needed anymore.
          * 
         Cloud cloud = parentNode.getCloud();
         Node rootNode = SubSiteUtil.getRepositoryRoot(cloud);

         Node subsiteChannel = RepositoryUtil.getChannelFromPath(cloud, SubSiteUtil.SUBSITE, rootNode);

         String channelNumber = null;
         if (subsiteChannel != null) {
            channelNumber = String.valueOf(subsiteChannel.getNumber());
            if (channelNumber != null) { //Only show subsite pages when content channels exist
               //Do content-channel stuff if needed.
            }
         }
         */ 

         if (SecurityUtil.isEditor(role)) {
            element.addOption(renderer.createTreeOption("edit_defaults.png", "site.sub.edit",
                  RESOURCEBUNDLE, "../subsite/SubSiteEdit.do?number=" + id));
            element.addOption(renderer.createTreeOption("delete.png", "site.sub.remove", 
                  RESOURCEBUNDLE, "../subsite/SubSiteDelete.do?number=" + id));
            element.addOption(renderer.createTreeOption("subsite_new.png", "site.personal.new.page",
            		RESOURCEBUNDLE, "../subsite/PersonalPageCreate.do?parentpage=" + id));
            element.addOption(renderer.createTreeOption("personalpage_go.png", "site.personal.showpages",
                  RESOURCEBUNDLE, "../subsite/SubSiteAction.do?subsite=" + id));
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
             element.addOption(renderer.createTreeOption("masspublish.png", "site.page.masspublish",
                   "../workflow/masspublish.jsp?number=" + id));
          }
         
         element.addOption(renderer.createTreeOption("rights.png", "site.page.rights",
                 "../usermanagement/pagerights.jsp?number=" + id));
         
         return element;
      }

   public void addParentOption(NavigationRenderer renderer,
         TreeElement element, String parentId) {
         element.addOption(renderer.createTreeOption("subsite_new.png", "site.sub.new",
        		 RESOURCEBUNDLE, "../subsite/SubSiteCreate.do?parentpage=" + parentId));
   }
   
	public boolean showChildren(Node parentNode) {
		return false; //Do not show PersonalPages
	}

}
