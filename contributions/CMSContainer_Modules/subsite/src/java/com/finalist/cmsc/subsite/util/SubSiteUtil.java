package com.finalist.cmsc.subsite.util;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.navigation.PagesUtil;
import com.finalist.cmsc.repository.RepositoryUtil;

public class SubSiteUtil {

  public static final String SUBSITE = "subsite";
  public static final String PERSONALPAGE = "personalpage";

  public static final String ALIAS_SUBSITE = "repository.subsite";

  public static final String USERID = "userid";

  public static final String PERSONAL_PAGE_ID = "personalPageId";

  public static boolean isSubSiteType(Node node) {
    return node.getNodeManager().getName().equals(SUBSITE);
  }

  public static boolean isPersonalPageType(Node node) {
    return node.getNodeManager().getName().equals(PERSONALPAGE);
  }

  public static Node createSubSiteContentChannel(Node subsite) {
    Node subsiteRootChannel = getRepositoryRoot(subsite.getCloud());
    return createContentChannel(subsite, subsiteRootChannel);
  }

  public static Node createPersonalPageContentChannel(Node personalpage) {
    Node personalpageChannel = getSubsiteChannel(personalpage);
    return createContentChannel(personalpage, personalpageChannel);
  }

  private static Node createContentChannel(Node page, Node parentChannel) {
    // create channel in the Content Repository
    Cloud cloud = page.getCloud();
    String fragment = page.getStringValue(PagesUtil.FRAGMENT_FIELD);
    Node channel = RepositoryUtil.createChannel(cloud, page.getStringValue(PagesUtil.TITLE_FIELD), fragment);
    RepositoryUtil.appendChild(parentChannel, channel);
    return channel;
  }

  public static Node getSubsiteChannel(Node personalpage) {
    Node subsite = NavigationUtil.getParent(personalpage);
    String fragment = subsite.getStringValue(PagesUtil.FRAGMENT_FIELD);
    Cloud cloud = personalpage.getCloud();
    Node repositoryRootChannel = getRepositoryRoot(cloud);
    return RepositoryUtil.getChild(repositoryRootChannel, fragment);
  }

  public static Node getPersonalpageChannel(Node personalpage) {
    Node ppChannel = getSubsiteChannel(personalpage);
    String fragment = personalpage.getStringValue(PagesUtil.FRAGMENT_FIELD);
    return RepositoryUtil.getChild(ppChannel, fragment);
  }

  public static Node getPersonalpageNodeByUserId(Cloud cloud, String userId) {
    if (userId == null || StringUtil.isEmpty(userId)) {
      return null;
    }
    
    NodeManager personalPageNodeManager = cloud.getNodeManager(PERSONALPAGE);
    NodeQuery query = personalPageNodeManager.createQuery();
    Field userIdField = personalPageNodeManager.getField(USERID);
    SearchUtil.addEqualConstraint(query, userIdField, userId);
    SearchUtil.addLimitConstraint(query, 0, 1);
    
    NodeList nodeList = personalPageNodeManager.getList(query);
    
    return nodeList.isEmpty() ? null : (Node) nodeList.get(0);
  }
  
  public static Node getRepositoryRoot(Cloud cloud) {
    Node repositoryRootChannel = cloud.getNode(ALIAS_SUBSITE);
    return repositoryRootChannel;
  }

  public static Node createPersonalPage(Cloud cloud, String name, Node layout, String personalPageId) {
    Node personalPage = PagesUtil.createPage(cloud, name, layout, PERSONALPAGE);
    personalPage.setStringValue(USERID, personalPageId);
    return personalPage;
  }
}
