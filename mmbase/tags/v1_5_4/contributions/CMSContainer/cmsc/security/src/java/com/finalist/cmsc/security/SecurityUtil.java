/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.security;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.mmbase.TreePathComparator;
import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.security.builders.Contexts;
import com.finalist.cmsc.util.NameUtil;


public final class SecurityUtil {


    /** MMbase logging system */
    private static final Logger log = Logging.getLoggerInstance(SecurityUtil.class.getName());

    private static final String CMSC_USERROLES = "cmsc.userroles.";

    public static final String ROLEREL = "rolerel";
    public static final String CONTAINS = "contains";
    public static final String RANKREL = "rank";

    public static final String USER = "user";
    public static final String GROUP = "mmbasegroups";
    public static final String RANK = "mmbaseranks";

    public static final String NUMBER_FIELD = "number";
    public static final String USERNAME_FIELD = "username";
    public static final String ROLE_FIELD = "role";

    private static final String DESTINATION = "DESTINATION";
    private static final String SOURCE = "SOURCE";

    private SecurityUtil() {
        // utility
    }

    public static Node getUserNode(Cloud userCloud) {
//        User user = (User) userCloud.getUser();
//        return userCloud.getNode(user.getNode().getNumber());
        return SearchUtil.findNode(userCloud, USER, USERNAME_FIELD, userCloud.getUser().getIdentifier());
    }

    public static Node getUserNode(Cloud cloud, String username) {
        return SearchUtil.findNode(cloud, USER, USERNAME_FIELD, username);
    }

    public static boolean isLoggedInUser(Cloud cloud, Node userNode) {
//        User user = (User) cloud.getUser();
        return cloud.getUser().getIdentifier().equals(userNode.getStringValue(USERNAME_FIELD));
    }

    public static UserRole getRole(Node channel, boolean rightsInherited, TreeMap<String,UserRole> channelsWithRole) {
       String path = channel.getStringValue(TreeUtil.PATH_FIELD);
       UserRole resultRole = getRole(path, channelsWithRole);
       if (resultRole == null) {
    	   if(path.indexOf("/") == -1) {
    		   resultRole = new UserRole(Role.NONE, rightsInherited);
    	   }
    	   else {
    		   resultRole = new UserRole(Role.NONE, true);
    	   }
       }
       return resultRole;
    }

    private static UserRole getRole(String path, TreeMap<String, UserRole> channelsWithRole) {
       UserRole resultRole = null;
       Iterator<String> keysIter = channelsWithRole.keySet().iterator();
       while (keysIter.hasNext()) {
          String keyPath = keysIter.next();

          // most specific keys are first in order so when the path of the channel startswith
          // the keypath we have the channel where the rights are from inherited.
          boolean onPath = false;
          if (path.length() == keyPath.length()) {
             onPath = path.equals(keyPath);
          }
          else {
             onPath = path.startsWith(keyPath + "/");
          }
          
          if (onPath) {
             UserRole userRole = channelsWithRole.get(keyPath);
             // when path is equal to the keypath than is the role not inherited
             resultRole = new UserRole(userRole.getRole(), !path.equals(keyPath));
             break;
          }
       }
       return resultRole;
    }

    public static Map<Integer,UserRole> buildRolesFromRequest(HttpServletRequest request) {
        Map<Integer,UserRole> roles = new HashMap<Integer,UserRole>();
        Enumeration<String> requestEnum = request.getParameterNames();
        while (requestEnum.hasMoreElements()) {
           String name= requestEnum.nextElement();
           if (name.startsWith("role_")) {
              String role = request.getParameter(name);
              if (!role.equals("-1")) {
                 roles.put( Integer.valueOf(name.substring(5)), new UserRole( Integer.parseInt(role),false));
              }
              else {
                 roles.put( Integer.valueOf(name.substring(5)), null);
              }
           }
        }
        return roles;
    }

    public static TreeMap<String,UserRole> getLoggedInRoleMap(Cloud cloud, String managerName,
            String relationName, String fragmentFieldname) {
        LinkedHashMap<String, String> treeManagers = new LinkedHashMap<String, String>();
        treeManagers.put(managerName, fragmentFieldname);
        return getLoggedInRoleMap(cloud, treeManagers, relationName);
    }


    public static TreeMap<String, UserRole> getLoggedInRoleMap(Cloud cloud,  LinkedHashMap<String,String> treeManagers,
            String relationName) {

        String channelName = TreeUtil.getRootManager(treeManagers);
        TreeMap<String, UserRole> channelsWithRole = (TreeMap<String, UserRole>) cloud
                .getProperty(CMSC_USERROLES + channelName);
        if (channelsWithRole == null) {
            // use a TreeMap so the that keys (in our case paths) are ordered
            channelsWithRole = getNewRolesMap();

            Node user = getUserNode(cloud);
            getUserRoleMap(user, treeManagers, relationName, channelsWithRole);

            cloud.setProperty(CMSC_USERROLES + channelName, channelsWithRole);
        }
        return channelsWithRole;
    }

	public static void getUserRoleMap(Node user,  LinkedHashMap<String,String> treeManagers, String relationName, TreeMap<String, UserRole> channelsWithRole) {
        checkUser(user);
		NodeList groups = getGroups(user);
		if (groups.size() >= 1) {
		    Node group = groups.getNode(0);
		    fillChannelsWithRole(group, channelsWithRole, treeManagers, relationName);
		    if (groups.size() > 1) {
		        for (int i = 1; i < groups.size(); i++) {
		            TreeMap<String, UserRole> extraRoles = getNewRolesMap();
		            Node extraGroup = groups.getNode(i);
		            fillChannelsWithRole(extraGroup, extraRoles, treeManagers, relationName);

                  // upgrade user role map based on the data of the new group data map
                  for (Map.Entry<String, UserRole> entry : channelsWithRole.entrySet()) {
                     String path = entry.getKey();
                     UserRole userRole = entry.getValue();
                     UserRole newGroupRole = getRole(path, extraRoles);
                     if (newGroupRole != null && userRole.getRole().getId() < newGroupRole.getRole().getId()) {
                         channelsWithRole.put(path, newGroupRole);
                     }
                 }

                 // add missing data to user role map based on new group data map
                 for (Map.Entry<String, UserRole> entry : extraRoles.entrySet()) {
                     String extraPath = entry.getKey();
                     UserRole newGroupRole = entry.getValue();
                     UserRole channelRole = getRole(extraPath, channelsWithRole);
                     Role userRole;
                     if (channelRole == null) {
                        userRole = Role.NONE;
                     }
                     else {
                        userRole = channelRole.getRole();
                     }
                     if (userRole.getId() < newGroupRole.getRole().getId()) {
                         channelsWithRole.put(extraPath, newGroupRole);
                     }
                 }
		        }
		    }
		}
	}

    public static TreeMap<String,UserRole> getNewRolesMap() {
        return new TreeMap<String,UserRole>(new TreePathComparator());
    }

    public static void fillChannelsWithRole(Node group, TreeMap<String,UserRole> channelsWithRole, String managerName,
            String relationName, String fragmentFieldname) {
        checkGroup(group);
        LinkedHashMap<String,String> treeManagers = new LinkedHashMap<String, String>();
        treeManagers.put(managerName, fragmentFieldname);
        fillChannelsWithRole(group, channelsWithRole, treeManagers, relationName);
    }

    /**
     * Fills a Map with the role for the channels with a rolerel to the given group.
     * @param group Node of group
     * @param channelsWithRole Map to fill with roles
     * @param treeManagers managers used in the tree
     * @param relationName relation between the managers
     * @param fragmentFieldname fieldname the path is constructed with
     */
    public static void fillChannelsWithRole(Node group, TreeMap<String,UserRole> channelsWithRole,
            LinkedHashMap<String,String> treeManagers, String relationName) {
        checkGroup(group);
        Cloud cloud = group.getCloud();
        List<String> managers = TreeUtil.convertToList(treeManagers);

        for (int i = managers.size() - 1; i >= 0; i--) {
            String managerName = managers.get(i);

            if (cloud.hasRelationManager(GROUP, managerName, ROLEREL)) {
                // retrieve a list of all channels with a rolerel to this group
                NodeList channelNodeList = cloud.getList(null,
                        managerName + "," + ROLEREL + "," + GROUP,
                        ROLEREL + "." + ROLE_FIELD+ ", " + managerName + "." + NUMBER_FIELD,
                        "[" + GROUP + "." + NUMBER_FIELD + "] = " + group.getNumber(),
                        null, null, null, true);

                NodeIterator nodeIterator = channelNodeList.nodeIterator();
                while (nodeIterator.hasNext()) {
                    Node channelRoleGroupNode = nodeIterator.nextNode();
                    Node channelNode = cloud.getNode( channelRoleGroupNode.getStringValue(managerName + "." + NUMBER_FIELD));
                    // no path found for this channel in cache retrieve it from mmbase
                    String path = TreeUtil.getPathToRootString(channelNode, treeManagers, relationName, true);
                    int role = channelRoleGroupNode.getIntValue(ROLEREL + "." + ROLE_FIELD);
                    channelsWithRole.put(path, new UserRole(role, false));
                }
            }
        }
    }

    public static void setGroupRights(Cloud cloud, Node group, Map<Integer, UserRole> rights, String managerName) {
        checkGroup(group);
        List<String> treeManagers = new ArrayList<String>();
        treeManagers.add(managerName);
        setGroupRights(cloud, group, rights, treeManagers);
    }

    public static void setGroupRights(Cloud cloud, Node group, Map<Integer, UserRole> rights, List<String> treeManagers) {
        checkGroup(group);
        clearUserRoles(cloud, treeManagers);

        List<Integer> rolesDone = new ArrayList<Integer>();

        for (String managerName : treeManagers) {
            if (cloud.hasRelationManager(GROUP, managerName, ROLEREL)) {
                RelationList list = group.getRelations(ROLEREL, cloud.getNodeManager(managerName), DESTINATION);
                for (RelationIterator iter = list.relationIterator(); iter.hasNext();) {
                   Relation rolerel = iter.nextRelation();
                   Integer channelNumber = Integer.valueOf(rolerel.getDestination().getNumber());
                   if (rights.containsKey(channelNumber)) {
                      rolesDone.add(channelNumber);
                      UserRole role = rights.get(channelNumber);
                      if (role == null) {
                         rolerel.delete();
                      } else {
                         rolerel.setIntValue(ROLE_FIELD, role.getRole().getId());
                         rolerel.commit();
                      }
                   }
                }
            }
        }

        Iterator<Integer> keys = rights.keySet().iterator();
        while (keys.hasNext()) {
           Integer channelNumber = keys.next();
           UserRole role = rights.get(channelNumber);
           if (!rolesDone.contains(channelNumber) && role != null) {
              Node channelNode = cloud.getNode(channelNumber.intValue());
              addRole(group, channelNode, role.getRole());
           }
        }
    }

    public static void clearUserRoles(Cloud cloud, List<String> treeManagers) {
        cloud.setProperty(CMSC_USERROLES + TreeUtil.getRootManager(treeManagers), null);
    }


    public static void clearUserRoles(Cloud cloud) {
        for (Iterator<Object> iter = cloud.getProperties().keySet().iterator(); iter.hasNext();) {
            String property = iter.next().toString();
            if (property.startsWith(CMSC_USERROLES)) {
                cloud.setProperty(property, null);
            }
        }
    }

    public static void addRole(Cloud cloud, String channelNumber, Node group, Role role, List<String> treeManagers) {
        Node channelNode = cloud.getNode(channelNumber);
        addRole(cloud, channelNode, group, role, treeManagers);
    }

    public static void addRole(Cloud cloud, Node channelNode, Node group, Role role,  List<String> treeManagers) {
        addRole(group, channelNode, role);
        clearUserRoles(cloud, treeManagers);
    }

    public static void addRole(Node group, Node channelNode, Role role) {
        checkGroup(group);

        RelationManager rolemanager =
            group.getCloud().getRelationManager(GROUP, channelNode.getNodeManager().getName(), ROLEREL);
          Relation relation = rolemanager.createRelation(group, channelNode);
          relation.setIntValue(ROLE_FIELD, role.getId());
          relation.commit();
    }

   private static void addToList(List<Node> l, Node node) {
       for (int i = 0; i < l.size(); i++) {
          Node n = l.get(i);
          if (n.getNumber() == node.getNumber()) {
             return;
          }
       }
       l.add(node);
    }

    private static void removeFromList(List<Node> ret, Node node) {
       for (int i = 0; i < ret.size(); i++) {
          Node user = ret.get(i);
          if (user.getNumber() == node.getNumber()) {
             ret.remove(i);
          }
       }
    }

    public static boolean isWriter(UserRole role) {
        return role.getRole().getId() >= Role.WRITER.getId();
    }

    public static boolean isEditor(UserRole role) {
        return role.getRole().getId() >= Role.EDITOR.getId();
    }

    public static boolean isChiefEditor(UserRole role) {
        return role.getRole().getId() >= Role.CHIEFEDITOR.getId();
    }

    public static boolean isWebmaster(UserRole role) {
        return role.getRole().getId() >= Role.WEBMASTER.getId();
    }

    public static TreeMap<String, UserRole> getRoleMap(LinkedHashMap<String, String> treeManagers, String relationName, Node group) {
        TreeMap<String, UserRole> channelsWithRole = getNewRolesMap();
        fillChannelsWithRole(group, channelsWithRole, treeManagers, relationName);
        return channelsWithRole;
    }

    public static TreeMap<String, UserRole> getRoleMap(String managerName, String relationName, String fragmentFieldname, Node group) {
        LinkedHashMap<String, String> treeManagers = new LinkedHashMap<String, String>();
        treeManagers.put(managerName, fragmentFieldname);
        return getRoleMap(treeManagers, relationName, group);
    }

    public static Node getAdministratorsGroup(Cloud cloud) {
        return SearchUtil.findNode(cloud, GROUP, "name", "Administrators");
    }

    public static NodeList getMembers(Node group) {
        checkGroup(group);
        return SearchUtil.findRelatedOrderedNodeList(group, USER, CONTAINS, "username");
    }

    public static NodeList getUsers(Cloud cloud) {
        NodeList users = SearchUtil.findOrderedNodeList(cloud, USER, "username");
        for (Iterator<Node> iter = users.iterator(); iter.hasNext();) {
            Node user = iter.next();
            if ("anonymous".equalsIgnoreCase(user.getStringValue("username"))) {
                iter.remove();
                break;
            }
        }
        return users;
    }

    public static void setGroupMembers(Cloud cloud, Node groupNode, String[] newMembers) {
        checkGroup(groupNode);
        List<String> currentMembers = new ArrayList<String>();

        NodeList membersList = SecurityUtil.getMembers(groupNode);
        for (Iterator<Node> iter = membersList.iterator(); iter.hasNext();) {
            Node member = iter.next();
            currentMembers.add(String.valueOf(member.getNumber()));
        }
        if (newMembers != null) {
            for (String newMember : newMembers) {
                if (currentMembers.contains(newMember)) {
                    currentMembers.remove(newMember);
                }
                else {
                    Node userNode = cloud.getNode(newMember);
                    addUser(cloud, groupNode, userNode);
                }
            }
        }

        RelationList relations = groupNode.getRelations(CONTAINS, USER);
        RelationIterator iter = relations.relationIterator();
        while (iter.hasNext()) {
            Relation r = iter.nextRelation();
            if (currentMembers.contains(String.valueOf(r.getDestination().getNumber()))) {
                r.delete();
            }
        }

        clearUserRoles(cloud);
    }

    private static void addUser(Cloud cloud, Node groupNode, Node userNode) {
        checkGroup(groupNode);
        checkUser(userNode);

        RelationManager GroupRelManager = cloud.getRelationManager(GROUP, USER, CONTAINS);
        if (GroupRelManager != null) {
            Relation GroupRel = groupNode.createRelation(userNode, GroupRelManager);
            GroupRel.commit();
        }
    }

    public static NodeList getGroups(Node user) {
        checkUser(user);
        return user.getRelatedNodes(GROUP, CONTAINS, SOURCE);
    }

    public static Node getRank(Node userNode) {
        checkUser(userNode);
        Node rankNode = null;
        NodeList userRanks = userNode.getRelatedNodes(RANK, RANKREL, "DESTINATION");
        if (userRanks.size() > 0) {
            rankNode = userRanks.getNode(0);
        }
        return rankNode;
    }

    public static void setRank(Cloud cloud, Node userNode, Node newRankNode) {
        checkUser(userNode);
        checkRank(newRankNode);


        RelationManager rankRelManager = cloud.getRelationManager(USER, RANK, RANKREL);
         if (rankRelManager != null) {
             userNode.deleteRelations(RANKREL);
             Relation rankRel = userNode.createRelation(newRankNode, rankRelManager);
             rankRel.commit();
         }
         else {
             log.warn("Relation manager for ranks not found: "+USER+", "+RANK+", " + RANKREL);
         }
    }

    public static Node getContext(Cloud cloud) {
        int newContext = ((Contexts) Contexts.getBuilder()).getDefaultContextNode().getNumber();
        Node newContextNode = cloud.getNode(newContext);
        return newContextNode;
    }

    public static String getFullname(Node user) {
        checkUser(user);

        return NameUtil.getFullName(user.getStringValue("firstname"),
                user.getStringValue("prefix"),
                user.getStringValue("surname"));
    }


    private static void checkGroup(Node group) {
        if (!GROUP.equals(group.getNodeManager().getName())) {
            throw new IllegalArgumentException("Node " + group.getNumber() + " is not a " + GROUP);
        }
    }

    private static void checkUser(Node user) {
        if (!USER.equals(user.getNodeManager().getName())) {
            throw new IllegalArgumentException("Node " + user.getNumber() + " is not a " + USER);
        }
    }

    private static void checkRank(Node rank) {
        if (!RANK.equals(rank.getNodeManager().getName())) {
            throw new IllegalArgumentException("Node " + rank.getNumber() + " is not a " + RANK);
        }
    }

    public static List<Node> getUsersWithRights(Node channel, Role requiredRole, String managerName, String relationName) {
        List<String> treeManagers = new ArrayList<String>();
        treeManagers.add(managerName);
        return getUsersWithRights(channel, requiredRole, treeManagers, relationName);
    }

    public static List<Node> getUsersWithRights(Node channel, Role requiredRole, List<String> treeManagers, String relationName) {
        List<Node> path = TreeUtil.getPathToRoot(channel, treeManagers, relationName);
        List<Node> groups = new ArrayList<Node>();

        Iterator<Node> iter = path.iterator();
        while (iter.hasNext()) {
           Node pathChannel = iter.next();
           RelationManager rolerelManager = channel.getCloud().getRelationManager(GROUP, pathChannel.getNodeManager().getName(), ROLEREL);

           RelationList rolerels = rolerelManager.getRelations(pathChannel);

           RelationIterator rels = rolerels.relationIterator();
           while (rels.hasNext()) {
              Relation relation = rels.nextRelation();

              Node sourceGroup = relation.getSource();

              if (relation.getIntValue("role") >= requiredRole.getId()) {
                 addToList(groups, sourceGroup);
              }
              if (relation.getIntValue("role") < requiredRole.getId()) {
                 removeFromList(groups, sourceGroup);
              }
           }
        }

        List<Node> users = new ArrayList<Node>();
        Iterator<Node> groupIter = groups.iterator();
        while (groupIter.hasNext()) {
           Node group = groupIter.next();
           List<Node> userNodes = getMembers(group);
           for (Node user : userNodes) {
                addToList(users, user);
           }
        }

        return users;
    }

    public static void changePassword(Node userNode, String newpassword) {
        userNode.setStringValue("password", newpassword);
        userNode.commit();

//        if (Publish.isPublished(userNode)) {
//            Publish.publish(userNode);
//        }
    }
}
