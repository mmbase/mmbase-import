/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.navigation;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.*;

import com.finalist.cmsc.security.Role;
import com.finalist.cmsc.security.SecurityUtil;

public class SiteUtil {

   private static final String STAGING_FRAGMENT = "stagingfragment";
   private static final String LIVE_FRAGMENT = "urlfragment";

   public static final String SITE = "site";

   public static final String TITLE_FIELD = "title";
   public static final String FRAGMENT_FIELD = ServerUtil.isLive() ? LIVE_FRAGMENT : STAGING_FRAGMENT;
   public static final String DESCRIPTION_FIELD = "description";

   public static String[] treeManagers = new String[] { SITE };


   private SiteUtil() {
      // utility
   }


   public static boolean isSite(Node node) {
      if (node == null) {
         throw new NullPointerException("node can not be null");
      }
      return SITE.equals(node.getNodeManager().getName());
   }


   public static Node getSite(Cloud cloud, String sitename) {
      NodeManager manager = cloud.getNodeManager(SITE);
      NodeList list = manager.getList(FRAGMENT_FIELD + " = '" + sitename + "'", null, null);
      if (!list.isEmpty()) {
         return list.getNode(0);
      }
      return null;
   }


   public static NodeList getSites(Cloud cloud) {
      NodeManager sitesManager = cloud.getNodeManager(SITE);
      NodeList sites = sitesManager.getList(sitesManager.createQuery());
      return sites;
   }


   public static Node createSite(Cloud cloud, String name, String pathname, Node layout) {
      return createSite(cloud, name, pathname, null, layout);
   }


   public static Node createSite(Cloud cloud, String name, String pathname, String description, Node layout) {
      Node site = cloud.getNodeManager(SITE).createNode();
      site.setStringValue(TITLE_FIELD, name);
      if (!StringUtil.isEmpty(pathname)) {
         site.setStringValue(STAGING_FRAGMENT, pathname);
         site.setStringValue(LIVE_FRAGMENT, pathname);
      }
      if (!StringUtil.isEmpty(description)) {
         site.setStringValue(DESCRIPTION_FIELD, description);
      }
      site.commit();

      PagesUtil.addLayout(site, layout);
      PagesUtil.linkPortlets(site, layout);

      Node administrators = SecurityUtil.getAdministratorsGroup(cloud);
      if (administrators != null) {
         NavigationUtil.addRole(cloud, site, administrators, Role.WEBMASTER);
      }
      NavigationUtil.getNavigationInfo(cloud).expand(site);

      return site;
   }

}
